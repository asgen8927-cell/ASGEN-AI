package com.example.model

import android.net.Uri

enum class MessageStatus(val displayName: String) {
    READ_DOUBLE_BLUE("Read (Double Blue)"),
    DELIVERED_DOUBLE_GREY("Delivered (Double Grey)"),
    SENT_SINGLE_GREY("Sent (Single Grey)"),
    SENDING_CLOCK("Sending (Clock)")
}

enum class MessageType(val displayName: String) {
    SENT("Sent (Right Green)"),
    RECEIVED("Received (Left Grey)")
}

data class HeaderState(
    val contactName: String = "+1 (555) 382-9014",
    val subtitleStatus: String = "online",
    val avatarUri: Uri? = null,
    val avatarCustomUrl: String = "",
    val avatarInitials: String = "W",
    val avatarColorHex: Long = 0xFF00A884,
    val isVerified: Boolean = false,
    val isOnline: Boolean = true
)

data class LinkPreviewData(
    val url: String = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
    val title: String = "Rick Astley - Never Gonna Give You Up (Official Music Video)",
    val description: String = "The official video for “Never Gonna Give You Up” by Rick Astley. Taken from the album ‘Whenever You Need Somebody’...",
    val domain: String = "youtube.com",
    val imageUrl: String = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
    val localImageUri: Uri? = null,
    val isScraping: Boolean = false,
    val scrapeError: String? = null
)

data class MockupMessageState(
    val bodyText: String = "Check out this amazing video! https://www.youtube.com/watch?v=dQw4w9WgXcQ",
    val timestamp: String = "10:42 AM",
    val messageType: MessageType = MessageType.SENT,
    val status: MessageStatus = MessageStatus.READ_DOUBLE_BLUE,
    val isStarred: Boolean = false,
    val isEdited: Boolean = false,
    val showLinkPreview: Boolean = true
)

data class DeviceChromeState(
    val statusTime: String = "10:42",
    val batteryLevel: Int = 94,
    val isCharging: Boolean = false,
    val isWifiOn: Boolean = true,
    val networkType: String = "5G", // "5G", "4G", "VoLTE"
    val showStatusBar: Boolean = true,
    val showBottomBar: Boolean = true,
    val showEncryptionBanner: Boolean = true,
    val showDatePill: Boolean = true,
    val dateText: String = "TODAY"
)

data class UrlPreset(
    val label: String,
    val url: String,
    val category: String,
    val previewTitle: String,
    val previewDesc: String,
    val domain: String,
    val imageUrl: String
)

object PresetSamples {
    val presets = listOf(
        UrlPreset(
            label = "YouTube Music",
            url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            category = "Video",
            previewTitle = "Rick Astley - Never Gonna Give You Up (Official Music Video)",
            previewDesc = "The official video for 'Never Gonna Give You Up' by Rick Astley.",
            domain = "youtube.com",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80"
        ),
        UrlPreset(
            label = "GitHub Repo",
            url = "https://github.com/torvalds/linux",
            category = "Developer",
            previewTitle = "torvalds/linux: Linux kernel source tree",
            previewDesc = "Linux kernel source tree - git repository mirrors for Linux maintainers.",
            domain = "github.com",
            imageUrl = "https://images.unsplash.com/photo-1618401471353-b98aedd04e11?w=800&auto=format&fit=crop&q=80"
        ),
        UrlPreset(
            label = "Google AI DeepMind",
            url = "https://deepmind.google/technologies/gemini",
            category = "AI & Tech",
            previewTitle = "Gemini: Google's next generation AI model family",
            previewDesc = "Explore Gemini, the multimodal model built from the ground up for reasoning and coding.",
            domain = "deepmind.google",
            imageUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=800&auto=format&fit=crop&q=80"
        ),
        UrlPreset(
            label = "Spotify Track",
            url = "https://open.spotify.com/track/4cOdK2wGLETKBW3PvgPWqT",
            category = "Music",
            previewTitle = "Starboy (feat. Daft Punk) - Song by The Weeknd",
            previewDesc = "Listen to Starboy on Spotify. The Weeknd · Song · 2016.",
            domain = "open.spotify.com",
            imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&auto=format&fit=crop&q=80"
        ),
        UrlPreset(
            label = "News Article",
            url = "https://www.bbc.com/news/technology",
            category = "News",
            previewTitle = "Major Breakthrough in Quantum Computing Announced",
            previewDesc = "Scientists achieve stable logical qubits marking a milestone in computational physics.",
            domain = "bbc.com",
            imageUrl = "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800&auto=format&fit=crop&q=80"
        )
    )
}
