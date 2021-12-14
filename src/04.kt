import java.io.File

fun main() {
    val data: List<String> = File("src/04.txt").readLines()

    val numbers = data[0].split(",").map { Integer.valueOf(it) }

    val boards = data.subList(2, data.size)
        .map { it.split(" ").filter { it.isNotEmpty() }.map { Integer.valueOf(it) } }
        .windowed(5, 6)

    println(DayFour.solveFirst(numbers, boards))
    println(DayFour.solveSecond(numbers, boards))
}

data class Position(val row: Int, val column: Int)
data class Board(val numberToPosition: Map<Int, Position>, val marked: List<Position>)

object DayFour {

    fun solveFirst(numbers: List<Int>, boards: List<List<List<Int>>>): Int {
        val boardsWithNumberToPosition = convertBoards(boards)

        return play(numbers, boardsWithNumberToPosition)
    }

    fun solveSecond(numbers: List<Int>, boards: List<List<List<Int>>>): Int {
        val boardsWithNumberToPosition = convertBoards(boards)

        return playSecond(numbers, boardsWithNumberToPosition)
    }

    private fun convertBoards(boards: List<List<List<Int>>>): List<Board> {
        val boardsWithNumberToPosition = boards.map { board ->
            board.flatMapIndexed { row, cellRow ->
                cellRow.mapIndexed { col, cell -> Pair(cell, Position(row, col)) }
                    .map { it.first to it.second }
            }.toMap()
        }.map { Board(it, emptyList()) }
        return boardsWithNumberToPosition
    }

    private fun play(numbersLeft: List<Int>, boards: List<Board>): Int {
        val nextRandom = numbersLeft[0]

        val updatedBoards = boards.map {
            b -> if (b.numberToPosition.contains(nextRandom))
                Board(b.numberToPosition.filterKeys { it != nextRandom }, b.marked + b.numberToPosition.getValue(nextRandom)) else b
        }

        val winningBoard = updatedBoards.firstOrNull { b ->
            b.marked.groupBy { it.row }.any { it.value.count() >= 5 } || b.marked.groupBy { it.column }.any { it.value.count() >= 5 }
        }

        if (winningBoard != null) {
            return winningBoard
                .numberToPosition
                .map { it.key }
                .sum() * nextRandom
        }

        return play(
            numbersLeft.drop(1),
            updatedBoards
        )
    }

    private fun playSecond(numbersLeft: List<Int>, boards: List<Board>): Int {
        val nextRandom = numbersLeft[0]

        val updatedBoards = boards.map {
                b -> if (b.numberToPosition.contains(nextRandom))
            Board(b.numberToPosition.filterKeys { it != nextRandom }, b.marked + b.numberToPosition.getValue(nextRandom)) else b
        }

        val winningBoards = updatedBoards.filter { b ->
            b.marked.groupBy { it.row }.any { it.value.count() >= 5 } || b.marked.groupBy { it.column }.any { it.value.count() >= 5 }
        }

        if (winningBoards.size == 1 && boards.size == 1) {
            return winningBoards[0]
                .numberToPosition
                .map { it.key }
                .sum() * nextRandom
        }

        return playSecond(
            numbersLeft.drop(1),
            updatedBoards.minus(winningBoards)
        )
    }

}