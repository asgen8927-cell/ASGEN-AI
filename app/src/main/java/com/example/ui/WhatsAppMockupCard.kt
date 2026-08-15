package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import com.example.ui.theme.WACheckBlue
import com.example.ui.theme.WACheckGrey
import com.example.ui.theme.WADarkBackground
import com.example.ui.theme.WADarkBottomBarBg
import com.example.ui.theme.WADarkBubbleIncoming
import com.example.ui.theme.WADarkBubbleOutgoing
import com.example.ui.theme.WADarkCardBg
import com.example.ui.theme.WADarkHeaderBg
import com.example.ui.theme.WADarkInputBg
import com.example.ui.theme.WAGreenFab
import com.example.ui.theme.WALinkDomainText
import com.example.ui.theme.WALinkUrlColor
import com.example.ui.theme.WATextPrimary
import com.example.ui.theme.WATextSecondary

@Composable
fun WhatsAppMockupCard(
    headerState: HeaderState,
    linkData: LinkPreviewData,
    messageState: MockupMessageState,
    chromeState: DeviceChromeState,
    modifier: Modifier = Modifier
) {
    // Phone Mockup Frame
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("whatsapp_mockup_container"),
        shape = RoundedCornerShape(24.dp),
        color = WADarkBackground,
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2A3942)),
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
        ) {
            // 1. Android Status Bar (Optional)
            if (chromeState.showStatusBar) {
                StatusBarSection(chromeState)
            }

            // 2. WhatsApp Top Navigation / Contact Bar
            WhatsAppHeaderSection(headerState)

            // 3. WhatsApp Chat Canvas with Pattern & Message Bubble
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .drawBehind {
                        // WhatsApp Dark Theme Subtle Doodle Pattern Simulation
                        val dotColor = Color(0x0CFFFFFF)
                        val step = 32.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            var y = 0f
                            while (y < size.height) {
                                drawCircle(
                                    color = dotColor,
                                    radius = 1.5.dp.toPx(),
                                    center = Offset(x + (if ((y / step).toInt() % 2 == 0) 0f else step / 2), y)
                                )
                                y += step
                            }
                            x += step
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Optional Encryption & Date Pill
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (chromeState.showEncryptionBanner) {
                            EncryptionNoticePill()
                        }

                        if (chromeState.showDatePill) {
                            DatePill(chromeState.dateText)
                        }
                    }

                    // The Core Link Preview Message Bubble
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (messageState.messageType == MessageType.SENT) Arrangement.End else Arrangement.Start
                    ) {
                        WhatsAppMessageBubble(
                            messageState = messageState,
                            linkData = linkData
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // 4. WhatsApp Bottom Action Bar (Optional)
            if (chromeState.showBottomBar) {
                WhatsAppBottomBarSection()
            }
        }
    }
}

@Composable
fun StatusBarSection(chromeState: DeviceChromeState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WADarkHeaderBg)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = chromeState.statusTime,
            color = WATextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = chromeState.networkType,
                color = WATextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            if (chromeState.isWifiOn) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "WiFi",
                    tint = WATextPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(1.dp, WATextPrimary.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width((chromeState.batteryLevel * 0.16f).dp.coerceIn(2.dp, 16.dp))
                        .background(
                            if (chromeState.batteryLevel <= 20) Color(0xFFEF4444) else WAGreenFab,
                            RoundedCornerShape(1.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun WhatsAppHeaderSection(headerState: HeaderState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WADarkHeaderBg)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = WATextSecondary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(38.dp)
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
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Contact info
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = headerState.contactName,
                    color = WATextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (headerState.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = WAGreenFab,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (headerState.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(WAGreenFab)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = headerState.subtitleStatus,
                    color = if (headerState.subtitleStatus.lowercase() == "online" || headerState.subtitleStatus.lowercase().startsWith("typing")) WAGreenFab else WATextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Action icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(Icons.Default.Videocam, contentDescription = "Video call", tint = WATextSecondary, modifier = Modifier.size(20.dp))
            Icon(Icons.Default.Call, contentDescription = "Voice call", tint = WATextSecondary, modifier = Modifier.size(19.dp))
            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = WATextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun EncryptionNoticePill() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0x99182229),
        modifier = Modifier.widthIn(max = 280.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock",
                tint = Color(0xFFFFD279),
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Messages and calls are end-to-end encrypted.",
                color = Color(0xFFFFD279),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DatePill(dateText: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xEE182229)
    ) {
        Text(
            text = dateText.uppercase(),
            color = WATextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun WhatsAppMessageBubble(
    messageState: MockupMessageState,
    linkData: LinkPreviewData
) {
    val isOutgoing = messageState.messageType == MessageType.SENT
    val bubbleBg = if (isOutgoing) WADarkBubbleOutgoing else WADarkBubbleIncoming
    val bubbleShape = if (isOutgoing) {
        RoundedCornerShape(topStart = 12.dp, topEnd = 2.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
    } else {
        RoundedCornerShape(topStart = 2.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
    }

    Box(
        modifier = Modifier
            .widthIn(min = 200.dp, max = 295.dp)
            .clip(bubbleShape)
            .background(bubbleBg)
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Link Preview Card (if enabled)
            if (messageState.showLinkPreview) {
                OpenGraphPreviewCard(linkData = linkData)
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 2. Message Body Text with formatted URL links
            Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                FormattedMessageText(
                    fullText = messageState.bodyText,
                    url = linkData.url
                )
            }

            // 3. Message Status & Timestamp Row (Bottom Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (messageState.isEdited) {
                    Text(
                        text = "Edited",
                        color = WATextSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                if (messageState.isStarred) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Starred",
                        tint = WATextSecondary,
                        modifier = Modifier
                            .size(11.dp)
                            .padding(end = 3.dp)
                    )
                }

                Text(
                    text = messageState.timestamp,
                    color = WATextSecondary,
                    fontSize = 10.5.sp
                )

                if (isOutgoing) {
                    Spacer(modifier = Modifier.width(3.dp))
                    StatusTickIcon(status = messageState.status)
                }
            }
        }
    }
}

@Composable
fun OpenGraphPreviewCard(linkData: LinkPreviewData) {
    val imageSrc = linkData.localImageUri ?: linkData.imageUrl

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(WADarkCardBg)
    ) {
        // Thumbnail Image Banner
        if (imageSrc.toString().isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.9f)
                    .background(Color(0xFF10171D))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageSrc)
                        .crossfade(true)
                        .build(),
                    contentDescription = linkData.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Title, Description & Domain
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (linkData.title.isNotBlank()) {
                Text(
                    text = linkData.title,
                    color = WATextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            if (linkData.description.isNotBlank()) {
                Text(
                    text = linkData.description,
                    color = WATextSecondary,
                    fontSize = 11.5.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = linkData.domain.ifBlank { "web" }.lowercase(),
                    color = WALinkDomainText,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun FormattedMessageText(fullText: String, url: String) {
    val annotatedString = buildAnnotatedString {
        if (url.isNotBlank() && fullText.contains(url)) {
            val startIndex = fullText.indexOf(url)
            val endIndex = startIndex + url.length

            if (startIndex > 0) {
                withStyle(SpanStyle(color = WATextPrimary, fontSize = 13.5.sp)) {
                    append(fullText.substring(0, startIndex))
                }
            }

            withStyle(
                SpanStyle(
                    color = WALinkUrlColor,
                    fontSize = 13.5.sp,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(url)
            }

            if (endIndex < fullText.length) {
                withStyle(SpanStyle(color = WATextPrimary, fontSize = 13.5.sp)) {
                    append(fullText.substring(endIndex))
                }
            }
        } else {
            withStyle(SpanStyle(color = WATextPrimary, fontSize = 13.5.sp)) {
                append(fullText)
            }
        }
    }

    Text(
        text = annotatedString,
        lineHeight = 18.sp
    )
}

@Composable
fun StatusTickIcon(status: MessageStatus) {
    when (status) {
        MessageStatus.PENDING -> {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Pending clock",
                tint = WACheckGrey,
                modifier = Modifier.size(13.dp)
            )
        }
        MessageStatus.SENT_SINGLE -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Single check",
                tint = WACheckGrey,
                modifier = Modifier.size(13.dp)
            )
        }
        MessageStatus.DELIVERED_DOUBLE -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Delivered double grey check",
                tint = WACheckGrey,
                modifier = Modifier.size(15.dp)
            )
        }
        MessageStatus.READ_DOUBLE_BLUE -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Read double blue check",
                tint = WACheckBlue,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
fun WhatsAppBottomBarSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WADarkBottomBarBg)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chat Input Box Pill
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(WADarkInputBg)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.EmojiEmotions, contentDescription = "Emoji", tint = WATextSecondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Message",
                color = WATextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = WATextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = WATextSecondary, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Mic/Voice FAB
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(WAGreenFab),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice note",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
