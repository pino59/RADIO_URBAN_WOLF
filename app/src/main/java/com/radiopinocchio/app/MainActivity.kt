package com.radiopinocchio.app

import androidx.media3.common.MediaItem
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class MainActivity : ComponentActivity() {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController by mutableStateOf<MediaController?>(null)

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Collegamento al RadioService tramite Media3 MediaController
        val sessionToken = SessionToken(this, ComponentName(this, RadioService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
        }, MoreExecutors.directExecutor())

        setContent {
            var isPlaying by remember { mutableStateOf(false) }
            var songTitle by remember { mutableStateOf("Caricamento metadati...") }

            // Listener per ascoltare i cambi di stato e i metadati AzuraCast in tempo reale
            DisposableEffect(mediaController) {
                val controller = mediaController
                val listener = object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        isPlaying = playing
                    }

                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        val title = mediaMetadata.title?.toString()
                        val artist = mediaMetadata.artist?.toString()

                        songTitle = when {
                            !title.isNullOrEmpty() && !artist.isNullOrEmpty() -> "$artist - $title"
                            !title.isNullOrEmpty() -> title
                            !artist.isNullOrEmpty() -> artist
                            else -> "Radio Urban Wolf - In Onda"
                        }
                    }
                }

                controller?.addListener(listener)
                isPlaying = controller?.isPlaying == true

                onDispose {
                    controller?.removeListener(listener)
                }
            }

            // Palette di colori Urban Wolf
            val backgroundColor = Color(0xFF0B0E14)
            val neonPurple = Color(0xFF9D4EDD)
            val neonCyan = Color(0xFF00F5D4)
            val electricPink = Color(0xFFFF007F)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A1229), backgroundColor)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {

                    // --- LOGO RADIO ---
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.sweepGradient(
                                    colors = listOf(neonPurple, neonCyan, neonPurple)
                                )
                            )
                            .padding(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.radiourbanwolf),
                                contentDescription = "Radio Urban Wolf Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .width(90.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                        ) {
                            Text(
                                text = "RUW",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- TITOLO RADIO ---
                    Text(
                        text = "RADIO URBAN WOLF",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The Voice of the Wolf • Live",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- METADATI SCORREVOLI AZURACAST (MARQUEE) ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = if (isPlaying) songTitle else "Premi Play per ascoltare la diretta",
                            fontSize = 15.sp,
                            color = if (isPlaying) neonCyan else Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .basicMarquee(
                                    iterations = Int.MAX_VALUE,
                                    velocity = 40.dp
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- PULSANTE RIPRODUZIONE ---
                    Button(
                        onClick = {
                            val controller = mediaController
                            if (controller != null) {
                                if (controller.isPlaying) {
                                    controller.pause()
                                } else {
                                    // Se la playlist è vuota, assegna l'URL
                                    if (controller.mediaItemCount == 0) {
                                        val mediaItem = MediaItem.fromUri("https://urbanwolf.zapto.org/listen/radiourbanwolf/radio.mp3")
                                        controller.setMediaItem(mediaItem)
                                    }
                                    controller.prepare()
                                    controller.play()
                                }
                            } else {
                                // Se il controller non è ancora pronto, avvia il servizio
                                val intent = Intent(this@MainActivity, RadioService::class.java)
                                startService(intent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .height(64.dp)
                            .width(240.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (isPlaying) {
                                        listOf(electricPink, neonPurple)
                                    } else {
                                        listOf(neonPurple, neonCyan)
                                    }
                                )
                            )
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(32.dp)
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isPlaying) "STOP HOWL" else "PLAY HOWL",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onDestroy()
    }
}