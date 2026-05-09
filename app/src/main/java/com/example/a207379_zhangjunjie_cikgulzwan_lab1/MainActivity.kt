package com.example.a207379_zhangjunjie_cikgulzwan_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a207379_zhangjunjie_cikgulzwan_lab1.ui.theme.AppTheme

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

/**
 * [要求 2] 路由定义 (Route Definitions) [cite: 55]
 */
object Routes {
    const val PAGE_HOME = "home_screen"
    const val PAGE_BOOKINGS = "bookings_screen"
    const val PAGE_FAVOURITES = "fav_screen"
    const val PAGE_PROFILE = "profile_screen"
    const val PAGE_EDIT_PROFILE = "edit_profile_screen"
}

@Composable
fun MainAppContainer() {
    val navController = rememberNavController()
    val foodViewModel: FoodViewModel = viewModel()

    Scaffold(
        bottomBar = {
            // 底部导航栏逻辑 (Bottom Navigation Logic)
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
                BottomNavItem("My Bookings", Icons.Default.List, onClick = { navController.navigate(Routes.PAGE_BOOKINGS) })
                BottomNavItem("Favourites", Icons.Default.FavoriteBorder, onClick = { navController.navigate(Routes.PAGE_FAVOURITES) })
                BottomNavItem("Profile", Icons.Default.Person, onClick = { navController.navigate(Routes.PAGE_PROFILE) })
            }
        }
    ) { innerPadding ->
        /**
         * [要求 2] 导航主机 (NavHost) [cite: 36]
         */
        NavHost(
            navController = navController,
            startDestination = Routes.PAGE_HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.PAGE_HOME) { FoodHomeScreen(navController, foodViewModel) }
            composable(Routes.PAGE_BOOKINGS) { BookingsScreen(navController) }
            composable(Routes.PAGE_FAVOURITES) { FavouritesScreen(navController, foodViewModel) }
            composable(Routes.PAGE_PROFILE) { ProfileScreen(foodViewModel, navController) }
            composable(Routes.PAGE_EDIT_PROFILE) { EditProfileScreen(foodViewModel, navController) }
        }
    }
}

// --- 界面定义 (Screen Definitions) ---

@Composable
fun FoodHomeScreen(navController: NavHostController, viewModel: FoodViewModel) {
    var addressInput by rememberSaveable { mutableStateOf("") }
    var displayedAddress by rememberSaveable { mutableStateOf("FTSM, UKM Bangi") }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).statusBarsPadding().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Chosen location", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    Text(displayedAddress, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader("Change Location")
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(value = addressInput, onValueChange = { addressInput = it }, label = { Text("New Address") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { if (addressInput.isNotBlank()) displayedAddress = addressInput }, modifier = Modifier.align(Alignment.End)) {
                        Text("Confirm")
                    }
                }
            }

            // --- 食物类别展示 (Food Categories) ---
            SectionHeader("Western Food")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("Fuel Shack", "RM 18.50", R.drawable.wr1, viewModel)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("TDG Gourmet", "RM 22.00", R.drawable.wr2, viewModel)
            }

            SectionHeader("Fast Food")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("KFC Mystery", "RM 19.90", R.drawable.kfc, viewModel)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("McD Mystery", "RM 19.90", R.drawable.mcd, viewModel)
            }

            SectionHeader("Mamak")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("Nasi Kandar", "RM 15.00", R.drawable.nk, viewModel)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("Nasi Goreng", "RM 12.00", R.drawable.ng, viewModel)
            }

            SectionHeader("Chinese Food")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SimpleFoodCard("Dragon-i", "RM 25.00", R.drawable.dgi, viewModel)
                Spacer(modifier = Modifier.width(12.dp))
                SimpleFoodCard("Din Tai Fung", "RM 28.00", R.drawable.dtf, viewModel)
            }
        }
    }
}

@Composable
fun BookingsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        SectionHeader("My Bookings")
        Text(
            "You don't have any bookings yet.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FavouritesScreen(navController: NavHostController, viewModel: FoodViewModel) {
    val user = viewModel.userProfile.value
    val favorites = viewModel.favorites

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            SectionHeader("My Favourites")

            if (favorites.isEmpty()) {
                Text("Your saved items will appear here.", fontSize = 14.sp)
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    favorites.forEach { item ->
                        SimpleFoodCard(
                            name = item.name,
                            price = item.price,
                            imageRes = item.imageRes,
                            viewModel = viewModel
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Card(
            modifier = Modifier.align(Alignment.TopEnd),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "User: ${user.username}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "ID: ${user.userId}", fontSize = 10.sp)
            }
        }
    }
}

// --- 新版个人主页 ---
@Composable
fun ProfileScreen(
    viewModel: FoodViewModel,
    navController: NavHostController
) {
    val user = viewModel.userProfile.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 头像（不可修改）
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar",
            modifier = Modifier.size(110.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 用户名居中显示
        Text(
            text = user.username,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // UserID居中显示（无标签）
        Text(
            text = user.userId,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 信息卡片（简约圆角）
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

        Spacer(modifier = Modifier.weight(1f))

        // 编辑按钮
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

// 个人信息行组件
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

// --- 第五界面：编辑个人资料 ---
@Composable
fun EditProfileScreen(
    viewModel: FoodViewModel,
    navController: NavHostController
) {
    val user = viewModel.userProfile.value

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

        // 头像（不可修改）
        Icon(
            Icons.Default.Person,
            "Avatar",
            modifier = Modifier.size(90.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text("Avatar cannot be changed", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        // 编辑表单
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

                // 性别选择
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

        Spacer(modifier = Modifier.weight(1f))

        // 保存按钮
        Button(
            onClick = {
                val ageInt = age.toIntOrNull() ?: user.age
                viewModel.updateUserProfile(username, ageInt, gender, residence)
                navController.popBackStack()
            },
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Save Changes")
        }
    }
}

// --- 带爱心收藏的食物卡片 ---
@Composable
fun SimpleFoodCard(
    name: String,
    price: String,
    imageRes: Int,
    viewModel: FoodViewModel
) {
    val currentItem = FoodItem(name, price, imageRes)
    val isFav = viewModel.isFavorite(currentItem)
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .width(200.dp)
            .animateContentSize()
            .clickable { expanded = !expanded }
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
                if (expanded) Text("Pick up before 9 PM!", fontSize = 11.sp)
            }
        }
    }
}

// --- 基础组件 ---
@Composable
fun SectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 12.dp))
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(8.dp)) {
        Icon(icon, label, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}