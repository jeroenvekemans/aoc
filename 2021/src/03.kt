import java.io.File

fun main() {
    val report: List<String> = File("2021/src/03.txt").readLines()

    println(DayThree().solve(report))
    println(DayThreeDelta().solve(report))
}

class DayThree {
    fun solve(report: List<String>): Int {
        val result = report.map { r -> r.map { Integer.valueOf(it.toString()) } }

        val resultSize = result.size

        val gamma = result.reduce { acc, list ->
            acc.zip(list).map { p -> p.first + p.second }
        }.map { sum -> if (sum > resultSize / 2) 1 else 0 }

        val gammaInteger = Integer.valueOf(gamma.joinToString(""), 2)
        val epsilon = gamma.map { bit -> if (bit == 1) 0 else 1 }
        val epsilonInteger = Integer.valueOf(epsilon.joinToString(""), 2)

        return gammaInteger * epsilonInteger
    }
}

class DayThreeDelta {
    fun filterInstructions(result: List<List<Int>>, currentPosition: Int, maxPosition: Int, comparator: (Int, Int) -> Int) : List<Int> {
        if (result.size == 1 || currentPosition == maxPosition) {
            return result[0]
        }

        val bitsAtCurrentPosition = result.map { r -> r[currentPosition] }
        val ones = bitsAtCurrentPosition.filter { i -> i == 1 }.count()
        val zeroes = bitsAtCurrentPosition.filter { i -> i == 0 }.count()

        val filtered = result.filter { r -> r[currentPosition] == comparator(zeroes, ones) }

        return filterInstructions(filtered, currentPosition + 1, maxPosition, comparator)
    }

    fun solve(report: List<String>): Int {
        val instructions = report.map { r -> r.map { Integer.valueOf(it.toString()) } }

        val oxygen = filterInstructions(instructions, 0, instructions[0].size) { zeroes, ones -> if (zeroes <= ones) 1 else 0 }
        val co2 = filterInstructions(instructions, 0, instructions[0].size) { zeroes, ones -> if (zeroes <= ones) 0 else 1 }

        return Integer.valueOf(oxygen.joinToString(""), 2) * Integer.valueOf(co2.joinToString(""), 2)
    }
}