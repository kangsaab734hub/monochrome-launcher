package com.example

import android.content.Context
import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import android.os.Build
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.HorizontalDivider
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
    val wallpaperPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val file = java.io.File(context.filesDir, "custom_wallpaper.png")
                    val outputStream = java.io.FileOutputStream(file)
                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    viewModel.setCustomWallpaperPath(file.absolutePath)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
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

    val showClockWidget by viewModel.showClockWidget.collectAsState()
    val clockAlignment by viewModel.clockAlignment.collectAsState()
    val drawerStyle by viewModel.drawerStyle.collectAsState()

    val triggerVibration = remember(context) {
        {
            val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
            if (vibrator != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(android.os.VibrationEffect.createOneShot(40, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(40)
                }
            }
        }
    }

    var longPressedApp by remember { mutableStateOf<LauncherApp?>(null) }
    var longPressedGridSlot by remember { mutableStateOf<Int?>(null) } // 0..149 for grid, -1..-5 for dock
    var isWorkspaceLongPressed by remember { mutableStateOf(false) }
    var isAddAppDialogOpen by remember { mutableStateOf(false) }
    var isWidgetsDialogOpen by remember { mutableStateOf(false) }
    var isHomeCustomisationOpen by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }

    var isViewingHiddenSpace by remember { mutableStateOf(false) }
    var showHiddenSpacePinDialog by remember { mutableStateOf(false) }
    var hiddenSpaceInputPin by remember { mutableStateOf("") }
    var selectedEditIndex by remember { mutableStateOf<Int?>(null) }

    // Hidden Space biometric / PIN lock states
    var isSecureHiddenSpaceOpen by remember { mutableStateOf(false) }
    var isSecureHiddenSpaceUnlocked by remember { mutableStateOf(false) }

    // Interactive Wallpaper Scale & Offset states
    var wallpaperScale by remember { mutableStateOf(1.0f) }
    var wallpaperOffsetX by remember { mutableStateOf(0f) }
    var wallpaperOffsetY by remember { mutableStateOf(0f) }
    var isWallpaperCustomizerOpen by remember { mutableStateOf(false) }

    // Drag and drop states for home grid icon rearrangement
    val slotBounds = remember { mutableStateMapOf<Int, Rect>() }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragStartPos by remember { mutableStateOf(Offset.Zero) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var currentHoveredIndex by remember { mutableStateOf<Int?>(null) }

    var isSettingsOpen by remember { mutableStateOf(false) }
    var isManageHiddenAppsOpen by remember { mutableStateOf(false) }
    var isChangePinOpen by remember { mutableStateOf(false) }
    var expandedFolderCategory by remember { mutableStateOf<String?>(null) }
    val gridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    var drawerCloseDragOffset by remember { mutableStateOf(0f) }

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

    val customWallpaperPath by viewModel.customWallpaperPath.collectAsState()
    val customWallpaperBitmap = remember(customWallpaperPath) {
        if (customWallpaperPath != null) {
            try {
                val file = java.io.File(customWallpaperPath!!)
                if (file.exists()) {
                    android.graphics.BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        val density = LocalDensity.current

        val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        // SLIDING FROSTED GLASS APP DRAWER WITH ADJUSTABLE REAL-TIME BLUR
        var isDrawerOpen by remember { mutableStateOf(false) }
        val drawerOffsetAnim = remember { Animatable(1f) } // 1f = closed, 0f = open
        val coroutineScope = rememberCoroutineScope()

        val screenHeightPx = with(density) { screenHeight.toPx() }
        val screenWidthPx = with(density) { screenWidth.toPx() }

        LaunchedEffect(isDrawerOpen) {
            if (!isDrawerOpen) {
                isViewingHiddenSpace = false
                viewModel.setHiddenSpaceUnlocked(false)
            }
            drawerOffsetAnim.animateTo(
                targetValue = if (isDrawerOpen) 0f else 1f,
                animationSpec = tween(
                    durationMillis = animationSpeed,
                    easing = FastOutSlowInEasing
                )
            )
        }

        val finalFraction = drawerOffsetAnim.value
        val drawerOffsetY = screenHeightPx * finalFraction

        var horizontalDragOffset by remember { mutableStateOf(0f) }

        // Dynamic Glassmorphic properties with instant adjustable blur strength from 0% to 100%
        val alphaLevel = transparency // Slider controlled 0% to 100%
        val glassColor = CmfBlack.copy(alpha = 0.15f + 0.75f * alphaLevel)
        // Blur calculated using the live settings value instantly
        val glassBlur = (30.dp * blurStrength)

        // Show Home Screen always unless fully covered, to avoid any possibility of a black background
        val showHomeScreen = finalFraction > 0.001f || !isDrawerOpen
        val showDrawer = isDrawerOpen || finalFraction < 0.99f

        if (isDrawerOpen) {
            BackHandler {
                isDrawerOpen = false
            }
        }

        // Draw background wallpaper (with live blur!)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (glassBlur > 0.dp && finalFraction < 0.999f) {
                        Modifier.blur(glassBlur * (1f - finalFraction))
                    } else {
                        Modifier
                    }
                )
        ) {
            if (customWallpaperBitmap != null) {
                Image(
                    bitmap = customWallpaperBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = wallpaperScale,
                            scaleY = wallpaperScale,
                            translationX = wallpaperOffsetX,
                            translationY = wallpaperOffsetY
                        )
                )
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
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
            }
        }

        // HOMEPAGE MAIN CONTENT
        if (showHomeScreen) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = insetsTop, bottom = 48.dp) // Leave space at bottom for drawer swipe gesture
                    .testTag("homepage_layout")
                    .pointerInput(screenHeightPx) {
                        detectVerticalDragGestures(
                            onDragStart = {},
                            onDragEnd = {
                                val targetOpen = drawerOffsetAnim.value < 0.75f
                                isDrawerOpen = targetOpen
                                coroutineScope.launch {
                                    drawerOffsetAnim.animateTo(
                                        targetValue = if (targetOpen) 0f else 1f,
                                        animationSpec = tween(
                                            durationMillis = animationSpeed,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                }
                            },
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                val delta = dragAmount / screenHeightPx
                                coroutineScope.launch {
                                    drawerOffsetAnim.snapTo((drawerOffsetAnim.value + delta).coerceIn(0f, 1f))
                                }
                            }
                        )
                    },
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
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = when (clockAlignment) {
                        1 -> Alignment.Center
                        2 -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    }
                ) {
                    if (showClockWidget) {
                        var isClockMenuOpen by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .combinedClickable(
                                    onClick = { /* normal tap */ },
                                    onLongClick = {
                                        isClockMenuOpen = true
                                    }
                                )
                                .testTag("clock_widget_container")
                        ) {
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

                        if (isClockMenuOpen) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(CmfBlack.copy(alpha = 0.8f))
                                    .clickable { isClockMenuOpen = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .border(1.5.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                                        .clickable(enabled = false) {},
                                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        DotMatrixText(
                                            text = "WIDGET",
                                            dotColor = CmfWhite,
                                            dotSize = 3.5.dp,
                                            spacing = 1.2.dp,
                                            charSpacing = 5.dp
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Option 1: Move Widget
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(CmfCharcoal.copy(alpha = 0.8f))
                                                .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                                .clickable {
                                                    isClockMenuOpen = false
                                                    val nextAlign = (clockAlignment + 1) % 3
                                                    viewModel.setClockAlignment(nextAlign)
                                                    android.widget.Toast.makeText(context, "Clock alignment adjusted!", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(horizontal = 16.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                text = "MOVE / REPOSITION WIDGET",
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = CmfWhite
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Option 2: Remove Widget
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(CmfCharcoal.copy(alpha = 0.8f))
                                                .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                                .clickable {
                                                    isClockMenuOpen = false
                                                    viewModel.setShowClockWidget(false)
                                                    android.widget.Toast.makeText(context, "Widget removed. Re-add from long-press menu -> Widgets!", android.widget.Toast.LENGTH_LONG).show()
                                                }
                                                .padding(horizontal = 16.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                text = "REMOVE WIDGET",
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = CmfRed
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(CmfWhite)
                                                .clickable { isClockMenuOpen = false },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "CANCEL",
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
                    } else {
                        Spacer(modifier = Modifier.size(1.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content Area
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
                val activePageCount = remember(homeGridApps) {
                    val lastOccupiedSlot = homeGridApps.indexOfLast { it != null }
                    if (lastOccupiedSlot == -1) 1 else {
                        val lastOccupiedPage = lastOccupiedSlot / 30
                        (lastOccupiedPage + 1).coerceIn(1, 5)
                    }
                }
                val pagerState = rememberPagerState(pageCount = { activePageCount })
                var isScrollingPage by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) { page ->
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            userScrollEnabled = false,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("home_5x6_grid_page_$page"),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(30) { localIndex ->
                                val index = page * 30 + localIndex
                                val app = homeGridApps.getOrNull(index)
                                val isSelected = selectedEditIndex == index
                                val isDragged = draggedIndex == index
                                val isHovered = currentHoveredIndex == index && draggedIndex != null && draggedIndex != index

                                Box(
                                    modifier = Modifier
                                        .height(76.dp)
                                        .testTag("grid_slot_$index")
                                        .onGloballyPositioned { coordinates ->
                                            val parentBounds = coordinates.positionInWindow()
                                            val size = coordinates.size
                                            slotBounds[index] = Rect(
                                                parentBounds.x,
                                                parentBounds.y,
                                                parentBounds.x + size.width,
                                                parentBounds.y + size.height
                                            )
                                        }
                                        .pointerInput(index, app) {
                                            detectDragGesturesAfterLongPress(
                                                onDragStart = { offset ->
                                                    if (app != null) {
                                                        triggerVibration()
                                                        isEditMode = true
                                                        draggedIndex = index
                                                        dragOffset = Offset.Zero
                                                        dragStartPos = slotBounds[index]?.topLeft ?: Offset.Zero
                                                    }
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    dragOffset += dragAmount
                                                    val currentPointerPos = dragStartPos + dragOffset
                                                    
                                                    // Auto-scroll logic for drag-to-edge
                                                    val leftBoundary = with(density) { 40.dp.toPx() }
                                                    val rightBoundary = screenWidthPx - with(density) { 40.dp.toPx() }
                                                    if (currentPointerPos.x < leftBoundary && pagerState.currentPage > 0 && !isScrollingPage) {
                                                        isScrollingPage = true
                                                        coroutineScope.launch {
                                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                                            isScrollingPage = false
                                                        }
                                                    } else if (currentPointerPos.x > rightBoundary && pagerState.currentPage < 4 && !isScrollingPage) {
                                                        isScrollingPage = true
                                                        coroutineScope.launch {
                                                            pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(4))
                                                            isScrollingPage = false
                                                        }
                                                    }

                                                    val hovered = slotBounds.entries.find { entry ->
                                                        entry.value.contains(currentPointerPos)
                                                    }?.key
                                                    currentHoveredIndex = hovered
                                                },
                                                onDragEnd = {
                                                    if (draggedIndex != null) {
                                                        val targetIndex = currentHoveredIndex
                                                        if (targetIndex != null && targetIndex != draggedIndex) {
                                                            val draggedApp = homeGridApps[draggedIndex!!]
                                                            val targetApp = homeGridApps[targetIndex]
                                                            if (draggedApp != null) {
                                                                viewModel.removeAppFromGrid(draggedIndex!!)
                                                                if (targetApp != null) {
                                                                    viewModel.removeAppFromGrid(targetIndex)
                                                                    viewModel.addAppToGrid(draggedIndex!!, targetApp)
                                                                }
                                                                viewModel.addAppToGrid(targetIndex, draggedApp)
                                                            }
                                                        }
                                                        draggedIndex = null
                                                        dragOffset = Offset.Zero
                                                        currentHoveredIndex = null
                                                    }
                                                },
                                                onDragCancel = {
                                                    draggedIndex = null
                                                    dragOffset = Offset.Zero
                                                    currentHoveredIndex = null
                                                }
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isEditMode) {
                                        if (app != null) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .graphicsLayer {
                                                        alpha = if (isDragged) 0.25f else 1.0f
                                                    }
                                                    .clickable {
                                                        if (selectedEditIndex == null) {
                                                            selectedEditIndex = index
                                                        } else if (selectedEditIndex == index) {
                                                            selectedEditIndex = null
                                                        } else {
                                                            // Swap/Rearrange apps
                                                            val app1 = homeGridApps[selectedEditIndex!!]
                                                            val app2 = app
                                                            if (app1 != null) {
                                                                viewModel.removeAppFromGrid(selectedEditIndex!!)
                                                                viewModel.removeAppFromGrid(index)
                                                                viewModel.addAppToGrid(index, app1)
                                                                viewModel.addAppToGrid(selectedEditIndex!!, app2)
                                                            }
                                                            selectedEditIndex = null
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(46.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) CmfWhite.copy(alpha = 0.25f)
                                                                else CmfCharcoal.copy(alpha = 0.8f)
                                                            )
                                                            .border(
                                                                width = if (isSelected) 2.dp else 1.2.dp,
                                                                color = if (isSelected) CmfWhite else CmfCoolGray.copy(alpha = 0.4f),
                                                                shape = CircleShape
                                                            ),
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
                                                        color = if (isSelected) CmfWhite else CmfCoolGray,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }

                                                // Small delete/remove button on top right of the icon
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .offset(x = (-4).dp, y = (-2).dp)
                                                        .size(16.dp)
                                                        .clip(CircleShape)
                                                        .background(CmfRed)
                                                        .clickable {
                                                            viewModel.removeAppFromGrid(index)
                                                            if (selectedEditIndex == index) {
                                                                selectedEditIndex = null
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Clear,
                                                        contentDescription = "Remove App",
                                                        tint = CmfWhite,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                }
                                            }
                                        } else {
                                            // Empty slot in Edit Mode: clickable or highlight if hovered
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clickable {
                                                        if (selectedEditIndex != null) {
                                                            val appToMove = homeGridApps[selectedEditIndex!!]
                                                            if (appToMove != null) {
                                                                viewModel.removeAppFromGrid(selectedEditIndex!!)
                                                                viewModel.addAppToGrid(index, appToMove)
                                                            }
                                                            selectedEditIndex = null
                                                        } else {
                                                            longPressedGridSlot = index
                                                            isAddAppDialogOpen = true
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(42.dp)
                                                        .border(
                                                            width = if (isHovered) 2.dp else 1.dp,
                                                            color = if (isHovered) CmfWhite else CmfCoolGray.copy(alpha = 0.35f),
                                                            shape = CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isHovered) CmfWhite else CmfCoolGray.copy(alpha = 0.5f)
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Normal mode (not edit mode)
                                        if (app != null) {
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
                                                    color = CmfCoolGray,
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
                        }
                    }

                    // Dynamic Workspace dots indicator
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        repeat(activePageCount) { p ->
                            val isSelectedPage = pagerState.currentPage == p
                            Box(
                                modifier = Modifier
                                    .size(if (isSelectedPage) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelectedPage) CmfWhite else CmfWhite.copy(alpha = 0.25f))
                            )
                        }
                    }
                }
                // Edit Mode Floating Guidance Banner
                if (isEditMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.95f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "HOME EDIT MODE",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfWhite
                                    )
                                    Text(
                                        text = "Tap app to select, empty slot to move, or 'x' to remove.",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CmfCoolGray
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        isEditMode = false
                                        selectedEditIndex = null
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CmfWhite,
                                        contentColor = CmfBlack
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "DONE",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
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
    }

        if (showDrawer) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // App Drawer Body sheet
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = drawerOffsetY
                        }
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
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
                        .pointerInput(screenHeightPx) {
                            detectVerticalDragGestures(
                                onDragStart = {},
                                onDragEnd = {
                                    val targetOpen = drawerOffsetAnim.value < 0.25f
                                    isDrawerOpen = targetOpen
                                    coroutineScope.launch {
                                        drawerOffsetAnim.animateTo(
                                            targetValue = if (targetOpen) 0f else 1f,
                                            animationSpec = tween(
                                                durationMillis = animationSpeed,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    }
                                },
                                onVerticalDrag = { change, dragAmount ->
                                    val isAtTop = if (drawerStyle == 1) true else {
                                        gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0
                                    }
                                    if (isAtTop && dragAmount > 0f) {
                                        change.consume()
                                        val delta = dragAmount / screenHeightPx
                                        coroutineScope.launch {
                                            drawerOffsetAnim.snapTo((drawerOffsetAnim.value + delta).coerceIn(0f, 1f))
                                        }
                                    }
                                }
                            )
                        }
                        .testTag("app_drawer_sheet")
                ) {
                    // Blurred background layer inside sheet (keeps icons and text perfectly sharp!)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && glassBlur > 0.dp) {
                                    Modifier.blur(glassBlur)
                                } else {
                                    Modifier
                                }
                            )
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(glassColor, glassColor.copy(alpha = 0.95f))
                                )
                            )
                    )
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
                        .padding(top = insetsTop + 12.dp, bottom = insetsBottom + 16.dp)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {},
                                onHorizontalDrag = { change, dragAmount ->
                                    if (dragAmount > 25f && !isViewingHiddenSpace) {
                                        change.consume()
                                        if (isHiddenSpaceUnlocked) {
                                            isViewingHiddenSpace = true
                                        } else {
                                            showHiddenSpacePinDialog = true
                                            hiddenSpaceInputPin = ""
                                        }
                                    } else if (dragAmount < -25f && isViewingHiddenSpace) {
                                        change.consume()
                                        isViewingHiddenSpace = false
                                    }
                                }
                            )
                        },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isViewingHiddenSpace) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(CmfWhite.copy(alpha = 0.08f))
                                        .clickable { isViewingHiddenSpace = false },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Exit Hidden Space",
                                        tint = CmfWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                DotMatrixText(
                                    text = "SECURE SPACE",
                                    dotColor = CmfWhite,
                                    dotSize = 2.5.dp,
                                    spacing = 0.8.dp,
                                    charSpacing = 3.dp
                                )

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(CmfWhite.copy(alpha = 0.08f))
                                        .clickable {
                                            viewModel.setHiddenSpaceUnlocked(false)
                                            isViewingHiddenSpace = false
                                            android.widget.Toast.makeText(context, "Hidden Space Locked", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock Hidden Space",
                                        tint = CmfWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = CmfWhite.copy(alpha = 0.08f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            val hiddenSpaceApps = remember(apps, hiddenApps) {
                                apps.filter { hiddenApps.contains(it.packageName) }
                            }

                            if (hiddenSpaceApps.isNotEmpty()) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(4),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(hiddenSpaceApps) { app ->
                                        AppIconItem(
                                            app = app,
                                            onClick = {
                                                viewModel.launchApp(context, app)
                                                isDrawerOpen = false
                                            },
                                            onLongClick = {
                                                longPressedApp = app
                                                longPressedGridSlot = null
                                            }
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = CmfCoolGray.copy(alpha = 0.3f),
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "NO HIDDEN APPS",
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = CmfWhite
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Long-press any application in the App Drawer and select 'Hide Application' to protect it here.",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CmfCoolGray,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "← SWIPE RIGHT TO EXIT",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CmfCoolGray.copy(alpha = 0.5f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    } else {

                        // Fast A-Z Indexing Logic
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

                        if (drawerStyle == 1 && searchQuery.isBlank()) {
                            // Folder Style Grid Layout
                            val categories = listOf("Google", "Media", "Social", "Games", "Utilities", "Apps")
                            val folderItems = categories.mapNotNull { cat ->
                                val categoryApps = sortedApps.filter { it.category == cat }
                                if (categoryApps.isNotEmpty()) cat to categoryApps else null
                            }

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .testTag("drawer_folders_grid"),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(folderItems) { (catName, catApps) ->
                                    FolderCardItem(
                                        categoryName = catName,
                                        apps = catApps,
                                        onClick = {
                                            expandedFolderCategory = catName
                                        }
                                    )
                                }
                            }
                        } else {
                            // Classic Grid Layout (Default, and fallback for Search)
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
                                            },
                                            onLongClick = {
                                                longPressedApp = app
                                                longPressedGridSlot = null
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // SearchBar Row at the bottom
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
                    }
                }
            }
        }

        // Floating Drag Overlay for real-time finger follow!
        if (draggedIndex != null) {
            val draggedApp = homeGridApps.getOrNull(draggedIndex!!)
            if (draggedApp != null) {
                val startPos = slotBounds[draggedIndex!!] ?: Rect(0f, 0f, 0f, 0f)
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(LocalDensity.current) { (startPos.left + dragOffset.x).toDp() },
                                y = with(LocalDensity.current) { (startPos.top + dragOffset.y).toDp() }
                            )
                            .size(
                                width = with(LocalDensity.current) { startPos.width.toDp() },
                                height = with(LocalDensity.current) { startPos.height.toDp() }
                            )
                            .shadow(8.dp, CircleShape)
                            .background(CmfCharcoal.copy(alpha = 0.9f), CircleShape)
                            .border(1.5.dp, CmfWhite, CircleShape)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (draggedApp.icon != null) {
                            AndroidIconWrapper(drawable = draggedApp.icon, modifier = Modifier.size(28.dp))
                        } else {
                            Text(
                                text = draggedApp.label.take(1).uppercase(),
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
                                        },
                                        onLongClick = {
                                            longPressedApp = app
                                            longPressedGridSlot = null
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

                            // Custom Wallpaper Selector
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Column {
                                    Text(
                                        text = "CUSTOM WALLPAPER",
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
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (customWallpaperPath != null) CmfWhite else CmfCharcoal)
                                                .border(
                                                    1.dp,
                                                    if (customWallpaperPath != null) CmfWhite else CmfCoolGray.copy(alpha = 0.3f),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable {
                                                    wallpaperPickerLauncher.launch("image/*")
                                                }
                                                .padding(vertical = 12.dp, horizontal = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (customWallpaperPath != null) "CHANGE WALLPAPER" else "SELECT FROM GALLERY",
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = if (customWallpaperPath != null) CmfBlack else CmfWhite,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        if (customWallpaperPath != null) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(0.5f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(CmfRed.copy(alpha = 0.15f))
                                                    .border(
                                                        1.dp,
                                                        CmfRed,
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable {
                                                        viewModel.setCustomWallpaperPath(null)
                                                    }
                                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "CLEAR",
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CmfRed,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }

                                    if (customWallpaperPath != null) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(CmfCharcoal.copy(alpha = 0.5f))
                                                .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                                .padding(14.dp)
                                        ) {
                                            Text(
                                                text = "ADJUST WALLPAPER POSITION",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = CmfWhite
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Scale Adjustment
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "SCALE",
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfCoolGray
                                                )
                                                Text(
                                                    text = "${String.format("%.2f", wallpaperScale)}x",
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfWhite,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Slider(
                                                value = wallpaperScale,
                                                onValueChange = { wallpaperScale = it },
                                                valueRange = 1.0f..3.0f,
                                                colors = SliderDefaults.colors(
                                                    activeTrackColor = CmfWhite,
                                                    inactiveTrackColor = CmfCharcoal,
                                                    thumbColor = CmfWhite
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))

                                            // Horizontal Offset Adjustment
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "OFFSET X",
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfCoolGray
                                                )
                                                Text(
                                                    text = "${wallpaperOffsetX.roundToInt()}px",
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfWhite,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Slider(
                                                value = wallpaperOffsetX,
                                                onValueChange = { wallpaperOffsetX = it },
                                                valueRange = -600f..600f,
                                                colors = SliderDefaults.colors(
                                                    activeTrackColor = CmfWhite,
                                                    inactiveTrackColor = CmfCharcoal,
                                                    thumbColor = CmfWhite
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))

                                            // Vertical Offset Adjustment
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "OFFSET Y",
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfCoolGray
                                                )
                                                Text(
                                                    text = "${wallpaperOffsetY.roundToInt()}px",
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = CmfWhite,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Slider(
                                                value = wallpaperOffsetY,
                                                onValueChange = { wallpaperOffsetY = it },
                                                valueRange = -1000f..1000f,
                                                colors = SliderDefaults.colors(
                                                    activeTrackColor = CmfWhite,
                                                    inactiveTrackColor = CmfCharcoal,
                                                    thumbColor = CmfWhite
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Reset Button
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(34.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(CmfCoolGray.copy(alpha = 0.15f))
                                                    .clickable {
                                                        wallpaperScale = 1.0f
                                                        wallpaperOffsetX = 0f
                                                        wallpaperOffsetY = 0f
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "RESET POSITION",
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CmfWhite
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // App Drawer Style Selector
                            item {
                                Column {
                                    Text(
                                        text = "APP DRAWER STYLE",
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
                                        listOf("CLASSIC GRID" to 0, "FOLDER STYLE" to 1).forEach { (name, styleId) ->
                                            val isSelected = drawerStyle == styleId
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
                                                    .clickable { viewModel.setDrawerStyle(styleId) }
                                                    .padding(vertical = 12.dp),
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
                                    Spacer(modifier = Modifier.height(20.dp))
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
        }

            // --- WORKSPACE & APP SHORTCUT POPUPS ---

            if (isWorkspaceLongPressed) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { isWorkspaceLongPressed = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .border(1.2.dp, CmfWhite.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = CmfCharcoal.copy(alpha = 0.92f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DotMatrixText(
                                text = "CUSTOMISE",
                                dotColor = CmfWhite,
                                dotSize = 3.dp,
                                spacing = 1.0.dp,
                                charSpacing = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(color = CmfWhite.copy(alpha = 0.08f), thickness = 1.dp, modifier = Modifier.padding(bottom = 8.dp))

                            // Option Row Helper for Workspace menu
                            @Composable
                            fun WorkspaceOptionRow(
                                label: String,
                                icon: androidx.compose.ui.graphics.vector.ImageVector,
                                onClick: () -> Unit
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onClick() }
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = CmfWhite.copy(alpha = 0.85f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = label.uppercase(),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfWhite
                                    )
                                }
                            }

                            // Option 1: Launcher Settings
                            WorkspaceOptionRow(
                                label = "Launcher Settings",
                                icon = Icons.Default.Settings
                            ) {
                                isWorkspaceLongPressed = false
                                isSettingsOpen = true
                            }

                            // Option 2: Widgets
                            WorkspaceOptionRow(
                                label = "Widgets",
                                icon = Icons.Default.Widgets
                            ) {
                                isWorkspaceLongPressed = false
                                isWidgetsDialogOpen = true
                            }

                            // Option 3: Customisation Settings
                            WorkspaceOptionRow(
                                label = "Home Settings",
                                icon = Icons.Default.Tune
                            ) {
                                isWorkspaceLongPressed = false
                                isHomeCustomisationOpen = true
                            }
                        }
                    }
                }
            }

            if (isWidgetsDialogOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CmfBlack.copy(alpha = 0.8f))
                        .clickable { isWidgetsDialogOpen = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(1.5.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DotMatrixText(
                                text = "WIDGETS",
                                dotColor = CmfWhite,
                                dotSize = 3.5.dp,
                                spacing = 1.2.dp,
                                charSpacing = 5.dp
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Clock Widget card
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CmfCharcoal.copy(alpha = 0.8f))
                                    .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "NOTHING CLOCK",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfWhite
                                    )
                                    Text(
                                        text = "Dot-matrix clock & date",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CmfCoolGray
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.setShowClockWidget(!showClockWidget)
                                        isWidgetsDialogOpen = false
                                        android.widget.Toast.makeText(
                                            context,
                                            if (!showClockWidget) "Clock Widget Added!" else "Clock Widget Removed!",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (showClockWidget) CmfRed else CmfWhite,
                                        contentColor = if (showClockWidget) CmfWhite else CmfBlack
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (showClockWidget) "REMOVE" else "ADD",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CmfWhite)
                                    .clickable { isWidgetsDialogOpen = false },
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

            if (isHomeCustomisationOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CmfBlack.copy(alpha = 0.8f))
                        .clickable { isHomeCustomisationOpen = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(1.5.dp, CmfWhite.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DotMatrixText(
                                text = "CUSTOMISE",
                                dotColor = CmfWhite,
                                dotSize = 3.5.dp,
                                spacing = 1.2.dp,
                                charSpacing = 5.dp
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Cycle CMF Wallpaper
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
                                        android.widget.Toast.makeText(context, "CMF Wallpaper Cycled!", android.widget.Toast.LENGTH_SHORT).show()
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

                            // Enter Edit Mode Option
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CmfCharcoal.copy(alpha = 0.8f))
                                    .border(1.dp, CmfCoolGray.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        isHomeCustomisationOpen = false
                                        isEditMode = true
                                        android.widget.Toast.makeText(context, "Entering home edit mode. Tap any icon to move or rearrange!", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "ARRANGE ICONS (EDIT MODE)",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CmfWhite
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CmfWhite)
                                    .clickable { isHomeCustomisationOpen = false },
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
                val isAppOnHomeScreen = homeGridApps.any { it?.packageName == app.packageName }
                val isHidden = hiddenApps.contains(app.packageName)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { longPressedApp = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .border(1.2.dp, CmfWhite.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                            .clickable(enabled = false) {},
                        colors = CardDefaults.cardColors(containerColor = CmfCharcoal.copy(alpha = 0.92f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header with app icon and title
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(CmfBlack.copy(alpha = 0.6f))
                                        .border(1.dp, CmfCoolGray.copy(alpha = 0.25f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (app.icon != null) {
                                        AndroidIconWrapper(drawable = app.icon, modifier = Modifier.size(26.dp))
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
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = app.label.uppercase(),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = CmfWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = app.category.uppercase(),
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CmfCoolGray,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            HorizontalDivider(color = CmfWhite.copy(alpha = 0.08f), thickness = 1.dp, modifier = Modifier.padding(bottom = 8.dp))

                            // Custom Row Composable Helper for Menu options
                            @Composable
                            fun MenuOptionRow(
                                label: String,
                                icon: androidx.compose.ui.graphics.vector.ImageVector,
                                isDestructive: Boolean = false,
                                onClick: () -> Unit
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onClick() }
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isDestructive) CmfRed else CmfWhite.copy(alpha = 0.85f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = label.uppercase(),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDestructive) CmfRed else CmfWhite
                                    )
                                }
                            }

                            // Option 1: Open Application
                            MenuOptionRow(
                                label = "Open Application",
                                icon = Icons.Default.PlayArrow
                            ) {
                                viewModel.launchApp(context, app)
                                longPressedApp = null
                            }

                            // Option 2: Pin/Unpin to/from Home Screen
                            MenuOptionRow(
                                label = if (isAppOnHomeScreen) "Unpin from Home Screen" else "Pin to Home Screen",
                                icon = Icons.Default.PushPin
                            ) {
                                if (isAppOnHomeScreen) {
                                    homeGridApps.forEachIndexed { idx, item ->
                                        if (item?.packageName == app.packageName) {
                                            viewModel.removeAppFromGrid(idx)
                                        }
                                    }
                                    android.widget.Toast.makeText(context, "Unpinned from Home Screen", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    val pinned = viewModel.addAppToFirstAvailableSlot(app)
                                    if (pinned) {
                                        android.widget.Toast.makeText(context, "Pinned to Home Screen", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        android.widget.Toast.makeText(context, "No empty slot on Home Screen", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                                longPressedApp = null
                            }

                            // Option 3: Remove from Home Screen (if applicable)
                            if (isAppOnHomeScreen || longPressedGridSlot != null) {
                                MenuOptionRow(
                                    label = "Remove from Home Screen",
                                    icon = Icons.Default.RemoveCircleOutline,
                                    isDestructive = true
                                ) {
                                    val slot = longPressedGridSlot
                                    if (slot != null && slot >= 0) {
                                        viewModel.removeAppFromGrid(slot)
                                    } else {
                                        homeGridApps.forEachIndexed { idx, item ->
                                            if (item?.packageName == app.packageName) {
                                                viewModel.removeAppFromGrid(idx)
                                            }
                                        }
                                    }
                                    longPressedApp = null
                                    android.widget.Toast.makeText(context, "Removed from Home Screen", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }

                            // Option 4: Hide/Unhide Application
                            MenuOptionRow(
                                label = if (isHidden) "Unhide Application" else "Hide Application",
                                icon = if (isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            ) {
                                if (isHidden) {
                                    viewModel.unhideApp(app.packageName)
                                    android.widget.Toast.makeText(context, "${app.label} unhidden", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.hideApp(app.packageName)
                                    android.widget.Toast.makeText(context, "${app.label} hidden", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                longPressedApp = null
                            }

                            // Option 5: App Info
                            MenuOptionRow(
                                label = "App Info",
                                icon = Icons.Default.Info
                            ) {
                                try {
                                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = android.net.Uri.fromParts("package", app.packageName, null)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Could not open App Info", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                longPressedApp = null
                            }

                            // Option 6: Uninstall Application
                            MenuOptionRow(
                                label = "Uninstall Application",
                                icon = Icons.Default.Delete,
                                isDestructive = true
                            ) {
                                try {
                                    val intent = Intent(Intent.ACTION_DELETE).apply {
                                        data = android.net.Uri.parse("package:${app.packageName}")
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Could not uninstall", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                longPressedApp = null
                            }
                        }
                    }
                }
            }

            if (showHiddenSpacePinDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.95f))
                        .clickable { showHiddenSpacePinDialog = false },
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
                                text = "SECURE PIN LOCK",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CmfWhite,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "ENTER 4-DIGIT SECURITY PIN",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CmfCoolGray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            // PIN indicator circles
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(4) { index ->
                                    val isFilled = index < hiddenSpaceInputPin.length
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(if (isFilled) CmfWhite else Color.Transparent)
                                            .border(1.5.dp, CmfWhite.copy(alpha = 0.4f), CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Keypad Layout (3 columns)
                            val keys = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf("C", "0", "⌫")
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                keys.forEach { rowKeys ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        rowKeys.forEach { key ->
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(CircleShape)
                                                    .background(CmfCharcoal.copy(alpha = 0.5f))
                                                    .border(1.dp, CmfWhite.copy(alpha = 0.08f), CircleShape)
                                                    .clickable {
                                                        if (key == "C") {
                                                            hiddenSpaceInputPin = ""
                                                        } else if (key == "⌫") {
                                                            if (hiddenSpaceInputPin.isNotEmpty()) {
                                                                hiddenSpaceInputPin = hiddenSpaceInputPin.dropLast(1)
                                                            }
                                                        } else {
                                                            if (hiddenSpaceInputPin.length < 4) {
                                                                hiddenSpaceInputPin += key
                                                                if (hiddenSpaceInputPin.length == 4) {
                                                                    // Verify PIN
                                                                    if (hiddenSpaceInputPin == hiddenSpacePin) {
                                                                        viewModel.setHiddenSpaceUnlocked(true)
                                                                        isViewingHiddenSpace = true
                                                                        showHiddenSpacePinDialog = false
                                                                        android.widget.Toast.makeText(context, "Hidden Space Unlocked", android.widget.Toast.LENGTH_SHORT).show()
                                                                    } else {
                                                                        android.widget.Toast.makeText(context, "Incorrect PIN", android.widget.Toast.LENGTH_SHORT).show()
                                                                        hiddenSpaceInputPin = ""
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = key,
                                                    fontSize = 16.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (key == "C" || key == "⌫") CmfOrange else CmfWhite
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Close / Cancel Text Button
                            Text(
                                text = "CANCEL",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CmfCoolGray,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { showHiddenSpacePinDialog = false }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
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
fun FolderCardItem(
    categoryName: String,
    apps: List<LauncherApp>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(1.dp, CmfWhite.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CmfCharcoal.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2x2 Mini App Icons preview
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CmfBlack.copy(alpha = 0.4f))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(4) { i ->
                        val app = apps.getOrNull(i)
                        if (app != null) {
                            if (app.icon != null) {
                                AndroidIconWrapper(drawable = app.icon, modifier = Modifier.fillMaxSize())
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(CmfCharcoal),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = app.label.take(1).uppercase(),
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CmfWhite
                                    )
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Folder Title & App count
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = categoryName.uppercase(),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CmfWhite,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "${apps.size} APPS",
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CmfCoolGray
                )
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AppIconItem(
    app: LauncherApp,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
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
            .then(
                if (onLongClick != null) {
                    Modifier.combinedClickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null,
                        onClick = { isClicked = true },
                        onLongClick = onLongClick
                    )
                } else {
                    Modifier.clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) { isClicked = true }
                }
            )
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
