import kotlin.math.max

fun main() {
    println(DayTwentyOne.solveDelta(4, 8))
    println(DayTwentyOne.solveDelta(9, 3))
}

object DayTwentyOne {

    fun solve(
        diceSequence: List<List<Int>>,
        step: Int,
        playerPositionOne: Int,
        playerScoreOne: Int,
        playerPositionTwo: Int,
        playerScoreTwo: Int
    ): Int {
        val resultOne = (playerPositionOne + diceSequence[step % diceSequence.size].sum()) % 10
        val scoreOne = if (resultOne == 0) 10 else resultOne
        val newScoreOne = playerScoreOne + scoreOne

        val resultTwo = (playerPositionTwo + diceSequence[(step + 1) % diceSequence.size].sum()) % 10
        val scoreTwo = if (resultTwo == 0) 10 else resultTwo
        val newScoreTwo = playerScoreTwo + scoreTwo

        if (newScoreOne >= 1000) {
            return ((step + 1) * 3) * playerScoreTwo
        }

        if (newScoreTwo >= 1000) {
            return ((step + 2) * 3) * playerScoreOne
        }

        return solve(diceSequence, step + 2, scoreOne, newScoreOne, scoreTwo, newScoreTwo)
    }

    fun solve(playerPositionOne: Int, playerPositionTwo: Int): Int {
        val diceSequence = (1..300).windowed(3, 3)

        return solve(diceSequence, 0, playerPositionOne, 0, playerPositionTwo, 0)
    }

    data class Universe(
        val posOne: Int,
        val scoreOne: Int,
        val posTwo: Int,
        val scoreTwo: Int,
        val nextTurnOne: Boolean
    ) {
        fun isResolved(): Boolean {
            return playerOneWon() || playerTwoWon()
        }

        fun playerOneWon(): Boolean {
            return scoreOne >= 21
        }

        fun playerTwoWon(): Boolean {
            return scoreTwo >= 21
        }
    }

    fun solveDelta(positionOne: Int, positionTwo: Int): Long {
        val startUniverse = Universe(positionOne, 0, positionTwo, 0, true)

        val result = play(startUniverse)

        return max(result.first, result.second)
    }

    private val cache: MutableMap<Universe, Pair<Long, Long>> = mutableMapOf()

    private fun play(universe: Universe): Pair<Long, Long> {
        if (universe.isResolved()) {
            return if (universe.playerOneWon()) Pair(1,0) else Pair(0,1)
        }

        if (cache.containsKey(universe)) {
            return cache[universe]!!
        }

        return simulateTurns(universe)
            .map {
                val res = play(it.first)
                Pair(res.first * it.second, res.second * it.second)
            }
            .reduce { a,b -> Pair(a.first + b.first, a.second + b.second) }
            .also { cache[universe] = it }
    }

    private fun advance(position: Int, increment: Int): Int {
        return (position + increment - 1) % 10 + 1
    }

    private fun simulateTurns(universe: Universe): List<Pair<Universe, Long>> {
        val tries = (1..3).flatMap { one -> (1..3).flatMap { two -> (1..3).map { three -> one + two + three } } }.groupBy { it }.mapValues { it.value.size.toLong() }

        return if (universe.nextTurnOne) {
            tries.map { entry ->
                val dies = entry.key
                val nextPos = advance(universe.posOne, dies)
                Pair(Universe(
                    nextPos,
                    nextPos + universe.scoreOne,
                    universe.posTwo,
                    universe.scoreTwo,
                    !universe.nextTurnOne
                ), entry.value)
            }
        } else {
            tries.map { entry ->
                val dies = entry.key
                val nextPos = advance(universe.posTwo, dies)
                Pair(Universe(
                    universe.posOne,
                    universe.scoreOne,
                    nextPos,
                    nextPos + universe.scoreTwo,
                    !universe.nextTurnOne
                ), entry.value)
            }
        }
    }

}