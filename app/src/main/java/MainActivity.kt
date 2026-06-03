package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.model.Phone
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.MainViewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Google AdMob SDK
        try {
            MobileAds.initialize(this) {}
        } catch (e: Exception) {
            e.printStackTrace()
        }
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Force RTL Layout Direction for beautiful Arabic rendering matching native devices
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            AdMobBanner(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                            )
                        }
                    ) { innerPadding ->
                        PhoneCompareApp(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhoneCompareApp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("استكشاف الهواتف 📱", "المقارنة القوية 🥊", "مستشار الذكاء الاصطناعي 🤖")

    val phoneDetailsToShow by viewModel.phoneDetailsToShow.collectAsState()

    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        // App Premium Header
        AppHeader()

        // Tab Navigation Bar - Styled beautifully with Geometric Balance colors & minimal border
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                if (activeTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = MaterialTheme.colorScheme.primary,
                        height = 4.dp
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)))
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Selected Content View
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                0 -> ExplorePhonesScreen(viewModel = viewModel)
                1 -> AdvancedCompareScreen(viewModel = viewModel)
                2 -> AiAdvisorScreen(viewModel = viewModel)
            }
        }
    }

    // Interactive details sheet showing all specifications and gaming statistics
    phoneDetailsToShow?.let { phone ->
        PhoneDetailsDialog(
            phone = phone,
            onDismiss = { viewModel.phoneDetailsToShow.value = null }
        )
    }
}

@Composable
fun AppHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)))
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "مقارنة الهواتف بالذكاء الاصطناعي",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.testTag("app_title")
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { /* Refresh or Settings action */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "قائمة",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExplorePhonesScreen(viewModel: MainViewModel) {
    val filteredPhones by viewModel.filteredPhones.collectAsState()
    val selectedBrand by viewModel.selectedBrand.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchLoading by viewModel.searchLoading.collectAsState()
    val searchError by viewModel.searchError.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val brands = listOf("All", "Samsung", "Huawei", "Xiaomi", "Redmi", "Poco", "Realme")
    val brandArabicNames = mapOf(
        "All" to "الكل",
        "Samsung" to "سامسونج",
        "Huawei" to "هواوي",
        "Xiaomi" to "شاومي",
        "Redmi" to "ردمي",
        "Poco" to "بوكو",
        "Realme" to "ريلمي"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & AI Generation Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            label = { Text("ابحث عن هاتف أو اكتب اسماً لتوليده بالذكاء الاصطناعي...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "مسح")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("phone_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // AI Dynamic Spec Retrieval Button (Generates specs from Gemini if offline db doesn't have it)
        Button(
            onClick = {
                keyboardController?.hide()
                viewModel.searchOnlinePhone(searchQuery)
            },
            enabled = searchQuery.trim().isNotEmpty() && !searchLoading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("ai_generate_button")
        ) {
            if (searchLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("جاري استخلاص المواصفات بالذكاء الاصطناعي...")
            } else {
                Text("توليد مواصفات هاتف جديد بالذكاء الاصطناعي ⚡")
            }
        }

        // Search errors alerting
        searchError?.let { err ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEE2E2), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, contentDescription = "خطأ", tint = Color(0xFFDC2626))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = err,
                    color = Color(0xFF991B1B),
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { viewModel.clearSearchError() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color(0xFF991B1B))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Brand Filtering Horizontal Scroll Chips
        Text(
            text = "تصفية حسب الشركة المصنعة:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            brands.forEach { brand ->
                val isSelected = selectedBrand.equals(brand, ignoreCase = true)
                val chipBg = animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    label = "chipBg"
                )
                val chipContentColor = animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                    label = "chipContentColor"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(chipBg.value)
                        .clickable { viewModel.selectedBrand.value = brand }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = brandArabicNames[brand] ?: brand,
                        color = chipContentColor.value,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resulting list of Real + AI Cached phones
        Text(
            text = "الهواتف المتاحة لعملية المقارنة (${filteredPhones.size}):",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        if (filteredPhones.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "لا يوجد هواتف",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد نتائج مطابقة لبحثك.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "اكتب اسم الهاتف واضغط على زر التوليد الذكي لإضافته!",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredPhones) { phone ->
                    PhoneGridItemCard(phone = phone, onClick = {
                        viewModel.phoneDetailsToShow.value = phone
                    })
                }
            }
        }
    }
}

@Composable
fun PhoneGridItemCard(phone: Phone, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual mockup colored circle matching PUBG frame bracket with Geometric outline
            val bracketColor = when {
                phone.pubgFps >= 90 -> Color(0xFF10B981) // Vibrant green for 90/120 FPS
                phone.pubgFps >= 60 -> Color(0xFF3B82F6) // Bright Blue for 60 FPS
                else -> Color(0xFFF59E0B) // Amber for 30/40 FPS
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bracketColor.copy(alpha = 0.12f))
                    .border(1.5.dp, bracketColor, RoundedCornerShape(16.dp))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${phone.pubgFps}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = bracketColor
                    )
                    Text(
                        text = "فريم",
                        fontSize = 9.sp,
                        color = bracketColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = phone.fullName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚙️ ${phone.cpu}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "🔋 ${phone.battery} • ⚡ ${phone.charging.substringBefore("،")}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Pricing component
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${phone.priceUsd}$",
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "تصنيف",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${phone.rating}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AdvancedCompareScreen(viewModel: MainViewModel) {
    val allPhones by viewModel.allPhones.collectAsState()
    val comparedPhone1 by viewModel.comparedPhone1.collectAsState()
    val comparedPhone2 by viewModel.comparedPhone2.collectAsState()
    val aiComparisonResult by viewModel.aiComparisonResult.collectAsState()
    val aiComparisonLoading by viewModel.aiComparisonLoading.collectAsState()

    var showDropdown1 by remember { mutableStateOf(false) }
    var showDropdown2 by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phone Selectors Side-By-Side
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dropdown Selector 1
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الهاتف الأول:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box {
                        Surface(
                            onClick = { showDropdown1 = true },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .border(
                                    1.2.dp,
                                    if (comparedPhone1 != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = comparedPhone1?.fullName ?: "اختر هاتفا...",
                                    fontWeight = if (comparedPhone1 != null) FontWeight.Bold else FontWeight.Normal,
                                    color = if (comparedPhone1 != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = if (showDropdown1) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "قائمة",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showDropdown1,
                            onDismissRequest = { showDropdown1 = false },
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .height(300.dp)
                        ) {
                            allPhones.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.fullName, fontSize = 12.sp) },
                                    onClick = {
                                        viewModel.selectComparedPhone1(p)
                                        showDropdown1 = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Dropdown Selector 2
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الهاتف الثاني:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box {
                        Surface(
                            onClick = { showDropdown2 = true },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .border(
                                    1.2.dp,
                                    if (comparedPhone2 != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = comparedPhone2?.fullName ?: "اختر هاتفا...",
                                    fontWeight = if (comparedPhone2 != null) FontWeight.Bold else FontWeight.Normal,
                                    color = if (comparedPhone2 != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = if (showDropdown2) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "قائمة",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showDropdown2,
                            onDismissRequest = { showDropdown2 = false },
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .height(300.dp)
                        ) {
                            allPhones.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.fullName, fontSize = 12.sp) },
                                    onClick = {
                                        viewModel.selectComparedPhone2(p)
                                        showDropdown2 = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Side-By-Side Spec Matrix table if both phones are loaded
        if (comparedPhone1 != null && comparedPhone2 != null) {
            val p1 = comparedPhone1!!
            val p2 = comparedPhone2!!

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "جدول مواصفات المقارنة الدقيق 📊",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Specification Rows
                        SpecCompareRow("الشركة", p1.brand, p2.brand)
                        SpecCompareRow("المعالج", p1.cpu, p2.cpu)
                        SpecCompareRow("الرام", p1.ram, p2.ram)
                        SpecCompareRow("الذاكرة", p1.storage, p2.storage)
                        SpecCompareRow("الشاشة", p1.screen, p2.screen)
                        SpecCompareRow("البطارية", p1.battery, p2.battery)
                        SpecCompareRow("الشحن", p1.charging, p2.charging)
                        SpecCompareRow("الكاميرات", p1.camera, p2.camera)
                        
                        // HIGHLIGHT PUBG Gaming Comparison row using brand specific models & styles
                        PubgCompareMatrixRow(p1.modelName, p2.modelName, p1.pubgFps, p2.pubgFps, p1.pubgGraphics, p2.pubgGraphics)

                        SpecCompareRow("السعر التقريبي", "${p1.priceUsd}$", "${p2.priceUsd}$")
                        SpecCompareRow("التقييم العام", "${p1.rating} / 10", "${p2.rating} / 10")
                    }
                }
            }

            // Action Trigger for Ai Analysis
            item {
                Button(
                    onClick = { viewModel.executeAiComparison() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "تشغيل")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حلل المقارنة بذكاء Gemini الاصطناعي 🧠", fontWeight = FontWeight.Bold)
                }
            }

            // Loading / Output generated block
            item {
                if (aiComparisonLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "جاري قراءة المعطيات الرياضية والخوارزميات لتشغيل المقارنة الأكثر دقة...",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    aiComparisonResult?.let { resultText ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "التقرير المقارن الذكي من Gemini:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = resultText,
                                    fontSize = 13.sp,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Justify
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Placeholder view
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🥊 اختر هاتفين للمقارنة",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "اختر من القائمتين بالأعلى لمقارنة المواصفات والأسعار وفريمات ببجي والعيوب والذكاء والبطارية وجهاً لوجه.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecCompareRow(label: String, val1: String, val2: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.width(70.dp)
            )

            Text(
                text = val1,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Right
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = val2,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Right
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
fun PubgCompareMatrixRow(model1: String, model2: String, fps1: Int, fps2: Int, g1: String, g2: String) {
    val bColor1 = if (fps1 >= 90) Color(0xFF10B981) else if (fps1 >= 60) Color(0xFF3B82F6) else Color(0xFFF59E0B)
    val bColor2 = if (fps2 >= 90) Color(0xFF10B981) else if (fps2 >= 60) Color(0xFF3B82F6) else Color(0xFFF59E0B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "أداء الألعاب (PUBG Mobile)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Row 1: Model 1
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = model1,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 100.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .padding(horizontal = 12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fps1 / 120f)
                            .clip(CircleShape)
                            .background(bColor1)
                    )
                }
                Text(
                    text = "$fps1 fps",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = bColor1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(50.dp)
                )
            }
            Text(
                text = "إعدادات: $g1",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 2.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Model 2
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = model2,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 100.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .padding(horizontal = 12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fps2 / 120f)
                            .clip(CircleShape)
                            .background(bColor2)
                    )
                }
                Text(
                    text = "$fps2 fps",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = bColor2,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(50.dp)
                )
            }
            Text(
                text = "إعدادات: $g2",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 2.dp, top = 2.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiAdvisorScreen(viewModel: MainViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val chatLoading by viewModel.chatLoading.collectAsState()

    var userMessageText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    // Autoscroll chat history
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val helperChips = listOf(
        "أفضل هاتف ببجي بـ 90 فريم بسعر اقتصادي 🎮",
        "تلفون متوسط بكاميرا وسلفي أسطوري 📸",
        "مقارنة سريعة: Galaxy S24 Ultra ضد Xiaomi 14 Ultra 🥊"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat List
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(chatMessages) { msg ->
                ChatMessageCard(msg = msg)
            }
            if (chatLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "مستشارك يفكر ويبحث الآن...", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick consultation helpers
        Text(
            text = "اقتراحات سريعة:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        helperChips.forEach { chipText ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE2E8F0))
                                    .clickable {
                                        viewModel.sendChatMessage(chipText.dropLast(2))
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(text = chipText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message input fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userMessageText,
                onValueChange = { userMessageText = it },
                placeholder = { Text("اكتب استفسارك هنا...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (userMessageText.isNotEmpty() && !chatLoading) {
                        viewModel.sendChatMessage(userMessageText)
                        userMessageText = ""
                        keyboardController?.hide()
                    }
                }),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text")
            )

            IconButton(
                onClick = {
                    if (userMessageText.isNotEmpty() && !chatLoading) {
                        viewModel.sendChatMessage(userMessageText)
                        userMessageText = ""
                        keyboardController?.hide()
                    }
                },
                enabled = userMessageText.isNotEmpty() && !chatLoading,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (userMessageText.isNotEmpty() && !chatLoading) MaterialTheme.colorScheme.primary else Color.Gray)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "إرسال",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatMessageCard(msg: ChatMessage) {
    val alignRight = msg.isUser
    val cardColor = if (alignRight) MaterialTheme.colorScheme.primary else Color(0xFFEEF2F6)
    val textColor = if (alignRight) MaterialTheme.colorScheme.onPrimary else Color(0xFF1E293B)
    val alignment = if (alignRight) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (alignRight) 12.dp else 2.dp,
                bottomEnd = if (alignRight) 2.dp else 12.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = msg.text,
                color = textColor,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun PhoneDetailsDialog(phone: Phone, onDismiss: () -> Unit) {
    // Elegant detail card with complete metrics breakdown
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)), RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Diagonal header view
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = phone.brand,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = phone.modelName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Gaming performance highlight
                val gamingColor = if (phone.pubgFps >= 90) Color(0xFF10B981) else if (phone.pubgFps >= 60) Color(0xFF3B82F6) else Color(0xFFF59E0B)
                Card(
                    colors = CardDefaults.cardColors(containerColor = gamingColor.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, gamingColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "معدل الإطارات في لعبة ببجي موبايل 🎮",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = gamingColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "الإعدادات المفضلة: ${phone.pubgGraphics}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Text(
                            text = "${phone.pubgFps} FPS",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = gamingColor
                        )
                    }
                }

                // Grid stats details
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { DetailEntryRow("المعالج (Processor)", phone.cpu) }
                    item { DetailEntryRow("الرام (RAM)", phone.ram) }
                    item { DetailEntryRow("التخزين (Storage)", phone.storage) }
                    item { DetailEntryRow("الشاشة (Display)", phone.screen) }
                    item { DetailEntryRow("البطارية (Battery)", phone.battery) }
                    item { DetailEntryRow("الشحن (Charging)", phone.charging) }
                    item { DetailEntryRow("الكاميرات (Cameras)", phone.camera) }
                    item { DetailEntryRow("السعر التقريبي", "${phone.priceUsd}$") }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Pros and Cons section
                Text(
                    text = "المميزات والعيوب الأساسية:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Pros
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("المميزات ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                            Spacer(modifier = Modifier.height(4.dp))
                            phone.mainPros.split("،").forEach { pr ->
                                if (pr.trim().isNotEmpty()) {
                                    Text("• ${pr.trim()}", fontSize = 10.sp, color = Color(0xFF166534))
                                }
                            }
                        }
                    }

                    // Cons
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("العيوب ❌", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                            Spacer(modifier = Modifier.height(4.dp))
                            phone.mainCons.split("،").forEach { cn ->
                                if (cn.trim().isNotEmpty()) {
                                    Text("• ${cn.trim()}", fontSize = 10.sp, color = Color(0xFF991B1B))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Overview statement summary
                Text(
                    text = phone.summary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("إغلاق المواصفات", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailEntryRow(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = desc,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
}

@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-6285042523747128/7396530477"
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { }
    )
}

