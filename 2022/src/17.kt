import java.io.File

data class Cell(val x: Int, val y: Int)
data class RockShape(val cells: Set<Cell>) {
    fun atChamberHeight(height: Int): RockShape {
        return RockShape(cells.map { it.copy(y = it.y + height) }.toSet())
    }

    fun highestPoint(): Int {
        return cells.maxOf{ it.y }
    }

    fun moveDown(): RockShape {
        val potential = cells.map { it.copy(y = it.y - 1) }.toSet()

        if (potential.all { it.y >= 0 }) {
            return RockShape(potential)
        }

        return this
    }

    fun pushLeftIfPossible(): RockShape {
        val potential = cells.map { it.copy(x = it.x - 1) }.toSet()

        if (potential.all { it.x >= 0 }) {
            return RockShape(potential)
        }

        return this
    }

    fun pushRightIfPossible(): RockShape {
        val potential = cells.map { it.copy(x = it.x + 1) }.toSet()

        if (potential.all { it.x <= 6 }) {
            return RockShape(potential)
        }

        return this
    }
}

fun main() {
    val jetSequence: String = File("2022/src/17.txt").readLines()[0]

    val horizontal = RockShape(setOf(Cell(2, 0), Cell(3, 0), Cell(4, 0), Cell(5, 0)))
    val plus = RockShape(setOf(Cell(2, 1), Cell(3, 0), Cell(3, 1), Cell(3, 2), Cell(4, 1)))
    val corner = RockShape(setOf(Cell(2, 0), Cell(3, 0), Cell(4, 0), Cell(4, 1), Cell(4, 2)))
    val vertical = RockShape(setOf(Cell(2, 0), Cell(2, 1), Cell(2, 2), Cell(2, 3)))
    val square = RockShape(setOf(Cell(2, 0), Cell(3, 0), Cell(2, 1), Cell(3, 1)))

    val rocks = (0..450).flatMap { listOf(horizontal, plus, corner, vertical, square) }.take(2022)

    val afterSinking = rocks.fold(Pair(emptyList<RockShape>(), jetSequence.repeat(1000))) { acc, next ->
        val shapes = acc.first

        val heightOfChamber = 3 + (if (shapes.isEmpty()) 0 else shapes.map { it.highestPoint() }.max() + 1)

        val startsFalling = next.atChamberHeight(heightOfChamber)
        val result = rockToTheBottom(startsFalling, shapes.flatMap { it.cells }.toSet(), acc.second)

        Pair(shapes + setOf(result.first), result.second)
    }

    println(afterSinking.first.maxByOrNull { it.highestPoint() }!!)

    println("first " + (afterSinking.first.maxByOrNull { it.highestPoint() }!!.highestPoint() + 1))

    printAllRocks(afterSinking.first.toSet())
}

fun rockToTheBottom(rockShape: RockShape, occupied: Set<Cell>, jetSequence: String): Pair<RockShape,String> {
    val jet = jetSequence[0]

    val jetEffectPotentially = if (jet == '>') {
        rockShape.pushRightIfPossible()
    } else {
        rockShape.pushLeftIfPossible()
    }
    val jetEffect = if (occupied.none { jetEffectPotentially.cells.contains(it) }) jetEffectPotentially else rockShape

    val downEffectPotentially = jetEffect.moveDown()
    val downEffect = if (occupied.none { downEffectPotentially.cells.contains(it) }) downEffectPotentially else jetEffect

    return if (downEffect == jetEffect) {
        Pair(downEffect, jetSequence.drop(1))
    } else {
        rockToTheBottom(downEffect, occupied, jetSequence.drop(1))
    }
}

fun printAllRocks(rocks: Set<RockShape>) {
    val cells = rocks.flatMap { it.cells }

    (20 downTo 0).forEach { y ->
        (0 until 7).forEach { x ->
            if (cells.contains(Cell(x, y))) {
                print("#")
            } else
                print(".")
        }
        println()
    }
}
