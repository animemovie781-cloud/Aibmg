package com.example.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.model.Episode
import com.example.ui.components.ErrorView
import com.example.ui.components.LoadingView
import com.example.ui.theme.Gold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    mediaId: String,
    onBackClick: () -> Unit,
    onPlayClick: (String, String) -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mediaId) {
        viewModel.loadDetail(mediaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is DetailUiState.Loading -> LoadingView()
                is DetailUiState.Success -> {
                    val detail = state.detail
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item {
                            Box(modifier = Modifier.height(400.dp).fillMaxWidth()) {
                                AsyncImage(
                                    model = detail.backdropUrl ?: detail.posterUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                                            )
                                        )
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = detail.title,
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = detail.releaseYear ?: "", color = Color.Gray)
                                        if (detail.rating != null) {
                                            Text(text = " • ", color = Color.Gray)
                                            Text(text = detail.rating, color = Gold, fontWeight = FontWeight.Bold)
                                        }
                                        if (detail.runtime != null) {
                                            Text(text = " • ", color = Color.Gray)
                                            Text(text = detail.runtime, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (detail.seasons == null || detail.seasons.isEmpty()) {
                                    Button(
                                        onClick = { onPlayClick(detail.id, detail.title) },
                                        modifier = Modifier.fillMaxWidth().height(56.dp).testTag("play_button"),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Play Now")
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Synopsis",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = detail.synopsis ?: "No synopsis available.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.LightGray
                                )
                                
                                if (!detail.genres.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(detail.genres) { genre ->
                                            SuggestionChip(
                                                onClick = { },
                                                label = { Text(genre) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (!detail.seasons.isNullOrEmpty()) {
                            items(detail.seasons) { season ->
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Season ${season.seasonNumber}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    season.episodes.forEach { episode ->
                                        EpisodeItem(
                                            episode = episode,
                                            onClick = { onPlayClick(episode.id, "${detail.title} - E${episode.episodeNumber}: ${episode.title}") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is DetailUiState.Error -> ErrorView(message = state.message)
            }
        }
    }
}

@Composable
fun EpisodeItem(
    episode: Episode,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(Gold, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = episode.episodeNumber.toString(), color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = episode.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray)
        }
    }
}
