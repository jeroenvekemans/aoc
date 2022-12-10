import java.io.File
import java.lang.Integer.valueOf

fun main() {
    val instructions: List<String> = File("2022/src/10.txt").readLines()


    val result = instructions.fold(Pair(1, listOf<Int>())) { acc, next ->
        val x = acc.first
        val cycles = acc.second

        when {
            next == "noop" -> Pair(x, cycles + listOf(x))
            next.startsWith("addx") -> {
                val increment = valueOf(next.substring(5))
                Pair(x+increment, cycles + listOf(x, x))
            }
            else -> acc
        }
    }

    val first = listOf(20, 60, 100, 140, 180, 220)
        .map {
            Pair(result.second[it - 1], it)
        }
        .sumOf { it.first * it.second }

    println(first)

    val second = result.second
        .chunked(40)
        .map { row ->
            row.mapIndexed { index, position ->
                if (index + 1 in listOf(position, position + 1, position + 2)) '#' else '.'
            }.joinToString("")
        }
        .joinToString("\n")

    println(second)
}