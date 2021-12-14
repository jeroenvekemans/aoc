import java.io.File

fun main() {
    val lines: List<Line> = File("2021/src/12.txt").readLines().map {
        val match = """([a-zA-z]+)-([a-zA-z]+)""".toRegex().find(it)
        val (begin, destination) = match!!.destructured
        Line(begin, destination)
    }

    println(DayTwelve.solve(lines))
    println(DayTwelve.solveDelta(lines))
}

data class Line(val begin: String, val destination: String) {
    fun contains(point: String) : Boolean {
        return begin == point || destination == point
    }

    fun other(point: String) : String {
        return if (begin == point) destination else begin
    }
}

object DayTwelve {

    private fun traversePaths(pathInProgress: List<String>, lines: List<Line>): List<List<String>> {
        val current = pathInProgress.last()

        if (current == "end") {
            return listOf(pathInProgress)
        }

        return lines
            .filter { it.contains(current) }
            .map { it.other(current) }
            .filter { it[0].isUpperCase() || !pathInProgress.contains(it) }
            .flatMap { otherEnd -> traversePaths(pathInProgress + listOf(otherEnd), lines) }
    }

    private fun traversePathsDelta(pathInProgress: List<String>, lines: List<Line>): List<List<String>> {
        val current = pathInProgress.last()

        if (current == "end") {
            return listOf(pathInProgress)
        }

        return lines
            .filter { it.contains(current) }
            .map { it.other(current) }
            .filter {
                val smallCavesVisited = pathInProgress.filter { p -> p != "start" && p != "end" }.filter { p -> p[0].isLowerCase() }

                it[0].isUpperCase() || (smallCavesVisited.size == smallCavesVisited.toSet().size && it != "start") || !pathInProgress.contains(it)
            }
            .flatMap { otherEnd -> traversePathsDelta(pathInProgress + listOf(otherEnd), lines) }
    }


    fun solve(lines: List<Line>): Int {
        return traversePaths(listOf("start"), lines).size
    }

    fun solveDelta(lines: List<Line>): Int {
        return traversePathsDelta(listOf("start"), lines).size
    }

}