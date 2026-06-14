package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a207379_zhangjunjie_cikgulzwan_lab1.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainAppContainer()
            }
        }
    }
}

data class BookingRecord(
    val storeName: String,
    val orderTime: String,
    val pickupCode: String
)

object Routes {
    const val PAGE_HOME = "home_screen"
    const val PAGE_BOOKINGS = "bookings_screen"
    const val PAGE_FAVOURITES = "fav_screen"
    const val PAGE_PROFILE = "profile_screen"
    const val PAGE_EDIT_PROFILE = "edit_profile_screen"
    const val PAGE_CHANGE_LOCATION = "change_location_screen"
    const val PAGE_BOX_DETAIL = "box_detail_screen"
}

@Composable
fun MainAppContainer() {
    val navController = rememberNavController()
    val foodViewModel: FoodViewModel = viewModel()

    var displayedAddress by rememberSaveable { mutableStateOf("FTSM, UKM Bangi") }
    val bookingsList = remember { mutableStateListOf<BookingRecord>() }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(70.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem("Discover", Icons.Default.Share, onClick = { navController.navigate(Routes.PAGE_HOME) })
                BottomNavItem("My Bookings", Icons.AutoMirrored.Filled.List, onClick = { navController.navigate(Routes.PAGE_BOOKINGS) })
                BottomNavItem("Favourites", Icons.Default.FavoriteBorder, onClick = { navController.navigate(Routes.PAGE_FAVOURITES) })
                BottomNavItem("Profile", Icons.Default.Person, onClick = { navController.navigate(Routes.PAGE_PROFILE) })
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.PAGE_HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.PAGE_HOME) {
                FoodHomeScreen(navController, foodViewModel, displayedAddress)
            }
            // 传入 ViewModel，这样预订界面可以调用取消订单功能
            composable(Routes.PAGE_BOOKINGS) {
                BookingsScreen(bookingsList, foodViewModel)
            }
            composable(Routes.PAGE_FAVOURITES) { FavouritesScreen(navController, foodViewModel) }
            composable(Routes.PAGE_PROFILE) { ProfileScreen(foodViewModel, navController) }
            composable(Routes.PAGE_EDIT_PROFILE) { EditProfileScreen(foodViewModel, navController) }

            composable(Routes.PAGE_CHANGE_LOCATION) {
                ChangeLocationScreen(
                    currentAddress = displayedAddress,
                    onAddressConfirmed = { newAddress -> displayedAddress = newAddress },
                    navController = navController
                )
            }

            composable(
                route = "${Routes.PAGE_BOX_DETAIL}/{name}/{price}/{imageRes}",
                arguments = listOf(
                    navArgument("name") { type = NavType.StringType },
                    navArgument("price") { type = NavType.StringType },
                    navArgument("imageRes") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val price = backStackEntry.arguments?.getString("price") ?: ""
                val imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0

                MysteryBoxDetailScreen(
                    name = name,
                    price = price,
                    imageRes = imageRes,
                    navController = navController,
                    foodViewModel = foodViewModel, // 传递 ViewModel
                    onBookingConfirmed = { newBooking ->
                        bookingsList.add(0, newBooking)
                    }
                )
            }
        }
    }
}

@Composable
fun FoodHomeScreen(
    navController: NavHostController,
    viewModel: FoodViewModel,
    displayedAddress: String
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Chosen location", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                        Text(displayedAddress, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, maxLines = 1)
                    }
                }

                IconButton(onClick = { navController.navigate(Routes.PAGE_CHANGE_LOCATION) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Change Location", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader("Western Food")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("Fuel Shack", "RM 18.50", R.drawable.wr1, viewModel, navController)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("TDG Gourmet", "RM 22.00", R.drawable.wr2, viewModel, navController)
            }

            SectionHeader("Fast Food")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("KFC Mystery", "RM 19.90", R.drawable.kfc, viewModel, navController)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("McD Mystery", "RM 19.90", R.drawable.mcd, viewModel, navController)
            }

            SectionHeader("Mamak")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("Nasi Kandar", "RM 15.00", R.drawable.nk, viewModel, navController)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("Nasi Goreng", "RM 12.00", R.drawable.ng, viewModel, navController)
            }

            SectionHeader("Chinese Food")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("Dragon-i", "RM 25.00", R.drawable.dgi, viewModel, navController)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("Din Tai Fung", "RM 28.00", R.drawable.dtf, viewModel, navController)
            }
        }
    }
}

@Composable
fun MysteryBoxDetailScreen(
    name: String,
    price: String,
    imageRes: Int,
    navController: NavHostController,
    foodViewModel: FoodViewModel,
    onBookingConfirmed: (BookingRecord) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Mystery Box Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        if (imageRes != 0) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = price, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "What's in the Box?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Every box is a delicious surprise! You will get a random assortment of main dishes, sides, or desserts from $name's premium menu, saved at an incredible discount price. Pick up fresh and hot!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Please pick up your food before 9:00 PM.", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            Button(
                onClick = {
                    val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
                    val randomCode = (1..6)
                        .map { Random.nextInt(0, charPool.size) }
                        .map(charPool::get)
                        .joinToString("")

                    // 1. 同步推送到 Firebase 云数据库 [cite: 9, 21]
                    foodViewModel.pushBookingToCloud(
                        storeName = name,
                        orderTime = currentTime,
                        pickupCode = randomCode
                    )

                    // 2. 更新本地临时 UI 状态列表
                    val newBooking = BookingRecord(
                        storeName = name,
                        orderTime = currentTime,
                        pickupCode = randomCode
                    )
                    onBookingConfirmed(newBooking)

                    navController.navigate(Routes.PAGE_BOOKINGS) {
                        popUpTo(Routes.PAGE_HOME)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Book Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BookingsScreen(
    bookingsList: MutableList<BookingRecord>, // 使用 MutableList 方便执行取消移除操作
    foodViewModel: FoodViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SectionHeader("My Bookings")

        if (bookingsList.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    "You don't have any bookings yet.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                bookingsList.forEach { booking ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = booking.storeName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                // 新增：取消预订按钮 (Cancel Booking Button)
                                TextButton(
                                    onClick = {
                                        // 从 Firebase 远程云端彻底擦除，成功后在本地 UI 列表中移除 [cite: 9, 21]
                                        foodViewModel.deleteBookingFromCloud(booking.pickupCode) {
                                            bookingsList.remove(booking)
                                        }
                                    }
                                ) {
                                    Text("Cancel", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Order Time", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = booking.orderTime, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Pickup Code", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = booking.pickupCode, fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
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
fun ChangeLocationScreen(
    currentAddress: String,
    onAddressConfirmed: (String) -> Unit,
    navController: NavHostController
) {
    var addressInput by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Change Location",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Current Location: $currentAddress",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = addressInput,
            onValueChange = { addressInput = it },
            label = { Text("Enter New Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = {
                    onAddressConfirmed("FTSM, UKM Bangi")
                    navController.popBackStack()
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Reset Default")
            }

            Button(
                onClick = {
                    if (addressInput.isNotBlank()) {
                        onAddressConfirmed(addressInput)
                    }
                    navController.popBackStack()
                }
            ) {
                Text("Confirm")
            }
        }
    }
}

@Composable
fun FavouritesScreen(navController: NavHostController, viewModel: FoodViewModel) {
    val favorites by viewModel.dbFavorites.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SectionHeader("My Favourites")

        if (favorites.isEmpty()) {
            Text("Your saved items will appear here.", fontSize = 14.sp)
        } else {
            Column {
                favorites.forEach { item ->
                    SimpleFoodCard(
                        name = item.name,
                        price = item.price,
                        imageRes = item.imageRes,
                        viewModel = viewModel,
                        navController = navController
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: FoodViewModel,
    navController: NavHostController
) {
    val user by viewModel.savedProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar",
            modifier = Modifier.size(110.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user.username,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = user.userId,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileInfoRow("Username", user.username)
                ProfileInfoRow("UserID", user.userId)
                ProfileInfoRow("Age", user.age.toString())
                ProfileInfoRow("Gender", user.gender)
                ProfileInfoRow("Residence", user.residence)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { navController.navigate(Routes.PAGE_EDIT_PROFILE) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Edit Profile", Modifier.padding(4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value)
    }
}

@Composable
fun EditProfileScreen(
    viewModel: FoodViewModel,
    navController: NavHostController
) {
    val user by viewModel.savedProfile.collectAsState()

    var username by rememberSaveable { mutableStateOf(user.username) }
    var age by rememberSaveable { mutableStateOf(user.age.toString()) }
    var gender by rememberSaveable { mutableStateOf(user.gender) }
    var residence by rememberSaveable { mutableStateOf(user.residence) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Edit Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Icon(
            Icons.Default.Person,
            "Avatar",
            modifier = Modifier.size(90.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text("Avatar cannot be changed", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(Modifier.fillMaxWidth(), MaterialTheme.shapes.medium) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Gender")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = { gender = "Male" },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                        colors = if (gender == "Male") ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Male")
                    }

                    Button(
                        onClick = { gender = "Female" },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                        colors = if (gender == "Female") ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Female")
                    }
                }

                OutlinedTextField(
                    value = residence,
                    onValueChange = { residence = it },
                    label = { Text("Residence (City, Country)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                val ageInt = age.toIntOrNull() ?: user.age
                viewModel.saveUserProfile(username, ageInt, gender, residence)
                navController.popBackStack()
            },
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun SimpleFoodCard(
    name: String,
    price: String,
    imageRes: Int,
    viewModel: FoodViewModel,
    navController: NavHostController
) {
    val currentItem = FoodItem(name, price, imageRes)
    val favorites by viewModel.dbFavorites.collectAsState()
    val isFav = favorites.any { it.name == currentItem.name }

    ElevatedCard(
        modifier = Modifier
            .width(200.dp)
            .animateContentSize()
            .clickable {
                navController.navigate("${Routes.PAGE_BOX_DETAIL}/$name/$price/$imageRes")
            }
    ) {
        Column {
            Box {
                Image(
                    painterResource(imageRes),
                    null,
                    modifier = Modifier.height(100.dp).fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { viewModel.toggleFavorite(currentItem) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null,
                        tint = if (isFav) Color.Red else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(price, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(all = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}