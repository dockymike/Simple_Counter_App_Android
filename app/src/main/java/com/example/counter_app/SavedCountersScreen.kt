package com.example.counter_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController

@Composable
fun SavedCountersScreen(
    navController: NavHostController,
    savedCounters: MutableList<Pair<String, Int>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Banner Ad
        AdBanner(adUnitId = "ca-app-pub-3940256099942544/6300978111")

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Saved Counters",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (savedCounters.isEmpty()) {
                    item {
                        Text(
                            text = "No saved counters available",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    items(savedCounters.size) { index ->
                        val savedCounter = savedCounters[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${savedCounter.first}: ${savedCounter.second}")

                            Row {
                                // Open Button
                                Button(onClick = {
                                    // Updated navigation call
                                    navController.navigate("counterScreen/${savedCounter.second}/${savedCounter.first}/$index")
                                }) {
                                    Text("Open")
                                }

                                // Delete Button
                                Button(
                                    onClick = { savedCounters.removeAt(index) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate("counterScreen/0/{initialName}/-1")
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Back")
            }
        }

        // Bottom Banner Ad
        AdBanner(adUnitId = "ca-app-pub-3940256099942544/6300978111")
    }
}
