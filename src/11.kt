import java.io.File
import kotlin.math.abs

fun main() {
    val rows: List<String> = File("src/11.txt").readLines()
    val octopuses = rows.map { r -> r.toCharArray().map { Integer.valueOf(it.toString()) } }

    println(DayEleven.solve(octopuses))
}

data class Location(val row: Int, val col: Int) {
    fun isAdjacent(other: Location): Boolean {
        return abs(this.row - other.row) <= 1 && abs(this.col - other.col) <= 1
    }
}


object DayEleven {

    fun solve(octopuses: List<List<Int>>): Int {
        val transformed = octopuses
            .flatMapIndexed { rIndex, row ->
                row.mapIndexed { cIndex, cell ->
                    Pair(Location(rIndex, cIndex), cell)
                }
            }
            .associateBy({ it.first }, { it.second })

        return (1..300).fold(Pair(transformed, 0)) { acc, step ->
            val result = performStep(acc.first)

            val updated = result.first + result.second.associateBy({ it }, { 0 })

            if (result.first.isEmpty()) {
                println("ALL FLASH STEP $step")
            }

            Pair(updated, acc.second + result.second.size)
        }.second
    }

    private fun performStep(transformed: Map<Location, Int>): Pair<Map<Location, Int>, Set<Location>> {
        val updated = transformed.mapValues { entry -> if (entry.value == 9) 0 else entry.value + 1 }

        val signals = updated.filterValues { it == 0 }
        val remaining = updated.filterValues { it != 0 }

        return ripple(remaining, signals.keys, signals.keys)
    }

    private fun ripple(remaining: Map<Location, Int>, signals: Set<Location>, accumulatedSignals: Set<Location>): Pair<Map<Location, Int>, Set<Location>> {
        val updated = remaining.mapValues { entry ->
            val adjacentSignals = signals.count { it.isAdjacent(entry.key) }
            val newLevel = entry.value + adjacentSignals
            if (newLevel > 9) 0 else newLevel
        }

        val newSignals = updated.filterValues { it == 0 }.keys
        val newRemaining = updated.filterValues { it != 0 }

        return if (newSignals.isEmpty()) Pair(updated, accumulatedSignals) else ripple(newRemaining,  newSignals, accumulatedSignals + newSignals)
    }

}