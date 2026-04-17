package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207379_zhangjunjie_cikgulzwan_lab1.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // LAB 3: Entry point using custom Material 3 Theme
            AppTheme {
                FoodMagicBagScreen()
            }
        }
    }
}

@Composable
fun FoodMagicBagScreen() {
    val themeColor = MaterialTheme.colorScheme.primary
    var addressInput by remember { mutableStateOf("") }
    var displayedAddress by remember { mutableStateOf("FTSM, UKM Bangi") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- SECTION 1: TOP BAR (LOCATION HEADER) ---
        // Displays the current selected location with theme integration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(themeColor)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Chosen location",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayedAddress,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- SECTION 2: CHANGE LOCATION (LAB 2 INTERACTION) ---
            // Handles state updates for address using TextField and Button
            SectionHeader(title = "Change Location")

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("Enter your new address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (addressInput.isNotBlank()) {
                                displayedAddress = addressInput
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Confirm Update")
                    }
                }
            }

            // --- SECTION 3: CATEGORIES ---
            // Horizontal scroll implementation for food categories
            SectionHeader(title = "Categories")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                CategoryBox(name = "All", isSelected = true)
                Spacer(modifier = Modifier.width(8.dp))
                CategoryBox(name = "Western Food")
                Spacer(modifier = Modifier.width(8.dp))
                CategoryBox(name = "Fast Food")
                Spacer(modifier = Modifier.width(8.dp))
                CategoryBox(name = "Mamak")
                Spacer(modifier = Modifier.width(8.dp))
                CategoryBox(name = "Chinese Food")
            }

            // --- SECTION 4: FOOD DISPLAY (LAB 3 ANIMATION) ---
            // Category sections using expandable cards
            SectionHeader(title = "Western Food")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("Fuel Shack Mystery Bag", "RM 18.50", R.drawable.wr1)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("TDG Gourmet Blind Box", "RM 22.00", R.drawable.wr2)
            }

            SectionHeader(title = "Fast Food")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("KFC Mystery Box", "RM 19.90", R.drawable.kfc)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("McD Mystery Box", "RM 19.90", R.drawable.mcd)
            }

            SectionHeader(title = "Mamak")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("Nasi Kandar Mystery Box", "RM 15.00", R.drawable.nk)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("Nasi Goreng Mystery Box", "RM 12.00", R.drawable.ng)
            }

            SectionHeader(title = "Chinese Food")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("Dragon-i Mystery Box", "RM 25.00", R.drawable.dgi)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("Din Tai Fung Blind Box", "RM 28.00", R.drawable.dtf)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // --- SECTION 5: BOTTOM NAVIGATION BOX ---
        // Fixed navigation bar using surface variant colors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(70.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Discover", icon = Icons.Default.Share, active = true)
            BottomNavItem(label = "Browse", icon = Icons.Default.Search)
            BottomNavItem(label = "Favourites", icon = Icons.Default.FavoriteBorder)
            BottomNavItem(label = "Profile", icon = Icons.Default.Person)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Black
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, active: Boolean = false) {
    val color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(26.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
    }
}

// --- LAB 3: EXPANDABLE CARD WITH ANIMATION ---
@Composable
fun SimpleFoodCard(name: String, price: String, imageRes: Int) {
    // Local state to track expansion
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .width(220.dp)
            .padding(vertical = 8.dp)
            .animateContentSize() // Lab 3: Enables smooth size transition
            .clickable { expanded = !expanded }, // Lab 3: Toggles state on click
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = price, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)

                // Lab 3: Conditional content displayed when expanded
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Details:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Pick up your magic bag at the counter before 9 PM today!", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun CategoryBox(name: String, isSelected: Boolean = false) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = MaterialTheme.shapes.medium,
        color = bgColor
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}