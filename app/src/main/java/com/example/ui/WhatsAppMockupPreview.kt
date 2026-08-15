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
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.WADarkBg
import com.example.ui.theme.WADarkHeaderBg
import com.example.ui.theme.WADarkHeaderIcons
import com.example.ui.theme.WADarkHeaderSubtitle
import com.example.ui.theme.WADarkHeaderTitle
import com.example.ui.theme.WADatePillBg
import com.example.ui.theme.WAEncryptionBg
import com.example.ui.theme.WAEncryptionGold
import com.example.ui.theme.WAGreenFab
import com.example.ui.theme.WAInputCapsule
import com.example.ui.theme.WAInputPlaceholder
import com.example.ui.theme.WALinkBlue
import com.example.ui.theme.WAReceivedBubble
import com.example.ui.theme.WAReceivedLinkCard
import com.example.ui.theme.WASentBubble
import com.example.ui.theme.WASentLinkCard
import com.example.ui.theme.WATextPrimary
import com.example.ui.theme.WATextSecondary
import com.example.ui.theme.WATickBlue
import com.example.ui.theme.WATickGrey

@Composable
fun WhatsAppMockupCard(
    headerState: HeaderState,
    linkData: LinkPreviewData,
    messageState: MockupMessageState,
    chromeState: DeviceChromeState,
    modifier: Modifier = Modifier
) {
    // Outer smartphone bezel frame
    Box(
        modifier = modifier
            .testTag("whatsapp_mockup_frame")
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp), spotColor = Color.Black)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF101820))
            .border(2.dp, Color(0xFF2A3942), RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WADarkBg)
        ) {
            // 1. Android Status Bar (toggleable)
            if (chromeState.showStatusBar) {
                WhatsAppStatusBar(chromeState = chromeState)
            }

            // 2. WhatsApp Top Navigation / Header
            WhatsAppTopHeader(headerState = headerState)

            // 3. Chat Canvas with Doodle Wallpaper
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                WhatsAppDoodleBackground(
                    modifier = Modifier.matchParentSize()
                ) { /* background doodles */ }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Date Pill
                    if (chromeState.showDatePill) {
                        WhatsAppDatePill(text = chromeState.dateText)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // End-to-End Encryption Banner
                    if (chromeState.showEncryptionBanner) {
                        WhatsAppEncryptionBanner()
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Main Message Bubble with Link Preview
                    WhatsAppMessageBubble(
                        linkData = linkData,
                        messageState = messageState,
                        modifier = Modifier
                            .align(
                                if (messageState.messageType == MessageType.SENT)
                                    Alignment.End
                                else
                                    Alignment.Start
                            )
                    )
                }
            }

            // 4. WhatsApp Bottom Action / Input Bar (toggleable)
            if (chromeState.showBottomBar) {
                WhatsAppBottomBar()
            }
        }
    }
}

@Composable
fun WhatsAppStatusBar(chromeState: DeviceChromeState) {
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
            color = WADarkHeaderTitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = chromeState.networkType,
                color = WADarkHeaderTitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            // Signal Bars icon representation
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                modifier = Modifier.height(10.dp)
            ) {
                Box(modifier = Modifier.width(2.5.dp).height(3.dp).background(WADarkHeaderTitle, RoundedCornerShape(0.5.dp)))
                Box(modifier = Modifier.width(2.5.dp).height(5.dp).background(WADarkHeaderTitle, RoundedCornerShape(0.5.dp)))
                Box(modifier = Modifier.width(2.5.dp).height(7.dp).background(WADarkHeaderTitle, RoundedCornerShape(0.5.dp)))
                Box(modifier = Modifier.width(2.5.dp).height(10.dp).background(WADarkHeaderTitle, RoundedCornerShape(0.5.dp)))
            }

            if (chromeState.isWifiOn) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "WiFi",
                    tint = WADarkHeaderTitle,
                    modifier = Modifier.size(13.dp)
                )
            }

            // Battery Percentage & Icon
            Text(
                text = "${chromeState.batteryLevel}%",
                color = WADarkHeaderTitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(9.dp)
                    .border(1.dp, WADarkHeaderTitle.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                    .padding(1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(chromeState.batteryLevel / 100f)
                        .fillMaxSize()
                        .background(WADarkHeaderTitle, RoundedCornerShape(1.dp))
                )
            }
        }
    }
}

@Composable
fun WhatsAppTopHeader(headerState: HeaderState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WADarkHeaderBg)
            .padding(horizontal = 6.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = WADarkHeaderIcons,
            modifier = Modifier
                .size(24.dp)
                .clickable { }
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Avatar + Status Badge
        Box(contentAlignment = Alignment.BottomEnd) {
            if (headerState.avatarUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(headerState.avatarUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )
            } else if (headerState.avatarCustomUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(headerState.avatarCustomUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(headerState.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = headerState.avatarInitials.take(2).uppercase(),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (headerState.isOnline) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(WAGreenFab, CircleShape)
                        .border(1.5.dp, WADarkHeaderBg, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Contact info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = headerState.contactName,
                    color = WADarkHeaderTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (headerState.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = WAGreenFab,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Text(
                text = headerState.subtitleStatus,
                color = if (headerState.subtitleStatus.equals("online", ignoreCase = true) ||
                    headerState.subtitleStatus.startsWith("typing", ignoreCase = true)
                ) WAGreenFab else WADarkHeaderSubtitle,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Action Icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Video Call",
                tint = WADarkHeaderIcons,
                modifier = Modifier.size(22.dp)
            )
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Voice Call",
                tint = WADarkHeaderIcons,
                modifier = Modifier.size(20.dp)
            )
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                tint = WADarkHeaderIcons,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun WhatsAppDatePill(text: String) {
    Box(
        modifier = Modifier
            .shadow(1.dp, RoundedCornerShape(8.dp))
            .background(WADatePillBg, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = WATextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
fun WhatsAppEncryptionBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .shadow(1.dp, RoundedCornerShape(8.dp))
            .background(WAEncryptionBg, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Encrypted",
                tint = WAEncryptionGold,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Messages and calls are end-to-end encrypted. No one outside of this chat, not even WhatsApp, can read or listen to them. Tap to learn more.",
                color = WAEncryptionGold.copy(alpha = 0.9f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun WhatsAppMessageBubble(
    linkData: LinkPreviewData,
    messageState: MockupMessageState,
    modifier: Modifier = Modifier
) {
    val isSent = messageState.messageType == MessageType.SENT
    val bubbleColor = if (isSent) WASentBubble else WAReceivedBubble
    val linkCardColor = if (isSent) WASentLinkCard else WAReceivedLinkCard

    val bubbleShape: Shape = if (isSent) {
        RoundedCornerShape(topStart = 12.dp, topEnd = 2.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
    } else {
        RoundedCornerShape(topStart = 2.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
    }

    Box(
        modifier = modifier
            .widthIn(min = 200.dp, max = 320.dp)
            .shadow(elevation = 2.dp, shape = bubbleShape)
            .background(bubbleColor, bubbleShape)
            .padding(4.dp)
    ) {
        Column {
            // 1. Link Preview Container
            if (messageState.showLinkPreview) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(linkCardColor)
                ) {
                    // Media Banner Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.85f)
                            .background(Color(0xFF131D24)),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageModel = linkData.localImageUri ?: linkData.imageUrl
                        if (linkData.isScraping) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = WAGreenFab,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Fetching Preview...",
                                    color = WATextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        } else if (imageModel.toString().isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageModel)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Link Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Fallback image graphic
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF1E2D38), Color(0xFF121B22))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Link",
                                    tint = WAGreenFab,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    // Metadata Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 9.dp, vertical = 7.dp)
                    ) {
                        // Title
                        Text(
                            text = linkData.title.ifBlank { "Untitled Link" },
                            color = WATextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 17.sp
                        )

                        // Description
                        if (linkData.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = linkData.description,
                                color = WATextSecondary,
                                fontSize = 11.5.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 15.sp
                            )
                        }

                        // Domain Footer
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Domain",
                                tint = WATextSecondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.5.dp))
                            Text(
                                text = linkData.domain.ifBlank { "link.to" }.lowercase(),
                                color = WATextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // 2. Message Body Text
            if (messageState.bodyText.isNotBlank()) {
                val annotatedBody = buildAnnotatedBody(
                    text = messageState.bodyText,
                    url = linkData.url
                )
                Text(
                    text = annotatedBody,
                    color = WATextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
                )
            }

            // 3. Timestamp & Status Row
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 4.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (messageState.isEdited) {
                    Text(
                        text = "Edited",
                        color = WATextSecondary,
                        fontSize = 10.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (messageState.isStarred) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Starred",
                        tint = WATextSecondary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }

                Text(
                    text = messageState.timestamp,
                    color = WATextSecondary,
                    fontSize = 11.sp
                )

                if (isSent) {
                    Spacer(modifier = Modifier.width(4.dp))
                    WhatsAppTickIcon(status = messageState.status)
                }
            }
        }
    }
}

@Composable
fun WhatsAppTickIcon(status: MessageStatus) {
    when (status) {
        MessageStatus.READ_DOUBLE_BLUE -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Read",
                tint = WATickBlue,
                modifier = Modifier.size(15.dp)
            )
        }
        MessageStatus.DELIVERED_DOUBLE_GREY -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Delivered",
                tint = WATickGrey,
                modifier = Modifier.size(15.dp)
            )
        }
        MessageStatus.SENT_SINGLE_GREY -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sent",
                tint = WATickGrey,
                modifier = Modifier.size(14.dp)
            )
        }
        MessageStatus.SENDING_CLOCK -> {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Sending",
                tint = WATickGrey,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun WhatsAppBottomBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WADarkBg)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pill Input Container
        Row(
            modifier = Modifier
                .weight(1f)
                .shadow(1.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(WAInputCapsule)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SentimentSatisfiedAlt,
                contentDescription = "Emoji",
                tint = WADarkHeaderIcons,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Message",
                color = WAInputPlaceholder,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "Attach",
                tint = WADarkHeaderIcons,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.CurrencyRupee,
                contentDescription = "Payment",
                tint = WADarkHeaderIcons,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Camera",
                tint = WADarkHeaderIcons,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        // Mic / Send FAB
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .background(WAGreenFab),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Record",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

private fun buildAnnotatedBody(text: String, url: String) = buildAnnotatedString {
    if (url.isBlank() || !text.contains(url)) {
        append(text)
    } else {
        val startIndex = text.indexOf(url)
        val endIndex = startIndex + url.length

        append(text.substring(0, startIndex))
        withStyle(
            style = SpanStyle(
                color = WALinkBlue,
                fontWeight = FontWeight.Medium
            )
        ) {
            append(url)
        }
        append(text.substring(endIndex))
    }
}
