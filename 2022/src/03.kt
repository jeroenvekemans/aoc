import java.io.File


fun main() {
    val itemsPerRuckSackRaw: List<String> = File("2022/src/03.txt").readLines()

    val sumOfPriorities = itemsPerRuckSackRaw
        .map { Pair(it.take(it.length / 2), it.takeLast(it.length / 2)) }
        .map { it.first.toCharArray().intersect(it.second.toCharArray().asIterable()) }
        .map { it.first() }
        .sumOf { priority(it) }


    println(sumOfPriorities)

    val elfGroups = itemsPerRuckSackRaw
        .chunked(3)
        .map {
            it[0].toCharArray().intersect(it[1].toCharArray().asIterable().intersect(it[2].toCharArray().asIterable()))
        }
        .map { it.first() }
        .sumOf { priority(it) }


    println(elfGroups)
}

private fun priority(it: Char) = if (it.isLowerCase()) it.code - ('a'.code - 1) else it.code - ('A'.code - 1) + 26