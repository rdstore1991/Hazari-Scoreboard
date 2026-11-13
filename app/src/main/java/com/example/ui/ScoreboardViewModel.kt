package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Player
import com.example.data.ScoreEntry
import com.example.data.ScoreboardRepository
import com.example.ui.scoring.ScoringInputUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONArray

class ScoreboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ScoreboardRepository
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = ScoreboardRepository(database.playerDao())
    }

    val playersFlow: StateFlow<List<Player>> = repository.allPlayers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val scoreEntriesFlow: StateFlow<List<ScoreEntry>> = repository.allScoreEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val winnerFlow: StateFlow<Player?> = playersFlow.map { list: List<Player> ->
        determineWinner(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Keep track of the player with the highest total score (leader)
    val closestToWinFlow: StateFlow<Player?> = playersFlow.map { list ->
        list.filter { it.totalScore > 0 }
            .maxByOrNull { it.totalScore }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _lastActionEntry = MutableStateFlow<ScoreEntry?>(null)
    val lastActionEntry = _lastActionEntry.asStateFlow()

    // State-managed scoring input state
    private val _scoringInputState = MutableStateFlow(ScoringInputUiState())
    val scoringInputState: StateFlow<ScoringInputUiState> = _scoringInputState.asStateFlow()

    init {
        refreshLastAction()

        // Keep round number in sync with score entries
        viewModelScope.launch {
            scoreEntriesFlow.collect { entries ->
                val roundNum = entries.map { it.timestamp }.distinct().size + 1
                _scoringInputState.value = _scoringInputState.value.copy(roundNumber = roundNum)
            }
        }

        // Restore draft inputs if available and keep player scores in sync
        viewModelScope.launch {
            playersFlow.collect { plist ->
                val activeIds = plist.map { it.id }.toSet()
                val currentScores = _scoringInputState.value.scores
                val filteredScores = currentScores.filterKeys { it in activeIds }
                if (filteredScores.size != currentScores.size) {
                    _scoringInputState.value = _scoringInputState.value.copy(scores = filteredScores)
                }

                if (plist.isNotEmpty() && _scoringInputState.value.scores.isEmpty()) {
                    val restoredScores = mutableMapOf<Int, String>()
                    plist.forEachIndexed { index, p ->
                        val saved = com.example.util.LocalStorage.getItem("draft_score_p${p.id}", "")
                            .ifEmpty { com.example.util.LocalStorage.getItem("draft_s$index", "") }
                        if (saved.isNotEmpty()) {
                            restoredScores[p.id] = saved
                        }
                    }
                    if (restoredScores.isNotEmpty()) {
                        _scoringInputState.value = _scoringInputState.value.copy(scores = restoredScores)
                    }
                }
            }
        }

        val database = AppDatabase.getDatabase(application)

        // Restore from LocalStorage if SQLite is empty
        viewModelScope.launch {
            com.example.util.LocalStorage.init(application)
            val currentPlayers = database.playerDao().getAllPlayers()
            if (currentPlayers.isEmpty()) {
                val saved = com.example.util.LocalStorage.getItem("saved_scorecard", "")
                if (saved.isNotEmpty()) {
                    try {
                        val rootObj = JSONObject(saved)
                        val playersArr = rootObj.optJSONArray("players")
                        val scoreEntriesArr = rootObj.optJSONArray("scoreEntries")

                        val plist = mutableListOf<Player>()
                        if (playersArr != null) {
                            for (i in 0 until playersArr.length()) {
                                val pObj = playersArr.getJSONObject(i)
                                plist.add(
                                    Player(
                                        id = pObj.getInt("id"),
                                        name = pObj.getString("name"),
                                        totalScore = pObj.getInt("totalScore")
                                    )
                                )
                            }
                        }

                        val selist = mutableListOf<ScoreEntry>()
                        if (scoreEntriesArr != null) {
                            for (i in 0 until scoreEntriesArr.length()) {
                                val seObj = scoreEntriesArr.getJSONObject(i)
                                selist.add(
                                    ScoreEntry(
                                        id = seObj.getInt("id"),
                                        playerId = seObj.getInt("playerId"),
                                        points = seObj.getInt("points"),
                                        timestamp = seObj.getLong("timestamp")
                                    )
                                )
                            }
                        }

                        if (plist.isNotEmpty()) {
                            repository.restoreGame(plist, selist)
                            refreshLastAction()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        // Auto-save any score changes to LocalStorage
        viewModelScope.launch {
            com.example.util.LocalStorage.init(application)
            kotlinx.coroutines.flow.combine(playersFlow, scoreEntriesFlow) { plist, selist ->
                Pair(plist, selist)
            }.collect { (plist, selist) ->
                if (plist.isNotEmpty()) {
                    try {
                        val rootObj = JSONObject()
                        val playersArr = JSONArray()
                        for (p in plist) {
                            val pObj = JSONObject()
                            pObj.put("id", p.id)
                            pObj.put("name", p.name)
                            pObj.put("totalScore", p.totalScore)
                            playersArr.put(pObj)
                        }
                        rootObj.put("players", playersArr)

                        val scoreEntriesArr = JSONArray()
                        for (se in selist) {
                            val seObj = JSONObject()
                            seObj.put("id", se.id)
                            seObj.put("playerId", se.playerId)
                            seObj.put("points", se.points)
                            seObj.put("timestamp", se.timestamp)
                            scoreEntriesArr.put(seObj)
                        }
                        rootObj.put("scoreEntries", scoreEntriesArr)

                        com.example.util.LocalStorage.setItem("saved_scorecard", rootObj.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    // If players list becomes empty (like after a full reset/new game setup), clear the saved scorecard
                    com.example.util.LocalStorage.removeItem("saved_scorecard")
                }
            }
        }
    }

    fun refreshLastAction() {
        viewModelScope.launch {
            _lastActionEntry.value = repository.getLatestScoreEntry()
        }
    }

    fun addPlayer(name: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addPlayer(name)
            refreshLastAction()
            onComplete()
        }
    }

    fun addScore(playerId: Int, points: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addScore(playerId, points)
            refreshLastAction()
            onComplete()
        }
    }

    fun createNewGame(names: List<String>, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.createNewGame(names)
            refreshLastAction()
            onComplete()
        }
    }

    fun saveRoundScores(playerIds: List<Int>, pointsList: List<Int>, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.saveRoundScores(playerIds, pointsList)
            refreshLastAction()
            onComplete()
        }
    }

    fun updateRoundScores(timestamp: Long, playerIds: List<Int>, pointsList: List<Int>, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateRoundScores(timestamp, playerIds, pointsList)
            refreshLastAction()
            onComplete()
        }
    }

    fun deleteRound(timestamp: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteRound(timestamp)
            refreshLastAction()
            onComplete()
        }
    }

    fun undoLastRound() {
        viewModelScope.launch {
            repository.undoLastRound()
            refreshLastAction()
        }
    }

    fun undoLastScore() {
        viewModelScope.launch {
            repository.undoLastScore()
            refreshLastAction()
        }
    }

    fun updateScoreInput(playerId: Int, rawValue: String) {
        val cleanValue = rawValue.filter { it.isDigit() }.take(3)
        val intVal = cleanValue.toIntOrNull() ?: 0
        val clampedValue = if (intVal > 360) "360" else cleanValue

        val currentMap = _scoringInputState.value.scores.toMutableMap()
        currentMap[playerId] = clampedValue
        _scoringInputState.value = _scoringInputState.value.copy(
            scores = currentMap,
            validationMessage = if (intVal > 360) "Score cannot exceed 360 points!" else null
        )

        // Persist draft to local storage
        com.example.util.LocalStorage.setItem("draft_score_p$playerId", clampedValue)
        val players = playersFlow.value
        val playerIndex = players.indexOfFirst { it.id == playerId }
        if (playerIndex in 0..3) {
            com.example.util.LocalStorage.setItem("draft_s$playerIndex", clampedValue)
        }
    }

    fun quickAddScore(playerId: Int, delta: Int) {
        val current = _scoringInputState.value.getScore(playerId)
        val newScore = (current + delta).coerceIn(0, 360)
        updateScoreInput(playerId, if (newScore == 0) "" else newScore.toString())
    }

    fun autoFillRemainingScore(playerId: Int) {
        val players = playersFlow.value
        if (players.size < 2) return
        val remainder = _scoringInputState.value.calculateRemainderFor(playerId, players.map { it.id })
        updateScoreInput(playerId, remainder.toString())
    }

    fun clearScoringInput() {
        val players = playersFlow.value
        players.forEachIndexed { index, p ->
            com.example.util.LocalStorage.removeItem("draft_score_p${p.id}")
            com.example.util.LocalStorage.removeItem("draft_s$index")
        }
        com.example.util.LocalStorage.removeItem("draft_s0")
        com.example.util.LocalStorage.removeItem("draft_s1")
        com.example.util.LocalStorage.removeItem("draft_s2")
        com.example.util.LocalStorage.removeItem("draft_s3")
        _scoringInputState.value = _scoringInputState.value.copy(
            scores = emptyMap(),
            validationMessage = null,
            saveSuccessEvent = false
        )
    }

    fun dismissValidationMessage() {
        _scoringInputState.value = _scoringInputState.value.copy(validationMessage = null)
    }

    fun submitCurrentRound(
        onSuccess: (roundNumber: Int) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val players = playersFlow.value
        if (players.size < 2) {
            val msg = "Scorecard requires at least 2 players to record a round."
            _scoringInputState.value = _scoringInputState.value.copy(validationMessage = msg)
            onError(msg)
            return
        }

        val currentState = _scoringInputState.value
        val total = currentState.currentTotal
        if (total != 360) {
            val diff = kotlin.math.abs(360 - total)
            val msg = if (total > 360) {
                "Total round score is $total (exceeds 360 by $diff points). Round total must be exactly 360!"
            } else {
                "Total round score is $total (needs $diff more points). Round total must be exactly 360!"
            }
            _scoringInputState.value = _scoringInputState.value.copy(validationMessage = msg)
            onError(msg)
            return
        }

        val playerIds = players.map { it.id }
        val pointsList = players.map { currentState.getScore(it.id) }

        viewModelScope.launch {
            val currentRound = currentState.roundNumber
            repository.saveRoundScores(playerIds, pointsList)
            refreshLastAction()
            clearScoringInput()
            _scoringInputState.value = _scoringInputState.value.copy(saveSuccessEvent = true)
            onSuccess(currentRound)
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            repository.resetGame()
            clearScoringInput()
            refreshLastAction()
        }
    }

    fun deletePlayer(player: Player, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deletePlayer(player)
            val updatedMap = _scoringInputState.value.scores.toMutableMap()
            updatedMap.remove(player.id)
            _scoringInputState.value = _scoringInputState.value.copy(scores = updatedMap)
            com.example.util.LocalStorage.removeItem("draft_score_p${player.id}")
            refreshLastAction()
            onComplete()
        }
    }

    companion object {
        /**
         * Determines the winner of the game.
         * The game concludes when at least one player reaches or exceeds 1000 points.
         * The winner is the player who scored the highest points (maximum total score).
         */
        fun determineWinner(players: List<Player>): Player? {
            return if (players.any { it.totalScore >= 1000 }) {
                // If one or more players reach or exceed 1000, the player with the highest total score wins.
                // In case of equal highest scores among 1000+ finishers, pick the highest scorer.
                players.filter { it.totalScore >= 1000 }.maxByOrNull { it.totalScore }
                    ?: players.maxByOrNull { it.totalScore }
            } else {
                null
            }
        }
    }
}
