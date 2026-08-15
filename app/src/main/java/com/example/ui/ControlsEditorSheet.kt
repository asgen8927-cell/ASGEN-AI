package com.example.ui

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.DeviceChromeState
import com.example.model.HeaderState
import com.example.model.LinkPreviewData
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.model.MockupMessageState
import com.example.model.PresetSamples
import com.example.model.UrlPreset
import com.example.ui.theme.StudioAccentCyan
import com.example.ui.theme.StudioAccentTeal
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.WAGreenFab

@Composable
fun ControlsEditorSheet(
    viewModel: MockupViewModel,
    headerState: HeaderState,
    linkData: LinkPreviewData,
    messageState: MockupMessageState,
    chromeState: DeviceChromeState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Media pickers
    val linkImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateLinkLocalImageUri(uri)
        }
    }

    val avatarImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateAvatarUri(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick URL Presets Bar
        PresetsSection(
            onSelectPreset = { preset -> viewModel.applyPreset(preset) }
        )

        // 1. Link & Scraping Section (OpenGraph)
        EditorCard(
            title = "Link & Auto OpenGraph Preview",
            icon = Icons.Default.Link,
            initiallyExpanded = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // URL input with Paste & Scrape buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = linkData.url,
                        onValueChange = { viewModel.onUrlInputChanged(it, autoScrape = true) },
                        label = { Text("Link / URL") },
                        placeholder = { Text("https://example.com/post") },
                        singleLine = true,
                        trailingIcon = {
                            if (linkData.url.isNotBlank()) {
                                IconButton(onClick = { viewModel.onUrlInputChanged("", autoScrape = false) }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear URL", tint = StudioTextMuted)
                                }
                            }
                        },
                        colors = studioTextFieldColors(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("link_url_input")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Paste from clipboard button
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            if (clipboard.hasPrimaryClip() &&
                                (clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true ||
                                 clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML) == true)
                            ) {
                                val item = clipboard.primaryClip?.getItemAt(0)
                                val text = item?.text?.toString().orEmpty()
                                if (text.isNotBlank()) {
                                    viewModel.onUrlInputChanged(text, autoScrape = true)
                                }
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioAccentCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = "Paste", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Paste", fontSize = 13.sp)
                    }

                    // Scrape action button
                    Button(
                        onClick = { viewModel.scrapeCurrentUrl() },
                        enabled = !linkData.isScraping && linkData.url.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioAccentTeal,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scrape_button")
                    ) {
                        if (linkData.isScraping) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fetching...", fontSize = 13.sp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Scrape", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fetch Preview", fontSize = 13.sp)
                        }
                    }
                }

                if (linkData.scrapeError != null) {
                    Text(
                        text = "Scrape info: ${linkData.scrapeError}",
                        color = Color(0xFFF87171),
                        fontSize = 12.sp
                    )
                }

                HorizontalDivider(color = StudioBorder, modifier = Modifier.padding(vertical = 4.dp))

                // Editable Fields & Fallbacks
                Text(
                    text = "Custom Metadata & Fallbacks",
                    color = StudioTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = linkData.title,
                    onValueChange = { viewModel.updateLinkTitle(it) },
                    label = { Text("OG Card Title") },
                    colors = studioTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = linkData.domain,
                    onValueChange = { viewModel.updateLinkDomain(it) },
                    label = { Text("Domain / Publisher") },
                    placeholder = { Text("e.g. youtube.com") },
                    colors = studioTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = linkData.description,
                    onValueChange = { viewModel.updateLinkDescription(it) },
                    label = { Text("OG Description") },
                    maxLines = 3,
                    colors = studioTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Image Selection Card (URL or Local Gallery)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val imageSrc = linkData.localImageUri ?: linkData.imageUrl
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurfaceVariant)
                            .border(1.dp, StudioBorder, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageSrc.toString().isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageSrc)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Preview Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.Image, contentDescription = "No image", tint = StudioTextMuted)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = {
                                linkImagePicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioSurfaceVariant,
                                contentColor = StudioTextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "Upload", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pick Custom Image", fontSize = 13.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = linkData.imageUrl,
                    onValueChange = { viewModel.updateLinkImageUrl(it) },
                    label = { Text("Image URL") },
                    singleLine = true,
                    colors = studioTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 2. Chat Message & Delivery Section
        EditorCard(
            title = "Chat Message & Checkmarks",
            icon = Icons.Default.Message,
            initiallyExpanded = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = messageState.bodyText,
                    onValueChange = { viewModel.updateMessageBody(it) },
                    label = { Text("Message Body (Text + Link)") },
                    minLines = 2,
                    maxLines = 4,
                    colors = studioTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Message Type (Sent / Received)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bubble Direction", color = StudioTextSecondary, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = messageState.messageType == MessageType.SENT,
                            onClick = { viewModel.updateMessageType(MessageType.SENT) },
                            label = { Text("Sent (Right)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioAccentTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = messageState.messageType == MessageType.RECEIVED,
                            onClick = { viewModel.updateMessageType(MessageType.RECEIVED) },
                            label = { Text("Received (Left)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioAccentCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // Checkmark Status (for sent messages)
                if (messageState.messageType == MessageType.SENT) {
                    Text("Read Status Ticks", color = StudioTextSecondary, fontSize = 13.sp)
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MessageStatus.values().forEach { status ->
                            FilterChip(
                                selected = messageState.status == status,
                                onClick = { viewModel.updateMessageStatus(status) },
                                label = { Text(status.displayName, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF005C4B),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Timestamp input + Quick Sync button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = messageState.timestamp,
                        onValueChange = { viewModel.updateMessageTimestamp(it) },
                        label = { Text("Message Timestamp") },
                        singleLine = true,
                        colors = studioTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { viewModel.setCurrentTimeForMockup() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioSurfaceVariant,
                            contentColor = StudioTextPrimary
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Now", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Current Time", fontSize = 12.sp)
                    }
                }

                // Additional message toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Link Preview Card", color = StudioTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = messageState.showLinkPreview,
                        onCheckedChange = { viewModel.toggleShowLinkPreview() },
                        colors = studioSwitchColors()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Starred Message (Star icon)", color = StudioTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = messageState.isStarred,
                        onCheckedChange = { viewModel.toggleStarred() },
                        colors = studioSwitchColors()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edited Label", color = StudioTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = messageState.isEdited,
                        onCheckedChange = { viewModel.toggleEdited() },
                        colors = studioSwitchColors()
                    )
                }
            }
        }

        // 3. Contact & Header Customization
        EditorCard(
            title = "Contact & Header Info",
            icon = Icons.Default.Person,
            initiallyExpanded = false
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = headerState.contactName,
                    onValueChange = { viewModel.updateContactName(it) },
                    label = { Text("Contact Name / Phone Number") },
                    singleLine = true,
                    colors = studioTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = headerState.subtitleStatus,
                    onValueChange = { viewModel.updateSubtitleStatus(it) },
                    label = { Text("Subtitle / Status") },
                    singleLine = true,
                    colors = studioTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Status Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("online", "typing...", "last seen today at 10:45 AM").forEach { presetStatus ->
                        OutlinedButton(
                            onClick = { viewModel.updateSubtitleStatus(presetStatus) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (headerState.subtitleStatus == presetStatus) WAGreenFab else StudioTextSecondary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (headerState.subtitleStatus == presetStatus) WAGreenFab else StudioBorder
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(presetStatus, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }

                // Verified Badge Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("WhatsApp Verified Badge", color = StudioTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = headerState.isVerified,
                        onCheckedChange = { viewModel.toggleVerified() },
                        colors = studioSwitchColors()
                    )
                }

                // Online Dot Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Online Green Dot", color = StudioTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = headerState.isOnline,
                        onCheckedChange = { viewModel.toggleOnline() },
                        colors = studioSwitchColors()
                    )
                }

                // Profile Avatar Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(headerState.avatarColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (headerState.avatarUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(headerState.avatarUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = headerState.avatarInitials.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = {
                                avatarImagePicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioSurfaceVariant,
                                contentColor = StudioTextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pick Profile Photo", fontSize = 12.sp)
                        }
                    }
                }

                // Avatar Initials & Color Palette
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = headerState.avatarInitials,
                        onValueChange = { viewModel.updateAvatarInitials(it) },
                        label = { Text("Initials") },
                        singleLine = true,
                        colors = studioTextFieldColors(),
                        modifier = Modifier.width(90.dp)
                    )

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(0xFF00A884, 0xFF38BDF8, 0xFFEC4899, 0xFFF59E0B, 0xFF8B5CF6, 0xFF10B981).forEach { colorHex ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorHex))
                                    .clickable { viewModel.updateAvatarColor(colorHex) }
                                    .border(
                                        width = if (headerState.avatarColorHex == colorHex) 2.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }

        // 4. Device Chrome & Mockup Frame Extras
        EditorCard(
            title = "Device Frame & Status Bar",
            icon = Icons.Default.Smartphone,
            initiallyExpanded = false
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Status Bar (Time, Battery, Signal)", color = StudioTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = chromeState.showStatusBar,
                        onCheckedChange = { viewModel.toggleStatusBar() },
                        colors = studioSwitchColors()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Bottom Action Bar", color = StudioTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = chromeState.showBottomBar,
                        onCheckedChange = { viewModel.toggleBottomBar() },
                        colors = studioSwitchColors()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show End-to-End Encryption Banner", color = StudioTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = chromeState.showEncryptionBanner,
                        onCheckedChange = { viewModel.toggleEncryptionBanner() },
                        colors = studioSwitchColors()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Date Pill", color = StudioTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = chromeState.showDatePill,
                        onCheckedChange = { viewModel.toggleDatePill() },
                        colors = studioSwitchColors()
                    )
                }

                if (chromeState.showDatePill) {
                    OutlinedTextField(
                        value = chromeState.dateText,
                        onValueChange = { viewModel.updateDateText(it) },
                        label = { Text("Date Pill Text") },
                        singleLine = true,
                        colors = studioTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (chromeState.showStatusBar) {
                    OutlinedTextField(
                        value = chromeState.statusTime,
                        onValueChange = { viewModel.updateStatusTime(it) },
                        label = { Text("Status Bar Clock") },
                        singleLine = true,
                        colors = studioTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Battery slider
                    Text("Battery Level: ${chromeState.batteryLevel}%", color = StudioTextSecondary, fontSize = 13.sp)
                    Slider(
                        value = chromeState.batteryLevel.toFloat(),
                        onValueChange = { viewModel.updateBatteryLevel(it.toInt()) },
                        valueRange = 1f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = StudioAccentTeal,
                            activeTrackColor = StudioAccentTeal,
                            inactiveTrackColor = StudioSurfaceVariant
                        )
                    )

                    // Network Type Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("5G", "VoLTE", "4G", "LTE").forEach { netType ->
                            FilterChip(
                                selected = chromeState.networkType == netType,
                                onClick = { viewModel.updateNetworkType(netType) },
                                label = { Text(netType, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StudioAccentTeal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresetsSection(
    onSelectPreset: (UrlPreset) -> Unit
) {
    Column {
        Text(
            text = "⚡ Instant Link Presets",
            color = StudioTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetSamples.presets.forEach { preset ->
                Surface(
                    onClick = { onSelectPreset(preset) },
                    shape = RoundedCornerShape(20.dp),
                    color = StudioSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier.testTag("preset_${preset.label.lowercase().replace(" ", "_")}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = preset.label,
                            tint = StudioAccentCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = preset.label,
                            color = StudioTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditorCard(
    title: String,
    icon: ImageVector,
    initiallyExpanded: Boolean = true,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = StudioAccentTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = StudioTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = StudioTextSecondary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun studioTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = StudioAccentTeal,
    unfocusedBorderColor = StudioBorder,
    focusedLabelColor = StudioAccentTeal,
    unfocusedLabelColor = StudioTextSecondary,
    focusedTextColor = StudioTextPrimary,
    unfocusedTextColor = StudioTextPrimary,
    cursorColor = StudioAccentTeal,
    focusedContainerColor = StudioDarkBg,
    unfocusedContainerColor = StudioDarkBg
)

@Composable
fun studioSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = Color.White,
    checkedTrackColor = StudioAccentTeal,
    uncheckedThumbColor = StudioTextMuted,
    uncheckedTrackColor = StudioSurfaceVariant
)
