import java.io.File
import java.lang.IllegalStateException
import java.lang.Integer.*

data class Rock(val pointEnds: List<Point>) {

    fun getFullShape(): Set<Point> {
        return pointEnds
            .zipWithNext()
            .flatMap { lineEnds ->
                val start = lineEnds.first
                val end = lineEnds.second

                if (start.x == end.x) {
                    (min(start.y, end.y)..max(start.y, end.y))
                        .map { Point(start.x, it) }
                } else if (start.y == end.y) {
                    (min(start.x, end.x)..max(start.x, end.x))
                        .map { Point(it, start.y) }
                } else {
                    throw IllegalStateException()
                }
            }
            .toSet()
    }

}
data class Point(val x: Int, val y: Int) {
    fun downOneStep(): Point {
        return this.copy(y = y + 1)
    }

    fun downOneStepAndToTheLeft(): Point {
        return this.copy(x = x - 1, y = y + 1)
    }


    fun downOneStepAndToTheRight(): Point {
        return this.copy(x = x + 1, y = y + 1)
    }
}

fun main() {
    val rocks: List<Rock> = File("2022/src/14.txt")
        .readLines()
            .map { line ->
            Rock(line
                .split("->")
                .map { it.split(",") }
                .map { Point(
                    valueOf(it[0].trim()),
                    valueOf(it[1].trim()))
                })
        }

    val pointCloud = rocks.flatMap { it.getFullShape() }.toSet()
    val borderToInfinity = pointCloud.maxBy { it.y }

    println(borderToInfinity)

    val first = dropSandUntilInfinity(pointCloud, borderToInfinity.y + 1, 0)
    println("first $first")

    val second = dropSandUntilBottom(pointCloud, borderToInfinity.y + 2, 0)
    println("second $second")

}

tailrec fun dropSandUntilBottom(pointCloud: Set<Point>, bottom: Int, acc: Int): Int {
    println("until bottom $acc")

    val source = Point(500, 0)

    val result = dropSand(pointCloud, source, bottom - 1)

    if (result.first.contains(source)) {
        return acc + 1
    }

    return dropSandUntilBottom(result.first, bottom, acc + 1)
}

fun dropSandUntilInfinity(pointCloud: Set<Point>, infinityY: Int, acc: Int): Int {
    val result = dropSand(pointCloud, Point(500,0), infinityY)

    if (result.second) {
        return acc
    }

    return dropSandUntilInfinity(result.first, infinityY, acc + 1)
}

tailrec fun dropSand(pointCloud: Set<Point>, position: Point, bottom: Int): Pair<Set<Point>, Boolean> {
    if (position.y >= bottom) {
        return Pair(pointCloud + setOf(position), true)
    }

    if (!pointCloud.contains(position.downOneStep())) {
        return dropSand(pointCloud, position.downOneStep(), bottom)
    }

    if (!pointCloud.contains(position.downOneStepAndToTheLeft())) {
        return dropSand(pointCloud, position.downOneStepAndToTheLeft(), bottom)
    }

    if (!pointCloud.contains(position.downOneStepAndToTheRight())) {
        return dropSand(pointCloud, position.downOneStepAndToTheRight(), bottom)
    }

    return Pair(pointCloud + setOf(position), false)
}

