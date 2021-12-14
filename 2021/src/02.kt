import java.io.File
import java.lang.Integer.*

fun main() {
    val commands: List<String> = File("2021/src/02.txt").readLines()

    val result = commands
        .fold(Pair(0, 0)) { acc, cmd ->
            val splittedCommand = cmd.split(" ")

            val steps = valueOf(splittedCommand[1])

            when (splittedCommand[0]) {
                "forward" -> Pair(acc.first + steps, acc.second)
                "down" -> Pair(acc.first, acc.second + steps)
                else -> Pair(acc.first, acc.second - steps)
            }
        }

    println(result)
    println(result.first * result.second)

    val result2 = commands
        .fold(Triple(0, 0, 0)) { acc, cmd ->
            val splittedCommand = cmd.split(" ")

            val steps = valueOf(splittedCommand[1])

            when (splittedCommand[0]) {
                "forward" -> Triple(acc.first + steps, acc.second + (steps * acc.third), acc.third)
                "down" -> Triple(acc.first, acc.second, acc.third + steps)
                else -> Triple(acc.first, acc.second, acc.third - steps)
            }
        }

    println(result2)
    println(result2.first * result2.second)
}