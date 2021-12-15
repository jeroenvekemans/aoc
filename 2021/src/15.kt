import java.io.File
import java.lang.RuntimeException
import java.util.*
import kotlin.math.abs

fun main() {
    val board: List<List<Int>> =
        File("2021/src/15.txt").readLines().map { r -> r.map { Integer.valueOf(it.toString()) } }

    println(DayFifteen.solve(board))

    val dimension = board.size * 5
    val big = (0 until dimension).map { r ->
        (0 until dimension).map { c ->
            val update = (board[r % board.size][c % board.size] + r / board.size + c / board.size) % 9
            if (update == 0) 9 else update
        }
    }

    println(DayFifteen.solve(big))
}

data class Spot(val row: Int, val column: Int) {
    fun isValid(max: Int): Boolean {
        return row in 0..max && column in 0..max
    }
}

data class Path(val visited: List<Spot>, val accCost: Int, val destination: Spot) : Comparable<Path> {
    override fun compareTo(other: Path): Int {
        return this.accCost - other.accCost + this.distanceToDestination() - other.distanceToDestination()
    }

    private fun distanceToDestination(): Int {
        return abs(current().row - destination.row) + abs(current().column - destination.column)
    }

    fun reachedEnd(): Boolean {
        val current = current()

        return current.row == destination.row && current.column == destination.column
    }

    fun current(): Spot {
        return visited.first()
    }

}

object DayFifteen {

    fun solve(board: List<List<Int>>): Int {
        val dimension = board[0].count()

        val priorityQueue = PriorityQueue<Path>()
        val maxCostPerSpot = HashMap<Spot, Int>()

        priorityQueue.add(Path(listOf(Spot(0, 0)), 0, Spot(dimension - 1, dimension - 1)))

        while (priorityQueue.isNotEmpty()) {
            val path = priorityQueue.poll()

            val current = path.current()

            if (path.reachedEnd()) {
                return path.accCost
            }

            listOf(
                Spot(current.row - 1, current.column),
                Spot(current.row + 1, current.column),
                Spot(current.row, current.column - 1),
                Spot(current.row, current.column + 1)
            )
                .filter { it.isValid(dimension - 1) }
                .filter { !path.visited.contains(it) }
                .forEach { newSpot ->
                    val newAccCost = path.accCost + board[newSpot.row][newSpot.column]
                    val pathToConstruct = Path(
                        listOf(newSpot) + path.visited,
                        newAccCost,
                        Spot(dimension - 1, dimension - 1)
                    )

                    if (!maxCostPerSpot.contains(newSpot) || maxCostPerSpot[newSpot]!! > newAccCost) {
                        maxCostPerSpot[newSpot] = newAccCost
                        priorityQueue.add(pathToConstruct)
                    }
                }
        }

        throw RuntimeException("Could not find best path.")
    }

}