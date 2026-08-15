package com.example

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceChromeState
import com.example.model.HeaderState
import com.example.model.LinkPreviewData
import com.example.model.MockupMessageState
import com.example.ui.ControlsEditorSheet
import com.example.ui.MockupViewModel
import com.example.ui.WhatsAppMockupCard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioAccentCyan
import com.example.ui.theme.StudioAccentTeal
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.WAGreenFab
import com.example.util.ExportUtil
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MockupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainMockupStudioScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMockupStudioScreen(viewModel: MockupViewModel) {
    val headerState by viewModel.headerState.collectAsState()
    val linkData by viewModel.linkData.collectAsState()
    val messageState by viewModel.messageState.collectAsState()
    val chromeState by viewModel.chromeState.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val graphicsLayer = rememberGraphicsLayer()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Preview, 1: Customize

    fun performExport(share: Boolean = false) {
        viewModel.setExporting(true)
        coroutineScope.launch {
            try {
                val imageBitmap = graphicsLayer.toImageBitmap()
                val bitmap: Bitmap = imageBitmap.asAndroidBitmap()

                if (share) {
                    val shareResult = ExportUtil.shareBitmap(context, bitmap)
                    if (shareResult.isFailure) {
                        snackbarHostState.showSnackbar("Failed to share image: ${shareResult.exceptionOrNull()?.message}")
                    }
                } else {
                    val saveResult = ExportUtil.saveBitmapToGallery(context, bitmap)
                    saveResult.onSuccess {
                        snackbarHostState.showSnackbar("Mockup saved to Gallery (Pictures/WhatsAppMockups)")
                        Toast.makeText(context, "Mockup saved to Gallery!", Toast.LENGTH_SHORT).show()
                    }.onFailure { err ->
                        snackbarHostState.showSnackbar("Error saving mockup: ${err.message}")
                    }
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Export error: ${e.localizedMessage}")
            } finally {
                viewModel.setExporting(false)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = StudioDarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(WAGreenFab, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("W", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "WhatsApp Mockup",
                                color = StudioTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Dark Mode & Link Preview Studio",
                                color = StudioAccentTeal,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // Share icon button
                    IconButton(
                        onClick = { performExport(share = true) },
                        enabled = !isExporting,
                        modifier = Modifier.testTag("share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Mockup",
                            tint = StudioAccentCyan
                        )
                    }

                    // Save / Download button
                    Button(
                        onClick = { performExport(share = false) },
                        enabled = !isExporting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioAccentTeal,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("download_screenshot_button")
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export PNG", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StudioSurface
                )
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 760.dp

            if (isWideScreen) {
                // Two-Column Responsive Layout for Tablet / Desktop / Wide screens
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Controls & Inputs
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                    ) {
                        ControlsEditorSheet(
                            viewModel = viewModel,
                            headerState = headerState,
                            linkData = linkData,
                            messageState = messageState,
                            chromeState = chromeState
                        )
                    }

                    // Right Column: Live Mobile Frame Preview
                    Box(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight()
                            .background(StudioSurface.copy(alpha = 0.5f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MockupCaptureContainer(
                            headerState = headerState,
                            linkData = linkData,
                            messageState = messageState,
                            chromeState = chromeState,
                            modifier = Modifier
                                .widthIn(max = 380.dp)
                                .drawWithContent {
                                    graphicsLayer.record {
                                        this@drawWithContent.drawContent()
                                    }
                                    drawLayer(graphicsLayer)
                                }
                        )
                    }
                }
            } else {
                // Mobile View: Top Switcher Tabs (Live Preview / Customize)
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = StudioSurface,
                        contentColor = StudioAccentTeal,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = StudioAccentTeal
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = "Preview",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Live Mockup", fontWeight = FontWeight.SemiBold)
                                }
                            },
                            selectedContentColor = StudioAccentTeal,
                            unselectedContentColor = StudioTextSecondary,
                            modifier = Modifier.testTag("tab_preview")
                        )

                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Customize",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Controls & Scraper", fontWeight = FontWeight.SemiBold)
                                }
                            },
                            selectedContentColor = StudioAccentTeal,
                            unselectedContentColor = StudioTextSecondary,
                            modifier = Modifier.testTag("tab_controls")
                        )
                    }

                    if (selectedTab == 0) {
                        // Live Preview Screen
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Quick action toolbar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "1:1 WhatsApp Dark Mode",
                                    color = StudioTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { selectedTab = 1 },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioAccentCyan),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit Details", fontSize = 12.sp)
                                    }
                                }
                            }

                            // Capture Container
                            MockupCaptureContainer(
                                headerState = headerState,
                                linkData = linkData,
                                messageState = messageState,
                                chromeState = chromeState,
                                modifier = Modifier
                                    .widthIn(max = 380.dp)
                                    .drawWithContent {
                                        graphicsLayer.record {
                                            this@drawWithContent.drawContent()
                                        }
                                        drawLayer(graphicsLayer)
                                    }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Bottom Export action row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { performExport(share = true) },
                                    enabled = !isExporting,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioAccentCyan),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share", fontSize = 13.sp)
                                }

                                Button(
                                    onClick = { performExport(share = false) },
                                    enabled = !isExporting,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StudioAccentTeal,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = "Save", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save to Photos", fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        // Controls & Scraper Screen
                        ControlsEditorSheet(
                            viewModel = viewModel,
                            headerState = headerState,
                            linkData = linkData,
                            messageState = messageState,
                            chromeState = chromeState
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MockupCaptureContainer(
    headerState: HeaderState,
    linkData: LinkPreviewData,
    messageState: MockupMessageState,
    chromeState: DeviceChromeState,
    modifier: Modifier = Modifier
) {
    WhatsAppMockupCard(
        headerState = headerState,
        linkData = linkData,
        messageState = messageState,
        chromeState = chromeState,
        modifier = modifier
    )
}
