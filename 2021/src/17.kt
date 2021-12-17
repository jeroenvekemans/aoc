import kotlin.math.max

fun main() {
    val input = "target area: x=240..292, y=-90..-57"
    val match = """target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)""".toRegex().find(input)
    val (x1, x2, y1, y2) = match!!.destructured

    val targetArea =
        DaySeventeen.Area((Integer.valueOf(x1)..Integer.valueOf(x2)), Integer.valueOf(y1)..Integer.valueOf(y2))

    println(DaySeventeen.solve(targetArea))
    println(DaySeventeen.solveDelta(targetArea))
}


object DaySeventeen {
    data class Velocity(val x: Int, val y: Int) {
        fun next(): Velocity {
            val newX = if (x == 0) 0 else (if (x > 0) x - 1 else x + 1)
            return Velocity(newX, y - 1)
        }
    }

    data class Position(val x: Int, val y: Int) {
        fun next(velocity: Velocity): Position {
            return Position(x + velocity.x, y + velocity.y)
        }
    }

    data class Area(val x: IntRange, val y: IntRange) {

        fun onTarget(position: Position): Boolean {
            return position.x in x && position.y in y
        }

        fun overshoots(position: Position): Boolean {
            return position.x > x.last || position.y < y.first
        }

    }

    fun solve(targetArea: Area): Int {
        val maxY = (1..100).flatMap { xVelocity ->
            (1..100).map { yVelocity ->
                advance(Position(0, 0), Velocity(xVelocity, yVelocity), targetArea, 0)
            }
        }.filter { it.first }.maxOf { it.second }

        return maxY
    }

    fun solveDelta(targetArea: Area): Int {
        val validInitialVelocities = (1..1000).flatMap { xVelocity ->
            (-1000..1000).map { yVelocity ->
                val initialVelocity = Velocity(xVelocity, yVelocity)
                Pair(initialVelocity, advance(Position(0, 0), initialVelocity, targetArea, 0))
            }
        }.filter { it.second.first }.map { it.first }.toSet()

        return validInitialVelocities.size
    }

    private fun advance(
        position: Position,
        velocity: Velocity,
        targetArea: Area,
        maxY: Int
    ): Pair<Boolean, Int> {
        if (targetArea.onTarget(position)) {
            return Pair(true, maxY)
        }

        if (targetArea.overshoots(position)) {
            return Pair(false, maxY)
        }

        val nextPosition = position.next(velocity)
        val nextVelocity = velocity.next()
        val newMaxY = max(nextPosition.y, maxY)

        return advance(nextPosition, nextVelocity, targetArea, newMaxY)
    }

}