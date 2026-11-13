package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `round by round scoring continues until a player reaches 1000 points or above`() {
    val playerIds = listOf(1, 2, 3, 4)
    val roundScores = mutableListOf<Map<Int, Int>>()
    val grandTotals = mutableMapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0)

    // Simulate rounds (360 points total per round) until any player reaches >= 1000
    var round = 1
    while (grandTotals.values.none { it >= 1000 }) {
      val roundMap = when (round % 3) {
        1 -> mapOf(1 to 140, 2 to 100, 3 to 70, 4 to 50)
        2 -> mapOf(1 to 130, 2 to 110, 3 to 80, 4 to 40)
        else -> mapOf(1 to 150, 2 to 90, 3 to 80, 4 to 40)
      }
      assertEquals(360, roundMap.values.sum())
      roundScores.add(roundMap)
      playerIds.forEach { id ->
        grandTotals[id] = (grandTotals[id] ?: 0) + (roundMap[id] ?: 0)
      }
      round++
    }

    // A player reached >= 1000
    val winner = grandTotals.entries.first { it.value >= 1000 }
    assertTrue(winner.value >= 1000)
    assertTrue(roundScores.isNotEmpty())
  }
}
