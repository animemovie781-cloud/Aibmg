package com.example.ui.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.StreamSource
import com.example.ui.components.ErrorView
import com.example.ui.components.LoadingView
import kotlin.OptIn

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    id: String,
    title: String,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedSource by viewModel.selectedSource.collectAsState()
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    LaunchedEffect(id) {
        viewModel.loadStream(id)
    }

    LaunchedEffect(selectedSource) {
        selectedSource?.let { source ->
            val mediaItem = MediaItem.fromUri(source.url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    var showQualityMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is PlayerUiState.Loading -> LoadingView()
                is PlayerUiState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    player = exoPlayer
                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top Controls Overlay
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.TopStart),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Text(
                                text = title,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                            )
                            IconButton(onClick = { showQualityMenu = true }) {
                                Icon(Icons.Default.Settings, contentDescription = "Quality", tint = Color.White)
                            }
                        }

                        if (showQualityMenu) {
                            ModalBottomSheet(
                                onDismissRequest = { showQualityMenu = false },
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Select Quality",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    state.sources.forEach { source ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .testTag("quality_item_${source.quality}")
                                                .background(
                                                    if (source == selectedSource) MaterialTheme.colorScheme.primaryContainer 
                                                    else Color.Transparent
                                                )
                                                .padding(horizontal = 16.dp)
                                                .clickable {
                                                    viewModel.selectSource(source)
                                                    showQualityMenu = false
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = source.quality,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = if (source == selectedSource) MaterialTheme.colorScheme.onPrimaryContainer
                                                        else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is PlayerUiState.Error -> ErrorView(message = state.message, onRetry = { viewModel.loadStream(id) })
            }
        }
    }
}
