package com.project.ZeeScraper.ui

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.project.ZeeScraper.data.CharacterViewModel
import com.project.ZeeScraper.log.AppLogger
import com.project.ZeeScraper.log.LogLevel
import com.project.ZeeScraper.ui.theme.Gold_S
import com.project.ZeeScraper.ui.theme.a_card

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: CharacterViewModel) {
    val characters by viewModel.characters.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var initialLoadDone by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.loadCharacters(forceRefresh = true)
        }
    )

    LaunchedEffect(Unit) {
        viewModel.loadCharacters()
    }

    LaunchedEffect(isLoading, characters) {
        if (!isLoading && characters.isNotEmpty() && !initialLoadDone) {
            initialLoadDone = true
        }

        // Reset isRefreshing when loading is complete
        if (!isLoading && isRefreshing) {
            isRefreshing = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when {
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: $error",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                characters.isEmpty() && !isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No characters available",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    val profileCardList = characters.map {
                        ProfileCard(
                            id = it.id,
                            name = it.name,
                            tier = it.tier,
                            link = it.link,
                            image = it.image,
                            element = it.element,
                            elementpict = it.element_picture
                        )
                    }

                    val cardRows = profileCardList.chunked(4)

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

        // Loading indicator hanya di center untuk initial load
        if (isLoading && !initialLoadDone) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Pull refresh indicator hanya untuk refresh action
        if (isRefreshing) {
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
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

    Card(
        modifier = modifier
            .width(180.dp)
            .aspectRatio(0.70f)
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable { onCardClick(card) }
            .shadow(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = RectangleShape
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            var isImageLoading by remember { mutableStateOf(true) }

            AsyncImage(
                model = card.image,
                contentDescription = "Gambar ${card.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor),
                onLoading = {
                    isImageLoading = true
                },
                onSuccess = {
                    isImageLoading = false
                },
                onError = {
                    isImageLoading = false
                    AppLogger.log(LogLevel.WARNING, "Char Image", "${card.name} error, Link: ${card.link}")
                }
            )

            if (isImageLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
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
                        AppLogger.log(LogLevel.WARNING, "Element Image", "${card.name} error, Link: ${card.elementpict}")
                    }
                )
            } else {
                AppLogger.log(LogLevel.WARNING, "Element Image", "${card.name} error, Link: ${card.elementpict}")
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