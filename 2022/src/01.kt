import java.io.File
import java.lang.Integer.valueOf

fun main() {
    val rawInput: List<String> = File("2022/src/01.txt").readLines()

    val blankIndices = listOf(-1) + rawInput.mapIndexed { index, s ->
        if (s.isEmpty()) index
        else -1
    }.filter { it != -1 } + listOf(rawInput.size)

    val caloriesPerElf = blankIndices
        .zipWithNext()
        .map { rawInput.subList(it.first + 1, it.second).map { valueOf(it) } }

    val totalCaloriesOfTopThree =
        caloriesPerElf
            .map { it.sum() }
            .sortedByDescending { it }
            .take(3)
            .sum()

    println(totalCaloriesOfTopThree)
}