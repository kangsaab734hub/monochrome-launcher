package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LauncherApp(
    val label: String,
    val packageName: String,
    val className: String,
    val icon: Drawable? = null,
    val isMock: Boolean = false,
    val category: String = "Other"
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs: SharedPreferences =
        application.getSharedPreferences("monochrome_launcher_prefs", Context.MODE_PRIVATE)

    private val _apps = MutableStateFlow<List<LauncherApp>>(emptyList())
    val apps: StateFlow<List<LauncherApp>> = _apps.asStateFlow()

    private val _homeGridApps = MutableStateFlow<List<LauncherApp?>>(List(150) { null })
    val homeGridApps: StateFlow<List<LauncherApp?>> = _homeGridApps.asStateFlow()

    private val _dockApps = MutableStateFlow<List<LauncherApp>>(emptyList())
    val dockApps: StateFlow<List<LauncherApp>> = _dockApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Real-time app drawer transparency (0.0f = fully clear transparent with blur, 1.0f = solid CMF gray)
    private val _drawerTransparency = MutableStateFlow(
        sharedPrefs.getFloat("drawer_transparency", 0.35f)
    )
    val drawerTransparency: StateFlow<Float> = _drawerTransparency.asStateFlow()

    // Custom wallpaper path from user's gallery
    private val _customWallpaperPath = MutableStateFlow(
        sharedPrefs.getString("custom_wallpaper_path", null)
    )
    val customWallpaperPath: StateFlow<String?> = _customWallpaperPath.asStateFlow()

    fun setCustomWallpaperPath(path: String?) {
        _customWallpaperPath.value = path
        if (path != null) {
            sharedPrefs.edit().putString("custom_wallpaper_path", path).apply()
        } else {
            sharedPrefs.edit().remove("custom_wallpaper_path").apply()
        }
    }

    // Real-time CMF wallpaper selection (0 = Carbon Matrix, 1 = Matte Slate, 2 = Vapor Glow)
    private val _selectedWallpaper = MutableStateFlow(
        sharedPrefs.getInt("selected_wallpaper", 0)
    )
    val selectedWallpaper: StateFlow<Int> = _selectedWallpaper.asStateFlow()

    // For buttery smooth 120 FPS monitoring / display
    private val _fpsValue = MutableStateFlow(120)
    val fpsValue: StateFlow<Int> = _fpsValue.asStateFlow()

    // Real-time adjustable blur strength (0.0f to 1.0f)
    private val _blurStrength = MutableStateFlow(
        sharedPrefs.getFloat("blur_strength", 0.5f)
    )
    val blurStrength: StateFlow<Float> = _blurStrength.asStateFlow()

    // Drawer animation duration in milliseconds (e.g., 200 for fast, 350 for standard, 500 for slow)
    private val _drawerAnimationSpeed = MutableStateFlow(
        sharedPrefs.getInt("drawer_animation_speed", 300)
    )
    val drawerAnimationSpeed: StateFlow<Int> = _drawerAnimationSpeed.asStateFlow()

    // Package names of apps hidden by the user
    private val _hiddenApps = MutableStateFlow<Set<String>>(
        sharedPrefs.getStringSet("hidden_apps_pkgs", emptySet()) ?: emptySet()
    )
    val hiddenApps: StateFlow<Set<String>> = _hiddenApps.asStateFlow()

    // Custom lock/unlock PIN for Hidden Space
    private val _hiddenSpacePin = MutableStateFlow(
        sharedPrefs.getString("hidden_space_pin", "1234") ?: "1234"
    )
    val hiddenSpacePin: StateFlow<String> = _hiddenSpacePin.asStateFlow()

    // Flag to enable system/device biometric or device lock authentication
    private val _useDeviceAuth = MutableStateFlow(
        sharedPrefs.getBoolean("use_device_auth", false)
    )
    val useDeviceAuth: StateFlow<Boolean> = _useDeviceAuth.asStateFlow()

    // Lock state of the Hidden Space
    private val _isHiddenSpaceUnlocked = MutableStateFlow(false)
    val isHiddenSpaceUnlocked: StateFlow<Boolean> = _isHiddenSpaceUnlocked.asStateFlow()

    private val _showClockWidget = MutableStateFlow(
        sharedPrefs.getBoolean("show_clock_widget", true)
    )
    val showClockWidget: StateFlow<Boolean> = _showClockWidget.asStateFlow()

    private val _clockAlignment = MutableStateFlow(
        sharedPrefs.getInt("clock_alignment", 0) // 0 = Left/Start, 1 = Center, 2 = Right/End
    )
    val clockAlignment: StateFlow<Int> = _clockAlignment.asStateFlow()

    private val _drawerStyle = MutableStateFlow(
        sharedPrefs.getInt("drawer_style", 0) // 0 = Classic, 1 = Folder Style
    )
    val drawerStyle: StateFlow<Int> = _drawerStyle.asStateFlow()

    fun setDrawerStyle(style: Int) {
        _drawerStyle.value = style
        sharedPrefs.edit().putInt("drawer_style", style).apply()
    }

    // Filtered apps for the App Drawer (hides hidden apps unless specifically searching or viewing hidden space)
    val filteredApps: StateFlow<List<LauncherApp>> = combine(
        _apps, _searchQuery, _hiddenApps, _isHiddenSpaceUnlocked
    ) { appList, query, hidden, unlocked ->
        if (query.isBlank()) {
            appList.filter { !hidden.contains(it.packageName) }
        } else {
            // Search matches normal apps; if unlocked, can also match hidden apps
            val allowedApps = if (unlocked) appList else appList.filter { !hidden.contains(it.packageName) }
            allowedApps.filter { it.label.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setShowClockWidget(visible: Boolean) {
        _showClockWidget.value = visible
        sharedPrefs.edit().putBoolean("show_clock_widget", visible).apply()
    }

    fun setClockAlignment(alignIndex: Int) {
        _clockAlignment.value = alignIndex
        sharedPrefs.edit().putInt("clock_alignment", alignIndex).apply()
    }

    fun setBlurStrength(strength: Float) {
        _blurStrength.value = strength
        sharedPrefs.edit().putFloat("blur_strength", strength).apply()
    }

    fun setDrawerAnimationSpeed(speed: Int) {
        _drawerAnimationSpeed.value = speed
        sharedPrefs.edit().putInt("drawer_animation_speed", speed).apply()
    }

    fun hideApp(packageName: String) {
        val current = _hiddenApps.value.toMutableSet()
        current.add(packageName)
        _hiddenApps.value = current
        sharedPrefs.edit().putStringSet("hidden_apps_pkgs", current).apply()
    }

    fun unhideApp(packageName: String) {
        val current = _hiddenApps.value.toMutableSet()
        current.remove(packageName)
        _hiddenApps.value = current
        sharedPrefs.edit().putStringSet("hidden_apps_pkgs", current).apply()
    }

    fun setHiddenSpacePin(pin: String) {
        _hiddenSpacePin.value = pin
        sharedPrefs.edit().putString("hidden_space_pin", pin).apply()
    }

    fun setUseDeviceAuth(enabled: Boolean) {
        _useDeviceAuth.value = enabled
        sharedPrefs.edit().putBoolean("use_device_auth", enabled).apply()
    }

    fun setHiddenSpaceUnlocked(unlocked: Boolean) {
        _isHiddenSpaceUnlocked.value = unlocked
    }

    fun resetSettings() {
        sharedPrefs.edit()
            .putFloat("drawer_transparency", 0.35f)
            .putInt("selected_wallpaper", 0)
            .putFloat("blur_strength", 0.5f)
            .putInt("drawer_animation_speed", 300)
            .putStringSet("hidden_apps_pkgs", emptySet())
            .putString("hidden_space_pin", "1234")
            .putBoolean("use_device_auth", false)
            .putInt("drawer_style", 0)
            .remove("custom_wallpaper_path")
            .apply()

        _drawerTransparency.value = 0.35f
        _selectedWallpaper.value = 0
        _blurStrength.value = 0.5f
        _drawerAnimationSpeed.value = 300
        _hiddenApps.value = emptySet()
        _hiddenSpacePin.value = "1234"
        _useDeviceAuth.value = false
        _isHiddenSpaceUnlocked.value = false
        _drawerStyle.value = 0
        _customWallpaperPath.value = null
    }

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            val appList = withContext(Dispatchers.IO) {
                fetchInstalledAndMockApps()
            }
            _apps.value = appList
            initializeHomeAndDock(appList)
        }
    }

    private fun initializeHomeAndDock(appList: List<LauncherApp>) {
        val grid = MutableList<LauncherApp?>(150) { null }
        val dock = mutableListOf<LauncherApp>()

        // 1. Populate Fixed Dock (5 Slots)
        val defaultDockPackages = listOf(
            "com.android.dialer",
            "com.android.messaging",
            "com.android.chrome",
            "com.android.camera",
            "com.android.settings"
        )
        for (i in 0..4) {
            val savedPkg = sharedPrefs.getString("dock_slot_$i", null)
            val app = if (savedPkg != null) {
                appList.find { it.packageName == savedPkg }
            } else {
                val targetPkg = defaultDockPackages.getOrNull(i) ?: ""
                appList.find { it.packageName == targetPkg } ?: appList.find { it.packageName.contains(targetPkg.substringAfterLast(".")) }
            }
            if (app != null) {
                dock.add(app)
            } else {
                // fallback to any app not already in dock
                val fallback = appList.find { item -> !dock.any { it.packageName == item.packageName } }
                if (fallback != null) {
                    dock.add(fallback)
                }
            }
        }
        _dockApps.value = dock

        // 2. Populate 5x6 Home Grid (150 Slots across 5 pages)
        val defaultGridPlacements = mapOf(
            1 to "com.nothing.weather",
            3 to "com.nothing.recorder",
            11 to "com.google.android.youtube",
            13 to "com.google.android.apps.maps",
            17 to "com.google.android.gm",
            21 to "com.nothing.settings",
            23 to "com.android.vending"
        )
        for (i in 0..149) {
            val savedPkg = sharedPrefs.getString("grid_slot_$i", null)
            if (savedPkg == "empty") {
                grid[i] = null
            } else if (savedPkg != null) {
                grid[i] = appList.find { it.packageName == savedPkg }
            } else {
                // default first-run placement
                val targetPkg = defaultGridPlacements[i]
                if (targetPkg != null) {
                    grid[i] = appList.find { it.packageName == targetPkg }
                }
            }
        }
        _homeGridApps.value = grid
    }

    fun addAppToGrid(slotIndex: Int, app: LauncherApp) {
        if (slotIndex in 0..149) {
            val newList = _homeGridApps.value.toMutableList()
            newList[slotIndex] = app
            _homeGridApps.value = newList
            sharedPrefs.edit().putString("grid_slot_$slotIndex", app.packageName).apply()
        }
    }

    fun removeAppFromGrid(slotIndex: Int) {
        if (slotIndex in 0..149) {
            val newList = _homeGridApps.value.toMutableList()
            newList[slotIndex] = null
            _homeGridApps.value = newList
            sharedPrefs.edit().putString("grid_slot_$slotIndex", "empty").apply()
        }
    }

    fun addAppToFirstAvailableSlot(app: LauncherApp): Boolean {
        val currentGrid = _homeGridApps.value
        val emptyIndex = currentGrid.indexOfFirst { it == null }
        if (emptyIndex != -1) {
            addAppToGrid(emptyIndex, app)
            return true
        }
        return false
    }

    fun updateDockApp(slotIndex: Int, app: LauncherApp) {
        if (slotIndex in 0..4) {
            val newList = _dockApps.value.toMutableList()
            newList[slotIndex] = app
            _dockApps.value = newList
            sharedPrefs.edit().putString("dock_slot_$slotIndex", app.packageName).apply()
        }
    }

    private fun fetchInstalledAndMockApps(): List<LauncherApp> {
        val context = getApplication<Application>()
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val list = mutableListOf<LauncherApp>()

        // 1. Query the actual launcher-enabled system activities
        try {
            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
            for (info in resolveInfos) {
                val label = info.loadLabel(pm).toString()
                val packageName = info.activityInfo.packageName
                val className = info.activityInfo.name
                val icon = try {
                    info.loadIcon(pm)
                } catch (e: Exception) {
                    null
                }

                // Categorize apps intelligently (Priority 6)
                val lowerPkg = packageName.lowercase()
                val lowerLabel = label.lowercase()
                val category = when {
                    // Communication
                    lowerPkg.contains("phone") || lowerPkg.contains("contacts") || lowerPkg.contains("message") || 
                    lowerPkg.contains("messaging") || lowerPkg.contains("whatsapp") || lowerPkg.contains("telegram") || 
                    lowerPkg.contains("signal") || lowerPkg.contains("viber") || lowerPkg.contains("dialer") || 
                    lowerPkg.contains("sms") || lowerLabel.contains("phone") || lowerLabel.contains("contacts") || 
                    lowerLabel.contains("messages") || lowerLabel.contains("chat") || lowerLabel.contains("sms") ||
                    lowerLabel.contains("call") -> "Communication"

                    // Social
                    lowerPkg.contains("facebook") || lowerPkg.contains("instagram") || lowerPkg.contains("twitter") || 
                    lowerPkg.contains("snapchat") || lowerPkg.contains("tiktok") || lowerPkg.contains("reddit") || 
                    lowerPkg.contains("discord") || lowerPkg.contains("pinterest") || lowerPkg.contains("linkedin") || 
                    lowerLabel.contains("social") || lowerLabel.contains("fb") || lowerLabel.contains("insta") -> "Social"

                    // Utilities
                    lowerPkg.contains("settings") || lowerPkg.contains("files") || lowerPkg.contains("clock") || 
                    lowerPkg.contains("calculator") || lowerPkg.contains("weather") || lowerPkg.contains("deskclock") || 
                    lowerPkg.contains("filemanager") || lowerPkg.contains("download") || lowerPkg.contains("browser") ||
                    lowerLabel.contains("settings") || lowerLabel.contains("files") || lowerLabel.contains("clock") || 
                    lowerLabel.contains("calculator") || lowerLabel.contains("weather") || lowerLabel.contains("browser") ||
                    lowerLabel.contains("manager") -> "Utilities"

                    // Media
                    lowerPkg.contains("camera") || lowerPkg.contains("gallery") || lowerPkg.contains("vn") || 
                    lowerPkg.contains("capcut") || lowerPkg.contains("snapseed") || lowerPkg.contains("recorder") || 
                    lowerPkg.contains("editor") || lowerLabel.contains("camera") || lowerLabel.contains("gallery") || 
                    lowerLabel.contains("photos") || lowerLabel.contains("recorder") || lowerLabel.contains("editor") ||
                    lowerLabel.contains("sound") || lowerLabel.contains("voice") -> "Media"

                    // Entertainment
                    lowerPkg.contains("youtube") || lowerPkg.contains("netflix") || lowerPkg.contains("spotify") || 
                    lowerPkg.contains("player") || lowerPkg.contains("music") || lowerPkg.contains("video") ||
                    lowerPkg.contains("twitch") || lowerPkg.contains("prime") || lowerLabel.contains("music") || 
                    lowerLabel.contains("video") || lowerLabel.contains("tv") || lowerLabel.contains("stream") -> "Entertainment"

                    // Tools
                    lowerPkg.contains("system") || lowerPkg.contains("android") || lowerPkg.contains("widget") || 
                    lowerPkg.contains("developer") || lowerPkg.contains("assistant") || lowerPkg.contains("search") || 
                    lowerLabel.contains("tools") || lowerLabel.contains("search") || lowerLabel.contains("system") || 
                    lowerLabel.contains("compass") || lowerLabel.contains("scanner") -> "Tools"

                    // Productivity
                    lowerPkg.contains("calendar") || lowerPkg.contains("drive") || lowerPkg.contains("gmail") || 
                    lowerPkg.contains("keep") || lowerPkg.contains("translate") || lowerPkg.contains("android.apps") ||
                    lowerPkg.contains("notes") || lowerPkg.contains("doc") || lowerPkg.contains("sheet") || 
                    lowerPkg.contains("office") || lowerLabel.contains("calendar") || lowerLabel.contains("mail") ||
                    lowerLabel.contains("notes") || lowerLabel.contains("translate") || lowerLabel.contains("office") -> "Productivity"

                    // Finance
                    lowerPkg.contains("wallet") || lowerPkg.contains("pay") || lowerPkg.contains("bank") || 
                    lowerPkg.contains("paypal") || lowerPkg.contains("finance") || lowerPkg.contains("stock") || 
                    lowerPkg.contains("cash") || lowerLabel.contains("wallet") || lowerLabel.contains("pay") || 
                    lowerLabel.contains("bank") || lowerLabel.contains("finance") || lowerLabel.contains("card") -> "Finance"

                    // Shopping
                    lowerPkg.contains("amazon") || lowerPkg.contains("ebay") || lowerPkg.contains("shop") || 
                    lowerPkg.contains("store") || lowerPkg.contains("cart") || lowerPkg.contains("checkout") || 
                    lowerLabel.contains("shop") || lowerLabel.contains("store") || lowerLabel.contains("cart") || 
                    lowerLabel.contains("amazon") -> "Shopping"

                    // Games
                    lowerPkg.contains("game") || lowerPkg.contains("playgames") || lowerPkg.contains("pubg") || 
                    lowerPkg.contains("roblox") || lowerPkg.contains("minecraft") || lowerPkg.contains("solitaire") || 
                    lowerLabel.contains("game") || lowerLabel.contains("puzzle") || lowerLabel.contains("play") || 
                    lowerLabel.contains("games") -> "Games"

                    else -> "Tools"
                }

                // Exclude the launcher itself so it doesn't show as a normal icon (Task 2)
                if (packageName != context.packageName) {
                    list.add(
                        LauncherApp(
                            label = label,
                            packageName = packageName,
                            className = className,
                            icon = icon,
                            isMock = false,
                            category = category
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list.sortedWith(compareBy<LauncherApp> { it.category }.thenBy { it.label.lowercase() })
    }

    fun launchApp(context: Context, app: LauncherApp) {
        if (app.isMock) {
            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    showDemoLaunch(context, app)
                }
            } else {
                showDemoLaunch(context, app)
            }
        } else {
            val intent = Intent().apply {
                setClassName(app.packageName, app.className)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                val fallbackIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (fallbackIntent != null) {
                    context.startActivity(fallbackIntent)
                } else {
                    showDemoLaunch(context, app)
                }
            }
        }
    }

    private fun showDemoLaunch(context: Context, app: LauncherApp) {
        android.widget.Toast.makeText(
            context,
            "Launching: ${app.label} (${app.packageName})",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDrawerTransparency(transparency: Float) {
        _drawerTransparency.value = transparency
        sharedPrefs.edit().putFloat("drawer_transparency", transparency).apply()
    }

    fun setSelectedWallpaper(wallpaperIndex: Int) {
        _selectedWallpaper.value = wallpaperIndex
        sharedPrefs.edit().putInt("selected_wallpaper", wallpaperIndex).apply()
    }

    fun updateFps(fps: Int) {
        _fpsValue.value = fps
    }
}
