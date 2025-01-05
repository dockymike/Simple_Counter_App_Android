package com.example.counter_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


import com.google.android.gms.ads.MobileAds

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}

        setContent {
            val navController = rememberNavController()
            val savedCounters = remember { mutableStateListOf<Pair<String, Int>>() }
            var isVibrationEnabled by remember { mutableStateOf(true) }
            var isSoundEnabled by remember { mutableStateOf(true) }

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(navController)
                }

                composable(
                    route = "counterScreen/{initialCounter}/{initialName}/{index}",
                    arguments = listOf(
                        navArgument("initialCounter") { type = NavType.IntType; defaultValue = 0 },
                        navArgument("initialName") { type = NavType.StringType; defaultValue = "" },
                        navArgument("index") { type = NavType.IntType; defaultValue = -1 }
                    )
                ) { backStackEntry ->
                    val initialCounter = backStackEntry.arguments?.getInt("initialCounter") ?: 0
                    val initialName = backStackEntry.arguments?.getString("initialName") ?: ""
                    val index = backStackEntry.arguments?.getInt("index") ?: -1

                    CounterScreen(
                        navController = navController,
                        savedCounters = savedCounters,
                        initialCounter = initialCounter,
                        initialName = initialName,
                        index = index,
                        isVibrationEnabled = isVibrationEnabled,
                        isSoundEnabled = isSoundEnabled
                    )
                }

                composable("savedCountersScreen") {
                    SavedCountersScreen(navController, savedCounters)
                }

                composable("settingsScreen") {
                    SettingsScreen(
                        navController = navController,
                        isVibrationEnabled = isVibrationEnabled,
                        isSoundEnabled = isSoundEnabled,
                        onVibrationChange = { isVibrationEnabled = it },
                        onSoundChange = { isSoundEnabled = it }
                    )
                }
            }
        }
    }
}

@Composable
fun AdBanner(adUnitId: String) {
    AndroidView(
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER) // Keeping your original setAdSize
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
                adListener = object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e("AdBanner", "Ad failed to load: ${adError.message}")
                    }

                    override fun onAdLoaded() {
                        Log.d("AdBanner", "Ad loaded successfully.")
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}




@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Banner Ad
        AdBanner(adUnitId = "ca-app-pub-2728273617584298/3431073612")

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Replace the logo with a text title
                Text(
                    text = "Simple Counter",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { navController.navigate("counterScreen/0//-1") }) {
                    Text("Start New Counter")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate("savedCountersScreen") }) {
                    Text("View Saved Counters")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate("settingsScreen") }) {
                    Text("Settings")
                }
            }
        }

        // Bottom Banner Ad
        AdBanner(adUnitId = "ca-app-pub-2728273617584298/3431073612")
    }
}



@Composable
fun SettingsScreen(
    navController: NavHostController,
    isVibrationEnabled: Boolean,
    isSoundEnabled: Boolean,
    onVibrationChange: (Boolean) -> Unit,
    onSoundChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp), // Reserve space for the bottom ad
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Banner Ad
            AdBanner(adUnitId = "ca-app-pub-2728273617584298/3431073612")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Toggle for Vibrations
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Toggle Vibrations",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isVibrationEnabled,
                        onCheckedChange = onVibrationChange
                    )
                }

                // Toggle for Sounds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Toggle Sounds",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isSoundEnabled,
                        onCheckedChange = onSoundChange
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { navController.navigate("home") }) {
                    Text("Back to Home")
                }
            }
        }

        // Bottom Banner Ad
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            AdBanner(adUnitId = "ca-app-pub-2728273617584298/3431073612")
        }
    }
}




@Composable
fun CounterScreen(
    navController: NavHostController,
    savedCounters: MutableList<Pair<String, Int>>,
    initialCounter: Int = 0,
    initialName: String = "",
    index: Int = -1, // Pass the index directly
    isVibrationEnabled: Boolean,
    isSoundEnabled: Boolean
) {
    val context = LocalContext.current // Obtain the context here
    var counter by remember { mutableStateOf(initialCounter) }
    var name by remember { mutableStateOf(initialName) } // Initialize with initialName

    // Function to play sound and vibrate
    fun playSoundAndVibrate(soundRes: Int, vibrationPattern: LongArray) {
        // Play sound if enabled
        if (isSoundEnabled) {
            val mediaPlayer = MediaPlayer.create(context, soundRes)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { it.release() }
        }

        // Vibrate if enabled
        if (isVibrationEnabled) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(vibrationPattern, -1)
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(vibrationPattern, -1)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp) // Reserve space for the bottom ad
        ) {
            // Top Banner Ad
            AdBanner(adUnitId = "ca-app-pub-2728273617584298/3431073612")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "$counter",
                    fontSize = 150.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color(0xFF00C853), shape = CircleShape)
                        .clickable {
                            counter++
                            playSoundAndVibrate(
                                R.raw.add_sound, // Replace with your add sound file
                                longArrayOf(0, 100) // Single short vibration
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+",
                        fontSize = 100.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .border(2.dp, Color(0xFFFF5252), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "-",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5252),
                            modifier = Modifier.clickable {
                                counter--
                                playSoundAndVibrate(
                                    R.raw.subtract_sound,
                                    longArrayOf(0, 200, 100, 200)
                                )
                            }
                        )
                    }

                    Text(
                        "Reset",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5252),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                counter = 0
                                playSoundAndVibrate(
                                    R.raw.reset_sound,
                                    longArrayOf(0, 3000)
                                )
                            }
                    )
                }

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Enter a name") },
                    placeholder = { Text("Save as...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (name.isNotEmpty()) {
                            if (index >= 0) {
                                savedCounters[index] = Pair(name, counter)
                            } else {
                                savedCounters.add(Pair(name, counter))
                            }
                            navController.navigate("savedCountersScreen") {
                                popUpTo("savedCountersScreen") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(if (index >= 0) "Update Counter" else "Save Counter")
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Button(onClick = { navController.navigate("savedCountersScreen") }) {
                        Text("View Saved")
                    }

                    Button(onClick = { navController.navigate("home") }) {
                        Text("Home")
                    }
                }
            }
        }

        // Bottom Banner Ad
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            AdBanner(adUnitId = "ca-app-pub-2728273617584298/3431073612")
        }
    }
}







@Composable
fun ModernCounterAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF00C853),
            onPrimary = Color.White,
            secondary = Color(0xFFFF5252),
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            onSurface = Color.Black
        ),
        typography = Typography().copy(
            bodyLarge = Typography().bodyLarge.copy(fontFamily = FontFamily.SansSerif)
        ),
        content = content
    )
}
