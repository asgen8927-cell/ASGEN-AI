package com.example.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.DeviceChromeState
import com.example.model.HeaderState
import com.example.model.LinkPreviewData
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.model.MockupMessageState
import com.example.model.PresetSamples
import com.example.model.UrlPreset
import com.example.network.LinkScraperService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MockupViewModel(
    private val scraperService: LinkScraperService = LinkScraperService()
) : ViewModel() {

    private val _headerState = MutableStateFlow(HeaderState())
    val headerState: StateFlow<HeaderState> = _headerState.asStateFlow()

    private val _linkData = MutableStateFlow(LinkPreviewData())
    val linkData: StateFlow<LinkPreviewData> = _linkData.asStateFlow()

    private val _messageState = MutableStateFlow(MockupMessageState())
    val messageState: StateFlow<MockupMessageState> = _messageState.asStateFlow()

    private val _chromeState = MutableStateFlow(DeviceChromeState())
    val chromeState: StateFlow<DeviceChromeState> = _chromeState.asStateFlow()

    private val _exportMessage = MutableStateFlow<String?>(null)
    val exportMessage: StateFlow<String?> = _exportMessage.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private var debounceJob: Job? = null

    init {
        // Automatically sync initial time with current device time
        setCurrentTimeForMockup()
    }

    fun onUrlInputChanged(newUrl: String, autoScrape: Boolean = true) {
        _linkData.update { it.copy(url = newUrl, scrapeError = null) }

        // Update message body if it previously matched the previous URL or empty
        val currentBody = _messageState.value.bodyText
        if (currentBody.isBlank() || currentBody.startsWith("Check out this") || currentBody.startsWith("http")) {
            _messageState.update { it.copy(bodyText = "Check out this: $newUrl") }
        }

        if (autoScrape && newUrl.length >= 8 && (newUrl.contains(".") || newUrl.startsWith("http"))) {
            debounceJob?.cancel()
            debounceJob = viewModelScope.launch {
                delay(800) // Debounce typing
                scrapeUrlInternal(newUrl)
            }
        }
    }

    fun scrapeCurrentUrl() {
        debounceJob?.cancel()
        viewModelScope.launch {
            scrapeUrlInternal(_linkData.value.url)
        }
    }

    private suspend fun scrapeUrlInternal(url: String) {
        if (url.isBlank()) return
        _linkData.update { it.copy(isScraping = true, scrapeError = null) }

        val result = scraperService.scrapeUrl(url)
        result.onSuccess { meta ->
            _linkData.update { current ->
                current.copy(
                    title = meta.title,
                    description = meta.description,
                    domain = meta.domain,
                    imageUrl = if (meta.imageUrl.isNotBlank()) meta.imageUrl else current.imageUrl,
                    isScraping = false,
                    scrapeError = null
                )
            }
        }.onFailure { error ->
            _linkData.update {
                it.copy(
                    isScraping = false,
                    scrapeError = error.localizedMessage ?: "Failed to fetch OpenGraph metadata"
                )
            }
        }
    }

    fun applyPreset(preset: UrlPreset) {
        debounceJob?.cancel()
        _linkData.update {
            it.copy(
                url = preset.url,
                title = preset.previewTitle,
                description = preset.previewDesc,
                domain = preset.domain,
                imageUrl = preset.imageUrl,
                localImageUri = null,
                isScraping = false,
                scrapeError = null
            )
        }
        _messageState.update {
            it.copy(bodyText = "Have you seen this? ${preset.url}")
        }
    }

    fun updateLinkTitle(title: String) {
        _linkData.update { it.copy(title = title) }
    }

    fun updateLinkDescription(description: String) {
        _linkData.update { it.copy(description = description) }
    }

    fun updateLinkDomain(domain: String) {
        _linkData.update { it.copy(domain = domain) }
    }

    fun updateLinkImageUrl(imageUrl: String) {
        _linkData.update { it.copy(imageUrl = imageUrl, localImageUri = null) }
    }

    fun updateLinkLocalImageUri(uri: Uri?) {
        _linkData.update { it.copy(localImageUri = uri) }
    }

    fun updateMessageBody(body: String) {
        _messageState.update { it.copy(bodyText = body) }
    }

    fun updateMessageTimestamp(time: String) {
        _messageState.update { it.copy(timestamp = time) }
    }

    fun updateMessageType(type: MessageType) {
        _messageState.update { it.copy(messageType = type) }
    }

    fun updateMessageStatus(status: MessageStatus) {
        _messageState.update { it.copy(status = status) }
    }

    fun toggleStarred() {
        _messageState.update { it.copy(isStarred = !it.isStarred) }
    }

    fun toggleEdited() {
        _messageState.update { it.copy(isEdited = !it.isEdited) }
    }

    fun toggleShowLinkPreview() {
        _messageState.update { it.copy(showLinkPreview = !it.showLinkPreview) }
    }

    fun updateContactName(name: String) {
        _headerState.update { it.copy(contactName = name) }
    }

    fun updateSubtitleStatus(status: String) {
        _headerState.update { it.copy(subtitleStatus = status) }
    }

    fun updateAvatarUri(uri: Uri?) {
        _headerState.update { it.copy(avatarUri = uri) }
    }

    fun updateAvatarInitials(initials: String) {
        _headerState.update { it.copy(avatarInitials = initials) }
    }

    fun updateAvatarColor(colorHex: Long) {
        _headerState.update { it.copy(avatarColorHex = colorHex) }
    }

    fun toggleVerified() {
        _headerState.update { it.copy(isVerified = !it.isVerified) }
    }

    fun toggleOnline() {
        _headerState.update { it.copy(isOnline = !it.isOnline) }
    }

    fun updateStatusTime(time: String) {
        _chromeState.update { it.copy(statusTime = time) }
    }

    fun updateBatteryLevel(battery: Int) {
        _chromeState.update { it.copy(batteryLevel = battery.coerceIn(1, 100)) }
    }

    fun updateNetworkType(type: String) {
        _chromeState.update { it.copy(networkType = type) }
    }

    fun toggleWifi() {
        _chromeState.update { it.copy(isWifiOn = !it.isWifiOn) }
    }

    fun toggleStatusBar() {
        _chromeState.update { it.copy(showStatusBar = !it.showStatusBar) }
    }

    fun toggleBottomBar() {
        _chromeState.update { it.copy(showBottomBar = !it.showBottomBar) }
    }

    fun toggleEncryptionBanner() {
        _chromeState.update { it.copy(showEncryptionBanner = !it.showEncryptionBanner) }
    }

    fun toggleDatePill() {
        _chromeState.update { it.copy(showDatePill = !it.showDatePill) }
    }

    fun updateDateText(text: String) {
        _chromeState.update { it.copy(dateText = text) }
    }

    fun setCurrentTimeForMockup() {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val statusFormat = SimpleDateFormat("h:mm", Locale.getDefault())
        val now = Date()
        val formattedTime = timeFormat.format(now).uppercase()
        val formattedStatus = statusFormat.format(now)

        _messageState.update { it.copy(timestamp = formattedTime) }
        _chromeState.update { it.copy(statusTime = formattedStatus) }
    }

    fun setExportStatus(message: String?) {
        _exportMessage.value = message
    }

    fun setExporting(isExporting: Boolean) {
        _isExporting.value = isExporting
    }
}
