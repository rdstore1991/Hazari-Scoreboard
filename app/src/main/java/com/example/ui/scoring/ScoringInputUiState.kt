package com.example.ui.scoring

import com.example.data.Player

/**
 * State representation for the round scoring input screen.
 * Hazari requires exactly 360 points in total per round distributed across the 4 players.
 */
data class ScoringInputUiState(
    val scores: Map<Int, String> = emptyMap(), // playerId -> raw text input
    val targetTotal: Int = 360,
    val roundNumber: Int = 1,
    val validationMessage: String? = null,
    val saveSuccessEvent: Boolean = false
) {
    /**
     * Parses the current score for a player, returning 0 if empty or invalid.
     */
    fun getScore(playerId: Int): Int = scores[playerId]?.toIntOrNull() ?: 0

    /**
     * Calculates the projected total score for a player after this round is saved.
     */
    fun getProjectedTotal(player: Player): Int = player.totalScore + getScore(player.id)

    /**
     * Sum of all entered player scores for the current round.
     */
    val currentTotal: Int
        get() = scores.values.sumOf { it.toIntOrNull() ?: 0 }

    /**
     * Remaining points needed to reach exactly the target total (360).
     */
    val remainingPoints: Int
        get() = targetTotal - currentTotal

    /**
     * Absolute difference from the target total.
     */
    val absoluteDiff: Int
        get() = kotlin.math.abs(remainingPoints)

    /**
     * True if the round total is valid (sum equals 360).
     */
    val isValidTotal: Boolean
        get() = currentTotal == targetTotal

    /**
     * True if the current score sum exceeds 360.
     */
    val isOverTarget: Boolean
        get() = currentTotal > targetTotal

    /**
     * True if the current score sum is under 360.
     */
    val isUnderTarget: Boolean
        get() = currentTotal < targetTotal

    /**
     * Calculates what score would balance the round to exactly 360 for this specific player.
     */
    fun calculateRemainderFor(playerId: Int, allPlayerIds: List<Int>): Int {
        val otherScoresSum = allPlayerIds
            .filter { it != playerId }
            .sumOf { scores[it]?.toIntOrNull() ?: 0 }
        return (targetTotal - otherScoresSum).coerceIn(0, targetTotal)
    }

    /**
     * Determines if auto-fill remainder is viable for this player.
     */
    fun canAutoFill(playerId: Int, allPlayerIds: List<Int>): Boolean {
        val otherScoresSum = allPlayerIds
            .filter { it != playerId }
            .sumOf { scores[it]?.toIntOrNull() ?: 0 }
        return otherScoresSum in 1..targetTotal
    }

    /**
     * Comprehensive check if the round scores can be safely persisted.
     */
    fun canSave(players: List<Player>): Boolean {
        if (players.size < 2) return false
        val total = players.sumOf { getScore(it.id) }
        val allValid = players.all {
            val score = getScore(it.id)
            score in 0..targetTotal
        }
        return total == targetTotal && allValid
    }
}
