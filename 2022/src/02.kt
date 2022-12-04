import java.io.File
import java.lang.Exception
import java.lang.Math.abs

enum class Move(val score: Int) {
    ROCK(1) {
        override fun defeats() = SCISSOR
        override fun loses() = PAPER
    },
    PAPER(2) {
        override fun defeats() = ROCK
        override fun loses() = SCISSOR
    },
    SCISSOR(3) {
        override fun defeats() = PAPER
        override fun loses() = ROCK
    };

    abstract fun defeats(): Move
    abstract fun loses(): Move
}


fun main() {
    data class Round(val opponent: Char, val me: Char) {
        fun score(): Int {
            val opponentMove = when(opponent) {
                'A' -> Move.ROCK
                'B' -> Move.PAPER
                'C' -> Move.SCISSOR
                else -> throw Exception()
            }

            val move = when(me) {
                'X' -> opponentMove.defeats()
                'Y' -> opponentMove
                'Z' -> opponentMove.loses()
                else -> throw Exception()
            }


            val score = if (move.defeats() == opponentMove) {
                6
            } else {
                if (move == opponentMove) {
                    3
                } else {
                    0
                }
            }

            return score + move.score
        }
    }

    val rounds: List<Round> = File("2022/src/02.txt").readLines().map { raw -> Round(raw[0], raw[2]) }

    println(rounds.sumOf { it.score() })
}