import java.io.File
import java.lang.Integer.valueOf
import kotlin.math.abs
import kotlin.math.absoluteValue

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

    val startPositions = (0 until 10).map { listOf(Position(0, 0)) }

    val result = direction.fold(startPositions) { acc, next ->
        val leading = acc[0]
        val head = leading[0].advance(next)

        simulateStep(listOf(head) + leading, acc.drop(1))
    }

    val visited = result[9].toSet()
    println(visited.size)
}

fun simulateStep(leadingPositions: List<Position>, remainingPositions: List<List<Position>>): List<List<Position>> {
    if (remainingPositions.isEmpty()) {
        return listOf(leadingPositions);
    }

    val nextPositionsToEvaluate = remainingPositions[0]

    val head = leadingPositions[0]
    val tail = nextPositionsToEvaluate[0]

    return if (head.touches(tail)) {
        listOf(leadingPositions) + simulateStep(remainingPositions.first(), remainingPositions.drop(1))
    } else {
        val diffRow = head.row - tail.row
        val diffCol = head.col - tail.col

        val newPositionForTail = if (diffRow.absoluteValue + diffCol.absoluteValue > 2) {
            val newRow = if (diffRow < 0) -1 else 1
            val newCol = if (diffCol < 0) -1 else 1

            Position(tail.row + newRow, tail.col + newCol)
        } else {
            val newRow = if (diffRow == 0) 0 else if (diffRow < 0) -1 else 1
            val newCol = if (diffCol == 0) 0 else if (diffCol < 0) -1 else 1

            Position(tail.row + newRow, tail.col + newCol)
        }

        val remainderExtendedWithTouchingPosition = listOf(newPositionForTail) + remainingPositions.first()
        listOf(leadingPositions) + simulateStep(remainderExtendedWithTouchingPosition, remainingPositions.drop(1))
    }
}