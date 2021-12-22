import java.io.File
import java.lang.RuntimeException
import kotlin.math.max
import kotlin.math.min

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

    fun intersection(otherCuboid: Cuboid): Cuboid {
        if (!this.overlaps(otherCuboid)) {
            throw RuntimeException("fail")
        }

        return Cuboid(
            (max(this.x.first, otherCuboid.x.first)..(min(this.x.last, otherCuboid.x.last))),
            (max(this.y.first, otherCuboid.y.first)..(min(this.y.last, otherCuboid.y.last))),
            (max(this.z.first, otherCuboid.z.first)..(min(this.z.last, otherCuboid.z.last)))
        )
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

    fun solve(steps: List<RebootStep>): Long {
        val stepsToConsider = steps.take(11).filter { it.cuboid.enclosedBy(Cuboid(-50..50, -50..50, -50..50)) }
        val initial = stepsToConsider.first()

        val naiveResult =
            stepsToConsider.drop(1).fold(if (initial.on) initial.cuboid.cubes() else emptySet()) { acc, next ->
                if (next.on) {
                    acc + next.cuboid.cubes()
                } else {
                    acc - next.cuboid.cubes()
                }
            }

        println("naive result " + naiveResult.size)

        val start = stepsToConsider[0].cuboid

        return solve(stepsToConsider.drop(1), start.size(), listOf(start))
    }

    private fun amountOfUniqueCubes(cuboids: Set<Cuboid>): Long {
        // TODO WAY TOO SLOW
        return cuboids.flatMap { it.cubes() }.toSet().size.toLong()
    }

    private fun solve(steps: List<RebootStep>, cubesOnAcc: Long, cuboids: List<Cuboid>): Long {
        println("acc $cubesOnAcc")

        if (steps.isEmpty()) {
            return cubesOnAcc
        }

        val next = steps.first()

        val intersections = cuboids.filter { it.overlaps(next.cuboid) }.map { it.intersection(next.cuboid) }.toSet()
        val cubesInIntersections = amountOfUniqueCubes(intersections)

        return if (next.on) {
            println("turning on " + cubesInIntersections + " cubes, based on intersections " + intersections.size + " and size of cuboid " + next.cuboid.size())

            solve(
                steps.drop(1),
                cubesOnAcc + next.cuboid.size() - cubesInIntersections,
                cuboids + listOf(next.cuboid),
            )
        } else {
            println("blacking out " + cubesInIntersections + " cubes, based on intersections " + intersections.size)

            return solve(
                steps.drop(1),
                cubesOnAcc - cubesInIntersections,
                cuboids
            )
        }
    }

}