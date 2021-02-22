package cz.okycelt.compose.composesurfaceview

import android.os.Bundle
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.MimeTypes
import cz.okycelt.compose.composesurfaceview.ui.theme.ComposeSurfaceViewTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSurfaceViewTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AppScreen()
                }
            }
        }
    }
}

@Composable
fun AppScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "first") {
        composable("first") {
            FirstScreen(onPlayClick = { navController.navigate("player") })
        }
        composable("player") {
            PlayerScreen()
        }
    }
}

@Composable
fun FirstScreen(onPlayClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = onPlayClick) {
            Text(text = "Play")
        }
    }
}

@Composable
fun PlayerScreen() {
    val context = AmbientContext.current
    val player = remember {
        SimpleExoPlayer.Builder(context)
            .build()
            .apply {
                playWhenReady = true
                addMediaItem(
                    MediaItem.Builder()
                        .setMimeType(MimeTypes.APPLICATION_MPD)
                        .setUri("https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd")
                        .build()
                )
                prepare()
            }
    }

    DisposableEffect("") {
        onDispose {
            player.stop()
            player.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        VideoSurface(player)
    }
}

@Composable
fun VideoSurface(player: SimpleExoPlayer) {
    AndroidView(
        viewBlock = {
            SurfaceView(it).apply {
                player.videoComponent?.setVideoSurfaceView(this)
            }
        }
    )
}