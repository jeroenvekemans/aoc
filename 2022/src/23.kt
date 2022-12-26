import ElfDirection.*
import java.io.File

data class ElfPosition(val row: Int, val column: Int)
enum class ElfDirection(val move: Pair<Int, Int>, val scan1: Pair<Int, Int>, val scan2: Pair<Int, Int>) {
    N(Pair(-1,0), Pair(-1,-1), Pair(-1,1)),
    E(Pair(0, 1), Pair(-1,1), Pair(1,1)),
    S(Pair(1,0), Pair(1,-1), Pair(1,1)),
    W(Pair(0,-1), Pair(-1,-1), Pair(1,-1));

    fun scanFor(position: ElfPosition): List<ElfPosition> {
        return listOf(
            ElfPosition(position.row + move.first, position.column + move.second),
            ElfPosition(position.row + scan1.first, position.column + scan1.second),
            ElfPosition(position.row + scan2.first, position.column + scan2.second))
    }

    fun move(position: ElfPosition): ElfPosition {
        return ElfPosition(position.row + move.first, position.column + move.second)
    }
}

fun main() {
    val raw: List<String> = File("2022/src/23.txt").readLines()

    val elves = raw.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, cell ->
            Triple(rowIndex, colIndex, cell)
        }
    }.filter { it.third == '#' }.map { ElfPosition(it.first, it.second) }.toSet()

    printElves(elves)

    val directions = listOf(N, S, W, E)
    val endElves = elvesPerformingRound(1, elves, directions)

    printElves(endElves)

    val cols = endElves.maxOf { it.column } - endElves.minOf { it.column } + 1
    val rows = endElves.maxOf { it.row } - endElves.minOf { it.row } + 1
    println(cols * rows - endElves.size)
}

fun printElves(elves: Set<ElfPosition>) {
    (-12..12).forEachIndexed { _, rowIndex ->
        (-12..12).forEachIndexed { _, colIndex   ->
            print(if (elves.contains(ElfPosition(rowIndex, colIndex))) "#" else ".")
        }
        println()
    }
}

fun elvesPerformingRound(rounds: Int, elves: Set<ElfPosition>, orderedDirectionsToConsider: List<ElfDirection>): Set<ElfPosition> {
//    if (roundsLeft == 0) {
//        return elves
//    }

    val (fixedElves, elvesToMove) = elves.partition { elf ->
        val adjacentPositions = ElfDirection.values().flatMap { dir -> dir.scanFor(elf) }
        adjacentPositions.none { elves.contains(it) }
    }

    if (elvesToMove.isEmpty()) {
        println(rounds)
        return elves
    }

    val proposedMoves = elvesToMove
        .map { elfToMove ->
            val possibleMove = orderedDirectionsToConsider.filter { dir -> dir.scanFor(elfToMove).none { elves.contains(it) } }

            if (possibleMove.isEmpty()) {
                Pair(elfToMove, elfToMove)
            } else {
                Pair(elfToMove, possibleMove[0].move(elfToMove))
            }
        }

    val futurePositions = (fixedElves + proposedMoves.map { it.second }).groupingBy { it }.eachCount()

    val elvesAtDestination = proposedMoves
        .map { (orig, proposed) ->
            if (futurePositions[proposed]!! <= 1) {
                proposed
            } else {
                orig
            }
        }

    return elvesPerformingRound(rounds + 1, (fixedElves + elvesAtDestination).toSet(), orderedDirectionsToConsider.drop(1) + orderedDirectionsToConsider[0])
}
