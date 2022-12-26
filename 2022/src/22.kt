import MapDirection.*
import java.io.File
import java.lang.Integer.valueOf
import java.lang.RuntimeException

enum class MapRotate { L, R }
sealed class MapInstruction() {
    data class ForwardInstruction(val steps: Int) : MapInstruction()
    data class RotateInstruction(val rotate: MapRotate) : MapInstruction()
}

enum class MapDirection(val mapDiff: MapPosition, val facing: Int, val sign: Char) {
    N(MapPosition(-1, 0), 3, '^') {
        override fun rotate(rotate: MapRotate): MapDirection {
            return if (rotate == MapRotate.L) W else E
        }
    },
    E(MapPosition(0, 1), 0, '>') {
        override fun rotate(rotate: MapRotate): MapDirection {
            return if (rotate == MapRotate.L) N else S
        }
    },
    S(MapPosition(1, 0), 1, 'v') {
        override fun rotate(rotate: MapRotate): MapDirection {
            return if (rotate == MapRotate.L) E else W
        }
    },
    W(MapPosition(0, -1), 2, '<') {
        override fun rotate(rotate: MapRotate): MapDirection {
            return if (rotate == MapRotate.L) S else N
        }
    };

    fun advance(pos: MapPosition): MapPosition {
        return MapPosition(mapDiff.row + pos.row, mapDiff.column + pos.column)
    }

    abstract fun rotate(rotate: MapRotate): MapDirection
}

data class MapPosition(val row: Int, val column: Int)
data class MapCell(val position: MapPosition, val blocked: Boolean)

fun main() {
    val raw: List<String> = File("2022/src/22.txt").readLines()

    val rawInstructions = raw.last()
    val map = raw.dropLast(2)

    val cells = map.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, col ->
            if (col == '#') {
                MapCell(MapPosition(rowIndex + 1, colIndex + 1), true)
            } else if (col == '.') {
                MapCell(MapPosition(rowIndex + 1, colIndex + 1), false)
            } else {
                null
            }
        }.filterNotNull()
    }

    val start = cells.filter { it.position.row == 1 }.minBy { it.position.column }
    val instructions = parseInstructions(rawInstructions, emptyList())

//    println(start)

    val end = traverseMap(instructions, start.position, E, cells.associateBy { it.position })

//    println(end)

    val result = end.first.row * 1000 + 4 * end.first.column + end.second.facing
    println(result)

    printCubeAndPath(cells, path)
}

fun printCubeAndPath(cells: List<MapCell>, path: MutableList<Pair<MapPosition, MapDirection>>) {
    val associateBy = cells.associateBy { it.position }
    val pathmap = path.associateBy { it.first }

    (1..200).forEach { row ->
        (1..200).forEach { column ->
            val key = MapPosition(row, column)
            if (associateBy.containsKey(key)) {
                if (pathmap.containsKey(key)) {
                    print(pathmap[key]!!.second.sign)
                } else if (associateBy[key]!!.blocked) {
                    print("#")
                } else {
                    print(".")
                }
            } else {
                print(" ")
            }
        }
        println()
    }
}

val path = mutableListOf<Pair<MapPosition, MapDirection>>()

tailrec fun traverseMap(
    instructions: List<MapInstruction>,
    currentPosition: MapPosition,
    currentDirection: MapDirection,
    cells: Map<MapPosition, MapCell>
): Pair<MapPosition, MapDirection> {

    if (instructions.isEmpty()) {
        return Pair(currentPosition, currentDirection)
    }

    val (newPosition: MapPosition, newDirection: MapDirection) = when (val currentInstruction = instructions[0]) {
        is MapInstruction.ForwardInstruction -> {
            (0 until currentInstruction.steps).fold(Pair(currentPosition, currentDirection)) { acc, next ->
                val previousPosition = acc.first
                val previousDirection = acc.second
                val newPos = previousDirection.advance(previousPosition)

                val (newCell, newDirection) = if (cells.contains(newPos)) {
                    Pair(cells[newPos]!!, previousDirection)
                } else {
                    wrappingAroundPartTwo(previousDirection, cells, previousPosition)
                }

                if (newCell.blocked) {
                    acc
                } else {
                    path.add(Pair(newCell.position, newDirection))
                    Pair(newCell.position, newDirection)
                }
            }
        }

        is MapInstruction.RotateInstruction -> {
            path.add(Pair(currentPosition, currentDirection.rotate(currentInstruction.rotate)))
            Pair(currentPosition, currentDirection.rotate(currentInstruction.rotate))
        }
    }

    return traverseMap(instructions.drop(1), newPosition, newDirection, cells)
}

data class MapRegion(val rowRange: IntRange, val colRang: IntRange)

val one = MapRegion((1..50), (101..150))
val two = MapRegion((1..50), (51..100))
val three = MapRegion((51..100), (51..100))
val four = MapRegion((101..150), (51..100))
val five = MapRegion((101..150), (1..50))
val six = MapRegion((151..200), (1..50))

private fun wrappingAroundPartTwo(
    currentDirection: MapDirection,
    cells: Map<MapPosition, MapCell>,
    previousPosition: MapPosition
): Pair<MapCell, MapDirection> {
    val regions = listOf(one, two, three, four, five, six)

    val howToMove = mapOf(
        Pair(Pair(one, E), Triple(four, W) { pos: MapPosition -> MapPosition(151 - pos.row, 100) }),
        Pair(Pair(one, N), Triple(six, N) { pos: MapPosition -> MapPosition(200, pos.column - 100) }),
        Pair(Pair(one, S), Triple(three, W) { pos: MapPosition -> MapPosition(pos.column - 50, 100) }),
        Pair(Pair(two, W), Triple(five, E) { pos: MapPosition -> MapPosition(151 - pos.row, 1) }),
        Pair(Pair(two, N), Triple(six, E) { pos: MapPosition -> MapPosition(pos.column + 100, 1) }),
        Pair(Pair(three, W), Triple(five, S) { pos: MapPosition -> MapPosition(101, pos.row - 50) }),
        Pair(Pair(three, E), Triple(one, N) { pos: MapPosition -> MapPosition(50, pos.row + 50) }),
        Pair(Pair(four, E), Triple(one, W) { pos: MapPosition -> MapPosition(151 - pos.row, 150) }),
        Pair(Pair(four, S), Triple(six, W) { pos: MapPosition -> MapPosition(100 + pos.column, 50) }),
        Pair(Pair(five, W), Triple(two, E) { pos: MapPosition -> MapPosition(151 - pos.row, 51) }),
        Pair(Pair(five, N), Triple(three, E) { pos: MapPosition -> MapPosition(50 + pos.column, 51) }),
        Pair(Pair(six, E), Triple(four, N) { pos: MapPosition -> MapPosition(150, pos.row - 100) }),
        Pair(Pair(six, S), Triple(one, S) { pos: MapPosition -> MapPosition(1, pos.column + 100) }),
        Pair(Pair(six, W), Triple(two, S) { pos: MapPosition -> MapPosition(1, pos.row - 100) }),
    )

    val region = regions.first { previousPosition.row in it.rowRange && previousPosition.column in it.colRang }
    val key = Pair(region, currentDirection)
    val move = howToMove[key]!!
    return Pair(cells[move.third(previousPosition)]!!, move.second)
}

private fun wrappingAroundPartOne(
    currentDirection: MapDirection,
    cells: Map<MapPosition, MapCell>,
    currentPosition: MapPosition,
): Pair<MapCell, MapDirection> {
    val newCell = when (currentDirection) {
        N -> cells.values.filter { it.position.column == currentPosition.column }.maxBy { it.position.row }
        E -> cells.values.filter { it.position.row == currentPosition.row }.minBy { it.position.column }
        S -> cells.values.filter { it.position.column == currentPosition.column }.minBy { it.position.row }
        W -> cells.values.filter { it.position.row == currentPosition.row }.maxBy { it.position.column }
    }

    return Pair(newCell, currentDirection)
}

tailrec fun parseInstructions(instructions: String, acc: List<MapInstruction>): List<MapInstruction> {
    if (instructions.isEmpty()) {
        return acc
    }

    if (instructions[0] == 'L' || instructions[0] == 'R') {
        return parseInstructions(
            instructions.drop(1),
            acc + listOf(MapInstruction.RotateInstruction(MapRotate.valueOf(instructions[0].toString())))
        )
    }

    if (instructions.length <= 1 || !instructions[1].isDigit()) {
        return parseInstructions(
            instructions.drop(1),
            acc + listOf(MapInstruction.ForwardInstruction(valueOf(instructions[0].toString())))
        )
    }

    if (instructions.length <= 2 || !instructions[2].isDigit()) {
        return parseInstructions(
            instructions.drop(
                2,
            ),
            acc + listOf(MapInstruction.ForwardInstruction(valueOf(instructions.take(2))))
        )
    }

    throw RuntimeException()
}
