import java.io.File

private const val DISTINCT_LENGTH = 14

fun main() {
    val raw: String = File("2022/src/06.txt").readLines()[0]

    val firstIndexWithDistinctChars = (0..raw.length - DISTINCT_LENGTH)
        .map { Pair(it, raw.drop(it).take(DISTINCT_LENGTH)) }
        .filter { it.second.toCharArray().toSet().size == DISTINCT_LENGTH }
        .take(1)


    println(firstIndexWithDistinctChars)
    println(firstIndexWithDistinctChars[0].first + DISTINCT_LENGTH)
}