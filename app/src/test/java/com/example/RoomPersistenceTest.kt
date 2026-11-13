package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.Player
import com.example.data.PlayerDao
import com.example.data.ScoreEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomPersistenceTest {

    private lateinit var db: AppDatabase
    private lateinit var playerDao: PlayerDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        playerDao = db.playerDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testGamePersistenceAcrossRounds() = runBlocking {
        // 1. Create a new game with 4 players
        val playerNames = listOf("Rahim", "Karim", "Sakib", "Abir")
        playerDao.createNewGame(playerNames)

        val initialPlayers = playerDao.getAllPlayersFlow().first()
        assertEquals(4, initialPlayers.size)
        assertTrue(initialPlayers.all { it.totalScore == 0 })

        val playerIds = initialPlayers.map { it.id }

        // 2. Save Round 1 scores (sum = 360)
        val round1Scores = listOf(140, 100, 70, 50)
        playerDao.saveRoundScores(playerIds, round1Scores)

        val playersAfterRound1 = playerDao.getAllPlayersFlow().first()
        assertEquals(140, playersAfterRound1.first { it.name == "Rahim" }.totalScore)
        assertEquals(100, playersAfterRound1.first { it.name == "Karim" }.totalScore)
        assertEquals(70, playersAfterRound1.first { it.name == "Sakib" }.totalScore)
        assertEquals(50, playersAfterRound1.first { it.name == "Abir" }.totalScore)

        val entriesAfterRound1 = playerDao.getAllScoreEntriesFlow().first()
        assertEquals(4, entriesAfterRound1.size)

        // 3. Save Round 2 scores (sum = 360)
        val round2Scores = listOf(120, 110, 80, 50)
        playerDao.saveRoundScores(playerIds, round2Scores)

        val playersAfterRound2 = playerDao.getAllPlayersFlow().first()
        assertEquals(260, playersAfterRound2.first { it.name == "Rahim" }.totalScore)
        assertEquals(210, playersAfterRound2.first { it.name == "Karim" }.totalScore)
        assertEquals(150, playersAfterRound2.first { it.name == "Sakib" }.totalScore)
        assertEquals(100, playersAfterRound2.first { it.name == "Abir" }.totalScore)

        val entriesAfterRound2 = playerDao.getAllScoreEntriesFlow().first()
        assertEquals(8, entriesAfterRound2.size)

        // 4. Test Undo Last Round
        playerDao.undoLastRound()
        val playersAfterUndo = playerDao.getAllPlayersFlow().first()
        assertEquals(140, playersAfterUndo.first { it.name == "Rahim" }.totalScore)
        assertEquals(100, playersAfterUndo.first { it.name == "Karim" }.totalScore)
        val entriesAfterUndo = playerDao.getAllScoreEntriesFlow().first()
        assertEquals(4, entriesAfterUndo.size)
    }

    @Test
    fun testReaching1000PointsWinnerThreshold() = runBlocking {
        val playerNames = listOf("Player A", "Player B")
        playerDao.createNewGame(playerNames)

        val players = playerDao.getAllPlayersFlow().first()
        val pIds = players.map { it.id }

        // Simulate high scoring rounds until Player A reaches >= 1000
        playerDao.saveRoundScores(pIds, listOf(300, 60))
        playerDao.saveRoundScores(pIds, listOf(300, 60))
        playerDao.saveRoundScores(pIds, listOf(300, 60))
        playerDao.saveRoundScores(pIds, listOf(200, 160))

        val finalPlayers = playerDao.getAllPlayersFlow().first()
        val winner = finalPlayers.firstOrNull { it.totalScore >= 1000 }
        assertNotNull(winner)
        assertEquals("Player A", winner?.name)
        assertEquals(1100, winner?.totalScore)
    }
}
