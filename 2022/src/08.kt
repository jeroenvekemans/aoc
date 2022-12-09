import java.io.File
import java.lang.Integer.valueOf
import kotlin.math.sqrt

data class Tree(val row: Int, val col: Int, val height: Int)

fun main() {
    val forest: Map<Pair<Int, Int>, Tree> = File("2022/src/08.txt")
        .readLines()
        .flatMapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, height ->
                Tree(rowIndex, colIndex, valueOf(height.toString()))
            }
        }
        .associateBy { tree -> Pair(tree.row, tree.col) }

    val forestSize = sqrt(forest.entries.size.toDouble()).toInt()

//    solvePartOne(forestSize, forest)
    solvePartTwo(forestSize, forest)
}

fun solvePartTwo(forestSize: Int, forest: Map<Pair<Int, Int>, Tree>) {
    val res = forest
        .entries
        .filter { it.key.first != 0 && it.key.first < forestSize - 1 && it.key.second != 0 && it.key.second != forestSize - 1 }
        .map { tree ->
            val pos = tree.key

            val down = (pos.first until forestSize - 1).map {
                forest[Pair(it, pos.second)]!!
            }.drop(1).takeWhile { it.height < tree.value.height  }.size + 1
            val up = (pos.first downTo 1).map {
                forest[Pair(it, pos.second)]!!
            }.drop(1).takeWhile { it.height < tree.value.height  }.size + 1
            val left = (pos.second downTo 1).map {
                forest[Pair(pos.first, it)]!!
            }.drop(1).takeWhile { it.height < tree.value.height  }.size + 1
            val right = (pos.second until forestSize - 1).map {
                forest[Pair(pos.first, it)]!!
            }.drop(1).takeWhile { it.height < tree.value.height  }.size + 1

            Pair(tree.key, down * up * left * right)
        }
        .maxBy { it.second }

    println(res)

}

private fun solvePartOne(
    forestSize: Int,
    forest: Map<Pair<Int, Int>, Tree>
) {
    val leftToRight = (0 until forestSize).map { row ->
        (0 until forestSize).map { col ->
            forest[Pair(row, col)]!!
        }
    }
    val rightToLeft = leftToRight.map { it.reversed() }

    val topToBottom = (0 until forestSize).map { col ->
        (0 until forestSize).map { row ->
            forest[Pair(row, col)]!!
        }
    }
    val bottomToTop = topToBottom.map { it.reversed() }

    val combos = leftToRight + rightToLeft + topToBottom + bottomToTop

    val visibleTrees = combos
        .flatMap { visible(-1, it) }
        .toSet()

    println(visibleTrees)
    println(visibleTrees.size)
}

fun visible(highest: Int, treesLeft: List<Tree>): List<Tree> {
    if (treesLeft.isEmpty()) return emptyList()

    val current = treesLeft.first()
    val remainder = treesLeft.drop(1)

    return if (current.height > highest) {
        listOf(current) + visible(current.height, remainder)
    } else {
        visible(highest, remainder)
    }
}
