package com.example.model

import android.net.Uri

enum class MessageType {
    SENT,
    RECEIVED
}

enum class MessageStatus(val displayName: String) {
    PENDING("Clock / Sending"),
    SENT_SINGLE("Single Grey Tick"),
    DELIVERED_DOUBLE("Double Grey Tick"),
    READ_DOUBLE_BLUE("Double Blue Tick")
}

data class LinkPreviewData(
    val url: String = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
    val title: String = "Rick Astley - Never Gonna Give You Up (Official Music Video)",
    val description: String = "The official video for 'Never Gonna Give You Up' by Rick Astley. Restored and remastered in high definition.",
    val domain: String = "youtube.com",
    val imageUrl: String = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&q=80",
    val localImageUri: Uri? = null,
    val isScraping: Boolean = false,
    val scrapeError: String? = null
)

data class HeaderState(
    val contactName: String = "Alex Rivera",
    val subtitleStatus: String = "online",
    val avatarUri: Uri? = null,
    val avatarInitials: String = "AR",
    val avatarColorHex: Long = 0xFF00A884,
    val isVerified: Boolean = false,
    val isOnline: Boolean = true
)

data class MockupMessageState(
    val bodyText: String = "Check out this incredible presentation! https://www.youtube.com/watch?v=dQw4w9WgXcQ",
    val timestamp: String = "10:42 AM",
    val messageType: MessageType = MessageType.SENT,
    val status: MessageStatus = MessageStatus.READ_DOUBLE_BLUE,
    val isStarred: Boolean = false,
    val isEdited: Boolean = false,
    val showLinkPreview: Boolean = true
)

data class DeviceChromeState(
    val showStatusBar: Boolean = true,
    val statusTime: String = "10:42",
    val batteryLevel: Int = 94,
    val networkType: String = "5G",
    val isWifiOn: Boolean = true,
    val showBottomBar: Boolean = true,
    val showEncryptionBanner: Boolean = true,
    val showDatePill: Boolean = true,
    val dateText: String = "TODAY"
)

data class UrlPreset(
    val label: String,
    val url: String,
    val previewTitle: String,
    val previewDesc: String,
    val domain: String,
    val imageUrl: String
)

object PresetSamples {
    val presets = listOf(
        UrlPreset(
            label = "YouTube Video",
            url = "https://youtube.com/watch?v=k3_tw44QsZQ",
            previewTitle = "Google I/O Keynote 2026 - AI in Android Ecosystem",
            previewDesc = "Catch up on the latest developer announcements, Gemini integration, and Android Jetpack updates.",
            domain = "youtube.com",
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&q=80"
        ),
        UrlPreset(
            label = "GitHub Repository",
            url = "https://github.com/developer/jetpack-compose-showcase",
            previewTitle = "developer/jetpack-compose-showcase: Modern Android UI toolkit",
            previewDesc = "A curated collection of production-ready Material 3 Jetpack Compose components and animations.",
            domain = "github.com",
            imageUrl = "https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?w=800&q=80"
        ),
        UrlPreset(
            label = "TechCrunch Article",
            url = "https://techcrunch.com/2026/08/ai-mobile-future",
            previewTitle = "The Next Generation of On-Device Mobile AI is Here",
            previewDesc = "How real-time multimodal intelligence is reshaping mobile app user experiences in 2026.",
            domain = "techcrunch.com",
            imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&q=80"
        ),
        UrlPreset(
            label = "Spotify Track",
            url = "https://open.spotify.com/track/4cOdK2wGLETKBW3PvgPWqT",
            previewTitle = "Midnight City - M83",
            previewDesc = "Song • M83 • Hurry Up, We're Dreaming • 4:03",
            domain = "open.spotify.com",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80"
        )
    )
}
