import java.io.File

data class Pixel(val row: Int, val column: Int, val sign: Char) {
    fun pixelsInAdjacentSquares(): Set<Pair<Int, Int>> {
        return (-3..3).flatMap { r -> (-3..3).map { c -> Pair(row + r, column + c) } }.toSet()
    }
}

fun main() {
    val input = File("2021/src/20.txt").readLines()

    val algo = input.take(1).joinToString("")
    val image = input.drop(2)

    println(DayTwenty.solve(algo, image))
}

object DayTwenty {

    private fun enhanceImage(algo: String, whitePixels: Set<Pixel>, background: Char): Set<Pixel> {
        val cellsToConsider = whitePixels.flatMap { it.pixelsInAdjacentSquares() }.toSet()

        return cellsToConsider
            .map { cell ->
                val signs = (-1..1).flatMap { nr ->
                    (-1..1).map { cr ->
                        val whiteCell = whitePixels.contains(Pixel(cell.first + nr, cr + cell.second, '#'))
                        val darkCell = whitePixels.contains(Pixel(cell.first + nr, cr + cell.second, '.'))

                        if (whiteCell) '1' else (if (darkCell) '0' else (if (background == '#') '1' else '0'))
                    }

                }

                val binary = signs.joinToString("").toInt(2)

                Pixel(cell.first, cell.second, algo[binary])
            }.toSet()
    }

    fun solve(algo: String, image: List<String>): Int {
        val pixels = image.flatMapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, cell ->
                Pixel(rowIndex, colIndex, cell)
            }
        }.filter { it.sign == '#' }.toSet()

        val result = (1..50).fold(Pair(pixels, '.')) { acc, _ ->
            Pair(enhanceImage(algo, acc.first, acc.second), if (acc.second == '.') '#' else '.')
        }

        return result.first.filter { it.sign == '#' }.size
    }

}