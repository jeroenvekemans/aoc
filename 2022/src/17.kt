import java.io.File

data class Cell(val x: Long, val y: Long)
data class RockShape(val cells: Set<Cell>) {
    fun atStartingPosition(height: Long): RockShape {
        return RockShape(cells.map { it.copy(y = it.y + height) }.toSet())
    }

    fun highestPoint(): Long {
        return cells.maxOf { it.y }
    }

    fun moveDown(): RockShape {
        val potential = cells.map { it.copy(y = it.y - 1L) }.toSet()

        if (potential.all { it.y >= 0 }) {
            return RockShape(potential)
        }

        return this
    }

    fun pushLeftIfPossible(): RockShape {
        val potential = cells.map { it.copy(x = it.x - 1L) }.toSet()

        if (potential.all { it.x >= 0 }) {
            return RockShape(potential)
        }

        return this
    }

    fun pushRightIfPossible(): RockShape {
        val potential = cells.map { it.copy(x = it.x + 1L) }.toSet()

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

    val rocks = listOf(horizontal, plus, corner, vertical, square)

    var rockPointer = 0L
    var sequencePointer = 0L
    val shapes = mutableListOf<RockShape>()
    var heightOfChamber = 3L
    var extraHeight = 0L

    val cache = mutableMapOf<Pair<Long, Long>, MutableList<Pair<Long, Long>>>()

    val totalRocks = 1_000_000_000_000L
//    val totalRocks = 2022L

    while (rockPointer < totalRocks) {
        val cacheKey = Pair(rockPointer % rocks.size, sequencePointer % jetSequence.length)
        val history = cache.getOrDefault(cacheKey, mutableListOf())

        if (history.size >= 3) {
            val heightDiff = history[2].second - history[1].second
            val rockPointerDiff = history[2].first - history[1].first
            val jump = (totalRocks - rockPointer) / rockPointerDiff

            println("height pointer diff $heightDiff")
            println("rock pointer diff $rockPointerDiff")
            println("jump $jump")


            extraHeight += jump * heightDiff
            rockPointer += jump * rockPointerDiff

        }

        val next = rocks[(rockPointer++ % rocks.size).toInt()]

        val startsFalling = next.atStartingPosition(heightOfChamber)

        val (bottomRock, seq) = rockToTheBottom(startsFalling, shapes.flatMap { it.cells }.toSet(), jetSequence, sequencePointer)
        sequencePointer = seq
        shapes.add(bottomRock)
        if (bottomRock.highestPoint() + 4 > heightOfChamber) {
            heightOfChamber = (bottomRock.highestPoint() + 4)
        }

        if (history.size < 3) {
            history.add(Pair(rockPointer, heightOfChamber))
        }
        cache[cacheKey] = history
    }

    val computedHeight = shapes.maxByOrNull { it.highestPoint() }!!.highestPoint() + 1
    println("computed height $computedHeight")
    println("extra $extraHeight")
    println("first ${computedHeight + extraHeight}")
}


fun rockToTheBottom(rockShape: RockShape, occupied: Set<Cell>, jetSequence: String, jetPointer: Long): Pair<RockShape, Long> {
    val jet = jetSequence[(jetPointer % jetSequence.length).toInt()]

    val jetEffectPotentially = if (jet == '>') {
        rockShape.pushRightIfPossible()
    } else {
        rockShape.pushLeftIfPossible()
    }
    val jetEffect = if (occupied.none { jetEffectPotentially.cells.contains(it) }) jetEffectPotentially else rockShape

    val downEffectPotentially = jetEffect.moveDown()
    val downEffect = if (occupied.none { downEffectPotentially.cells.contains(it) }) downEffectPotentially else jetEffect

    return if (downEffect == jetEffect) {
        Pair(downEffect, jetPointer + 1)
    } else {
        rockToTheBottom(downEffect, occupied, jetSequence, jetPointer + 1)
    }
}