import java.io.File
import java.lang.Integer.valueOf
import kotlin.math.abs

enum class Direction { U, R, D, L }
data class Instruction(val direction: Direction, val steps: Int)
data class Position(val row: Int, val col: Int) {
    fun advance(direction: Direction): Position {
        return when(direction) {
            Direction.U -> this.copy(row = this.row + 1)
            Direction.D -> this.copy(row = this.row - 1)
            Direction.L -> this.copy(col = this.col - 1)
            Direction.R -> this.copy(col = this.col + 1)
        }
    }

    fun touches(other: Position): Boolean {
        return abs(this.row - other.row) <= 1 && abs(this.col - other.col) <= 1
    }
}
data class State(val head: Position, val tail: Position)

fun main() {
    val direction: List<Direction> = File("2022/src/09.txt")
        .readLines()
        .map { Instruction(Direction.valueOf(it[0].toString()), valueOf(it.substring(2)))  }
        .flatMap { ins -> (0 until ins.steps).map { ins.direction } }

    val result = direction.fold(Pair(listOf(Position(0,0)), listOf(Position(0, 0)))) { acc, next ->
        val heads = acc.first
        val tails = acc.second

        val head = heads[0].advance(next)

        if (head.touches(tails[0])) {
            acc.copy(first = listOf(head) + heads)
        } else {
            acc.copy(
                first = listOf(head) + heads,
                second = listOf(heads[0]) + tails
            )
        }
    }

    println(result.second.toSet().size)
}