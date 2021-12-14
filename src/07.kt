import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

fun main() {
    val positions: List<String> = File("src/07.txt").readLines()
    val posses = positions[0].split(",").map { Integer.valueOf(it) }

    println(DaySeven.solve(posses))
    println(DaySevenMedian.solve(posses))
}

object DaySevenMedian {
    private fun cost(positions: List<Int>, position: Int): Int {
        return positions.sumOf { p -> abs(p - position) }
    }

    private fun median(l: List<Double>) = l.sorted().let { (it[it.size/2] + it[(it.size - 1)/2])/2 }

    fun solve(positions: List<Int>) : Int {
        val median = median(positions.map { it.toDouble() }).roundToInt()

        return cost(positions, median)
    }
}

object DaySeven {

    private fun cost(positions: List<Int>, position: Int): Int {
        return positions.sumOf { p -> abs(p - position) }
    }

    private fun costDelta(positions: List<Int>, position: Int): Int {
        return positions.sumOf { pos ->
            val n = abs(pos - position)
            (n * (n + 1)) / 2
        }
    }

    fun solve(positions: List<Int>) : Int {
        val min = positions.minOf { it }
        val max = positions.maxOf { it }

        return (min until max).minOf { costDelta(positions, it) }
    }

}