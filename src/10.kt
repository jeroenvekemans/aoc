import java.io.File
import java.util.*

fun main() {
    val linesOfCode: List<String> = File("src/10.txt").readLines()

    println(DayTen.solve(linesOfCode))
    println(DayTen.solveDelta(linesOfCode))
}

enum class CodeCharacter(val open: Char, val close: Char, val corruptionPenalty: Int, val autoCompleteScore: Int) {
    Braces('(', ')', 3, 1),
    Brackets('[', ']', 57, 2),
    Curly('{', '}', 1197, 3),
    Angle('<', '>', 25137, 4);
}

object DayTen {
    fun solve(linesOfCode: List<String>): Int {
        return linesOfCode.sumOf { getCorruptionScore(it) }
    }

    fun solveDelta(linesOfCode: List<String>): Long {
        val completions = linesOfCode.map { getLineCompletionScore(it) }.filter { it > 0 }.sorted()

        return completions[completions.size / 2]
    }

    private fun getLineCompletionScore(line: String): Long {
        val stack = Stack<CodeCharacter>()

        line.toCharArray().forEach { character ->
            val opensNewGroupCharacter = CodeCharacter.values().find { cc -> cc.open == character }

            if (opensNewGroupCharacter != null) {
                stack.push(opensNewGroupCharacter)
            } else {
                val mostRecentlyOpenedGroup = stack.pop()

                if (mostRecentlyOpenedGroup.close != character) {
                    return 0
                }
            }
        }

        return stack
            .map { it.autoCompleteScore }
            .reversed()
            .fold(0) { acc, next -> acc * 5 + next }
    }


    private fun getCorruptionScore(line: String) : Int {
        val stack = Stack<CodeCharacter>()

        line.toCharArray().forEach { character ->
            val opensNewGroupCharacter = CodeCharacter.values().find { cc -> cc.open == character }

            if (opensNewGroupCharacter != null) {
                stack.push(opensNewGroupCharacter)
            } else {
                val mostRecentlyOpenedGroup = stack.pop()

                if (mostRecentlyOpenedGroup.close != character) {
                    return CodeCharacter.values().find { cc -> cc.close == character }!!.corruptionPenalty
                }
            }
        }

        return 0
    }

}