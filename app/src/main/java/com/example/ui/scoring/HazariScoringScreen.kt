package com.example.ui.scoring

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.UndoIcon
import com.example.data.Player
import com.example.data.ScoreEntry
import com.example.ui.ScoreboardViewModel
import com.example.ui.theme.ThemePreset
import com.example.util.LocalStorage

// Deep authentic Hazari Red color palette
val HazariRed = Color(0xFFB71C1C) // Primary Deep Crimson
val HazariRedDark = Color(0xFF7F0000)
val HazariRedLight = Color(0xFFD32F2F)
val HazariGold = Color(0xFFFFD54F)

/**
 * Primary Composable screen adapting dynamically to the chosen theme
 * where users can input and manage scores for each player across multiple rounds of Hazari.
 * Rounds are stored sequentially until any player's total score reaches 1000 or above.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazariScoringScreen(
    viewModel: ScoreboardViewModel,
    currentTheme: ThemePreset = ThemePreset.ROYAL_EMERALD,
    modifier: Modifier = Modifier,
    onRoundSaved: (roundNumber: Int) -> Unit = {}
) {
    val context = LocalContext.current
    var scoringMode by remember {
        mutableStateOf(LocalStorage.getItem("hazari_scoring_mode", "multi"))
    }
    val players by viewModel.playersFlow.collectAsState()
    val scoreEntries by viewModel.scoreEntriesFlow.collectAsState()
    val closestToWin by viewModel.closestToWinFlow.collectAsState()

    // Group score entries by timestamp into completed rounds
    val completedRounds = remember(scoreEntries) {
        scoreEntries.groupBy { it.timestamp }
            .entries
            .sortedBy { it.key }
            .map { it.value }
    }
    val currentRoundNum = completedRounds.size + 1

    // Leader (highest total score)
    val currentLeader = remember(players) {
        players.filter { it.totalScore > 0 }.maxByOrNull { it.totalScore }
    }

    // State for editing a previously recorded round
    var roundToEdit by remember { mutableStateOf<List<ScoreEntry>?>(null) }
    var roundToEditIndex by remember { mutableStateOf(0) }
    var showUndoConfirmDialog by remember { mutableStateOf(false) }

    // Draft scores map for the active round: playerId -> score string
    val draftScores = remember { mutableStateMapOf<Int, String>() }

    // Initialize/restore draft scores from LocalStorage
    remember(players, currentRoundNum) {
        players.forEachIndexed { index, p ->
            val saved = LocalStorage.getItem("draft_score_p${p.id}", "")
                .ifEmpty { LocalStorage.getItem("draft_s$index", "") }
            draftScores[p.id] = saved
        }
    }

    val currentSum = players.sumOf { draftScores[it.id]?.toIntOrNull() ?: 0 }
    val remainingPoints = 360 - currentSum
    val isCurrentRoundValid = currentSum == 360 && players.size >= 2

    val onScoreChange: (playerId: Int, originalIndex: Int, value: String) -> Unit = { playerId, originalIndex, raw ->
        val clean = raw.filter { it.isDigit() }.take(3)
        val intVal = clean.toIntOrNull() ?: 0
        val clamped = if (intVal > 360) "360" else clean
        draftScores[playerId] = clamped
        LocalStorage.setItem("draft_score_p$playerId", clamped)
        if (originalIndex in 0..3) {
            LocalStorage.setItem("draft_s$originalIndex", clamped)
        }
    }

    // Root Container with Dynamic Theme Gradient Background
    val themeGradient = remember(currentTheme) {
        Brush.verticalGradient(
            listOf(
                currentTheme.primaryColor.copy(alpha = 0.90f),
                currentTheme.primaryColor.copy(alpha = 0.72f),
                currentTheme.darkBgColor
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(themeGradient)
            .testTag("hazari_scoring_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Mode Switcher: 10+ Rounds Sheet vs Single Round Entry
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = scoringMode == "multi",
                        onClick = {
                            scoringMode = "multi"
                            LocalStorage.setItem("hazari_scoring_mode", "multi")
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = Color.White,
                            activeContentColor = MaterialTheme.colorScheme.primary,
                            inactiveContainerColor = Color.White.copy(alpha = 0.22f),
                            inactiveContentColor = Color.White,
                            activeBorderColor = Color.White,
                            inactiveBorderColor = Color.White.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.testTag("mode_tab_multi_round")
                    ) {
                        Text(
                            text = "10+ Rounds Sheet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                    SegmentedButton(
                        selected = scoringMode == "single",
                        onClick = {
                            scoringMode = "single"
                            LocalStorage.setItem("hazari_scoring_mode", "single")
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = Color.White,
                            activeContentColor = MaterialTheme.colorScheme.primary,
                            inactiveContainerColor = Color.White.copy(alpha = 0.22f),
                            inactiveContentColor = Color.White,
                            activeBorderColor = Color.White,
                            inactiveBorderColor = Color.White.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.testTag("mode_tab_single_round")
                    ) {
                        Text(
                            text = "Single Round",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            if (scoringMode == "multi") {
                HazariMultiRoundScreen(
                    players = players,
                    scoreEntries = scoreEntries,
                    currentTheme = currentTheme,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    onSaveRound = { pointsList, onDone ->
                        viewModel.saveRoundScores(
                            playerIds = players.map { it.id },
                            pointsList = pointsList
                        ) {
                            onDone()
                            onRoundSaved(completedRounds.size + 1)
                        }
                    },
                    onSaveAllValidRounds = { roundsPoints, onDone ->
                        val startRound = completedRounds.size + 1
                        viewModel.saveMultipleRounds(
                            playerIds = players.map { it.id },
                            roundsPoints = roundsPoints
                        ) {
                            onDone()
                            onRoundSaved(startRound + roundsPoints.size - 1)
                        }
                    },
                    onUpdateRound = { timestamp, pointsList, onDone ->
                        viewModel.updateRoundScores(
                            timestamp = timestamp,
                            playerIds = players.map { it.id },
                            pointsList = pointsList
                        ) {
                            onDone()
                        }
                    },
                    onUndoLastRound = {
                        viewModel.undoLastRound()
                    },
                    onDeleteRound = { timestamp, onDone ->
                        viewModel.deleteRound(timestamp) {
                            onDone()
                        }
                    }
                )
            } else {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Player Standings Mini-Grid (Shows all 4 players and progress to 1000)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.94f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            Text(
                                text = "Match Standings (Target: 1000 pts)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121),
                                modifier = Modifier.weight(1f, fill = false),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            currentLeader?.let { leader ->
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, currentTheme.accentColor)
                                ) {
                                    Text(
                                        text = "👑 Leader: ${leader.name} (${leader.totalScore} pts)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        softWrap = false,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val sortedPlayers = players.sortedByDescending { it.totalScore }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            players.forEachIndexed { idx, player ->
                                val rank = sortedPlayers.indexOfFirst { it.id == player.id } + 1
                                val medal = when (rank) {
                                    1 -> "🥇"
                                    2 -> "🥈"
                                    3 -> "🥉"
                                    else -> "🏅"
                                }
                                val progress = (player.totalScore / 1000f).coerceIn(0f, 1f)

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF9F9F9),
                                    border = BorderStroke(
                                        1.dp,
                                        if (rank == 1 && player.totalScore > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color(0xFFE0E0E0)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = medal, fontSize = 11.sp)
                                            Text(
                                                text = player.name,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF212121),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${player.totalScore}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (rank == 1) MaterialTheme.colorScheme.primary else Color(0xFF424242)
                                        )
                                        Text(
                                            text = "pts",
                                            fontSize = 9.sp,
                                            color = Color(0xFF757575)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                            color = if (rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                            trackColor = Color(0xFFE0E0E0)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Current Round Input Card (360 pts threshold validation)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("round_input_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
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
                                            color = Color.White,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = "Input Round Scores",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF212121),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                if (currentSum < 360) {
                                    Text(
                                        text = "⚠️ Needs $remainingPoints more points to reach 360",
                                        fontSize = 11.sp,
                                        color = Color(0xFF616161),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                } else if (currentSum > 360) {
                                    Text(
                                        text = "❌ Exceeded 360! (${currentSum - 360} extra points)",
                                        fontSize = 11.sp,
                                        color = Color(0xFFD32F2F),
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                } else {
                                    Text(
                                        text = "✅ Exact 360 points entered! Ready to save.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Sum badge matching ROUND 1 style and color (solid primary, white text, never breaks line)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCurrentRoundValid) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = "Sum: $currentSum / 360",
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

                        // Progress indicator toward 360
                        val progress = (currentSum / 360f).coerceIn(0f, 1f)
                        val progressColor = when {
                            currentSum == 360 -> Color(0xFF2E7D32)
                            currentSum > 360 -> Color(0xFFD32F2F)
                            else -> MaterialTheme.colorScheme.primary
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .testTag("round_progress_bar"),
                            color = progressColor,
                            trackColor = Color(0xFFEEEEEE)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 4 Player Inputs (2x2 grid)
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            players.chunked(2).forEachIndexed { chunkIdx, pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEachIndexed { pairIdx, player ->
                                        val originalIdx = chunkIdx * 2 + pairIdx
                                        val scoreStr = draftScores[player.id] ?: ""
                                        val pts = scoreStr.toIntOrNull() ?: 0
                                        val projectedTotal = player.totalScore + pts

                                        // Auto-fill remainder calculation for this player
                                        val otherScoresSum = players.filter { it.id != player.id }
                                            .sumOf { draftScores[it.id]?.toIntOrNull() ?: 0 }
                                        val remainderForPlayer = (360 - otherScoresSum).coerceIn(0, 360)
                                        val canAutoFill = otherScoresSum in 1..359 && scoreStr.isEmpty()

                                        Column(modifier = Modifier.weight(1f)) {
                                            // Prominent, highly visible Player Name header badge
                                            Surface(
                                                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Surface(
                                                            shape = CircleShape,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(18.dp)
                                                        ) {
                                                            Box(
                                                                modifier = Modifier.fillMaxSize(),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    text = "${originalIdx + 1}",
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Black,
                                                                    color = Color.White
                                                                )
                                                            }
                                                        }
                                                        Text(
                                                            text = player.name,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = Color(0xFF1A1A1A),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    Text(
                                                        text = "${player.totalScore} pts",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF616161)
                                                    )
                                                }
                                            }

                                            OutlinedTextField(
                                                value = scoreStr,
                                                onValueChange = { onScoreChange(player.id, originalIdx, it) },
                                                label = {
                                                    Text(
                                                        text = "Round Score",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                },
                                                placeholder = { Text("0 pts") },
                                                supportingText = {
                                                    Text(
                                                        text = "Total after round: $projectedTotal pts",
                                                        fontSize = 11.sp,
                                                        color = if (projectedTotal >= 1000) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold
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
                                                                    onScoreChange(player.id, originalIdx, remainderForPlayer.toString())
                                                                }
                                                                .padding(end = 4.dp)
                                                        ) {
                                                            Text(
                                                                text = "+$remainderForPlayer",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Black,
                                                                color = MaterialTheme.colorScheme.primary,
                                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                                            )
                                                        }
                                                    }
                                                },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("round_input_$originalIdx"),
                                                textStyle = TextStyle(
                                                    color = Color.Black,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedTextColor = Color.Black,
                                                    unfocusedTextColor = Color.Black,
                                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                                    unfocusedLabelColor = Color(0xFF616161),
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                                    focusedPlaceholderColor = Color(0xFF9E9E9E),
                                                    unfocusedPlaceholderColor = Color(0xFF9E9E9E)
                                                )
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
                                if (isCurrentRoundValid) {
                                    val pointsList = players.map { draftScores[it.id]?.toIntOrNull() ?: 0 }
                                    viewModel.saveRoundScores(
                                        playerIds = players.map { it.id },
                                        pointsList = pointsList
                                    ) {
                                        Toast.makeText(context, "Round $currentRoundNum saved!", Toast.LENGTH_SHORT).show()
                                        onRoundSaved(currentRoundNum)
                                        // Clear drafts
                                        players.forEachIndexed { idx, p ->
                                            LocalStorage.removeItem("draft_score_p${p.id}")
                                            LocalStorage.removeItem("draft_s$idx")
                                            draftScores[p.id] = ""
                                        }
                                    }
                                }
                            },
                            enabled = isCurrentRoundValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("round_save_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = Color(0xFFBDBDBD)
                            )
                        ) {
                            Text(
                                text = "Save Round $currentRoundNum Score ✓",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }

                        if (completedRounds.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { showUndoConfirmDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("undo_last_round_score_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = UndoIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Revert Last Round (Round ${completedRounds.size}) Score",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Stored Rounds List (Allows reviewing & editing across multiple rounds)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Recorded Rounds (${completedRounds.size})",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = "Each round score stored until 1000 points reached",
                                    fontSize = 11.sp,
                                    color = Color(0xFF757575)
                                )
                            }

                            if (completedRounds.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { showUndoConfirmDialog = true },
                                    modifier = Modifier.testTag("screen_undo_last_round_btn"),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = UndoIcon,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text("Undo Last", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (completedRounds.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "🃏", fontSize = 36.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "No rounds recorded yet",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF616161)
                                    )
                                    Text(
                                        text = "Save Round 1 scores above to begin.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF9E9E9E)
                                    )
                                }
                            }
                        } else {
                            // Table Header: Round | P1 | P2 | P3 | P4 | Actions
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Round",
                                    modifier = Modifier.weight(1f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                players.forEach { player ->
                                    Text(
                                        text = player.name,
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF212121),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "Edit",
                                    modifier = Modifier.width(36.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF757575),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Each completed round row
                            completedRounds.forEachIndexed { rIdx, round ->
                                val rNum = rIdx + 1
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (rIdx % 2 == 0) Color.Transparent
                                            else Color(0xFFF5F5F5)
                                        )
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "R$rNum",
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF616161),
                                        textAlign = TextAlign.Center
                                    )
                                    players.forEach { player ->
                                        val pts = round.firstOrNull { it.playerId == player.id }?.points ?: 0
                                        Text(
                                            text = "$pts",
                                            modifier = Modifier.weight(1f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF212121),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            roundToEdit = round
                                            roundToEditIndex = rNum
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Round $rNum",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFEEEEEE))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Grand Totals Footer
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total",
                                    modifier = Modifier.weight(1f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                players.forEach { player ->
                                    Text(
                                        text = "${player.totalScore}",
                                        modifier = Modifier.weight(1f),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.width(36.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
            }
        }
    }

    // --- Undo Round Confirmation Dialog ---
    if (showUndoConfirmDialog && completedRounds.isNotEmpty()) {
        val lastRoundIndex = completedRounds.size
        val lastRoundEntries = completedRounds.last()
        AlertDialog(
            onDismissRequest = { showUndoConfirmDialog = false },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = UndoIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "Revert Round $lastRoundIndex?",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Are you sure you want to revert the last entered round score? This will remove Round $lastRoundIndex from match history and restore each player's previous total score.",
                        fontSize = 13.sp,
                        color = Color(0xFF616161)
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Scores recorded in Round $lastRoundIndex:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            players.forEach { p ->
                                val pts = lastRoundEntries.firstOrNull { it.playerId == p.id }?.points ?: 0
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(p.name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF212121))
                                    Text("$pts pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUndoConfirmDialog = false
                        // Restore undone round scores into inputs so user can adjust or re-save without retyping
                        lastRoundEntries.forEachIndexed { idx, entry ->
                            val strVal = entry.points.toString()
                            draftScores[entry.playerId] = strVal
                            LocalStorage.setItem("draft_score_p${entry.playerId}", strVal)
                            if (idx in 0..3) {
                                LocalStorage.setItem("draft_s$idx", strVal)
                            }
                        }
                        viewModel.undoLastRound()
                        Toast.makeText(context, "Round $lastRoundIndex reverted! Scores restored in inputs.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_undo_dialog_btn")
                ) {
                    Text("Revert & Edit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUndoConfirmDialog = false },
                    modifier = Modifier.testTag("cancel_undo_dialog_btn")
                ) {
                    Text("Cancel", color = Color(0xFF757575))
                }
            }
        )
    }

    // --- Edit Round Modal Dialog ---
    roundToEdit?.let { roundEntries ->
        EditRoundDialog(
            roundNumber = roundToEditIndex,
            roundEntries = roundEntries,
            players = players,
            onDismiss = { roundToEdit = null },
            onSave = { updatedPointsList ->
                val timestamp = roundEntries.firstOrNull()?.timestamp ?: return@EditRoundDialog
                viewModel.updateRoundScores(
                    timestamp = timestamp,
                    playerIds = players.map { it.id },
                    pointsList = updatedPointsList
                ) {
                    Toast.makeText(context, "Round $roundToEditIndex updated!", Toast.LENGTH_SHORT).show()
                    roundToEdit = null
                }
            },
            onDelete = {
                val timestamp = roundEntries.firstOrNull()?.timestamp ?: return@EditRoundDialog
                viewModel.deleteRound(timestamp) {
                    Toast.makeText(context, "Round $roundToEditIndex deleted!", Toast.LENGTH_SHORT).show()
                    roundToEdit = null
                }
            }
        )
    }
}

/**
 * Dialog allowing users to modify or delete a past round's scores.
 */
@Composable
fun EditRoundDialog(
    roundNumber: Int,
    roundEntries: List<ScoreEntry>,
    players: List<Player>,
    onDismiss: () -> Unit,
    onSave: (List<Int>) -> Unit,
    onDelete: () -> Unit
) {
    val editedScores = remember {
        mutableStateMapOf<Int, String>().apply {
            players.forEach { p ->
                val existing = roundEntries.firstOrNull { it.playerId == p.id }?.points ?: 0
                put(p.id, existing.toString())
            }
        }
    }

    val sum = players.sumOf { editedScores[it.id]?.toIntOrNull() ?: 0 }
    val isValid = sum == 360

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Round $roundNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isValid) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "Sum: $sum / 360",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Adjust player scores for Round $roundNumber. Total must equal 360 points.",
                    fontSize = 12.sp,
                    color = Color(0xFF616161)
                )

                players.forEach { player ->
                    val currentVal = editedScores[player.id] ?: ""
                    OutlinedTextField(
                        value = currentVal,
                        onValueChange = { raw ->
                            val clean = raw.filter { it.isDigit() }.take(3)
                            editedScores[player.id] = clean
                        },
                        label = { Text(player.name) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
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
                            unfocusedLabelColor = Color(0xFF616161)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                if (!isValid) {
                    Text(
                        text = if (sum < 360) "Needs ${360 - sum} more points to reach 360" else "Exceeds 360 by ${sum - 360} points",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onSave(players.map { editedScores[it.id]?.toIntOrNull() ?: 0 })
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Text("Delete Round")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
