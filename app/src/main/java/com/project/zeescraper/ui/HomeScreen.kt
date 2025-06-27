package com.project.zeescraper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.project.zeescraper.data.CharacterViewModel
import com.project.zeescraper.log.AppLogger
import com.project.zeescraper.log.LogLevel
import com.project.zeescraper.ui.theme.Gold_S
import com.project.zeescraper.ui.theme.a_card
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: CharacterViewModel) {
    val characters by viewModel.characters.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.loadCharacters(forceRefresh = true)
        }
    )

    val showContent = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        if (characters.isEmpty()) {
            viewModel.loadCharacters()
        }
        showContent.value = true
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) isRefreshing = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when {
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }
                characters.isEmpty() && !isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No characters available")
                }
                showContent.value -> {
                    val cardRows by remember(characters) {
                        mutableStateOf(
                            characters.map {
                                ProfileCard(
                                    id = it.id,
                                    name = it.name,
                                    tier = it.tier,
                                    link = it.link,
                                    image = it.image,
                                    element = it.element,
                                    elementpict = it.element_picture
                                )
                            }.chunked(4)
                        )
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.padding(5.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(cardRows) { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                row.forEach { card ->
                                    ProfileCardItem(
                                        card = card,
                                        modifier = Modifier.weight(1f),
                                        onCardClick = {
                                            navController.navigate("detail/${card.id}")
                                        }
                                    )
                                }
                                repeat(4 - row.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isLoading && characters.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (isRefreshing) {
            PullRefreshIndicator(
                refreshing = true,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun ProfileCardItem(
    card: ProfileCard,
    modifier: Modifier = Modifier,
    onCardClick: (ProfileCard) -> Unit
) {
    val bgColor = when (card.tier) {
        "S" -> Gold_S
        "U" -> Color.Black.copy(alpha = 0.3f)
        else -> a_card
    }

    val request = ImageRequest.Builder(LocalContext.current)
        .data(card.image)
        .size(256) // Thumbnail size for smoother loading
        .crossfade(true)
        .build()

    Card(
        modifier = modifier
            .aspectRatio(0.7f) // Changed to 1f for square shape
            .padding(4.dp)
            .clickable { onCardClick(card) },
        shape = RectangleShape, // Changed to RectangleShape for square shape
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            var isImageLoading by remember { mutableStateOf(true) }
            AsyncImage(
                model = request,
                contentDescription = "Character Image",
                modifier = Modifier.fillMaxSize().background(bgColor),
                contentScale = ContentScale.FillHeight,
                onLoading = {
                    isImageLoading = true
                },
                onSuccess = {
                    isImageLoading = false
                },
                onError = {
                    isImageLoading = false
                    AppLogger.log(
                        LogLevel.WARNING,
                        "Char Image",
                        "${card.name} error, Link: ${card.link}"
                    )
                }
            )
            if (isImageLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            if (card.elementpict != "unknown") {
                AsyncImage(
                    model = card.elementpict,
                    contentDescription = "Elemen ${card.element}",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(20.dp)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    onError = {
                        AppLogger.log(
                            LogLevel.WARNING,
                            "Element Image",
                            "${card.name} error, Link: ${card.elementpict}"
                        )
                    }
                )
            } else {
                AppLogger.log(
                    LogLevel.WARNING,
                    "Element Image",
                    "${card.name} error, Link: ${card.elementpict}"
                )
            }

            Text(
                text = card.name,
                lineHeight = 12.sp,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    shadow = Shadow(color = Color.Black, offset = Offset(2f, 2f))
                ),
                fontSize = 11.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 20.dp)
            )
        }
    }
}

data class ProfileCard(
    val id: Int,
    val name: String,
    val tier: String,
    val link: String,
    val image: String,
    val element: String,
    val elementpict: String
)
