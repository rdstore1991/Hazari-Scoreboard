package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY id ASC")
    fun getAllPlayersFlow(): Flow<List<Player>>

    @Query("SELECT * FROM score_entries ORDER BY timestamp DESC, id DESC")
    fun getAllScoreEntriesFlow(): Flow<List<ScoreEntry>>

    @Query("SELECT * FROM players ORDER BY id ASC")
    suspend fun getAllPlayers(): List<Player>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: Int): Player?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player): Long

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScoreEntry(entry: ScoreEntry): Long

    @Query("SELECT * FROM score_entries ORDER BY timestamp DESC, id DESC LIMIT 1")
    suspend fun getLatestScoreEntry(): ScoreEntry?

    @Delete
    suspend fun deleteScoreEntry(entry: ScoreEntry)

    @Query("DELETE FROM score_entries WHERE playerId = :playerId")
    suspend fun deleteScoreEntriesForPlayer(playerId: Int)

    @Query("DELETE FROM score_entries")
    suspend fun deleteAllScoreEntries()

    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()

    @Query("UPDATE players SET totalScore = 0")
    suspend fun resetAllPlayerScores()

    @Query("SELECT * FROM score_entries ORDER BY id DESC LIMIT 4")
    suspend fun getLatestFourScoreEntries(): List<ScoreEntry>

    @Transaction
    suspend fun createNewGame(names: List<String>) {
        deleteAllPlayers()
        deleteAllScoreEntries()
        for (name in names) {
            insertPlayer(Player(name = name, totalScore = 0))
        }
    }

    @Transaction
    suspend fun saveRoundScores(playerIds: List<Int>, pointsList: List<Int>) {
        val timestamp = System.currentTimeMillis()
        for (i in playerIds.indices) {
            val pId = playerIds[i]
            val pts = pointsList[i]
            val player = getPlayerById(pId) ?: continue
            val newScore = player.totalScore + pts
            updatePlayer(player.copy(totalScore = newScore))
            insertScoreEntry(ScoreEntry(playerId = pId, points = pts, timestamp = timestamp))
        }
    }

    @Query("SELECT timestamp FROM score_entries ORDER BY timestamp DESC, id DESC LIMIT 1")
    suspend fun getLatestRoundTimestamp(): Long?

    @Transaction
    suspend fun undoLastRound() {
        val latestTimestamp = getLatestRoundTimestamp() ?: return
        deleteScoreEntriesByTimestamp(latestTimestamp)
        recalculateAllPlayerTotalScores()
    }

    @Transaction
    suspend fun addScoreToPlayer(playerId: Int, points: Int) {
        val player = getPlayerById(playerId) ?: return
        val newScore = player.totalScore + points
        updatePlayer(player.copy(totalScore = newScore))
        insertScoreEntry(ScoreEntry(playerId = playerId, points = points))
    }

    @Transaction
    suspend fun undoLastScore() {
        val latest = getLatestScoreEntry() ?: return
        val player = getPlayerById(latest.playerId)
        if (player != null) {
            val restoredScore = (player.totalScore - latest.points).coerceAtLeast(0)
            updatePlayer(player.copy(totalScore = restoredScore))
        }
        deleteScoreEntry(latest)
    }

    @Transaction
    suspend fun resetGame() {
        resetAllPlayerScores()
        deleteAllScoreEntries()
    }

    @Transaction
    suspend fun restoreGame(playersList: List<Player>, scoreEntriesList: List<ScoreEntry>) {
        deleteAllPlayers()
        deleteAllScoreEntries()
        for (player in playersList) {
            insertPlayer(player)
        }
        for (entry in scoreEntriesList) {
            insertScoreEntry(entry)
        }
    }

    @Transaction
    suspend fun deletePlayerAndScores(player: Player) {
        deletePlayer(player)
        deleteScoreEntriesForPlayer(player.id)
    }

    @Query("DELETE FROM score_entries WHERE timestamp = :timestamp")
    suspend fun deleteScoreEntriesByTimestamp(timestamp: Long)

    @Query("UPDATE players SET totalScore = (SELECT COALESCE(SUM(points), 0) FROM score_entries WHERE playerId = players.id)")
    suspend fun recalculateAllPlayerTotalScores()

    @Transaction
    suspend fun updateRoundScores(timestamp: Long, playerIds: List<Int>, pointsList: List<Int>) {
        deleteScoreEntriesByTimestamp(timestamp)
        for (i in playerIds.indices) {
            val pId = playerIds[i]
            val pts = pointsList[i]
            insertScoreEntry(ScoreEntry(playerId = pId, points = pts, timestamp = timestamp))
        }
        recalculateAllPlayerTotalScores()
    }

    @Transaction
    suspend fun deleteRound(timestamp: Long) {
        deleteScoreEntriesByTimestamp(timestamp)
        recalculateAllPlayerTotalScores()
    }
}
