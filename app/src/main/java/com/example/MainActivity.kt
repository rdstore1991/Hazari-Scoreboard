package com.example

import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import org.json.JSONObject
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.theme.ThemePreset
import com.example.ui.theme.ThemePickerDialog
import com.example.ui.scoring.HazariScoringScreen
import com.example.ui.scoring.HazariRed
import com.example.ui.scoring.HazariGold
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.FastOutSlowInEasing
import com.example.data.Player
import com.example.data.ScoreEntry
import com.example.ui.ScoreboardViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.util.LocalStorage
import kotlinx.coroutines.launch
import kotlin.random.Random

// Custom built elegant Undo vector icon
val UndoIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Undo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color(0xFF6750A4)),
            strokeLineWidth = 2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            moveTo(3f, 7f)
            lineTo(3f, 2f)
            moveTo(3f, 7f)
            lineTo(8f, 7f)
            moveTo(3f, 7f)
            curveTo(5.62f, 4.38f, 9.62f, 3.25f, 13.5f, 4.12f)
            curveTo(17.38f, 4.99f, 20.6f, 7.74f, 22.01f, 11.41f)
            curveTo(23.42f, 15.08f, 22.81f, 19.14f, 20.39f, 22.25f)
            curveTo(17.97f, 25.36f, 14.1f, 27.08f, 10.15f, 26.81f)
        }
    }.build()

// Custom elegant Clipboard icon
val ClipboardIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Clipboard",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color(0xFF6750A4)),
            strokeLineWidth = 2.2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            moveTo(9f, 5f)
            lineTo(7f, 5f)
            curveTo(5.9f, 5f, 5f, 5.9f, 5f, 7f)
            lineTo(5f, 19f)
            curveTo(5f, 20.1f, 5.9f, 21f, 7f, 21f)
            lineTo(17f, 21f)
            curveTo(18.1f, 21f, 19f, 20.1f, 19f, 19f)
            lineTo(19f, 7f)
            curveTo(19f, 5.9f, 18.1f, 5f, 17f, 5f)
            lineTo(15f, 5f)
            moveTo(9f, 5f)
            curveTo(9f, 6.1f, 9.9f, 7f, 11f, 7f)
            lineTo(13f, 7f)
            curveTo(14.1f, 7f, 15f, 6.1f, 15f, 5f)
            moveTo(9f, 5f)
            curveTo(9f, 3.9f, 9.9f, 3f, 11f, 3f)
            lineTo(13f, 3f)
            curveTo(14.1f, 3f, 15f, 3.9f, 15f, 5f)
        }
    }.build()

// Custom History Clock Icon
val HistoryIcon: ImageVector
    get() = ImageVector.Builder(
        name = "History",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color(0xFF6750A4)),
            strokeLineWidth = 2.2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            moveTo(12f, 8f)
            lineTo(12f, 12f)
            lineTo(14.5f, 13.5f)
            moveTo(3.05f, 11f)
            curveTo(3.22f, 6.05f, 7.15f, 2f, 12f, 2f)
            curveTo(17.52f, 2f, 22f, 6.48f, 22f, 12f)
            curveTo(22f, 17.52f, 17.52f, 22f, 12f, 22f)
            curveTo(7.15f, 22f, 3.22f, 17.95f, 3.05f, 13f)
            moveTo(3f, 12f)
            lineTo(1f, 12f)
            moveTo(3f, 12f)
            lineTo(3f, 9f)
        }
    }.build()

// Custom Theme Palette Icon
val PaletteIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Palette",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color(0xFF6750A4)),
            strokeLineWidth = 2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
            curveTo(12.83f, 22f, 13.5f, 21.33f, 13.5f, 20.5f)
            curveTo(13.5f, 20.12f, 13.36f, 19.77f, 13.12f, 19.51f)
            curveTo(12.89f, 19.24f, 12.75f, 18.89f, 12.75f, 18.5f)
            curveTo(12.75f, 17.67f, 13.42f, 17f, 14.25f, 17f)
            lineTo(16f, 17f)
            curveTo(19.31f, 17f, 22f, 14.31f, 22f, 11f)
            curveTo(22f, 6.03f, 17.52f, 2f, 12f, 2f)
            close()
        }
        path(fill = SolidColor(Color(0xFF6750A4))) {
            moveTo(6.5f, 11.5f)
            curveTo(7.33f, 11.5f, 8f, 10.83f, 8f, 10f)
            curveTo(8f, 9.17f, 7.33f, 8.5f, 6.5f, 8.5f)
            curveTo(5.67f, 8.5f, 5f, 9.17f, 5f, 10f)
            curveTo(5f, 10.83f, 5.67f, 11.5f, 6.5f, 11.5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF6750A4))) {
            moveTo(9.5f, 7.5f)
            curveTo(10.33f, 7.5f, 11f, 6.83f, 11f, 6f)
            curveTo(11f, 5.17f, 10.33f, 4.5f, 9.5f, 4.5f)
            curveTo(8.67f, 4.5f, 8f, 5.17f, 8f, 6f)
            curveTo(8f, 6.83f, 8.67f, 7.5f, 9.5f, 7.5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF6750A4))) {
            moveTo(14.5f, 7.5f)
            curveTo(15.33f, 7.5f, 16f, 6.83f, 16f, 6f)
            curveTo(16f, 5.17f, 15.33f, 4.5f, 14.5f, 4.5f)
            curveTo(13.67f, 4.5f, 13f, 5.17f, 13f, 6f)
            curveTo(13f, 6.83f, 13.67f, 7.5f, 14.5f, 7.5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF6750A4))) {
            moveTo(17.5f, 11.5f)
            curveTo(18.33f, 11.5f, 19f, 10.83f, 19f, 10f)
            curveTo(19f, 9.17f, 18.33f, 8.5f, 17.5f, 8.5f)
            curveTo(16.67f, 8.5f, 16f, 9.17f, 16f, 10f)
            curveTo(16f, 10.83f, 16.67f, 11.5f, 17.5f, 11.5f)
            close()
        }
    }.build()

class MainActivity : ComponentActivity() {
    private val viewModel: ScoreboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalStorage.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            var selectedThemePreset by remember {
                mutableStateOf(
                    ThemePreset.fromId(LocalStorage.getItem("app_selected_theme", ThemePreset.ROYAL_EMERALD.id))
                )
            }

            MyApplicationTheme(themePreset = selectedThemePreset) {
                ScoreboardApp(
                    viewModel = viewModel,
                    currentTheme = selectedThemePreset,
                    onThemeChange = { newTheme ->
                        selectedThemePreset = newTheme
                        LocalStorage.setItem("app_selected_theme", newTheme.id)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreboardApp(
    viewModel: ScoreboardViewModel,
    currentTheme: ThemePreset = ThemePreset.ROYAL_EMERALD,
    onThemeChange: (ThemePreset) -> Unit = {}
) {
    val players by viewModel.playersFlow.collectAsState()
    val scoreEntries by viewModel.scoreEntriesFlow.collectAsState()
    val winner by viewModel.winnerFlow.collectAsState()
    val closestToWin by viewModel.closestToWinFlow.collectAsState()
    val lastAction by viewModel.lastActionEntry.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showResetDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showRoundsModal by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var playerToDelete by remember { mutableStateOf<Player?>(null) }
    var activeTab by remember { mutableStateOf("score") }

    // Popup notification for "পাইট্টা মাল" winner
    var showPaittaMaalPopup by remember { mutableStateOf(false) }
    var celebratedWinnerId by remember { mutableStateOf<Int?>(null) }

    // Group score entries by timestamp into completed rounds
    val completedRounds = remember(scoreEntries) {
        scoreEntries.groupBy { it.timestamp }
            .entries
            .sortedBy { it.key }
            .map { it.value }
    }
    val currentRoundNum = completedRounds.size + 1

    // Identify current leader (player scoring the highest total points)
    val currentLeader = remember(players) {
        players.filter { it.totalScore > 0 }.maxByOrNull { it.totalScore }
    }

    // Trigger celebratory popup & snackbar when someone reaches 1000 or above
    LaunchedEffect(winner) {
        val currentWinner = winner
        if (currentWinner != null && currentWinner.id != celebratedWinnerId) {
            celebratedWinnerId = currentWinner.id
            showPaittaMaalPopup = true
            snackbarHostState.showSnackbar(
                message = "🎉 অভিনন্দন! ${currentWinner.name} এখন পাইট্টা মাল! 👑 (${currentWinner.totalScore} pts)",
                duration = androidx.compose.material3.SnackbarDuration.Long
            )
        } else if (currentWinner == null) {
            celebratedWinnerId = null
            showPaittaMaalPopup = false
        }
    }

    BackHandler(enabled = activeTab != "score") {
        activeTab = "score"
    }

    if (players.isEmpty()) {
        // Player setup screen
        CreateGameSetupView(
            onCreateGame = { names ->
                viewModel.createNewGame(names) {
                    Toast.makeText(context, "Game started! Round 1 begins.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    } else if (winner != null && !showPaittaMaalPopup) {
        // Winner celebration screen (when popup is dismissed or directly celebrating)
        WinnerCelebrationScreen(
            winner = winner!!,
            players = players,
            scoreEntries = scoreEntries,
            onResetKeepNames = {
                clearDraftScores()
                viewModel.resetGame()
                Toast.makeText(context, "Scores reset for new game!", Toast.LENGTH_SHORT).show()
            },
            onResetNewGame = {
                clearDraftScores()
                viewModel.createNewGame(emptyList()) {}
            },
            onCopyToClipboard = {
                val sorted = players.sortedByDescending { it.totalScore }
                val sb = StringBuilder()
                sb.append("♣️ Hazari Result ♣️\n")
                sb.append("=========================\n")
                sb.append("👑 পাইট্টা মাল: ${winner!!.name} (${winner!!.totalScore} pts)\n")
                sorted.forEachIndexed { i, p ->
                    val prefix = when (i) {
                        0 -> "🏆 1st: "
                        1 -> "🥈 2nd: "
                        2 -> "🥉 3rd: "
                        else -> "🏅 4th: "
                    }
                    sb.append("$prefix${p.name} - ${p.totalScore} pts\n")
                }
                sb.append("=========================\n")
                sb.append("Generated by Hazari Scoreboard.")
                clipboardManager.setText(AnnotatedString(sb.toString()))
                Toast.makeText(context, "Score copied!", Toast.LENGTH_SHORT).show()
            }
        )
    } else {
        // Main Active App Scaffold without bottom navigation bar
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = ClipboardIcon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = "Hazari Scoreboard",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = (-0.5).sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Target: 1000 pts • Round $currentRoundNum",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        actions = {
                            // Active Theme Switcher Action
                            IconButton(
                                onClick = { showThemePicker = true },
                                modifier = Modifier.testTag("theme_picker_button")
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = currentTheme.primaryColor.copy(alpha = 0.18f),
                                    border = BorderStroke(1.5.dp, currentTheme.primaryColor),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = PaletteIcon,
                                            contentDescription = "Change Theme (${currentTheme.title})",
                                            tint = currentTheme.primaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            // Completed Rounds Modal Action
                            IconButton(
                                onClick = { showRoundsModal = true },
                                modifier = Modifier.testTag("completed_rounds_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = "Completed Rounds",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Undo round score action
                            if (completedRounds.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        val lastRoundNum = completedRounds.size
                                        viewModel.undoLastRound()
                                        Toast.makeText(context, "Round $lastRoundNum score reverted!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("undo_button")
                                ) {
                                    Icon(
                                        imageVector = UndoIcon,
                                        contentDescription = "Undo Last Round",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Export Scoreboard Action
                            IconButton(
                                onClick = { showExportDialog = true },
                                modifier = Modifier.testTag("export_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Export & Share",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Reset Game action
                            IconButton(
                                onClick = { showResetDialog = true },
                                modifier = Modifier.testTag("reset_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reset Options",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                HazariScoringScreen(
                    viewModel = viewModel,
                    currentTheme = currentTheme,
                    modifier = Modifier.fillMaxSize(),
                    onRoundSaved = { roundNum ->
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            val result = snackbarHostState.showSnackbar(
                                message = "Round $roundNum scores saved successfully!",
                                actionLabel = "UNDO",
                                duration = androidx.compose.material3.SnackbarDuration.Short
                            )
                            if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                viewModel.undoLastRound()
                                Toast.makeText(context, "Round $roundNum scores reverted!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            // --- Celebratory "পাইট্টা মাল" Popup Notification Dialog ---
            if (showPaittaMaalPopup && winner != null) {
                PaittaMaalCelebrationDialog(
                    winner = winner!!,
                    onDismiss = { showPaittaMaalPopup = false },
                    onPlayAgain = {
                        showPaittaMaalPopup = false
                        clearDraftScores()
                        viewModel.resetGame()
                        Toast.makeText(context, "Scores reset for new game!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // --- Remove Player Confirmation Dialog ---
            if (playerToDelete != null) {
                AlertDialog(
                    onDismissRequest = { playerToDelete = null },
                    title = { Text(text = "Remove Player?", fontWeight = FontWeight.Bold) },
                    text = { Text("Are you sure you want to remove ${playerToDelete?.name} from the scorecard? All past round scores for this player will also be removed.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                playerToDelete?.let { viewModel.deletePlayer(it) }
                                playerToDelete = null
                                Toast.makeText(context, "Player removed", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("confirm_remove_player_dialog_button")
                        ) {
                            Text("Remove")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { playerToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // --- Reset Options Dialog ---
            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = { Text(text = "Reset Game Options", fontWeight = FontWeight.Bold) },
                    text = { Text("Do you want to reset current scores to 0 or start a new game with different player names?") },
                    confirmButton = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    clearDraftScores()
                                    viewModel.resetGame()
                                    showResetDialog = false
                                    Toast.makeText(context, "Scoreboard has been reset!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth().testTag("reset_scores_confirm")
                            ) {
                                Text("Reset Scores (Make 0)")
                            }

                            OutlinedButton(
                                onClick = {
                                    clearDraftScores()
                                    viewModel.createNewGame(emptyList()) {}
                                    showResetDialog = false
                                    Toast.makeText(context, "Start new setup", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().testTag("reset_new_game_confirm")
                            ) {
                                Text("Start with New 4 Players")
                            }

                            OutlinedButton(
                                onClick = {
                                    showResetDialog = false
                                    showThemePicker = true
                                },
                                modifier = Modifier.fillMaxWidth().testTag("open_theme_picker_dialog_button")
                            ) {
                                Text("🎨 Change App Theme (${currentTheme.title})")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetDialog = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                )
            }

            // --- Theme Picker Dialog ---
            if (showThemePicker) {
                ThemePickerDialog(
                    currentTheme = currentTheme,
                    onThemeSelected = { newTheme ->
                        onThemeChange(newTheme)
                        showThemePicker = false
                        Toast.makeText(context, "Applied ${newTheme.title} theme!", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { showThemePicker = false }
                )
            }

            // --- Export Scoreboard Dialog ---
            if (showExportDialog) {
                ExportScoreboardDialog(
                    players = players,
                    scoreEntries = scoreEntries,
                    onDismiss = { showExportDialog = false }
                )
            }

            // --- Completed Rounds Modal Dialog ---
            if (showRoundsModal) {
                CompletedRoundsModal(
                    isOpen = showRoundsModal,
                    onClose = { showRoundsModal = false },
                    players = players,
                    scoreEntries = scoreEntries,
                    viewModel = viewModel
                )
            }
        }
    }
}

/**
 * Celebratory Popup Modal highlighting the title "পাইট্টা মাল"
 * when any player reaches 1000 points or above!
 */
@Composable
fun PaittaMaalCelebrationDialog(
    winner: Player,
    onDismiss: () -> Unit,
    onPlayAgain: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PaittaMaalPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PaittaMaalScale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "👑 🏆 👑",
                    fontSize = 44.sp,
                    modifier = Modifier.scale(pulseScale)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Prominently highlighted "পাইট্টা মাল" badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    border = BorderStroke(2.dp, HazariGold),
                    modifier = Modifier.scale(pulseScale)
                ) {
                    Text(
                        text = "★ পাইট্টা মাল ★",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = HazariGold,
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                        letterSpacing = 1.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🎉 অভিনন্দন!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${winner.name} হলেন এই খেলার আসল পাইট্টা মাল!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, HazariGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🔥", fontSize = 18.sp)
                        Text(
                            text = "মোট স্কোর: ${winner.totalScore} পয়েন্ট (১০০০+ লক্ষ্য পূরণ!)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = "১০০০ পয়েন্ট বা তার বেশি অর্জন করে ${winner.name} বিজয়ী হয়েছেন!",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onPlayAgain,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("paitta_maal_play_again_button")
            ) {
                Text("Play Again (পুনরায় খেলুন)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("paitta_maal_close_button")
            ) {
                Text("View Details (বিস্তারিত দেখুন)")
            }
        },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.testTag("paitta_maal_celebration_dialog")
    )
}

/**
 * Content for the "Players" tab displaying match hero banner, leader, live standings grid,
 * and a list view showing the history of all rounds played in the current session so users can track score progression.
 */
@Composable
fun PlayersTabContent(
    players: List<Player>,
    scoreEntries: List<ScoreEntry>,
    currentLeader: Player?,
    closestToWin: Player?,
    currentRoundNum: Int,
    onNavigateToScoring: () -> Unit,
    onPlayerClick: (Player) -> Unit
) {
    val completedRounds = remember(scoreEntries) {
        scoreEntries.groupBy { it.timestamp }
            .entries
            .sortedBy { it.key }
            .map { it.value }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_scoreboard_hero_1784065667443),
                        contentDescription = "Cosmic Card Suits Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.72f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "HAZARI PLAYERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Target: 1000 Points to Win • Round $currentRoundNum",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Current Leader Card (if points > 0)
        currentLeader?.let { leader ->
            item {
                val pointsRemaining = (1000 - leader.totalScore).coerceAtLeast(0)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "CURRENT LEADER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = leader.name,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${leader.totalScore}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFeatureSettings = "tnum"
                                )
                            )
                            Text(
                                text = if (pointsRemaining == 0) "Winner! (Target Met)" else "$pointsRemaining to Win",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Live Standings Grid (Player Scores)
        item {
            PlayerLeaderboardTable(
                players = players,
                closestToWin = closestToWin,
                onClickPlayer = onPlayerClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // List View Below Player Scores: History of All Rounds Played in Current Session
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("round_history_list_view"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "📜", fontSize = 16.sp)
                                Text(
                                    text = "Round History & Progression",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = if (completedRounds.isEmpty()) "No rounds played yet in this session"
                                       else "${completedRounds.size} round${if (completedRounds.size > 1) "s" else ""} played",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (completedRounds.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "${completedRounds.size} Rnds",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (completedRounds.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🎯", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "No round scores recorded yet",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Enter round scores above or in the Scoring tab to track score progression.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    } else {
                        // Cumulative running score progression calculation
                        // Table header: Round | Player Columns
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Round",
                                modifier = Modifier.weight(1.1f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            players.forEach { player ->
                                Text(
                                    text = player.name,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        val runningTotals = remember(players, completedRounds) {
                            val map = mutableMapOf<Int, Int>()
                            players.forEach { map[it.id] = 0 }
                            map
                        }
                        // Reset running totals before computing round by round
                        players.forEach { runningTotals[it.id] = 0 }

                        completedRounds.forEachIndexed { rIdx, round ->
                            val rNum = rIdx + 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (rIdx % 2 == 0) Color.Transparent
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(vertical = 7.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = HazariRed.copy(alpha = 0.12f),
                                    modifier = Modifier.weight(1.1f)
                                ) {
                                    Text(
                                        text = "R$rNum",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HazariRed,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                                players.forEach { player ->
                                    val pts = round.firstOrNull { it.playerId == player.id }?.points ?: 0
                                    val priorTotal = runningTotals[player.id] ?: 0
                                    val newTotal = priorTotal + pts
                                    runningTotals[player.id] = newTotal

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (pts > 0) "+$pts" else "$pts",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (pts > 0) MaterialTheme.colorScheme.primary else Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "($newTotal)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                thickness = 0.5.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Current Total Score Progression Summary Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    HazariGold.copy(alpha = 0.18f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                modifier = Modifier.weight(1.1f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF5D4037),
                                textAlign = TextAlign.Center
                            )
                            players.forEach { player ->
                                Text(
                                    text = "${player.totalScore}",
                                    modifier = Modifier.weight(1f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (player.totalScore >= 1000) HazariRed else Color(0xFF3E2723),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Jump to Scoring Button
        item {
            Button(
                onClick = onNavigateToScoring,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HazariRed)
            ) {
                Text("Enter Round Scores ➔", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

/**
 * Content for the "Scorecard" tab displaying completed rounds table.
 */
@Composable
fun ScorecardTabContent(
    players: List<Player>,
    scoreEntries: List<ScoreEntry>,
    onOpenRoundsModal: () -> Unit
) {
    val completedRounds = remember(scoreEntries) {
        scoreEntries.groupBy { it.timestamp }
            .entries
            .sortedBy { it.key }
            .map { it.value }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scorecard History (${completedRounds.size} Rounds)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (completedRounds.isNotEmpty()) {
                    TextButton(onClick = onOpenRoundsModal) {
                        Text("View Details", fontSize = 12.sp)
                    }
                }
            }
        }

        if (completedRounds.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📜", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No rounds recorded yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Switch to the Scoring tab to record round points.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            item {
                ScoreboardTable(
                    players = players,
                    scoreEntries = scoreEntries,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun clearDraftScores() {
    LocalStorage.removeItem("draft_s0")
    LocalStorage.removeItem("draft_s1")
    LocalStorage.removeItem("draft_s2")
    LocalStorage.removeItem("draft_s3")
}

@Composable
fun CreateGameSetupView(
    onCreateGame: (List<String>) -> Unit
) {
    var p1Name by remember { mutableStateOf(LocalStorage.getItem("setup_p1Name", "Rahim")) }
    var p2Name by remember { mutableStateOf(LocalStorage.getItem("setup_p2Name", "Karim")) }
    var p3Name by remember { mutableStateOf(LocalStorage.getItem("setup_p3Name", "Sakib")) }
    var p4Name by remember { mutableStateOf(LocalStorage.getItem("setup_p4Name", "Abir")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🃏 Hazari Scoreboard 🃏",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter 4 player names. The app will store each round score until any player reaches 1000 points.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = p1Name,
                    onValueChange = { p1Name = it; LocalStorage.setItem("setup_p1Name", it) },
                    label = { Text("Player 1") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("setup_player_1"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = p2Name,
                    onValueChange = { p2Name = it; LocalStorage.setItem("setup_p2Name", it) },
                    label = { Text("Player 2") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("setup_player_2"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = p3Name,
                    onValueChange = { p3Name = it; LocalStorage.setItem("setup_p3Name", it) },
                    label = { Text("Player 3") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("setup_player_3"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = p4Name,
                    onValueChange = { p4Name = it; LocalStorage.setItem("setup_p4Name", it) },
                    label = { Text("Player 4") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("setup_player_4"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        val names = listOf(p1Name.trim(), p2Name.trim(), p3Name.trim(), p4Name.trim())
                        if (names.all { it.isNotEmpty() }) {
                            onCreateGame(names)
                        }
                    },
                    enabled = p1Name.trim().isNotEmpty() &&
                            p2Name.trim().isNotEmpty() &&
                            p3Name.trim().isNotEmpty() &&
                            p4Name.trim().isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_game_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Start Game ➔",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Score entry card for the current sequential round.
 */
@Composable
fun RoundScoreInputCard(
    players: List<Player>,
    currentRoundNum: Int,
    onSaveRound: (List<Int>) -> Unit
) {
    var scoresMap by remember(players, currentRoundNum) {
        val initial = mutableMapOf<Int, String>()
        players.forEachIndexed { index, p ->
            val saved = LocalStorage.getItem("draft_score_p${p.id}", "")
                .ifEmpty { LocalStorage.getItem("draft_s$index", "") }
            initial[p.id] = saved
        }
        mutableStateOf(initial.toMap())
    }

    val sum = players.sumOf { scoresMap[it.id]?.toIntOrNull() ?: 0 }
    val remaining = 360 - sum
    val isValid = sum == 360 && players.size >= 2

    val context = LocalContext.current
    var validationAlertMessage by remember { mutableStateOf<String?>(null) }

    fun updateFieldAndSave(playerId: Int, originalIndex: Int, value: String) {
        val newMap = scoresMap.toMutableMap()
        newMap[playerId] = value
        scoresMap = newMap.toMap()

        LocalStorage.setItem("draft_score_p$playerId", value)
        if (originalIndex in 0..3) {
            LocalStorage.setItem("draft_s$originalIndex", value)
        }
    }

    fun validateAndSetScore(player: Player, originalIndex: Int, rawValue: String) {
        val cleanValue = rawValue.filter { it.isDigit() }.take(3)
        val intValue = cleanValue.toIntOrNull() ?: 0

        if (intValue > 360) {
            validationAlertMessage = "${player.name}'s score cannot exceed 360!"
            Toast.makeText(context, "Score cannot exceed 360!", Toast.LENGTH_SHORT).show()
            updateFieldAndSave(player.id, originalIndex, "360")
            return
        }

        updateFieldAndSave(player.id, originalIndex, cleanValue)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("round_score_input_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "ROUND $currentRoundNum",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = "Score Input",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (sum < 360) {
                        Text(
                            text = "⚠️ Needs $remaining more points to reach 360",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    } else if (sum > 360) {
                        Text(
                            text = "❌ Exceeded 360! (${sum - 360} extra points)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "✅ Exact 360 points entered! Ready to save.",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isValid) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = "Sum: $sum / 360",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val progress = (sum / 360f).coerceIn(0f, 1f)
            val indicatorColor = when {
                sum == 360 -> Color(0xFF2E7D32)
                sum > 360 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.primary
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "360-pt Round Threshold",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (sum == 360) "✓ Target Reached" else "${(progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = indicatorColor
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .testTag("round_progress_bar"),
                    color = indicatorColor,
                    trackColor = indicatorColor.copy(alpha = 0.12f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                players.chunked(2).forEachIndexed { chunkIndex, pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEachIndexed { pairIndex, player ->
                            val originalIndex = chunkIndex * 2 + pairIndex
                            val currentScore = scoresMap[player.id] ?: ""
                            val intScore = currentScore.toIntOrNull() ?: 0
                            val targetTotal = player.totalScore + intScore

                            val otherSum = players.filter { it.id != player.id }
                                .sumOf { scoresMap[it.id]?.toIntOrNull() ?: 0 }
                            val remainderForThis = (360 - otherSum).coerceIn(0, 360)
                            val canAutoFill = otherSum in 1..359 && currentScore.isEmpty()

                            Column(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = currentScore,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) validateAndSetScore(player, originalIndex, it) },
                                    label = { Text(player.name) },
                                    placeholder = { Text("0") },
                                    supportingText = {
                                        Text(
                                            text = "New: $targetTotal pts",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingIcon = {
                                        if (canAutoFill) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        validateAndSetScore(player, originalIndex, remainderForThis.toString())
                                                    }
                                                    .padding(end = 4.dp)
                                            ) {
                                                Text(
                                                    text = "+$remainderForThis",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("round_input_$originalIndex"),
                                    textStyle = TextStyle(
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                                        unfocusedLabelColor = Color(0xFF616161),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                        focusedPlaceholderColor = Color(0xFF9E9E9E),
                                        unfocusedPlaceholderColor = Color(0xFF9E9E9E)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (isValid) {
                        onSaveRound(players.map { scoresMap[it.id]?.toIntOrNull() ?: 0 })
                        players.forEachIndexed { idx, p ->
                            LocalStorage.removeItem("draft_score_p${p.id}")
                            LocalStorage.removeItem("draft_s$idx")
                        }
                        scoresMap = emptyMap()
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("round_save_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Text(
                    text = "Save Round $currentRoundNum Score ✓",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }

    if (validationAlertMessage != null) {
        AlertDialog(
            onDismissRequest = { validationAlertMessage = null },
            confirmButton = {
                TextButton(onClick = { validationAlertMessage = null }) {
                    Text("OK")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Invalid Input!", color = MaterialTheme.colorScheme.error, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(validationAlertMessage ?: "", fontSize = 14.sp)
            }
        )
    }
}

@Composable
fun PlayerLeaderboardTable(
    players: List<Player>,
    closestToWin: Player?,
    onClickPlayer: (Player) -> Unit,
    modifier: Modifier = Modifier,
    onDeletePlayer: ((Player) -> Unit)? = null
) {
    val sortedPlayers = remember(players) {
        players.sortedByDescending { it.totalScore }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Leaderboard",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Live Player Standings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                ) {
                    Text(
                        text = "Target: 1000",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (sortedPlayers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No players available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val chunkedPlayers = sortedPlayers.chunked(2)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    chunkedPlayers.forEach { rowPlayers ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowPlayers.forEach { player ->
                                val rank = sortedPlayers.indexOfFirst { it.id == player.id } + 1
                                val isPlayerClosest = player.id == closestToWin?.id
                                val pointsRemaining = (1000 - player.totalScore).coerceAtLeast(0)
                                val originalIndex = players.indexOfFirst { it.id == player.id }

                                PlayerStandingBox(
                                    player = player,
                                    rank = rank,
                                    isPlayerClosest = isPlayerClosest,
                                    pointsRemaining = pointsRemaining,
                                    originalIndex = originalIndex,
                                    onClickPlayer = onClickPlayer,
                                    onDeletePlayer = onDeletePlayer,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowPlayers.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerStandingBox(
    player: Player,
    rank: Int,
    isPlayerClosest: Boolean,
    pointsRemaining: Int,
    originalIndex: Int,
    onClickPlayer: (Player) -> Unit,
    onDeletePlayer: ((Player) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val rankBadge = when (rank) {
        1 -> "🥇 1st"
        2 -> "🥈 2nd"
        3 -> "🥉 3rd"
        else -> "🏅 #${rank}"
    }

    val (cardBorderColor, cardBgColor, rankColor) = when (rank) {
        1 -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.primary
        )
        2 -> Triple(
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        3 -> Triple(
            Color(0xFFCD7F32).copy(alpha = 0.4f),
            Color(0xFFCD7F32).copy(alpha = 0.08f),
            Color(0xFFCD7F32)
        )
        else -> Triple(
            MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClickPlayer(player) }
            .testTag("leaderboard_player_box_${player.id}")
            .testTag("leaderboard_row_player_${originalIndex}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor
        ),
        border = BorderStroke(1.dp, cardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = rankColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = rankBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = rankColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPlayerClosest) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Leader",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (onDeletePlayer != null) {
                        IconButton(
                            onClick = { onDeletePlayer(player) },
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("remove_leaderboard_player_${player.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove ${player.name}",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${player.totalScore}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = if (rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontFeatureSettings = "tnum"
                    )
                )
                Text(
                    text = "pts",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val progressFraction = (player.totalScore / 1000f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progressFraction)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (rank == 1) MaterialTheme.colorScheme.primary else rankColor)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (pointsRemaining == 0) "WINNER! 🎉" else "$pointsRemaining needed",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (pointsRemaining == 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            )
        }
    }
}

/**
 * Scoreboard Table displaying each round sequentially with individual player scores and running totals.
 */
@Composable
fun ScoreboardTable(
    players: List<Player>,
    scoreEntries: List<ScoreEntry>,
    modifier: Modifier = Modifier
) {
    val rounds = remember(scoreEntries) {
        scoreEntries.groupBy { it.timestamp }
            .entries
            .sortedBy { it.key }
            .map { it.value }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Round | P1 | P2 | P3 | P4
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Round",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                )
                players.forEachIndexed { idx, player ->
                    Text(
                        text = player.name,
                        modifier = Modifier.weight(1f).testTag("scoreboard_header_player_$idx"),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Rounds List
            rounds.forEachIndexed { roundIndex, round ->
                val roundNum = roundIndex + 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (roundIndex % 2 == 0) Color.Transparent
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)
                        )
                        .padding(vertical = 8.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "R$roundNum",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    )
                    players.forEach { player ->
                        val pts = round.firstOrNull { it.playerId == player.id }?.points ?: 0
                        Text(
                            text = "$pts",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Running Total Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    modifier = Modifier.weight(1f).testTag("scoreboard_total_label"),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                )
                players.forEachIndexed { idx, player ->
                    val totalSum = scoreEntries.filter { it.playerId == player.id }.sumOf { it.points }
                    Text(
                        text = "$totalSum",
                        modifier = Modifier.weight(1f).testTag("scoreboard_total_player_$idx"),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun WinnerCelebrationScreen(
    winner: Player,
    players: List<Player>,
    scoreEntries: List<ScoreEntry>,
    onResetKeepNames: () -> Unit,
    onResetNewGame: () -> Unit,
    onCopyToClipboard: () -> Unit
) {
    var showExportDialog by remember { mutableStateOf(false) }
    val sortedPlayers = remember(players) {
        players.sortedByDescending { it.totalScore }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ConfettiShower()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "👑 🏆 👑",
                fontSize = 64.sp,
                modifier = Modifier
                    .scale(pulseScale)
                    .padding(bottom = 12.dp)
            )

            Text(
                text = "Game Over!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.scale(pulseScale)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = winner.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prominently highlighted "পাইট্টা মাল" victory badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        border = BorderStroke(2.dp, HazariGold),
                        modifier = Modifier
                            .scale(pulseScale)
                            .testTag("winner_paitta_maal_badge")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "👑 পাইট্টা মাল 👑",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = HazariGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "🏆 HAZARI CHAMPION (1000+ PTS) 🏆",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "🎉 অভিনন্দন! ${winner.name} হলেন এই খেলার আসল পাইট্টা মাল! 🎉",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Total Score: ${winner.totalScore} pts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Final Rankings",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) Color(0x28FFFFFF) else Color(0xF2FFFFFF)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    sortedPlayers.forEachIndexed { idx, player ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${idx + 1}. ",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = player.name,
                                    fontWeight = if (player.id == winner.id) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${player.totalScore} pts",
                                fontWeight = FontWeight.Bold,
                                color = if (player.id == winner.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        if (idx < sortedPlayers.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { showExportDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_to_clipboard_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Share & Export Scoreboard", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onResetKeepNames,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("reset_keep_names_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Play Again with Same Players", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onResetNewGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("reset_new_game_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Start with New Players", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showExportDialog) {
        ExportScoreboardDialog(
            players = players,
            scoreEntries = scoreEntries,
            onDismiss = { showExportDialog = false }
        )
    }
}

private class ConfettiParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    val color: Color,
    val speedY: Float,
    val speedX: Float,
    var rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiShower() {
    val confettiColors = listOf(
        Color(0xFFFFD700),
        Color(0xFFFF4081),
        Color(0xFF00E676),
        Color(0xFF2979FF),
        Color(0xFFFF6D00),
        Color(0xFFAA00FF)
    )

    val particles = remember {
        List(50) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 1.5f - 0.5f,
                size = Random.nextFloat() * 8f + 6f,
                color = confettiColors.random(),
                speedY = Random.nextFloat() * 0.003f + 0.002f,
                speedX = (Random.nextFloat() - 0.5f) * 0.0015f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 5f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "ConfettiAnimation")
    val animatedProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ConfettiProgress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { particle ->
            particle.y += particle.speedY
            particle.x += particle.speedX
            particle.rotation += particle.rotationSpeed

            if (particle.y > 1.1f) {
                particle.y = -0.1f
                particle.x = Random.nextFloat()
            }
            if (particle.x < -0.1f) particle.x = 1.1f
            if (particle.x > 1.1f) particle.x = -0.1f

            val px = particle.x * w
            val py = particle.y * h

            rotate(particle.rotation, pivot = androidx.compose.ui.geometry.Offset(px, py)) {
                drawRect(
                    color = particle.color,
                    topLeft = androidx.compose.ui.geometry.Offset(px - particle.size / 2, py - particle.size / 2),
                    size = androidx.compose.ui.geometry.Size(particle.size, particle.size)
                )
            }
        }
    }
}

fun generateTextSummary(players: List<Player>, scoreEntries: List<ScoreEntry>): String {
    val sorted = players.sortedByDescending { it.totalScore }
    val sb = StringBuilder()
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateStr = sdf.format(Date())

    sb.append("♣️ Hazari Scoreboard Game Summary ♣️\n")
    sb.append("Exported At: $dateStr\n")
    sb.append("=========================\n\n")

    sb.append("🏆 STANDINGS:\n")
    sorted.forEachIndexed { index, player ->
        val rankText = when (index) {
            0 -> "🥇 1st (পাইট্টা মাল)"
            1 -> "🥈 2nd"
            2 -> "🥉 3rd"
            else -> "🏅 4th"
        }
        val remaining = (1000 - player.totalScore).coerceAtLeast(0)
        sb.append("$rankText. ${player.name}: ${player.totalScore} pts (Need: $remaining)\n")
    }
    sb.append("\n=========================\n\n")

    sb.append("📊 ROUND DETAILS:\n")
    val rounds = scoreEntries.groupBy { it.timestamp }
        .entries
        .sortedBy { it.key }
        .map { it.value }

    rounds.forEachIndexed { roundIndex, roundScores ->
        sb.append("Round ${roundIndex + 1}:\n")
        players.forEach { player ->
            val pts = roundScores.firstOrNull { it.playerId == player.id }?.points ?: 0
            sb.append("   - ${player.name}: $pts pts\n")
        }
        sb.append("\n")
    }

    sb.append("=========================\n")
    sb.append("Generated via Hazari Scoreboard App")
    return sb.toString()
}

fun generateJsonSummary(players: List<Player>, scoreEntries: List<ScoreEntry>): String {
    try {
        val rootObj = JSONObject()
        rootObj.put("game", "Hazari Scoreboard")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        rootObj.put("exportedAt", sdf.format(Date()))

        val playersArray = JSONArray()
        players.sortedByDescending { it.totalScore }.forEachIndexed { index, player ->
            val pObj = JSONObject()
            pObj.put("name", player.name)
            pObj.put("totalScore", player.totalScore)
            pObj.put("rank", index + 1)
            pObj.put("isPaittaMaal", index == 0 && player.totalScore >= 1000)
            pObj.put("pointsRemaining", (1000 - player.totalScore).coerceAtLeast(0))
            playersArray.put(pObj)
        }
        rootObj.put("standings", playersArray)

        val roundsArray = JSONArray()
        val rounds = scoreEntries.groupBy { it.timestamp }
            .entries
            .sortedBy { it.key }
            .map { it.value }

        rounds.forEachIndexed { roundIndex, roundScores ->
            val rObj = JSONObject()
            rObj.put("roundNumber", roundIndex + 1)
            val scoresObj = JSONObject()
            players.forEach { player ->
                val pts = roundScores.firstOrNull { it.playerId == player.id }?.points ?: 0
                scoresObj.put(player.name, pts)
            }
            rObj.put("scores", scoresObj)
            roundsArray.put(rObj)
        }
        rootObj.put("rounds", roundsArray)

        return rootObj.toString(4)
    } catch (e: Exception) {
        return "{\"error\": \"Could not generate JSON: ${e.message}\"}"
    }
}

@Composable
fun ExportScoreboardDialog(
    players: List<Player>,
    scoreEntries: List<ScoreEntry>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedFormatIsJson by remember { mutableStateOf(false) }

    val exportedText = remember(players, scoreEntries) {
        generateTextSummary(players, scoreEntries)
    }
    val exportedJson = remember(players, scoreEntries) {
        generateJsonSummary(players, scoreEntries)
    }

    val activeContent = if (selectedFormatIsJson) exportedJson else exportedText

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Share & Export Scoreboard",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Share your current game scoreboard to other apps or with friends.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (!selectedFormatIsJson) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { selectedFormatIsJson = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Text Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (!selectedFormatIsJson) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedFormatIsJson) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { selectedFormatIsJson = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JSON Data",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedFormatIsJson) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Text(
                        text = activeContent,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(activeContent))
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("export_copy_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Copy")
                }

                Button(
                    onClick = {
                        try {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, activeContent)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share")
                            context.startActivity(shareIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to share!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("export_share_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                    )
                    Text("Share")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("export_close_button")
            ) {
                Text("Close", color = MaterialTheme.colorScheme.outline)
            }
        }
    )
}

@Composable
fun CompletedRoundsModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    players: List<Player>,
    scoreEntries: List<ScoreEntry>,
    viewModel: ScoreboardViewModel
) {
    if (!isOpen) return

    val context = LocalContext.current
    val rounds = remember(scoreEntries) {
        scoreEntries.groupBy { it.timestamp }
            .entries
            .sortedBy { it.key }
            .map { it.value }
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🏆 Completed Rounds",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Round-by-round point breakdown",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                if (rounds.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📜",
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            text = "No rounds completed",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Round details will appear here as each round's scores are saved.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Need to correct a score?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Cancel/delete the last recorded round",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.undoLastRound()
                                    Toast.makeText(context, "Last round cancelled!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("modal_undo_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text("Undo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(rounds.reversed()) { revIdx, round ->
                            val roundNum = rounds.size - revIdx
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.06f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Round $roundNum",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFE8F5E9))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            val totalPoints = round.sumOf { it.points }
                                            Text(
                                                text = "Total $totalPoints pts",
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        players.forEach { player ->
                                            val pts = round.firstOrNull { it.playerId == player.id }?.points ?: 0
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = player.name,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "$pts",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onClose,
                modifier = Modifier.testTag("completed_rounds_close_button")
            ) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.testTag("completed_rounds_dialog")
    )
}
