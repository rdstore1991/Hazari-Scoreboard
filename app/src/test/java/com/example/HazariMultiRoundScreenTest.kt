package com.example

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.Player
import com.example.ui.scoring.HazariMultiRoundScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.LocalStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HazariMultiRoundScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val samplePlayers = listOf(
        Player(id = 1, name = "Rahim", totalScore = 0),
        Player(id = 2, name = "Karim", totalScore = 0),
        Player(id = 3, name = "Sakib", totalScore = 0),
        Player(id = 4, name = "Abir", totalScore = 0)
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStorage.init(context)
        LocalStorage.removeItem("multi_round_drafts")
        LocalStorage.removeItem("multi_round_visible_count")
    }

    @Test
    fun displays4PlayersAndStandard10RoundsAndCanAddRound11Plus() {
        composeTestRule.setContent {
            MyApplicationTheme {
                HazariMultiRoundScreen(
                    players = samplePlayers,
                    scoreEntries = emptyList()
                )
            }
        }

        // Verify all 4 players appear (in Match Standings and Score Sheet table header)
        samplePlayers.forEach { player ->
            composeTestRule.onAllNodesWithText(player.name).assertCountEquals(2)
        }

        // Verify 10 standard rounds R1..R10 exist
        for (round in 1..10) {
            composeTestRule.onNodeWithTag("multi_round_row_$round").performScrollTo().assertIsDisplayed()
        }

        // Click Add Round to add Round 11
        composeTestRule.onNodeWithTag("add_round_button").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("multi_round_row_11").performScrollTo().assertIsDisplayed()

        // Click Add Round again to add Round 12
        composeTestRule.onNodeWithTag("add_round_button").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("multi_round_row_12").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun inputtingValid360ScoresFor4PlayersSavesRound() {
        var savedPoints: List<Int>? = null

        composeTestRule.setContent {
            MyApplicationTheme {
                HazariMultiRoundScreen(
                    players = samplePlayers,
                    scoreEntries = emptyList(),
                    onSaveRound = { pts, onDone ->
                        savedPoints = pts
                        onDone()
                    }
                )
            }
        }

        composeTestRule.onNodeWithTag("multi_round_input_1_0").performTextInput("120")
        composeTestRule.onNodeWithTag("multi_round_input_1_1").performTextInput("100")
        composeTestRule.onNodeWithTag("multi_round_input_1_2").performTextInput("80")
        composeTestRule.onNodeWithTag("multi_round_input_1_3").performTextInput("60")

        composeTestRule.onNodeWithTag("save_round_row_1").performScrollTo().performClick()

        assertEquals(listOf(120, 100, 80, 60), savedPoints)
    }

    @Test
    fun autoFillRemainderCompletesFourthPlayerTo360() {
        var savedPoints: List<Int>? = null

        composeTestRule.setContent {
            MyApplicationTheme {
                HazariMultiRoundScreen(
                    players = samplePlayers,
                    scoreEntries = emptyList(),
                    onSaveRound = { pts, onDone ->
                        savedPoints = pts
                        onDone()
                    }
                )
            }
        }

        // Enter scores for first 3 players (sum = 270 -> remainder = 90)
        composeTestRule.onNodeWithTag("multi_round_input_1_0").performTextInput("100")
        composeTestRule.onNodeWithTag("multi_round_input_1_1").performTextInput("90")
        composeTestRule.onNodeWithTag("multi_round_input_1_2").performTextInput("80")

        // Tap autofill chip for Round 1
        composeTestRule.onNodeWithTag("autofill_round_1").performClick()

        // Save Round 1
        composeTestRule.onNodeWithTag("save_round_row_1").performScrollTo().performClick()

        assertEquals(listOf(100, 90, 80, 90), savedPoints)
    }

    @Test
    fun saveMultipleRoundsAcross11RoundsPersistsInRoom() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.playerDao()

        dao.createNewGame(listOf("Rahim", "Karim", "Sakib", "Abir"))
        val players = dao.getAllPlayersFlow().first()
        val playerIds = players.map { it.id }

        // Save 11 rounds (10 standard + 1 extra) of 360 pts each
        val rounds = List(11) { listOf(90, 90, 90, 90) }
        dao.saveMultipleRounds(playerIds, rounds)

        val entries = dao.getAllScoreEntriesFlow().first()
        val groupedRounds = entries.groupBy { it.timestamp }
        assertEquals(11, groupedRounds.size)

        val updatedPlayers = dao.getAllPlayersFlow().first()
        updatedPlayers.forEach { player ->
            assertEquals(990, player.totalScore)
        }

        db.close()
    }
}
