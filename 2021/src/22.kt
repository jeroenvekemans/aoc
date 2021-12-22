import java.io.File

data class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange) {
    private fun overlapping(range: IntRange, otherRange: IntRange): Boolean {
        return range.first in otherRange || range.last in otherRange
    }

    fun overlaps(otherCuboid: Cuboid): Boolean {
        return overlapping(x, otherCuboid.x) && overlapping(y, otherCuboid.y) && overlapping(z, otherCuboid.z)
    }

    private fun within(innerRange: IntRange, outerRange: IntRange): Boolean {
        return innerRange.first in outerRange && innerRange.last in outerRange
    }

    fun enclosedBy(otherCuboid: Cuboid): Boolean {
        return within(x, otherCuboid.x) && within(y, otherCuboid.y) && within(z, otherCuboid.z)
    }

//    fun intersection(otherCuboid: Cuboid): Cuboid {
//        return
//    }

    fun reduce(otherCuboid: Cuboid): Set<Cuboid> {
        if (!this.overlaps(otherCuboid)) {
            return setOf(this, otherCuboid)
        }

        if (this.enclosedBy(otherCuboid)) {
            return setOf(otherCuboid)
        }

        if (otherCuboid.enclosedBy(this)) {
            return setOf(this)
        }

        val closerToOrigin = listOf(this, otherCuboid).sortedBy { it.x.first + it.y.first + it.z.first }
        val keepOriginal = closerToOrigin.first()
        val breakDown = closerToOrigin.drop(1).first()

        val reductions = mutableSetOf(keepOriginal)

        if (breakDown.x.first in keepOriginal.x) {
            reductions.add(Cuboid((keepOriginal.x.last + 1..breakDown.x.last), breakDown.y, breakDown.z))
        }

        if (breakDown.y.first in keepOriginal.y) {
            reductions.add(Cuboid(breakDown.x, ((keepOriginal.y.last + 1)..breakDown.y.last), breakDown.z))
        }

        if (breakDown.z.first in keepOriginal.z) {
            reductions.add(Cuboid(breakDown.x, breakDown.y, (keepOriginal.z.last + 1..breakDown.z.last)))
        }

        return reductions
    }

    fun size(): Long {
        return x.count().toLong() * y.count().toLong() * z.count().toLong()
    }

    fun cubes(): Set<Cuboid> {
        return x.flatMap { hor ->
            y.flatMap { vert ->
                z.map { depth ->
                    Cuboid((hor..hor), (vert..vert), (depth..depth))
                }
            }
        }.toSet()
    }
}

data class RebootStep(val on: Boolean, val cuboid: Cuboid)

fun main() {
    val rebootSteps = File("2021/src/22.txt").readLines()
        .map { instruction ->
            val match =
                """(on|off) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""".toRegex().find(instruction)
            val (on, xd, xu, yd, yu, zd, zu) = match!!.destructured
            RebootStep(on == "on", Cuboid(xd.toInt()..xu.toInt(), yd.toInt()..yu.toInt(), zd.toInt()..zu.toInt()))
        }

    println(DayTwentyTwo.solve(rebootSteps))
}

object DayTwentyTwo {

    fun solve(steps: List<RebootStep>): Int {
//        val initial = stepsToConsider.first()
//
//        val naiveResult = stepsToConsider.drop(1).fold(if (initial.on) initial.cuboid.cubes() else emptySet()) { acc, next ->
//            if (next.on) {
//                acc + next.cuboid.cubes()
//            } else {
//                acc - next.cuboid.cubes()
//            }
//        }

        val cuboids = steps.map { it.cuboid }

        val notEnclosed = cuboids.filter { c -> cuboids.filter { it != c }.none { c.enclosedBy(it) } }

        val nonOverlapping = cuboids.filter { c -> cuboids.filter { it != c }.none { c.overlaps(it) } }

        println(cuboids.size)
        println(notEnclosed.size)
        println(nonOverlapping.size)

//        val second = Cuboid((0..10), (0..10), (0..10))
//        val first = Cuboid((5..15), (5..15), (5..15))
//
//        println((first.cubes() + second.cubes()).toSet().size)
//        println(first.reduce(second))
//        println(first.reduce(second).sumOf { it.size() })
//        println(first.reduce(second).flatMap { it.cubes() }.toSet().size)

        return -1
    }

}