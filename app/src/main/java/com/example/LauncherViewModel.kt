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

    private val _homeGridApps = MutableStateFlow<List<LauncherApp?>>(List(30) { null })
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

    // Real-time CMF wallpaper selection (0 = Carbon Matrix, 1 = Matte Slate, 2 = Vapor Glow)
    private val _selectedWallpaper = MutableStateFlow(
        sharedPrefs.getInt("selected_wallpaper", 0)
    )
    val selectedWallpaper: StateFlow<Int> = _selectedWallpaper.asStateFlow()

    // For buttery smooth 120 FPS monitoring / display
    private val _fpsValue = MutableStateFlow(120)
    val fpsValue: StateFlow<Int> = _fpsValue.asStateFlow()

    // Filtered apps for the App Drawer
    val filteredApps: StateFlow<List<LauncherApp>> = combine(_apps, _searchQuery) { appList, query ->
        if (query.isBlank()) {
            appList
        } else {
            appList.filter { it.label.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
        val grid = MutableList<LauncherApp?>(30) { null }
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

        // 2. Populate 5x6 Home Grid (30 Slots)
        val defaultGridPlacements = mapOf(
            1 to "com.nothing.weather",
            3 to "com.nothing.recorder",
            11 to "com.google.android.youtube",
            13 to "com.google.android.apps.maps",
            17 to "com.google.android.gm",
            21 to "com.nothing.settings",
            23 to "com.android.vending"
        )
        for (i in 0..29) {
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
        if (slotIndex in 0..29) {
            val newList = _homeGridApps.value.toMutableList()
            newList[slotIndex] = app
            _homeGridApps.value = newList
            sharedPrefs.edit().putString("grid_slot_$slotIndex", app.packageName).apply()
        }
    }

    fun removeAppFromGrid(slotIndex: Int) {
        if (slotIndex in 0..29) {
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

                    // Categorize for larger app folders
                    val category = when {
                        packageName.contains("google", ignoreCase = true) -> "Google"
                        packageName.contains("android", ignoreCase = true) || packageName.contains("system", ignoreCase = true) -> "System"
                        packageName.contains("chrome", ignoreCase = true) || packageName.contains("browser", ignoreCase = true) -> "Tools"
                        packageName.contains("camera", ignoreCase = true) || packageName.contains("gallery", ignoreCase = true) || packageName.contains("media", ignoreCase = true) -> "Media"
                        else -> "Apps"
                    }

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
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Supplement with high-quality mock apps if none exist (such as on blank dev systems),
        // ensuring an instantly functional, gorgeous showcase Nothing OS launcher setup.
        val defaultApps = listOf(
            Triple("Phone", "com.android.dialer", "Tools"),
            Triple("Messages", "com.android.messaging", "Social"),
            Triple("Browser", "com.android.chrome", "Tools"),
            Triple("Camera", "com.android.camera", "Media"),
            Triple("Settings", "com.android.settings", "System"),
            Triple("Nothing Center", "com.nothing.settings", "System"),
            Triple("Weather", "com.nothing.weather", "Tools"),
            Triple("Nothing Recorder", "com.nothing.recorder", "Media"),
            Triple("Google Play", "com.android.vending", "Google"),
            Triple("YouTube", "com.google.android.youtube", "Google"),
            Triple("Gmail", "com.google.android.gm", "Google"),
            Triple("Google Maps", "com.google.android.apps.maps", "Google")
        )

        val installedPackages = list.map { it.packageName }.toSet()

        for (item in defaultApps) {
            if (!installedPackages.contains(item.second)) {
                list.add(
                    LauncherApp(
                        label = item.first,
                        packageName = item.second,
                        className = "",
                        icon = null,
                        isMock = true,
                        category = item.third
                    )
                )
            }
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
