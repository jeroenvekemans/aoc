import java.io.File

fun main() {
    val rows: List<String> = File("2021/src/09.txt").readLines()

    val board = rows.map { it.toCharArray().map { Integer.valueOf(it.toString()) }.toTypedArray() }.toTypedArray()

    println(DayNineDelta.solve(board))
}

data class Cell(val row: Int, val column: Int)

object DayNine {

    fun solve(board: Array<Array<Int>>): Int {
        val indices = (board.indices).flatMap { r -> (0 until board[r].size).map { Cell(r, it) } }

        return indices
            .filter { isLowPoint(it, board) }
            .sumOf { board[it.row][it.column] + 1 }
    }

    fun computeLowPoints(board: Array<Array<Int>>): List<Cell> {
        val indices = (board.indices).flatMap { r -> (0 until board[r].size).map { Cell(r, it) } }

        return indices.filter { isLowPoint(it, board) }
    }

    private fun isLowPoint(cell: Cell, board: Array<Array<Int>>): Boolean {
        val adjacentCells = (-1..1).flatMap { r -> (-1..1).map { c -> Cell(r + cell.row, c + cell.column) } }
            .filter { it.row >= 0 && it.column >= 0 }
            .filter { it.row < board.size && it.column < board[0].size }
            .filter { it != cell }

        return adjacentCells.all { board[it.row][it.column] > board[cell.row][cell.column] }
    }

}

object DayNineDelta {

    fun collectBassinLocations(collectedLocations: Set<Cell>, board: Array<Array<Int>>): Set<Cell> {
        val newLocations = collectedLocations
            .flatMap { loc ->
                    listOf(Cell(0, 1), Cell(0, -1), Cell(-1, 0), Cell(1, 0))
                        .map { Cell(it.row + loc.row, it.column + loc.column) }
                        .filter { it.row >= 0 && it.column >= 0 }
                        .filter { it.row < board.size && it.column < board[0].size }
                        .filter { board[it.row][it.column] != 9 }
                        .map { Cell(it.row, it.column) }
            }

        if (collectedLocations.containsAll(newLocations)) {
            return collectedLocations;
        }

        return collectBassinLocations(collectedLocations + newLocations, board)
    }

    fun solve(board: Array<Array<Int>>): Int {
        val lowPoints = DayNine.computeLowPoints(board)

        val bassins = lowPoints.map { collectBassinLocations(setOf(it), board) }

        println(bassins.toSet().size == bassins.size)

        return bassins
            .sortedBy { -it.size }
            .take(3)
            .fold(1) { acc, next -> acc * next.size }
    }

}