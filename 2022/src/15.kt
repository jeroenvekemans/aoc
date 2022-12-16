import java.io.File
import java.lang.Integer.valueOf
import kotlin.math.abs

data class SensorPoint(val x: Int, val y: Int) {
    fun manhattan(sensorPoint: SensorPoint): Int {
        return abs(x - sensorPoint.x) + abs(y - sensorPoint.y)
    }
}

data class SensorPlane(val center: SensorPoint, val closest: SensorPoint) {

    fun affectsRow(y: Int): Boolean {
        val diff = center.manhattan(closest)

        return center.y - diff <= y && y <= center.y + diff
    }


    fun spansRow(y: Int): IntRange {
        val manhattan = center.manhattan(closest)
        val height = abs(center.y - y)
        val result = manhattan - height

        return (center.x - result..center.x + result)
    }
}

fun main() {
    val planes: List<SensorPlane> = File("2022/src/15.txt")
        .readLines()
        .map { raw ->
            val match =
                Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)").find(raw)!!
            val (xCenter, yCenter, xClosest, yClosest) = match.destructured
            SensorPlane(
                SensorPoint(valueOf(xCenter), valueOf(yCenter)),
                SensorPoint(valueOf(xClosest), valueOf(yClosest))
            )
        }

    val beaconsPerRow = planes.map { it.closest }.groupBy { it.y }

    val row = 0

    val spans = computeSpansForRow(planes, row)

    val first = spans.flatMap { it.toSet() }.toSet()
    val beaconsInRow = beaconsPerRow.getOrDefault(row, emptyList()).toSet()

    println(spans)
    println(first.size - beaconsInRow.size)

    val space = (0  ..4000000)
    val second = space.map { row ->
        val sp = computeSpansForRow(planes, row).map { r ->
            val start = if (r.first < 0) 0 else r.first
            val end = if (r.last > space.last) space.last else r.last
            (start..end)
        }.sortedBy { it.first }

        val new = sp.drop(1).fold(sp[0]) { acc, next ->
            if (next.first >= acc.first && next.last <= acc.last) {
                acc
            } else {
                if (next.first <= acc.last + 1) {
                    (acc.first..next.last)
                } else {
                    next
                }
            }
        }

        SensorPoint(new.first - 1, row)
    }.first { it.x != -1}

    val secondAnswer = second.x * 4000000L + second.y
    println("second $secondAnswer")
}

private fun computeSpansForRow(
    planes: List<SensorPlane>,
    row: Int
) = planes
    .filter { it.affectsRow(row) }
    .map { it.spansRow(row) }
    .filter { !it.isEmpty() }
    .sortedBy { it.first }