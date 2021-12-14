import java.io.File

fun main() {
    val text = File("2021/src/13.txt").readLines()

    val dots: List<Dot> = text
        .filter { !it.startsWith("fold along") }
        .filter { it.isNotEmpty() }
        .map {
            val match = """(\d+),(\d+)""".toRegex().find(it)
            val (x, y) = match!!.destructured
            Dot(Integer.valueOf(x), Integer.valueOf(y))
        }

    val folds = text
        .filter { it.startsWith("fold along") }
        .map {
            val match = """fold along ([a-z]+)=(\d+)""".toRegex().find(it)
            val (direction, index) = match!!.destructured
            Fold(if (direction == "x") Direction.VERTICAL else Direction.HORIZONTAL, Integer.valueOf(index))
        }

    val dotsAfterFolding = DayThirteen.solve(dots.toSet(), folds)

    println(dotsAfterFolding.size)
    DayThirteen.print(dotsAfterFolding)
}

enum class Direction {
    HORIZONTAL,
    VERTICAL
}

data class Fold(val direction: Direction, val index: Int)
typealias Dot = Pair<Int, Int>

object DayThirteen {

    fun solve(dots: Set<Dot>, folds: List<Fold>): Set<Dot> {
        val end = folds.fold(dots) { acc, fold ->
            acc.map {
                if (fold.direction == Direction.HORIZONTAL && it.second > fold.index) {
                    Dot(it.first, it.second - (it.second - fold.index) * 2)
                } else if (fold.direction == Direction.VERTICAL && it.first > fold.index) {
                    Dot(it.first - (it.first - fold.index) * 2, it.second)
                } else {
                    it
                }
            }.toSet()
        }

        return end
    }

    fun print(dots: Set<Dot>) {
        val maxX = dots.maxOf { it.first}
        val maxY = dots.maxOf { it.second }

        for (y in (0..maxY)) {
            for (x in (0..maxX)) {
                if (dots.contains(Dot(x, y))) {
                    print("X")
                } else {
                    print(".")
                }
            }
            println()
        }
    }

}