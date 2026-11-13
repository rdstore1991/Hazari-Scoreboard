package com.example.data

import kotlinx.coroutines.flow.Flow

class ScoreboardRepository(private val playerDao: PlayerDao) {
    val allPlayers: Flow<List<Player>> = playerDao.getAllPlayersFlow()
    val allScoreEntries: Flow<List<ScoreEntry>> = playerDao.getAllScoreEntriesFlow()

    suspend fun getPlayerById(id: Int): Player? = playerDao.getPlayerById(id)

    suspend fun addPlayer(name: String): Long {
        return playerDao.insertPlayer(Player(name = name, totalScore = 0))
    }

    suspend fun addScore(playerId: Int, points: Int) {
        playerDao.addScoreToPlayer(playerId, points)
    }

    suspend fun createNewGame(names: List<String>) {
        playerDao.createNewGame(names)
    }

    suspend fun saveRoundScores(playerIds: List<Int>, pointsList: List<Int>) {
        playerDao.saveRoundScores(playerIds, pointsList)
    }

    suspend fun updateRoundScores(timestamp: Long, playerIds: List<Int>, pointsList: List<Int>) {
        playerDao.updateRoundScores(timestamp, playerIds, pointsList)
    }

    suspend fun deleteRound(timestamp: Long) {
        playerDao.deleteRound(timestamp)
    }

    suspend fun undoLastRound() {
        playerDao.undoLastRound()
    }

    suspend fun undoLastScore() {
        playerDao.undoLastScore()
    }

    suspend fun resetGame() {
        playerDao.resetGame()
    }

    suspend fun restoreGame(playersList: List<Player>, scoreEntriesList: List<ScoreEntry>) {
        playerDao.restoreGame(playersList, scoreEntriesList)
    }

    suspend fun deletePlayer(player: Player) {
        playerDao.deletePlayerAndScores(player)
    }

    suspend fun getLatestScoreEntry(): ScoreEntry? = playerDao.getLatestScoreEntry()
}
