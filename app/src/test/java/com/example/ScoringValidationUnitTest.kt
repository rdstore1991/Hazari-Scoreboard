package com.example

import com.example.data.Player
import com.example.ui.scoring.ScoringInputUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoringValidationUnitTest {

    @Test
    fun testValidRoundScoreTotal() {
        val players = listOf(
            Player(id = 1, name = "Player 1", totalScore = 0),
            Player(id = 2, name = "Player 2", totalScore = 0),
            Player(id = 3, name = "Player 3", totalScore = 0),
            Player(id = 4, name = "Player 4", totalScore = 0)
        )

        val state = ScoringInputUiState(
            scores = mapOf(
                1 to "100",
                2 to "80",
                3 to "120",
                4 to "60"
            )
        )

        assertEquals(360, state.currentTotal)
        assertEquals(0, state.remainingPoints)
        assertTrue(state.isValidTotal)
        assertFalse(state.isOverTarget)
        assertFalse(state.isUnderTarget)
        assertTrue(state.canSave(players))
    }

    @Test
    fun testUnderTargetRoundScoreTotal() {
        val players = listOf(
            Player(id = 1, name = "Player 1", totalScore = 0),
            Player(id = 2, name = "Player 2", totalScore = 0),
            Player(id = 3, name = "Player 3", totalScore = 0),
            Player(id = 4, name = "Player 4", totalScore = 0)
        )

        val state = ScoringInputUiState(
            scores = mapOf(
                1 to "100",
                2 to "50",
                3 to "100",
                4 to "50"
            )
        )

        assertEquals(300, state.currentTotal)
        assertEquals(60, state.remainingPoints)
        assertFalse(state.isValidTotal)
        assertFalse(state.isOverTarget)
        assertTrue(state.isUnderTarget)
        assertFalse(state.canSave(players))
    }

    @Test
    fun testOverTargetRoundScoreTotal() {
        val players = listOf(
            Player(id = 1, name = "Player 1", totalScore = 0),
            Player(id = 2, name = "Player 2", totalScore = 0)
        )

        val state = ScoringInputUiState(
            scores = mapOf(
                1 to "200",
                2 to "200"
            )
        )

        assertEquals(400, state.currentTotal)
        assertEquals(-40, state.remainingPoints)
        assertFalse(state.isValidTotal)
        assertTrue(state.isOverTarget)
        assertFalse(state.canSave(players))
    }

    @Test
    fun testDynamicPlayerCountAutoFill() {
        // Test with 3 players
        val players3 = listOf(
            Player(id = 10, name = "Alice", totalScore = 150),
            Player(id = 20, name = "Bob", totalScore = 200),
            Player(id = 30, name = "Charlie", totalScore = 180)
        )
        val playerIds3 = players3.map { it.id }

        val state = ScoringInputUiState(
            scores = mapOf(
                10 to "120",
                20 to "140"
            )
        )

        // Charlie should balance to 360 - (120 + 140) = 100
        val charlieRemainder = state.calculateRemainderFor(30, playerIds3)
        assertEquals(100, charlieRemainder)
        assertTrue(state.canAutoFill(30, playerIds3))

        // When Charlie's score is applied:
        val balancedState = state.copy(
            scores = state.scores + (30 to charlieRemainder.toString())
        )
        assertTrue(balancedState.isValidTotal)
        assertTrue(balancedState.canSave(players3))
    }

    @Test
    fun testWinnerIsPlayerWithHighestPointsWhenPlayer1ScoresHigherThanPlayer2() {
        // Player 2 has lower ID (1) and reached 1000, but Player 1 has ID (2) and scored higher (1150 vs 1050)
        val players = listOf(
            Player(id = 1, name = "Player 2", totalScore = 1050),
            Player(id = 2, name = "Player 1", totalScore = 1150)
        )

        val winner = com.example.ui.ScoreboardViewModel.determineWinner(players)
        assertEquals("Player 1", winner?.name)
        assertEquals(1150, winner?.totalScore)
    }

    @Test
    fun testWinnerWhenPlayer1Crosses1000AndPlayer2Under1000() {
        val players = listOf(
            Player(id = 1, name = "Player 1", totalScore = 1080),
            Player(id = 2, name = "Player 2", totalScore = 960)
        )

        val winner = com.example.ui.ScoreboardViewModel.determineWinner(players)
        assertEquals("Player 1", winner?.name)
        assertEquals(1080, winner?.totalScore)
    }

    @Test
    fun testWinnerWhenPlayer2ScoresHigherThanPlayer1() {
        val players = listOf(
            Player(id = 1, name = "Player 1", totalScore = 1020),
            Player(id = 2, name = "Player 2", totalScore = 1180)
        )

        val winner = com.example.ui.ScoreboardViewModel.determineWinner(players)
        assertEquals("Player 2", winner?.name)
        assertEquals(1180, winner?.totalScore)
    }

    @Test
    fun testNoWinnerWhenScoresAreBelow1000() {
        val players = listOf(
            Player(id = 1, name = "Player 1", totalScore = 950),
            Player(id = 2, name = "Player 2", totalScore = 800)
        )

        val winner = com.example.ui.ScoreboardViewModel.determineWinner(players)
        org.junit.Assert.assertNull(winner)
    }

    @Test
    fun testHighestScorerWinsAmongMultiple1000PointFinishers() {
        val players = listOf(
            Player(id = 1, name = "Player A", totalScore = 1010),
            Player(id = 2, name = "Player B", totalScore = 1240),
            Player(id = 3, name = "Player C", totalScore = 1080),
            Player(id = 4, name = "Player D", totalScore = 800)
        )

        val winner = com.example.ui.ScoreboardViewModel.determineWinner(players)
        assertEquals("Player B", winner?.name)
        assertEquals(1240, winner?.totalScore)
    }
}
