package com.example.ui.scoring

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import com.example.ui.theme.ThemePreset
import com.example.util.LocalStorage
import org.json.JSONObject

private const val MULTI_ROUND_DRAFTS_KEY = "multi_round_drafts"
private const val MULTI_ROUND_VISIBLE_COUNT_KEY = "multi_round_visible_count"
private const val STANDARD_MIN_ROUNDS = 10
private const val ROUND_TARGET_POINTS = 360

/**
 * Multi-round Hazari Score Sheet composable enabling score entry for 4 players
 * across the standard 10 or more rounds of Hazari.
 */
@Composable
fun HazariMultiRoundScreen(
    players: List<Player>,
    scoreEntries: List<ScoreEntry>,
    currentTheme: ThemePreset = ThemePreset.ROYAL_EMERALD,
    modifier: Modifier = Modifier,
    onSaveRound: (pointsList: List<Int>, onDone: () -> Unit) -> Unit = { _, onDone -> onDone() },
    onSaveAllValidRounds: (roundsPoints: List<List<Int>>, onDone: () -> Unit) -> Unit = { _, onDone -> onDone() },
    onUpdateRound: (timestamp: Long, pointsList: List<Int>, onDone: () -> Unit) -> Unit = { _, _, onDone -> onDone() },
    onUndoLastRound: () -> Unit = {},
    onDeleteRound: (timestamp: Long, onDone: () -> Unit) -> Unit = { _, onDone -> onDone() }
) {
    val context = LocalContext.current

    // Group saved score entries chronologically into completed rounds
    val completedRounds = remember(scoreEntries) {
        scoreEntries.groupBy { it.timestamp }
            .entries
            .sortedBy { it.key }
            .map { it.value }
    }

    // Leader (player with highest total score)
    val currentLeader = remember(players) {
        players.filter { it.totalScore > 0 }.maxByOrNull { it.totalScore }
    }

    // Track number of round rows displayed (standard minimum 10 rounds, expandable to 11+)
    var visibleRoundCount by remember {
        val savedCount = LocalStorage.getItem(MULTI_ROUND_VISIBLE_COUNT_KEY, "10").toIntOrNull() ?: STANDARD_MIN_ROUNDS
        mutableIntStateOf(maxOf(STANDARD_MIN_ROUNDS, savedCount, completedRounds.size))
    }

    LaunchedEffect(completedRounds.size) {
        if (completedRounds.size >= visibleRoundCount && completedRounds.size >= STANDARD_MIN_ROUNDS) {
            // Keep at least one empty round visible after 10 rounds are filled
            val targetCount = maxOf(STANDARD_MIN_ROUNDS, completedRounds.size + 1)
            if (targetCount > visibleRoundCount) {
                visibleRoundCount = targetCount
                LocalStorage.setItem(MULTI_ROUND_VISIBLE_COUNT_KEY, visibleRoundCount.toString())
            }
        }
    }

    // Draft scores per roundNumber (1-based) -> Map<playerId, String>
    val draftRounds = remember {
        mutableStateMapOf<Int, Map<Int, String>>().apply {
            val rawJson = LocalStorage.getItem(MULTI_ROUND_DRAFTS_KEY, "")
            if (rawJson.isNotEmpty()) {
                try {
                    val root = JSONObject(rawJson)
                    val keys = root.keys()
                    while (keys.hasNext()) {
                        val roundKey = keys.next()
                        val roundNum = roundKey.toIntOrNull() ?: continue
                        val inner = root.optJSONObject(roundKey) ?: continue
                        val pMap = mutableMapOf<Int, String>()
                        val pKeys = inner.keys()
                        while (pKeys.hasNext()) {
                            val pKey = pKeys.next()
                            val pId = pKey.toIntOrNull() ?: continue
                            pMap[pId] = inner.optString(pKey, "")
                        }
                        if (pMap.isNotEmpty()) {
                            put(roundNum, pMap)
                        }
                    }
                } catch (_: Exception) {
                    // Ignore malformed draft cache
                }
            }
        }
    }

    // Helper to persist drafts to LocalStorage
    fun persistDrafts() {
        try {
            val root = JSONObject()
            draftRounds.forEach { (rNum, pMap) ->
                val hasContent = pMap.values.any { it.isNotEmpty() }
                if (hasContent) {
                    val inner = JSONObject()
                    pMap.forEach { (pId, v) ->
                        if (v.isNotEmpty()) inner.put(pId.toString(), v)
                    }
                    root.put(rNum.toString(), inner)
                }
            }
            LocalStorage.setItem(MULTI_ROUND_DRAFTS_KEY, root.toString())
        } catch (_: Exception) {
        }
    }

    // Clear drafts if the match was fully reset
    LaunchedEffect(completedRounds.size, players) {
        if (completedRounds.isEmpty() && players.all { it.totalScore == 0 }) {
            // Check if reset cleared local storage externally
            val stored = LocalStorage.getItem(MULTI_ROUND_DRAFTS_KEY, "")
            if (stored.isEmpty() && draftRounds.isNotEmpty()) {
                draftRounds.clear()
                visibleRoundCount = STANDARD_MIN_ROUNDS
            }
        }
    }

    var focusedRoundNumber by remember { mutableStateOf<Int?>(1) }
    var showUndoConfirmDialog by remember { mutableStateOf(false) }
    var roundToDeleteConfirm by remember { mutableStateOf<Pair<Int, Long>?>(null) }

    // Helper: get display value for a cell
    fun getCellValue(roundIndex: Int, playerId: Int): String {
        val roundNumber = roundIndex + 1
        val draftMap = draftRounds[roundNumber]
        if (draftMap != null && draftMap.containsKey(playerId)) {
            return draftMap[playerId] ?: ""
        }
        return if (roundIndex < completedRounds.size) {
            val entry = completedRounds[roundIndex].firstOrNull { it.playerId == playerId }
            entry?.points?.toString() ?: "0"
        } else {
            ""
        }
    }

    // Helper: update a cell score
    fun updateCell(roundIndex: Int, playerId: Int, rawValue: String) {
        val roundNumber = roundIndex + 1
        val clean = rawValue.filter { it.isDigit() }.take(3)
        val intVal = clean.toIntOrNull() ?: 0
        val clamped = if (intVal > ROUND_TARGET_POINTS) ROUND_TARGET_POINTS.toString() else clean

        val existingMap = draftRounds[roundNumber]?.toMutableMap() ?: run {
            if (roundIndex < completedRounds.size) {
                val saved = completedRounds[roundIndex]
                players.associate { p ->
                    p.id to (saved.firstOrNull { it.playerId == p.id }?.points?.toString() ?: "0")
                }.toMutableMap()
            } else {
                mutableMapOf()
            }
        }
        existingMap[playerId] = clamped
        draftRounds[roundNumber] = existingMap
        persistDrafts()
    }

    // Helper: compute contiguous unsaved valid rounds starting immediately after completedRounds.size
    val sequentialValidUnsavedRounds = remember(completedRounds.size, visibleRoundCount, draftRounds.toMap(), players) {
        val result = mutableListOf<Pair<Int, List<Int>>>()
        var idx = completedRounds.size
        while (idx < visibleRoundCount) {
            val rNum = idx + 1
            val dMap = draftRounds[rNum] ?: break
            val hasAnyInput = players.any { !dMap[it.id].isNullOrEmpty() }
            if (!hasAnyInput) break
            val pts = players.map { dMap[it.id]?.toIntOrNull() ?: 0 }
            if (pts.sum() == ROUND_TARGET_POINTS && players.size >= 2) {
                result.add(rNum to pts)
                idx++
            } else {
                break
            }
        }
        result
    }

    // Live cumulative draft preview per player (saved + unsaved drafts)
    val liveTotalByPlayer = remember(players, completedRounds, visibleRoundCount, draftRounds.toMap()) {
        players.associate { player ->
            var sum = 0
            for (rIdx in 0 until visibleRoundCount) {
                val rNum = rIdx + 1
                val draftMap = draftRounds[rNum]
                val pts = if (draftMap != null && draftMap.containsKey(player.id)) {
                    draftMap[player.id]?.toIntOrNull() ?: 0
                } else if (rIdx < completedRounds.size) {
                    completedRounds[rIdx].firstOrNull { it.playerId == player.id }?.points ?: 0
                } else {
                    0
                }
                sum += pts
            }
            player.id to sum
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("hazari_multi_round_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Match Standings Card (with single-line non-wrapping Leader badge)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
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
                            players.forEach { player ->
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
                                        if (rank == 1 && player.totalScore > 0) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                        } else {
                                            Color(0xFFE0E0E0)
                                        }
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
                                            color = if (rank == 1 && player.totalScore > 0) MaterialTheme.colorScheme.primary else Color(0xFF424242)
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

            // 2. 10+ Rounds Hazari Score Sheet Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("multi_round_sheet_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Sheet Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "$visibleRoundCount ROUNDS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = "Hazari Score Sheet",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF212121),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${completedRounds.size} saved • Each round must total 360 pts",
                                    fontSize = 11.sp,
                                    color = Color(0xFF616161),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (completedRounds.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { showUndoConfirmDialog = true },
                                    modifier = Modifier
                                        .heightIn(min = 40.dp)
                                        .testTag("multi_round_undo_header_btn"),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = UndoIcon,
                                            contentDescription = "Revert Last Round",
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Undo R${completedRounds.size}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Table Header Row: Rnd | P1 | P2 | P3 | P4 | Sum
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Rnd",
                                modifier = Modifier.width(34.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            players.forEachIndexed { idx, player ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${idx + 1}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = player.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Text(
                                text = "Sum",
                                modifier = Modifier.width(54.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Round Rows: Round 1 .. visibleRoundCount (10 or more)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (rIdx in 0 until visibleRoundCount) {
                                val roundNumber = rIdx + 1
                                val isSaved = rIdx < completedRounds.size
                                val savedTimestamp = if (isSaved) completedRounds[rIdx].firstOrNull()?.timestamp else null
                                val hasDraftEdits = draftRounds[roundNumber]?.isNotEmpty() == true

                                val rowValues = players.map { p -> getCellValue(rIdx, p.id) }
                                val rowScores = rowValues.map { it.toIntOrNull() ?: 0 }
                                val rowSum = rowScores.sum()
                                val hasAnyEntry = rowValues.any { it.isNotEmpty() }

                                val isDirty = if (isSaved && hasDraftEdits) {
                                    val savedMap = completedRounds[rIdx].associate { it.playerId to it.points }
                                    players.any { p ->
                                        val draftVal = draftRounds[roundNumber]?.get(p.id)?.toIntOrNull()
                                        draftVal != null && draftVal != (savedMap[p.id] ?: 0)
                                    } || players.any { p ->
                                        draftRounds[roundNumber]?.get(p.id) == ""
                                    }
                                } else {
                                    false
                                }

                                val isValid360 = rowSum == ROUND_TARGET_POINTS && players.size >= 2
                                val isFocused = focusedRoundNumber == roundNumber
                                val isNextToSave = roundNumber == completedRounds.size + 1

                                val rowContainerColor = when {
                                    isDirty -> Color(0xFFFFF8E1)
                                    isSaved -> Color(0xFFF1F8E9)
                                    isFocused -> MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                                    rIdx % 2 == 1 -> Color(0xFFFAFAFA)
                                    else -> Color.White
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(rowContainerColor)
                                        .clickable { focusedRoundNumber = roundNumber }
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                        .testTag("multi_round_row_$roundNumber")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        // Round Number Label
                                        Column(
                                            modifier = Modifier.width(34.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = when {
                                                    isSaved && !isDirty -> Color(0xFF2E7D32)
                                                    isNextToSave || isFocused -> MaterialTheme.colorScheme.primary
                                                    else -> Color(0xFFEEEEEE)
                                                }
                                            ) {
                                                Text(
                                                    text = "R$roundNumber",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (isSaved || isNextToSave || isFocused) Color.White else Color(0xFF424242),
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        // 4 Player Input Cells
                                        players.forEachIndexed { pIdx, player ->
                                            val cellVal = rowValues[pIdx]
                                            var cellFocused by remember { mutableStateOf(false) }

                                            val borderColor = when {
                                                cellFocused -> MaterialTheme.colorScheme.primary
                                                isDirty -> Color(0xFFFFA000)
                                                isSaved -> Color(0xFFA5D6A7)
                                                cellVal.isNotEmpty() -> MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                                else -> Color(0xFFDADADA)
                                            }

                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .heightIn(min = 48.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color.White,
                                                border = BorderStroke(if (cellFocused) 1.8.dp else 1.dp, borderColor)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .heightIn(min = 48.dp)
                                                        .padding(horizontal = 4.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (cellVal.isEmpty()) {
                                                        Text(
                                                            text = "0",
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = Color(0xFFBDBDBD),
                                                            textAlign = TextAlign.Center
                                                        )
                                                    }
                                                    BasicTextField(
                                                        value = cellVal,
                                                        onValueChange = { updateCell(rIdx, player.id, it) },
                                                        singleLine = true,
                                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                                        textStyle = TextStyle(
                                                            color = Color.Black,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center
                                                        ),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .onFocusChanged { fs ->
                                                                cellFocused = fs.isFocused
                                                                if (fs.isFocused) {
                                                                    focusedRoundNumber = roundNumber
                                                                }
                                                            }
                                                            .testTag("multi_round_input_${roundNumber}_$pIdx")
                                                    )
                                                }
                                            }
                                        }

                                        // Row Sum / Quick Save Status Cell
                                        Box(
                                            modifier = Modifier.width(54.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            when {
                                                (!isSaved && isValid360) -> {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0xFF2E7D32),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .heightIn(min = 40.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .clickable {
                                                                if (roundNumber == completedRounds.size + 1) {
                                                                    onSaveRound(rowScores) {
                                                                        draftRounds.remove(roundNumber)
                                                                        persistDrafts()
                                                                        focusedRoundNumber = roundNumber + 1
                                                                        Toast.makeText(context, "Round $roundNumber saved!", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                } else if (sequentialValidUnsavedRounds.isNotEmpty()) {
                                                                    val roundsToSave = sequentialValidUnsavedRounds.map { it.second }
                                                                    val savedNums = sequentialValidUnsavedRounds.map { it.first }
                                                                    onSaveAllValidRounds(roundsToSave) {
                                                                        savedNums.forEach { draftRounds.remove(it) }
                                                                        persistDrafts()
                                                                        Toast.makeText(context, "Saved ${savedNums.size} round(s)!", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                } else {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Please complete & save Round ${completedRounds.size + 1} first.",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                            }
                                                            .testTag("save_round_row_$roundNumber")
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            Text(
                                                                text = "Save",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Black,
                                                                color = Color.White,
                                                                maxLines = 1
                                                            )
                                                            Text(
                                                                text = "360 ✓",
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White.copy(alpha = 0.9f),
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                }
                                                (isSaved && isDirty && isValid360 && savedTimestamp != null) -> {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .heightIn(min = 40.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .clickable {
                                                                onUpdateRound(savedTimestamp, rowScores) {
                                                                    draftRounds.remove(roundNumber)
                                                                    persistDrafts()
                                                                    Toast.makeText(context, "Round $roundNumber updated!", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                            .testTag("update_round_row_$roundNumber")
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            Text(
                                                                text = "Update",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Black,
                                                                color = Color.White,
                                                                maxLines = 1
                                                            )
                                                            Text(
                                                                text = "360 ✓",
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White.copy(alpha = 0.9f),
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                }
                                                (isSaved && !isDirty) -> {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0xFFE8F5E9)
                                                    ) {
                                                        Text(
                                                            text = "360 ✓",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = Color(0xFF2E7D32),
                                                            maxLines = 1,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                                        )
                                                    }
                                                }
                                                (rowSum > ROUND_TARGET_POINTS) -> {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0xFFFFEBEE)
                                                    ) {
                                                        Text(
                                                            text = "$rowSum!",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = Color(0xFFD32F2F),
                                                            maxLines = 1,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                                        )
                                                    }
                                                }
                                                (hasAnyEntry && rowSum in 0 until ROUND_TARGET_POINTS) -> {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0xFFFFF3E0)
                                                    ) {
                                                        Text(
                                                            text = "$rowSum",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFFE65100),
                                                            maxLines = 1,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                                        )
                                                    }
                                                }
                                                else -> {
                                                    Text(
                                                        text = "—",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = Color(0xFF9E9E9E)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Contextual helper bar when this row is focused and needs guidance / quick-fill / edit actions
                                    val showHelper = isFocused && (hasAnyEntry || isSaved)
                                    AnimatedVisibility(
                                        visible = showHelper,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 4.dp, start = 38.dp, end = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            when {
                                                rowSum < ROUND_TARGET_POINTS -> {
                                                    val needed = ROUND_TARGET_POINTS - rowSum
                                                    // Check if exactly 1 player cell is empty and others have positive sum
                                                    val emptyIndices = rowValues.indices.filter { rowValues[it].isEmpty() }
                                                    Text(
                                                        text = "Needs $needed more pts",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = Color(0xFFE65100),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    if (emptyIndices.size == 1 && rowSum > 0) {
                                                        val targetPlayer = players[emptyIndices.first()]
                                                        Surface(
                                                            shape = RoundedCornerShape(6.dp),
                                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .clickable {
                                                                    updateCell(rIdx, targetPlayer.id, needed.toString())
                                                                }
                                                                .testTag("autofill_round_${roundNumber}")
                                                        ) {
                                                            Text(
                                                                text = "Fill ${targetPlayer.name}: +$needed",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.primary,
                                                                maxLines = 1,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                            )
                                                        }
                                                    } else if (!isSaved && hasAnyEntry) {
                                                        TextButton(
                                                            onClick = {
                                                                draftRounds.remove(roundNumber)
                                                                persistDrafts()
                                                            },
                                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                                        ) {
                                                            Text("Clear", fontSize = 11.sp, color = Color(0xFF757575))
                                                        }
                                                    }
                                                }
                                                rowSum > ROUND_TARGET_POINTS -> {
                                                    Text(
                                                        text = "Exceeded 360 by ${rowSum - ROUND_TARGET_POINTS} pts",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFD32F2F),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    if (isDirty) {
                                                        TextButton(
                                                            onClick = {
                                                                draftRounds.remove(roundNumber)
                                                                persistDrafts()
                                                            },
                                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                                        ) {
                                                            Text("Discard", fontSize = 11.sp, color = Color(0xFF757575))
                                                        }
                                                    }
                                                }
                                                isSaved && isDirty -> {
                                                    Text(
                                                        text = "Edited (tap Update to save)",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    TextButton(
                                                        onClick = {
                                                            draftRounds.remove(roundNumber)
                                                            persistDrafts()
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                                    ) {
                                                        Text("Discard", fontSize = 11.sp, color = Color(0xFF757575))
                                                    }
                                                }
                                                isSaved && savedTimestamp != null -> {
                                                    Text(
                                                        text = "Saved • Tap any cell above to edit",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF2E7D32),
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    IconButton(
                                                        onClick = { roundToDeleteConfirm = roundNumber to savedTimestamp },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = "Delete Round $roundNumber",
                                                            tint = Color(0xFFD32F2F).copy(alpha = 0.75f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                                !isSaved && isValid360 -> {
                                                    val canSaveNow = roundNumber == completedRounds.size + 1
                                                    Text(
                                                        text = if (canSaveNow) "Exact 360 pts! Tap Save." else "Valid 360 pts (save R${completedRounds.size + 1} first)",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF2E7D32),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                if (rIdx < visibleRoundCount - 1) {
                                    HorizontalDivider(color = Color(0xFFF0F0F0))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add Round Button (allows expanding beyond 10 rounds to 11, 12, 13+)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    visibleRoundCount += 1
                                    LocalStorage.setItem(MULTI_ROUND_VISIBLE_COUNT_KEY, visibleRoundCount.toString())
                                    focusedRoundNumber = visibleRoundCount
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("add_round_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Round",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Add Round (Round ${visibleRoundCount + 1})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Allow removing extra empty rounds beyond 10 if needed
                            val lastRowEmpty = (draftRounds[visibleRoundCount]?.values?.all { it.isEmpty() } ?: true) &&
                                visibleRoundCount > maxOf(STANDARD_MIN_ROUNDS, completedRounds.size)
                            if (lastRowEmpty) {
                                IconButton(
                                    onClick = {
                                        draftRounds.remove(visibleRoundCount)
                                        visibleRoundCount -= 1
                                        LocalStorage.setItem(MULTI_ROUND_VISIBLE_COUNT_KEY, visibleRoundCount.toString())
                                        persistDrafts()
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("remove_extra_round_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Empty Round",
                                        tint = Color(0xFF757575)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Grand Totals Footer Row across all 4 players
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("multi_round_totals_row")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Total",
                                    modifier = Modifier.width(34.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                players.forEach { player ->
                                    val savedTotal = player.totalScore
                                    val liveTotal = liveTotalByPlayer[player.id] ?: savedTotal
                                    val hasPendingDiff = liveTotal != savedTotal
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "$savedTotal",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center
                                        )
                                        if (hasPendingDiff) {
                                            Text(
                                                text = "($liveTotal)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF616161),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${players.sumOf { it.totalScore }}",
                                    modifier = Modifier.width(54.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Save All Sequential Valid Rounds Button (if 1+ valid unsaved rounds are ready)
                        if (sequentialValidUnsavedRounds.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val roundsPoints = sequentialValidUnsavedRounds.map { it.second }
                                    val savedNums = sequentialValidUnsavedRounds.map { it.first }
                                    onSaveAllValidRounds(roundsPoints) {
                                        savedNums.forEach { draftRounds.remove(it) }
                                        persistDrafts()
                                        focusedRoundNumber = (savedNums.lastOrNull() ?: completedRounds.size) + 1
                                        val msg = if (savedNums.size == 1) {
                                            "Round ${savedNums.first()} saved!"
                                        } else {
                                            "Saved ${savedNums.size} rounds (${savedNums.first()}–${savedNums.last()})!"
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp)
                                    .testTag("save_all_valid_rounds_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val label = if (sequentialValidUnsavedRounds.size == 1) {
                                    "Save Round ${sequentialValidUnsavedRounds.first().first} Score"
                                } else {
                                    "Save ${sequentialValidUnsavedRounds.size} Valid Rounds (R${sequentialValidUnsavedRounds.first().first}–R${sequentialValidUnsavedRounds.last().first})"
                                }
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }

    // Undo Last Round Confirmation Dialog
    if (showUndoConfirmDialog && completedRounds.isNotEmpty()) {
        val lastRoundIdx = completedRounds.size
        AlertDialog(
            onDismissRequest = { showUndoConfirmDialog = false },
            title = { Text("Revert Round $lastRoundIdx?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will remove Round $lastRoundIdx from saved totals and restore the entered scores as editable drafts.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val lastEntries = completedRounds.last()
                        val restored = players.associate { p ->
                            p.id to (lastEntries.firstOrNull { it.playerId == p.id }?.points?.toString() ?: "0")
                        }
                        draftRounds[lastRoundIdx] = restored
                        persistDrafts()
                        showUndoConfirmDialog = false
                        onUndoLastRound()
                        Toast.makeText(context, "Round $lastRoundIdx reverted!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("confirm_multi_undo_btn")
                ) {
                    Text("Revert")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUndoConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Specific Saved Round Confirmation Dialog
    roundToDeleteConfirm?.let { (roundNum, timestamp) ->
        AlertDialog(
            onDismissRequest = { roundToDeleteConfirm = null },
            title = { Text("Delete Round $roundNum?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete Round $roundNum? Subsequent rounds will shift up and player totals will be recalculated.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteRound(timestamp) {
                            draftRounds.remove(roundNum)
                            persistDrafts()
                            roundToDeleteConfirm = null
                            Toast.makeText(context, "Round $roundNum deleted!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { roundToDeleteConfirm = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
