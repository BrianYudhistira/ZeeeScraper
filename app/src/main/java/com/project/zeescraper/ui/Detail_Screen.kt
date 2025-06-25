package com.project.zeescraper.ui

import android.util.Log
import android.widget.TextView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import coil.compose.SubcomposeAsyncImageContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.project.zeescraper.data.BestDiskDriveStat
import com.project.zeescraper.data.CharacterViewModel
import com.project.zeescraper.data.DiskDrive
import com.project.zeescraper.data.WEngine
import com.project.zeescraper.log.AppLogger
import com.project.zeescraper.log.LogLevel
import com.project.zeescraper.ui.theme.Gold_S
import com.project.zeescraper.ui.theme.a_card
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Detail_screen(id: Int, viewModel: CharacterViewModel, onNavigateBack: () -> Unit) {
    var selectedContent by remember { mutableStateOf("W-Engine") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val characterDetail by viewModel.character.collectAsState()

    // Load detail saat pertama kali masuk
    LaunchedEffect(id) { // Gunakan id sebagai key, bukan Unit
        Log.d("DetailScreen", "Loading character with ID: $id")
        viewModel.loadCharacterById(id)
    }

    // Pull-to-refresh
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = {
            viewModel.loadCharacterById(id, forceRefresh = true)
        }
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading && characterDetail == null -> {
                    PullRefreshIndicator(
                        refreshing = true,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error: $error",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadCharacterById(id, forceRefresh = true) }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                characterDetail != null -> {
                    val characters by viewModel.characters.collectAsState()
                    val char = characters.find { it.id == id }
                    val bgColor = when (char?.tier) {
                        "S" -> Gold_S
                        "U" -> Color.Black.copy(alpha = 0.3f)
                        else -> a_card
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                var isImageLoading by remember { mutableStateOf(true) }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(480.dp)
                                ) {
                                    // PERBAIKAN: Hapus coroutineScope yang tidak perlu
                                    FloatingActionButton(
                                        onClick = onNavigateBack, // Langsung panggil tanpa coroutine
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(10.dp)
                                            .zIndex(1f),
                                        containerColor = Color.Black.copy(alpha = 0.5f),
                                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Back To Home",
                                            tint = Color.White
                                        )
                                    }

                                    AsyncImage(
                                        model = char?.image,
                                        contentDescription = "Character Image",
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(480.dp)
                                            .background(bgColor),
                                        onLoading = {
                                            isImageLoading = true
                                        },
                                        onSuccess = {
                                            isImageLoading = false
                                        },
                                        onError = {
                                            isImageLoading = false
                                            AppLogger.log(LogLevel.WARNING, "Char Image", "${char?.name} error, Link: ${char?.image}")
                                        }
                                    )

                                    AsyncImage(
                                        model = char?.element_picture,
                                        contentDescription = "Element",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(70.dp)
                                            .align(Alignment.TopEnd)
                                            .padding(10.dp)
                                    )

                                    if (isImageLoading) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .align(Alignment.BottomCenter)
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.3f),
                                                        Color.Black.copy(alpha = 0.7f),
                                                        Color.Black
                                                    )
                                                )
                                            )
                                    )

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = 420.dp)
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = char?.name.toString(),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 29.sp,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                }
                            }

                            item {
                                NavigationButtons(
                                    selectedContent = selectedContent,
                                    onContentSelected = { selectedContent = it }
                                )
                            }

                            when (selectedContent) {
                                "W-Engine" -> {
                                    item { SectionTitle(title = "Best W-Engine") }
                                    item {
                                        WeaponList(wEngine = characterDetail!!.w_engines)
                                    }
                                }

                                "Disk Drive" -> {
                                    item { SectionTitle(title = "Best Disk Drive") }
                                    item {
                                        DiskDriveList(diskDrives = characterDetail!!.disk_drives)
                                    }
                                }

                                "Best Stat" -> {
                                    item { SectionTitle(title = "Best MainStat") }
                                    item {
                                        BestStatList(
                                            bestStat = characterDetail!!.best_disk_drive_stats,
                                            substat = characterDetail!!.substats,
                                            endgamestats = characterDetail!!.endgame_stats
                                        )
                                    }
                                }
                            }
                        }

                        // PERBAIKAN: Kondisi yang lebih tepat untuk scroll to top button
                        if (listState.firstVisibleItemIndex > 0) {
                            FloatingActionButton(
                                onClick = {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                                    .zIndex(1f),
                                containerColor = Color.Black.copy(alpha = 0.5f),
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Scroll to Top",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Pull refresh indicator untuk refresh state
            if (isLoading && characterDetail != null) {
                PullRefreshIndicator(
                    refreshing = true,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    selectedContent: String,
    onContentSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Button(
            onClick = { onContentSelected("W-Engine") },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedContent == "W-Engine")
                    MaterialTheme.colorScheme.primary
                else
                    Color.Gray
            )
        ) {
            Text("W-Engine")
        }

        Spacer(modifier = Modifier.width(5.dp))

        Button(
            onClick = { onContentSelected("Disk Drive") },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedContent == "Disk Drive")
                    MaterialTheme.colorScheme.primary
                else
                    Color.Gray
            )
        ) {
            Text("Disk Drive")
        }

        Spacer(modifier = Modifier.width(5.dp))

        Button(
            onClick = { onContentSelected("Best Stat") },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedContent == "Best Stat")
                    MaterialTheme.colorScheme.primary
                else
                    Color.Gray
            )
        ) {
            Text("Best Stat")
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
private fun WeaponList(wEngine: List<WEngine>?) {
    if (wEngine.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Black), // Added background to match the Column
            contentAlignment = Alignment.Center // Center the text
        ) {
            Text(
                text = "Upcoming Character!!!", color = Color.Red, fontSize = 20.sp
            )
        }
    }
    else{
        Column(modifier = Modifier.background(Color.Black)) {
            wEngine.forEachIndexed { index, weapon ->
                WeaponCard(
                    weapon = Weapon(
                        name = weapon.build_name,
                        baseatt = weapon.build_s,
                        substat = "PR",
                        description = weapon.detail,
                        imageUrl = weapon.w_engine_picture
                    ),
                    isLast = index < wEngine.size - 1
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun DiskDriveList(diskDrives: List<DiskDrive>?) {
    if (diskDrives.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Black), // Added background to match the Column
            contentAlignment = Alignment.Center // Center the text
        ) {
            Text(
                text = "Upcoming Character!!!", color = Color.Red, fontSize = 20.sp
            )
        }
    }
    else{
        Column(modifier = Modifier.background(Color.Black)) {
            diskDrives.forEachIndexed { index, disk ->
                DiskDriveCard(
                    disk = Disk(
                        name = disk.name,
                        link = disk.image_link,
                        detail_2pc = disk.detail_2pc,
                        detail_4pc = disk.detail_4pc
                    ),
                    isLast = index < diskDrives.size - 1
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun BestStatList(bestStat: List<BestDiskDriveStat>, substat: String, endgamestats: String){
    if (bestStat.isEmpty() && substat.isEmpty() && endgamestats.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Black), // Added background to match the Column
            contentAlignment = Alignment.Center // Center the text
        ) {
            Text(
                text = "Upcoming Character!!!", color = Color.Red, fontSize = 20.sp
            )
        }
    }
    else{
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Black), // Added background to match the Column
            contentAlignment = Alignment.Center // Center the text
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 1.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                    .background(Color.DarkGray.copy(alpha = 0.99f), shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)) {

                    bestStat.forEachIndexed { index, bestDiskDriveStat ->
                        BestDiskStatCard(
                            bestDiskDriveStat = BestStat(
                                disk_number = bestDiskDriveStat.disk_number,
                                disk_description = bestDiskDriveStat.disk_description
                            ),
                            isLast = index < bestStat.size - 1
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        SectionTitle(title = "Best SubStat")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Black)
                .padding(horizontal = 12.dp, vertical = 1.dp), // Added background to match the Column
            contentAlignment = Alignment.Center // Center the text
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                    .background(Color.DarkGray.copy(alpha = 0.99f), shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)){
                Text(
                    text = substat,
                    color = Color.White,
                    fontSize = 16.sp,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        SectionTitle(title = "Best EndGame Stats")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Black)
                .padding(horizontal = 12.dp, vertical = 1.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                    .background(Color.DarkGray.copy(alpha = 0.99f), shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            text = HtmlCompat.fromHtml(endgamestats, HtmlCompat.FROM_HTML_MODE_COMPACT)
                            setTextColor(android.graphics.Color.WHITE)
                            textSize = 16f
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

    }
}

// Data Classes
data class Weapon(
    val name: String,
    val baseatt: String,
    val substat: String,
    val description: String,
    val imageUrl: String
)

data class Disk(
    val name: String,
    val link: String,
    val detail_2pc: String,
    val detail_4pc: String,
)

data class BestStat(
    val disk_number: String,
    val disk_description: String
)

@Composable
fun BestDiskStatCard(bestDiskDriveStat: BestStat, isLast: Boolean){
    Text(
        text = "Disk Number: ${bestDiskDriveStat.disk_number}",
        color = Color.White,
        fontSize = 16.sp,
        )

    Text(
        text = "Disk Description: ${bestDiskDriveStat.disk_description}",
        color = Color.White,
        fontSize = 16.sp,
        )
    if (isLast){
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun WeaponCard(weapon: Weapon, isLast: Boolean = false) {
    var isExpanded by remember { mutableStateOf(false) }

    val imageScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.1f else 1.0f,
        label = "WeaponImageScale"
    )

    val verticalOffset by animateDpAsState(
        targetValue = if (isExpanded) 8.dp else 0.dp,
        label = "VerticalOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = verticalOffset)
            .padding(horizontal = 12.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp)
                .background(Color.DarkGray.copy(alpha = 0.99f), shape = RoundedCornerShape(16.dp))
                .clickable { isExpanded = !isExpanded }
                .animateContentSize()
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.CenterVertically)
                        .graphicsLayer(
                            scaleX = imageScale,
                            scaleY = imageScale
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = weapon.imageUrl,
                        contentDescription = "Weapon Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val state = painter.state
                        when (state) {
                            is AsyncImagePainter.State.Loading, is AsyncImagePainter.State.Empty -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            }

                            is AsyncImagePainter.State.Error -> {
                                Text(
                                    text = "Image failed",
                                    color = Color.Red,
                                    fontSize = 12.sp
                                )
                            }

                            else -> {
                                SubcomposeAsyncImageContent()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = "${weapon.name} (S5)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "Base Att (Lv 60): ${weapon.baseatt}",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Substat (Lv 60): ${weapon.substat}",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = weapon.description,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    if (isLast) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
private fun DiskDriveCard(disk: Disk, isLast: Boolean = false) {
    var isExpanded by remember { mutableStateOf(false) }

    val imageScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.1f else 1.0f,
        label = "DiskImageScale"
    )

    val verticalOffset by animateDpAsState(
        targetValue = if (isExpanded) 8.dp else 0.dp,
        label = "VerticalOffsetDisk"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = verticalOffset)
            .padding(horizontal = 12.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp)
                .background(Color.DarkGray.copy(alpha = 0.99f), shape = RoundedCornerShape(16.dp))
                .clickable { isExpanded = !isExpanded }
                .animateContentSize()
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.CenterVertically)
                        .graphicsLayer(
                            scaleX = imageScale,
                            scaleY = imageScale
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = disk.link,
                        contentDescription = "Disk Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading,
                            is AsyncImagePainter.State.Empty -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            }
                            is AsyncImagePainter.State.Error -> {
                                Text(
                                    text = "Image failed",
                                    color = Color.Red,
                                    fontSize = 10.sp
                                )
                            }
                            else -> {
                                SubcomposeAsyncImageContent()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = disk.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2-PC: ${disk.detail_2pc}",
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "4-PC: ${disk.detail_4pc}",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    if (isLast) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}