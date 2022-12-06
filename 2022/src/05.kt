import java.io.File
import java.lang.Integer.valueOf

data class Rearrangement(val amount: Int, val source: Int, val destination: Int)

fun main() {
    val raw: List<String> = File("2022/src/06.txt").readLines()

    val stacksWithNumbers = raw.takeWhile { it.isNotEmpty() }
    val stacks = stacksWithNumbers.dropLast(1)
    val amountOfStacks = valueOf(stacksWithNumbers.last().filter { it.isDigit() }.last().toString())

    val parsedStacks = (0 until amountOfStacks)
        .map { stackIndex ->
            ArrayDeque(stacks.map { it.getOrNull(stackIndex * 4 + 1) }.filter { it != null && it != ' ' })
        }

    val rearrangements = raw.takeLastWhile { it.isNotEmpty() }
        .map {
            val result = "move ([0-9]+) from ([0-9]+) to ([0-9]+)+".toRegex().matchEntire(it)!!
            Rearrangement(valueOf(result.groupValues[1]), valueOf(result.groupValues[2]), valueOf(result.groupValues[3]))
        }


    rearrangements.forEach { rearrangement ->
        val source = parsedStacks[rearrangement.source - 1]
        val destination = parsedStacks[rearrangement.destination - 1]

        val elms = source.take(rearrangement.amount)
        elms.reversed().forEach {
            source.removeFirst()
            destination.addFirst(it)
        }
    }

    println(parsedStacks)

    println(parsedStacks.map { it.first() }.joinToString(""))
}