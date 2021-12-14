import java.io.File
import kotlin.math.ceil
import kotlin.math.max

fun main() {
    val text = File("src/14.txt").readLines()

    val template = text.first()

    val insertions = text.drop(2).map {
        val match = """([a-zA-z]+) -> ([a-zA-z]+)""".toRegex().find(it)
        val (base, insertion) = match!!.destructured
        Pair(base, insertion.toCharArray()[0])
    }.associate { it.first to it.second }

    println(DayFourteen.solve(template, insertions))
    println(DayFourteen.solveDelta(template, insertions))
}

object DayFourteen {

    fun solve(template: String, insertions: Map<String, Char>): Int {
        val transformed = (1..10).fold(template) { acc, _ ->
            acc.windowed(2, 1, true).map {
                if (insertions.contains(it)) {
                    val newCharacter = insertions[it]
                    it[0].toString() + newCharacter
                } else {
                    it[0]
                }
            }.joinToString("")
        }

        val occurrences = transformed.groupingBy { it }.eachCount()

        println(occurrences)

        return occurrences.entries.maxOf { it.value } - occurrences.entries.minOf { it.value }
    }

    fun solveDelta(template: String, insertions: Map<String, Char>): Long {
        val pairs = template.windowed(2, 1).groupingBy { it }.eachCount().mapValues { it.value.toLong() }

        val transformed = (1..40).fold(pairs) { acc, _ ->
            val diffs = acc
                .flatMap { pair ->
                    val key = pair.key
                    if (insertions.containsKey(key)) {
                        val replacement = insertions[key]!!
                        listOf(
                            Pair(key, -1 * pair.value),
                            Pair(key[0].toString() + replacement, pair.value),
                            Pair(replacement + key[1].toString(), pair.value)
                        )
                    } else {
                        emptyList()
                    }
                }
                .groupBy({ it.first }, { it.second })
                .mapValues { it.value.sum() }

            (acc.keys + diffs.keys).toSet().associateWith { key -> max(0L, acc.getOrDefault(key, 0L).plus(diffs.getOrDefault(key, 0L))) }
        }

        val occurrences = transformed
            .flatMap { listOf(Pair(it.key[0], it.value), Pair(it.key[1], it.value)) }
            .groupBy({ it.first }, { it.second })
            .mapValues { ceil(it.value.sum() / 2.0).toLong() }

        return occurrences.maxOf { it.value } - occurrences.minOf { it.value }
    }

}