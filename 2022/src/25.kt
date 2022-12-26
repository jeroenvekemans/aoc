import java.io.File
import kotlin.math.abs
import kotlin.math.pow

class SnafuConverter {

    fun toDecimal(snafu: String): Long {
        val charWithIndex = snafu
            .reversed()
            .mapIndexed { index, character -> Pair(character, index) }

        return charWithIndex.fold(0) { acc, next ->
            val based = 5.0.pow(next.second).toLong()

            val change: Long = when (next.first) {
                '2' -> 2 * based
                '1' -> 1 * based
                '0' -> 0L
                '-' -> -1 * based
                '=' -> -2 * based
                else -> throw IllegalStateException()
            }

            acc + change
        }
    }

    fun toSnafu(decimal: Long): String {
        return (19 downTo 0).fold(Pair("", decimal)) { (snafu, remainder), next ->
            val based = 5.0.pow(next).toLong()

            val closest = listOf(
                Pair('2', 2 * based),
                Pair('1', 1 * based),
                Pair('0', 0L),
                Pair('-', -1 * based),
                Pair('=', -2 * based)
            ).minBy {
                abs(remainder - it.second)
            }

            Pair(snafu + closest.first, remainder - closest.second)
        }.first
    }

}

fun main() {
    val raw: List<String> = File("2022/src/25.txt").readLines()

    val converter = SnafuConverter()

    val sumOfDecimals = raw.sumOf { converter.toDecimal(it) }

    println(sumOfDecimals)

    val input = converter.toSnafu(sumOfDecimals)

    println(input)
}