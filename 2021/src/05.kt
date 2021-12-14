import java.io.File
import kotlin.math.max
import kotlin.math.min

data class Coordinate(val x: Int, val y: Int)

fun main() {
    val lines: List<String> = File("2021/src/05.txt").readLines()

    val lineEnds =
        lines
            .map { text ->
                val match = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex().find(text)
                val (x1, y1, x2, y2) = match!!.destructured
                val start = Coordinate(Integer.valueOf(x1), Integer.valueOf(y1))
                val second = Coordinate(Integer.valueOf(x2), Integer.valueOf(y2))
                Pair(start, second)
            }

    val cells = lineEnds
        .flatMap { ends ->
            if (ends.first.x == ends.second.x)
                (min(ends.first.y, ends.second.y)..max(ends.first.y, ends.second.y)).map { y ->
                    Coordinate(
                        ends.first.x,
                        y
                    )
                }
            else if (ends.first.y == ends.second.y) (min(ends.first.x, ends.second.x)..max(
                ends.first.x,
                ends.second.x
            )).map { x -> Coordinate(x, ends.first.y) }
            else diagonalCells(ends)
        }

    val dangerousAreas = cells.groupingBy { it }.eachCount().filter { entry -> entry.value >= 2 }.count()

    println(dangerousAreas)
}

fun diagonalCells(ends: Pair<Coordinate, Coordinate>): List<Coordinate> {
    val first = ends.first
    val second = ends.second
    return if (first.x < second.x)
        if (first.y < second.y) (first.x..second.x).zip((first.y..second.y)).map { Coordinate(it.first, it.second) }
        else (first.x..second.x).zip((first.y downTo second.y)).map { Coordinate(it.first, it.second) }
    else if (first.y < second.y) (first.x downTo second.x).zip((first.y..second.y)).map { Coordinate(it.first, it.second) }
    else (first.x downTo second.x).zip((first.y downTo second.y)).map { Coordinate(it.first, it.second) }
}
