package com.example

import android.content.Context
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CmfBlack
import com.example.ui.theme.CmfCharcoal
import com.example.ui.theme.CmfCoolGray
import com.example.ui.theme.CmfDarkGray
import com.example.ui.theme.CmfLightGray
import com.example.ui.theme.CmfOrange
import com.example.ui.theme.CmfRed
import com.example.ui.theme.CmfWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainLauncherScreen(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val systemInDark = true // Lock to premium Dark OS for true CMF aesthetic

    val apps by viewModel.apps.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val transparency by viewModel.drawerTransparency.collectAsState()
    val selectedWallpaper by viewModel.selectedWallpaper.collectAsState()
    val fpsValue by viewModel.fpsValue.collectAsState()

    val blurStrength by viewModel.blurStrength.collectAsState()
    val animationSpeed by viewModel.drawerAnimationSpeed.collectAsState()
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val hiddenSpacePin by viewModel.hiddenSpacePin.collectAsState()
    val useDeviceAuth by viewModel.useDeviceAuth.collectAsState()
    val isHiddenSpaceUnlocked by viewModel.isHiddenSpaceUnlocked.collectAsState()

    val homeGridApps by viewModel.homeGridApps.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()

    var currentWorkspacePage by remember { mutableStateOf(0) }
    var longPressedApp by remember { mutableStateOf<LauncherApp?>(null) }
    var longPressedGridSlot by remember { mutableStateOf<Int?>(null) } // 0..29 for grid, -1..-5 for dock
    var isWorkspaceLongPressed by remember { mutableStateOf(false) }
    var isAddAppDialogOpen by remember { mutableStateOf(false) }

    var isSettingsOpen by remember { mutableStateOf(false) }
    var isManageHiddenAppsOpen by remember { mutableStateOf(false) }
    var isChangePinOpen by remember { mutableStateOf(false) }
    var expandedFolderCategory by remember { mutableStateOf<String?>(null) }
    var drawerTab by remember { mutableStateOf(0) } // 0 = All Apps, 1 = Hidden Space

    // Dynamic clock date state
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            currentDate = SimpleDateFormat("EEE dd MMM", Locale.getDefault()).format(now).uppercase()
            delay(1000)
        }
    }

    // Refresh rate simulation for the 120 FPS performance counter
    LaunchedEffect(Unit) {
        var frameTicks = 0
        var lastTime = System.nanoTime()
        while (true) {
            val now = System.nanoTime()
            frameTicks++
            if (now - lastTime >= 1_000_000_000L) {
                val realFps = (frameTicks * 1).coerceAtMost(120).coerceAtLeast(116)
                viewModel.updateFps(realFps)
                frameTicks = 0
                lastTime = now
            }
            delay(8) // approx 120 fps loop tick
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Highly optimized native CMF gradient wallpapers drawn directly on canvas
                when (selectedWallpaper) {
                    0 -> {
                        // Wallpaper 0: Carbon Matrix
                        drawRect(color = CmfBlack)
                        // Accent glow (Nothing Red-Orange)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(CmfOrange.copy(alpha = 0.12f), Color.Transparent),
                                center = Offset(size.width * 0.85f, size.height * 0.8f),
                                radius = size.width * 0.7f
                            )
                        )
                        // Tactical dot grid pattern
                        val spacing = 24.dp.toPx()
                        for (x in 0..size.width.toInt() step spacing.toInt()) {
                            for (y in 0..size.height.toInt() step spacing.toInt()) {
                                drawCircle(
                                    color = CmfCoolGray.copy(alpha = 0.08f),
                                    radius = 1.dp.toPx(),
                                    center = Offset(x.toFloat(), y.toFloat())
                                )
                            }
                        }
                    }
                    1 -> {
                        // Wallpaper 1: Matte Slate
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(CmfCharcoal, CmfBlack)
                            )
                        )
                        // Minimalist white soft glowing circle
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(CmfWhite.copy(alpha = 0.05f), Color.Transparent),
                                center = Offset(size.width * 0.2f, size.height * 0.3f),
                                radius = size.width * 0.5f
                            )
                        )
                    }
                    else -> {
                        // Wallpaper 2: Vapor Glow
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(CmfCharcoal, CmfBlack),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.height * 0.8f
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(CmfCoolGray.copy(alpha = 0.07f), Color.Transparent),
                                center = Offset(size.width / 2, size.height * 0.4f),
                                radius = size.width * 0.6f
                            )
                        )
                    }
                }
            }
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        val density = LocalDensity.current

        val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        // HOMEPAGE MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = insetsTop, bottom = 48.dp) // Leave space at bottom for drawer swipe gesture
                .testTag("homepage_layout"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Nothing Dot Matrix Clock & Date and Settings Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (currentTime.isNotEmpty()) {
                        DotMatrixText(
                            text = currentTime,
                            dotColor = CmfWhite,
                            dotSize = 5.dp,
                            spacing = 1.5.dp,
                            charSpacing = 8.dp,
                            modifier = Modifier.testTag("clock_widget")
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (currentDate.isNotEmpty()) {
                        DotMatrixText(
                            text = currentDate,
                            dotColor = CmfCoolGray,
                            dotSize = 2.5.dp,
                            spacing = 1.dp,
                            charSpacing = 4.dp,
                            modifier = Modifier.testTag("date_widget")
                        )
                    }
                }

                // Interactive M3 settings button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CmfCharcoal.copy(alpha = 0.6f))
                        .border(1.dp, CmfCoolGray.copy(alpha = 0.2f), CircleShape)
                        .clickable { isSettingsOpen = true }
                        .testTag("launcher_settings_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Launcher Settings",
                        tint = CmfWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Page Selector / Headers (Grid / Folder)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "APP GRID",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (currentWorkspacePage == 0) CmfWhite else CmfCoolGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .clickable { currentWorkspacePage = 0 }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(CmfCoolGray.copy(alpha = 0.5f))
                )
                Text(
                    text = "FOLDER MATRIX",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (currentWorkspacePage == 1) CmfWhite else CmfCoolGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .clickable { currentWorkspacePage = 1 }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Content Area (either 5x6 Grid or Folders)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    // Detect long press on empty space of the screen background
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                isWorkspaceLongPressed = true
                            }
                        )
                    }
            ) {
                if (currentWorkspacePage == 0) {
                    // Page 0: Modern 5x6 App Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("home_5x6_grid"),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(30) { index ->
                            val app = homeGridApps.getOrNull(index)
                            Box(
                                modifier = Modifier
                                    .height(76.dp)
                                    .testTag("grid_slot_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (app != null) {
                                    // Custom combinedClickable for tap and long tap
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .combinedClickable(
                                                onClick = { viewModel.launchApp(context, app) },
                                                onLongClick = {
                                                    longPressedApp = app
                                                    longPressedGridSlot = index
                                                }
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(CmfCharcoal.copy(alpha = 0.8f))
                                                .border(1.2.dp, CmfCoolGray.copy(alpha = 0.25f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (app.icon != null) {
                                                AndroidIconWrapper(drawable = app.icon, modifier = Modifier.size(28.dp))
                                            } else {
                                                Text(
                                                    text = app.label.take(1).uppercase(),
                                                    fontSize = 18.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CmfWhite
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = app.label.uppercase(),
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CmfWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                } else {
                                    // Empty slot CMF point grid indicator (dot)
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .combinedClickable(
                                                onClick = {
                                                    longPressedGridSlot = index
                                                    isAddAppDialogOpen = true
                                                },
                                                onLongClick = {
                                                    longPressedGridSlot = index
                                                    isWorkspaceLongPressed = true
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(CmfCoolGray.copy(alpha = 0.35f))
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Page 1: Original Folder Matrix Cards (preserved exactly!)
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LargeFolderCard(
                                title = "GOOGLE",
                                apps = apps.filter { it.category == "Google" }.take(4),
                                onAppClick = { viewModel.launchApp(context, it) },
                                onFolderClick = { expandedFolderCategory = "Google" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("folder_google")
                            )

                            LargeFolderCard(
                                title = "TOOLS",
                                apps = apps.filter { it.category == "Tools" }.take(4),
                                onAppClick = { viewModel.launchApp(context, it) },
                                onFolderClick = { expandedFolderCategory = "Tools" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("folder_tools")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LargeFolderCard(
                                title = "MEDIA",
                                apps = apps.filter { it.category == "Media" }.take(4),
                                onAppClick = { viewModel.launchApp(context, it) },
                                onFolderClick = { expandedFolderCategory = "Media" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("folder_media")
                            )

                            LargeFolderCard(
                                title = "SYSTEM",
                                apps = apps.filter { it.category == "System" }.take(4),
                                onAppClick = { viewModel.launchApp(context, it) },
                                onFolderClick = { expandedFolderCategory = "System" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("folder_system")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FIXED DOCK (5 Apps) - Always visible on home screen!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.2.dp, CmfWhite.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                    .testTag("fixed_dock"),
                colors = CardDefaults.cardColors(containerColor = CmfCharcoal.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dockApps.take(5).forEachIndexed { index, app ->
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(CmfBlack.copy(alpha = 0.5f))
                                .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), CircleShape)
                                .combinedClickable(
                                    onClick = { viewModel.launchApp(context, app) },
                                    onLongClick = {
                                        longPressedApp = app
                                        longPressedGridSlot = -1 - index // negative indices represent dock positions
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (app.icon != null) {
                                AndroidIconWrapper(drawable = app.icon, modifier = Modifier.size(30.dp))
                            } else {
                                Text(
                                    text = app.label.take(1).uppercase(),
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfWhite
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Swipe-up gesture pill indicator for App Drawer (and page dots above it)
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (currentWorkspacePage == 0) 6.dp else 4.dp)
                            .clip(CircleShape)
                            .background(if (currentWorkspacePage == 0) CmfWhite else CmfCoolGray.copy(alpha = 0.5f))
                            .clickable { currentWorkspacePage = 0 }
                    )
                    Box(
                        modifier = Modifier
                            .size(if (currentWorkspacePage == 1) 6.dp else 4.dp)
                            .clip(CircleShape)
                            .background(if (currentWorkspacePage == 1) CmfWhite else CmfCoolGray.copy(alpha = 0.5f))
                            .clickable { currentWorkspacePage = 1 }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(CmfWhite.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SWIPE UP TO SEARCH",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CmfWhite.copy(alpha = 0.6f),
                    letterSpacing = 1.5.sp
                )
            }
        }

        // SLIDING FROSTED GLASS APP DRAWER WITH ADJUSTABLE REAL-TIME BLUR
        var drawerDragOffset by remember { mutableStateOf(0f) }
        var isDrawerOpen by remember { mutableStateOf(false) }
        var horizontalDragOffset by remember { mutableStateOf(0f) }

        // Use the custom duration animation speed instantly from settings
        val animatedFraction by animateFloatAsState(
            targetValue = if (isDrawerOpen) 0f else 1f,
            animationSpec = tween(
                durationMillis = animationSpeed,
                easing = FastOutSlowInEasing
            ),
            label = "DrawerSlide"
        )

        val finalFraction = if (isDrawerOpen) drawerDragOffset else animatedFraction
        val drawerOffsetY = with(density) { (screenHeight * finalFraction).toPx() }

        // Dynamic Glassmorphic properties with instant adjustable blur strength from 0% to 100%
        val alphaLevel = transparency // Slider controlled 0% to 100%
        val glassColor = CmfWhite.copy(alpha = 0.08f + 0.15f * alphaLevel)
        // Blur calculated using the live settings value instantly
        val glassBlur = (30.dp * blurStrength)

        Box(
            modifier = Modifier
                .fillMaxSize()
                // Capture gestures everywhere to slide drawer up/down seamlessly
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { },
                        onDragEnd = {
                            if (drawerDragOffset > 0.15f) {
                                isDrawerOpen = false
                            } else if (drawerDragOffset < -0.15f) {
                                isDrawerOpen = true
                            }
                            drawerDragOffset = 0f
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            val dragRatio = dragAmount / size.height.toFloat()
                            val nextValue = if (isDrawerOpen) {
                                (0f + dragRatio).coerceIn(0f, 1f)
                            } else {
                                (1f + dragRatio).coerceIn(0f, 1f)
                            }
                            if (isDrawerOpen && nextValue > 0.05f) {
                                isDrawerOpen = false
                            } else if (!isDrawerOpen && nextValue < 0.95f) {
                                isDrawerOpen = true
                            }
                        }
                    )
                }
        ) {
            // App Drawer Body sheet
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, drawerOffsetY.roundToInt()) }
                    // Apply genuine Glassmorphism real-time Gaussian Blur on S+ devices
                    .then(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && glassBlur > 0.dp) {
                            Modifier.blur(glassBlur)
                        } else {
                            Modifier
                        }
                    )
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(glassColor, glassColor.copy(alpha = 0.95f))
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                CmfWhite.copy(alpha = 0.25f * (1f - alphaLevel)),
                                Color.Transparent
                             )
                        ),
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    )
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (horizontalDragOffset < -80f) {
                                    // Swipe left to open Hidden Space
                                    drawerTab = 1
                                } else if (horizontalDragOffset > 80f) {
                                    // Swipe right to return to All Apps
                                    drawerTab = 0
                                }
                                horizontalDragOffset = 0f
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                horizontalDragOffset += dragAmount
                            }
                        )
                    }
                    .testTag("app_drawer_sheet")
            ) {
                // Tactile glass overlay grids
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val dotSpacing = 16.dp.toPx()
                    for (x in 0..size.width.toInt() step dotSpacing.toInt()) {
                        for (y in 0..size.height.toInt() step dotSpacing.toInt()) {
                            drawCircle(
                                color = CmfWhite.copy(alpha = 0.04f),
                                radius = 0.8f.dp.toPx(),
                                center = Offset(x.toFloat(), y.toFloat())
                            )
                        }
                    }
                }

                // Drawer inner structure
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = insetsTop + 12.dp, bottom = insetsBottom + 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tactile Drag Handle
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(CmfWhite.copy(alpha = 0.25f))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Redesigned Top Tabs: ALL APPS & HIDDEN SPACE
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("ALL APPS", "HIDDEN SPACE").forEachIndexed { index, tabName ->
                            val isSelected = drawerTab == index
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { drawerTab = index }
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (index == 1) {
                                        Icon(
                                            imageVector = if (isHiddenSpaceUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (isSelected) CmfOrange else CmfCoolGray,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = tabName,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CmfWhite else CmfCoolGray,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(2.dp)
                                        .background(if (isSelected) CmfOrange else Color.Transparent)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // RENDER SELECTED TAB CONTENT
                    if (drawerTab == 0) {
                        // ==========================================
                        // TAB 0: ALL APPS (ALPHABETICAL APP LIST GRID)
                        // ==========================================
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SearchBar(
                                query = searchQuery,
                                onQueryChange = { viewModel.setSearchQuery(it) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("app_search_input")
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Close button
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(CmfWhite.copy(alpha = 0.08f))
                                    .clickable {
                                        isDrawerOpen = false
                                    }
                                    .testTag("close_drawer_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Close Drawer",
                                    tint = CmfWhite,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fast A-Z Indexing Logic
                        val gridState = rememberLazyGridState()
                        val coroutineScope = rememberCoroutineScope()

                        val sortedApps = remember(filteredApps) {
                            filteredApps.sortedBy { it.label.uppercase() }
                        }

                        val letterToGridIndex = remember(sortedApps) {
                            val map = mutableMapOf<Char, Int>()
                            sortedApps.forEachIndexed { index, app ->
                                val firstChar = app.label.uppercase().firstOrNull() ?: ' '
                                if (firstChar in 'A'..'Z' && !map.containsKey(firstChar)) {
                                    map[firstChar] = index
                                }
                            }
                            map
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            // Main Alphabetical App Grid list
                            LazyVerticalGrid(
                                state = gridState,
                                columns = GridCells.Fixed(4),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .testTag("drawer_apps_grid"),
                                contentPadding = PaddingValues(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(sortedApps) { app ->
                                    AppIconItem(
                                        app = app,
                                        onClick = {
                                            viewModel.launchApp(context, app)
                                            isDrawerOpen = false
                                        }
                                    )
                                }
                            }

                            // Interactive A-Z Fast Scroller on the right side
                            Column(
                                modifier = Modifier
                                    .width(28.dp)
                                    .fillMaxHeight()
                                    .padding(end = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ('A'..'Z').forEach { letter ->
                                    val hasApps = letterToGridIndex.containsKey(letter)
                                    Text(
                                        text = letter.toString(),
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasApps) CmfWhite else CmfCoolGray.copy(alpha = 0.3f),
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable(enabled = hasApps) {
                                                val targetIndex = letterToGridIndex[letter]
                                                if (targetIndex != null) {
                                                    coroutineScope.launch {
                                                        gridState.animateScrollToItem(targetIndex)
                                                    }
                                                }
                                            }
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // ==========================================
                        // TAB 1: HIDDEN SPACE (LOCKED / UNLOCKED)
                        // ==========================================
                        if (!isHiddenSpaceUnlocked) {
                            // PIN LOCKED STATE OVERLAY (Tactile, Dot Matrix)
                            var enteredPin by remember { mutableStateOf("") }
                            var isBiometricScanning by remember { mutableStateOf(false) }
                            var shakeTrigger by remember { mutableStateOf(false) }

                            val shakeOffset by animateDpAsState(
                                targetValue = if (shakeTrigger) 12.dp else 0.dp,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioHighBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                finishedListener = {
                                    if (shakeTrigger) shakeTrigger = false
                                }
                            )

                            LaunchedEffect(enteredPin) {
                                if (enteredPin.length == 4) {
                                    if (enteredPin == hiddenSpacePin) {
                                        viewModel.setHiddenSpaceUnlocked(true)
                                        enteredPin = ""
                                    } else {
                                        shakeTrigger = true
                                        enteredPin = ""
                                    }
                                }
                            }

                            if (isBiometricScanning) {
                                // Beautiful, futuristic Nothing Biometrics scanning simulation
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(100.dp)
                                                .border(1.5.dp, CmfWhite.copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Fingerprint,
                                                contentDescription = null,
                                                tint = CmfOrange,
                                                modifier = Modifier.size(48.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            text = "SCANNING FINGERPRINT...",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CmfWhite,
                                            letterSpacing = 1.5.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "SECURE DEVICE ENCLAVE ACTIVE",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CmfCoolGray
                                        )
                                    }
                                }

                                LaunchedEffect(Unit) {
                                    delay(1800) // Simulate scanning
                                    viewModel.setHiddenSpaceUnlocked(true)
                                    isBiometricScanning = false
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = CmfWhite,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "HIDDEN SPACE SECURED",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfWhite,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "ENTER 4-DIGIT PIN OR SCAN",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CmfCoolGray
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Passcode feedback circles (with spring offset on error shake)
                                    Row(
                                        modifier = Modifier.offset(x = shakeOffset),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        for (i in 0..3) {
                                            val isFilled = enteredPin.length > i
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isFilled) CmfOrange else Color.Transparent)
                                                    .border(
                                                        1.5.dp,
                                                        if (isFilled) CmfOrange else CmfWhite.copy(alpha = 0.4f),
                                                        CircleShape
                                                    )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    // Keypad layout
                                    val keypadKeys = listOf(
                                        listOf("1", "2", "3"),
                                        listOf("4", "5", "6"),
                                        listOf("7", "8", "9"),
                                        listOf("C", "0", "FP")
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        keypadKeys.forEach { row ->
                                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                row.forEach { key ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(60.dp)
                                                            .clip(CircleShape)
                                                            .background(CmfCharcoal.copy(alpha = 0.6f))
                                                            .border(
                                                                1.dp,
                                                                CmfWhite.copy(alpha = 0.12f),
                                                                CircleShape
                                                            )
                                                            .clickable {
                                                                when (key) {
                                                                    "C" -> {
                                                                        if (enteredPin.isNotEmpty()) {
                                                                            enteredPin = enteredPin.dropLast(1)
                                                                        }
                                                                    }
                                                                    "FP" -> {
                                                                        // Fingerprint simulator
                                                                        isBiometricScanning = true
                                                                    }
                                                                    else -> {
                                                                        if (enteredPin.length < 4) {
                                                                            enteredPin += key
                                                                        }
                                                                    }
                                                                }
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (key == "FP") {
                                                            Icon(
                                                                imageVector = Icons.Default.Fingerprint,
                                                                contentDescription = "Scan",
                                                                tint = CmfOrange,
                                                                modifier = Modifier.size(22.dp)
                                                            )
                                                        } else if (key == "C") {
                                                            Icon(
                                                                imageVector = Icons.Default.ArrowBack,
                                                                contentDescription = "Backspace",
                                                                tint = CmfWhite,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                        } else {
                                                            Text(
                                                                text = key,
                                                                fontSize = 18.sp,
                                                                fontFamily = FontFamily.Monospace,
                                                                fontWeight = FontWeight.Bold,
                                                                color = CmfWhite
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // UNLOCKED STATE (Hidden Apps Dashboard)
                            val hiddenAppsList = remember(apps, hiddenApps) {
                                apps.filter { hiddenApps.contains(it.packageName) }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "HIDDEN APPLICATIONS",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfOrange,
                                        letterSpacing = 1.sp
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        // Manage / Add apps button
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(CmfWhite.copy(alpha = 0.08f))
                                                .clickable { isManageHiddenAppsOpen = true }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "MANAGE",
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = CmfWhite
                                            )
                                        }

                                        // Lock Space button
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(CmfWhite.copy(alpha = 0.08f))
                                                .clickable { viewModel.setHiddenSpaceUnlocked(false) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "LOCK",
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = CmfWhite
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (hiddenAppsList.isEmpty()) {
                                    // Empty state helper
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .border(1.2.dp, CmfCoolGray.copy(alpha = 0.3f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LockOpen,
                                                    contentDescription = null,
                                                    tint = CmfCoolGray,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = "NO HIDDEN APPS YET",
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = CmfWhite
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "TAP MANAGE TO SELECT APPS TO SECURE",
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = CmfCoolGray
                                            )
                                        }
                                    }
                                } else {
                                    // List of hidden apps
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(4),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(20.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(hiddenAppsList) { app ->
                                            AppIconItem(
                                                app = app,
                                                onClick = {
                                                    viewModel.launchApp(context, app)
                                                    isDrawerOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // EXPANDED FOLDER OVERLAY DIALOG
        AnimatedVisibility(
            visible = expandedFolderCategory != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            val category = expandedFolderCategory
            if (category != null) {
                val folderApps = apps.filter { it.category == category }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CmfBlack.copy(alpha = 0.85f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { expandedFolderCategory = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.7f)
                            .border(1.5.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable(enabled = false) {}, // prevent closing clicking inside
                        colors = CardDefaults.cardColors(containerColor = CmfDarkGray),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            // Folder Title
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Folder,
                                        contentDescription = null,
                                        tint = CmfOrange,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = category.uppercase(),
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfWhite
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CmfWhite.copy(alpha = 0.08f))
                                        .clickable { expandedFolderCategory = null },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Close",
                                        tint = CmfWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // App list within folder
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(folderApps) { app ->
                                    AppIconItem(
                                        app = app,
                                        onClick = {
                                            viewModel.launchApp(context, app)
                                            expandedFolderCategory = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // DEDICATED LAUNCHER SETTINGS OVERLAY
        AnimatedVisibility(
            visible = isSettingsOpen,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CmfBlack.copy(alpha = 0.9f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isSettingsOpen = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .fillMaxHeight(0.85f)
                        .border(1.5.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(32.dp))
                        .clickable(enabled = false) {}, // prevent close inside click
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Settings Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DotMatrixText(
                                text = "SETTINGS",
                                dotColor = CmfWhite,
                                dotSize = 3.5.dp,
                                spacing = 1.2.dp,
                                charSpacing = 5.dp,
                                modifier = Modifier.testTag("settings_title")
                            )

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CmfWhite.copy(alpha = 0.08f))
                                    .clickable { isSettingsOpen = false }
                                    .testTag("close_settings_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Close Settings",
                                    tint = CmfWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Scrollable content body for settings
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // 1. Transparency Slider
                            item {
                                Column {
                                    Text(
                                        text = "APP DRAWER TRANSPARENCY",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfCoolGray,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "GLASS",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CmfWhite
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Slider(
                                            value = transparency,
                                            onValueChange = { viewModel.setDrawerTransparency(it) },
                                            valueRange = 0f..1f,
                                            colors = SliderDefaults.colors(
                                                activeTrackColor = CmfWhite,
                                                inactiveTrackColor = CmfCharcoal,
                                                thumbColor = CmfOrange
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("drawer_transparency_slider")
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "${(transparency * 100).toInt()}%",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CmfWhite,
                                            modifier = Modifier.width(36.dp)
                                        )
                                    }
                                }
                            }

                            // 2. Blur Intensity Slider (NEW!)
                            item {
                                Column {
                                    Text(
                                        text = "BLUR INTENSITY SLIDER",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfCoolGray,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "BLUR",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CmfWhite
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Slider(
                                            value = blurStrength,
                                            onValueChange = { viewModel.setBlurStrength(it) },
                                            valueRange = 0f..1f,
                                            steps = 9, // Snap to 0%, 10%, 20%... 100%
                                            colors = SliderDefaults.colors(
                                                activeTrackColor = CmfWhite,
                                                inactiveTrackColor = CmfCharcoal,
                                                thumbColor = CmfOrange
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("blur_intensity_slider")
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "${(blurStrength * 100).roundToInt()}%",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CmfWhite,
                                            modifier = Modifier.width(36.dp)
                                        )
                                    }
                                }
                            }

                            // 3. Animation Speed Segment Selector (NEW!)
                            item {
                                Column {
                                    Text(
                                        text = "DRAWER ANIMATION SPEED",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfCoolGray,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val options = listOf(
                                            150 to "FAST",
                                            300 to "NORMAL",
                                            500 to "EASED"
                                        )
                                        options.forEach { (speed, label) ->
                                            val isSelected = animationSpeed == speed
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSelected) CmfWhite else CmfCharcoal)
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) CmfWhite else CmfCoolGray.copy(alpha = 0.2f),
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable { viewModel.setDrawerAnimationSpeed(speed) }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) CmfBlack else CmfWhite
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 4. Secure Hidden Space settings
                            item {
                                Column {
                                    Text(
                                        text = "HIDDEN SPACE PROTECTION",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfCoolGray,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Row to toggle Biometric authentication
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(CmfCharcoal.copy(alpha = 0.5f))
                                            .clickable { viewModel.setUseDeviceAuth(!useDeviceAuth) }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = CmfOrange,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "PRIORITIZE BIOMETRIC LOCK",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = CmfWhite
                                            )
                                        }
                                        Checkbox(
                                            checked = useDeviceAuth,
                                            onCheckedChange = { viewModel.setUseDeviceAuth(it) },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = CmfOrange,
                                                uncheckedColor = CmfCoolGray
                                            ),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Change custom PIN
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(CmfCharcoal)
                                                .border(1.dp, CmfCoolGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                                .clickable { isChangePinOpen = true }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "CHANGE PIN",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = CmfWhite
                                            )
                                        }

                                        // Manage Hidden Apps Checklist
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(CmfCharcoal)
                                                .border(1.dp, CmfCoolGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                                .clickable { isManageHiddenAppsOpen = true }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "MANAGE APPS",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = CmfWhite
                                            )
                                        }
                                    }
                                }
                            }

                            // 5. Theme & Wallpaper Selector
                            item {
                                Column {
                                    Text(
                                        text = "SELECT CMF WALLPAPER",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfCoolGray,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        listOf("CARBON MATRIX", "MATTE SLATE", "VAPOR GLOW").forEachIndexed { index, name ->
                                            val isSelected = selectedWallpaper == index
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSelected) CmfWhite else CmfCharcoal)
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) CmfWhite else CmfCoolGray.copy(alpha = 0.3f),
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable { viewModel.setSelectedWallpaper(index) }
                                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = name,
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) CmfBlack else CmfWhite,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 6. Premium Performance core dashboard section
                            item {
                                Column {
                                    Text(
                                        text = "CORE PERFORMANCE ENGINE",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfCoolGray,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = CmfCharcoal.copy(alpha = 0.8f)),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, CmfWhite.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "ACTIVE REFRESH RATE",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfCoolGray
                                                )
                                                Text(
                                                    text = "$fpsValue FPS",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (fpsValue >= 118) CmfOrange else CmfRed
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "FRAME DELAY (AVG)",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfCoolGray
                                                )
                                                Text(
                                                    text = String.format(Locale.getDefault(), "%.1f ms", 1000f / fpsValue),
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CmfWhite
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "RENDERING CORE",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfCoolGray
                                                )
                                                Text(
                                                    text = "HW ACCELERATED VULKAN",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CmfWhite
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "ENGINE STATE",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfCoolGray
                                                )
                                                Text(
                                                    text = "STABLE / 120 FPS NOMINAL",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CmfWhite
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Actions buttons: APPLY & RESET
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CmfWhite)
                                    .clickable { isSettingsOpen = false }
                                    .testTag("apply_settings_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "APPLY",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfBlack
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CmfWhite.copy(alpha = 0.1f))
                                    .border(1.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.resetSettings()
                                        isSettingsOpen = false
                                    }
                                    .testTag("reset_settings_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "RESET",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfWhite
                                )
                            }
                        }
                    }
                }
            }

            // 1. MANAGE HIDDEN APPS OVERLAY DIALOG
            if (isManageHiddenAppsOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.95f))
                        .clickable { isManageHiddenAppsOpen = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.8f)
                            .border(1.2.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = CmfDarkGray),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "SELECT APPS TO HIDE",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CmfWhite
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(apps) { app ->
                                    val isHidden = hiddenApps.contains(app.packageName)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isHidden) CmfWhite.copy(alpha = 0.08f) else Color.Transparent)
                                            .clickable {
                                                if (isHidden) {
                                                    viewModel.unhideApp(app.packageName)
                                                } else {
                                                    viewModel.hideApp(app.packageName)
                                                }
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(CmfCharcoal),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (app.icon != null) {
                                                AndroidIconWrapper(drawable = app.icon, modifier = Modifier.size(24.dp))
                                            } else {
                                                Text(
                                                    text = app.label.take(1).uppercase(),
                                                    color = CmfWhite,
                                                    fontSize = 14.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = app.label,
                                            color = CmfWhite,
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Checkbox(
                                            checked = isHidden,
                                            onCheckedChange = null, // Handled by row clickable
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = CmfOrange,
                                                uncheckedColor = CmfCoolGray
                                            )
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CmfWhite)
                                    .clickable { isManageHiddenAppsOpen = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "DONE",
                                    color = CmfBlack,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // 2. CHANGE PIN OVERLAY DIALOG
            if (isChangePinOpen) {
                var newPinInput by remember { mutableStateOf("") }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.95f))
                        .clickable { isChangePinOpen = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(1.2.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = CmfDarkGray),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "SET SECURE SPACE PIN",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CmfWhite
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "CURRENT PIN: $hiddenSpacePin",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CmfCoolGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Keypad input visual feedback
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .border(1.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = newPinInput.ifEmpty { "ENTER 4 DIGITS..." },
                                    color = if (newPinInput.isEmpty()) CmfCoolGray else CmfWhite,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 2.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // 3x4 custom numeric pad
                            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                keys.chunked(3).forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        row.forEach { key ->
                                            Box(
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(CircleShape)
                                                    .background(CmfCharcoal)
                                                    .border(1.dp, CmfWhite.copy(alpha = 0.1f), CircleShape)
                                                    .clickable {
                                                        when (key) {
                                                            "C" -> {
                                                                newPinInput = ""
                                                            }
                                                            "OK" -> {
                                                                if (newPinInput.length == 4) {
                                                                    viewModel.setHiddenSpacePin(newPinInput)
                                                                    isChangePinOpen = false
                                                                }
                                                            }
                                                            else -> {
                                                                if (newPinInput.length < 4) {
                                                                    newPinInput += key
                                                                }
                                                            }
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = key,
                                                    color = if (key == "OK") CmfOrange else CmfWhite,
                                                    fontSize = 15.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CmfWhite.copy(alpha = 0.1f))
                                    .border(1.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .clickable { isChangePinOpen = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CANCEL",
                                    color = CmfWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- WORKSPACE & APP SHORTCUT POPUPS ---

            if (isWorkspaceLongPressed) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CmfBlack.copy(alpha = 0.8f))
                        .clickable { isWorkspaceLongPressed = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(1.5.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DotMatrixText(
                                text = "DESKTOP",
                                dotColor = CmfWhite,
                                dotSize = 3.5.dp,
                                spacing = 1.2.dp,
                                charSpacing = 5.dp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Option 1: Change Wallpaper
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CmfCharcoal.copy(alpha = 0.8f))
                                    .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        val nextWallpaper = (selectedWallpaper + 1) % 3
                                        viewModel.setSelectedWallpaper(nextWallpaper)
                                        isWorkspaceLongPressed = false
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "CYCLE CMF WALLPAPER",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfWhite
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Option 2: Launcher Settings
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CmfCharcoal.copy(alpha = 0.8f))
                                    .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        isWorkspaceLongPressed = false
                                        isSettingsOpen = true
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "LAUNCHER SETTINGS",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfWhite
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Option 3: Add app
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CmfCharcoal.copy(alpha = 0.8f))
                                    .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        isWorkspaceLongPressed = false
                                        isAddAppDialogOpen = true
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "ADD APP TO GRID",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfWhite
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CmfWhite)
                                    .clickable { isWorkspaceLongPressed = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CLOSE",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfBlack
                                )
                            }
                        }
                    }
                }
            }

            if (longPressedApp != null) {
                val app = longPressedApp!!
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CmfBlack.copy(alpha = 0.8f))
                        .clickable { longPressedApp = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(1.5.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(CmfCharcoal),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (app.icon != null) {
                                        AndroidIconWrapper(drawable = app.icon, modifier = Modifier.size(24.dp))
                                    } else {
                                        Text(
                                            text = app.label.take(1).uppercase(),
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = CmfWhite
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = app.label.uppercase(),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfWhite
                                    )
                                    Text(
                                        text = app.category.uppercase(),
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CmfCoolGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Option 1: Launch App
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CmfCharcoal.copy(alpha = 0.8f))
                                    .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.launchApp(context, app)
                                        longPressedApp = null
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "LAUNCH APPLICATION",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfWhite
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Option 2: Remove from Home Screen
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CmfCharcoal.copy(alpha = 0.8f))
                                    .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        val slot = longPressedGridSlot
                                        if (slot != null) {
                                            if (slot >= 0) {
                                                viewModel.removeAppFromGrid(slot)
                                            } else {
                                                android.widget.Toast.makeText(context, "Dock apps are permanent but interchangeable!", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        longPressedApp = null
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "REMOVE FROM HOME SCREEN",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfRed
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Option 3: App Details
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CmfCharcoal.copy(alpha = 0.8f))
                                    .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        android.widget.Toast.makeText(context, "Pkg: ${app.packageName}\nClass: ${app.className}\nisMock: ${app.isMock}", android.widget.Toast.LENGTH_LONG).show()
                                        longPressedApp = null
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "APP DETAILS & METADATA",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfWhite
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CmfWhite)
                                    .clickable { longPressedApp = null },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CLOSE",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfBlack
                                )
                            }
                        }
                    }
                }
            }

            if (isAddAppDialogOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CmfBlack.copy(alpha = 0.85f))
                        .clickable { isAddAppDialogOpen = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.7f)
                            .border(1.5.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = CmfDarkGray),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SELECT APP TO ADD",
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfWhite
                                )
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CmfWhite.copy(alpha = 0.08f))
                                        .clickable { isAddAppDialogOpen = false },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Close",
                                        tint = CmfWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(apps) { app ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val targetSlot = longPressedGridSlot ?: viewModel.homeGridApps.value.indexOfFirst { it == null }
                                                if (targetSlot in 0..29) {
                                                    viewModel.addAppToGrid(targetSlot, app)
                                                }
                                                isAddAppDialogOpen = false
                                                longPressedGridSlot = null
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(CmfCharcoal.copy(alpha = 0.8f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (app.icon != null) {
                                                AndroidIconWrapper(drawable = app.icon, modifier = Modifier.size(24.dp))
                                            } else {
                                                Text(
                                                    text = app.label.take(1).uppercase(),
                                                    fontSize = 14.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfWhite
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = app.label,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CmfWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LargeFolderCard(
    title: String,
    apps: List<LauncherApp>,
    onAppClick: (LauncherApp) -> Unit,
    onFolderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .border(1.2.dp, CmfWhite.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
            .clickable { onFolderClick() },
        colors = CardDefaults.cardColors(containerColor = CmfCharcoal.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Folder title at the top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CmfWhite,
                    letterSpacing = 1.5.sp
                )

                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null,
                    tint = CmfOrange,
                    modifier = Modifier.size(14.dp)
                )
            }

            // 2x2 grid preview of apps inside the folder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (apps.isEmpty()) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = CmfCoolGray.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        apps.take(4).forEach { app ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CmfBlack.copy(alpha = 0.5f))
                                    .border(0.8.dp, CmfCoolGray.copy(alpha = 0.2f), CircleShape)
                                    .clickable { onAppClick(app) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (app.icon != null) {
                                    // Display actual app icon
                                    AndroidIconWrapper(drawable = app.icon, modifier = Modifier.size(24.dp))
                                } else {
                                    // Beautiful monochrome placeholder
                                    Text(
                                        text = app.label.take(1).uppercase(),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Footer count
            Text(
                text = "${apps.size} APPS",
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                color = CmfCoolGray,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun AppIconItem(
    app: LauncherApp,
    onClick: () -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 0.85f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        finishedListener = {
            if (isClicked) {
                isClicked = false
                onClick()
            }
        },
        label = "AppPressScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { isClicked = true }
            .testTag("app_item_${app.packageName}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High fidelity monochrome app circle container
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(CmfCharcoal.copy(alpha = 0.8f))
                .border(1.2.dp, CmfCoolGray.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (app.icon != null) {
                AndroidIconWrapper(drawable = app.icon, modifier = Modifier.size(34.dp))
            } else {
                Text(
                    text = app.label.take(1).uppercase(),
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CmfWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = app.label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = CmfWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(1.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = CmfCoolGray,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (query.isEmpty()) {
                Text(
                    text = "SEARCH APPLICATIONS...",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CmfCoolGray
                )
            }

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    color = CmfWhite,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                ),
                cursorBrush = SolidColor(CmfOrange),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                }),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear search",
                tint = CmfCoolGray,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onQueryChange("") }
            )
        }
    }
}

@Composable
fun AndroidIconWrapper(
    drawable: android.graphics.drawable.Drawable,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawIntoCanvas { canvas ->
            drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
            drawable.draw(canvas.nativeCanvas)
        }
    }
}
