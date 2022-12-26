import java.io.File
import java.util.*
import kotlin.math.abs


data class ValleyPosition(val row: Int, val col: Int)
data class Blizzard(val row: Int, val col: Int, val rowRange: IntProgression, val colRange: IntProgression, val evolve: Int) {
    fun advance(): Blizzard {
        val newRow = if (row + evolve in rowRange) row + evolve else rowRange.first
        val newCol = if (col + evolve in colRange) col + evolve else colRange.first

        return this.copy(row = newRow, col = newCol)
    }
}

data class ExpeditionState(
    val path: List<ValleyPosition>,
    val end: ValleyPosition,
) : Comparable<ExpeditionState> {

    private fun cost(): Int = path.size

    private fun heuristic(): Int {
        val current = path[0]
        return abs(current.row - end.row) + abs(current.col - end.col)
    }

    override fun compareTo(other: ExpeditionState): Int {
        return this.cost() - other.cost() + this.heuristic() - other.heuristic()
    }

    fun reachedEnd(): Boolean = this.path[0] == end

    fun nextStates(walls: List<ValleyPosition>, updatedBlizzards: Set<Blizzard>, max: Pair<Int, Int>): List<ExpeditionState> {
        val current = path[0]

        val possibleMoves = listOf(
            Pair(1, 0),
            Pair(-1, 0),
            Pair(0, 1),
            Pair(0, -1),
            Pair(0, 0)
        )
            .map {
                ValleyPosition(it.first + current.row, it.second + current.col)
            }
            .filter { it.row >= 0 && it.col >= 0 && it.row < max.first && it.col < max.second }
            .filter { !walls.contains(it) }
            .filter { position -> updatedBlizzards.none { blizzard -> blizzard.row == position.row && blizzard.col == position.col } }

        return possibleMoves.map { this.copy(path = listOf(it) + this.path) }
    }
}

fun main() {
    val raw: List<String> = File("2022/src/24.txt").readLines()

    for (row in raw) {
        println(row)
    }

    val height = raw.size
    val width = raw[0].length
    val start = ValleyPosition(0, 1)
    val end = ValleyPosition(height - 1, width - 2)

    val blizzards = raw.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, cell ->
            when (cell) {
                '>' -> Blizzard(rowIndex, colIndex, (rowIndex .. rowIndex), (1 until width - 1), 1)
                '<' -> Blizzard(rowIndex, colIndex, (rowIndex .. rowIndex), (width - 2 downTo  1), -1)
                'v' -> Blizzard(rowIndex, colIndex, (1 until height - 1), (colIndex..colIndex), 1)
                '^' -> Blizzard(rowIndex, colIndex, (height - 2 downTo  1), (colIndex..colIndex), -1)
                else -> null
            }
        }
    }.filterNotNull().toSet()

    val walls =
        ((0 until width).map { ValleyPosition(0, it) } - start) +
                ((0 until width).map { ValleyPosition(height - 1, it) } - end) +
                ((1 until height - 1).flatMap { h -> listOf(ValleyPosition(h, 0), ValleyPosition(h, width - 1)) })

    val max = Pair(height, width)
    val initial = ExpeditionState(
        listOf(start),
        end,
    )
    val result = searchForOptimalExpedition(initial, walls, blizzards, max)

    println(result.path.reversed())
    println(result.path.size)

    val result2 = searchForOptimalExpedition(
        ExpeditionState(
            result.path,
            start,
        ), walls, blizzards, max
    )
    println(result2.path.reversed())
    println(result2.path.size)


    val result3 = searchForOptimalExpedition(
        ExpeditionState(
            result2.path,
            end,
        ), walls, blizzards, max
    )
    println(result3.path.reversed())
    println(result3.path.size)
}

fun searchForOptimalExpedition(
    start: ExpeditionState,
    walls: List<ValleyPosition>,
    blizzards: Set<Blizzard>,
    max: Pair<Int, Int>
): ExpeditionState {
    val queue = PriorityQueue<ExpeditionState>()

    queue.add(start)

    val situations = mutableSetOf<Pair<ValleyPosition, Int>>()
    val blizzardsAtMinute = mutableMapOf<Int, Set<Blizzard>>()

    while (queue.isNotEmpty()) {
        val state = queue.poll()

        if (state.reachedEnd()) {
            return state
        }

        val updatedBlizzards = blizzardsAtMinute
            .computeIfAbsent(state.path.size) { minutes ->
                (0 until minutes).fold(blizzards) { acc, _ -> acc.map { it.advance() }.toSet() }
            }

//        println(state.path.reversed().map { Pair(it.row, it.col) }.joinToString(","))

        val situation = Pair(state.path[0], state.path.size)
        if (situations.contains(situation)) continue
        situations.add(situation)

        state.nextStates(walls,updatedBlizzards,max).forEach { queue.add(it) }
    }

    throw RuntimeException("Could not find best path.")
}
