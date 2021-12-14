import java.io.File

data class Entry(val input: List<String>, val output: List<String>)

fun main() {
    val signals: List<Entry> = File("2021/src/08.txt").readLines()
        .map { entry ->
            Entry(
                entry.substringBefore('|').split(" ").filter { it.isNotEmpty() },
                entry.substringAfter('|').split(" ").filter { it.isNotEmpty() }
            )
        }


    println(DayEight.solve(signals))
    println(DayEightDelta.solve(signals))
}

object DayEight {

    fun solve(signals: List<Entry>): Int {
        return signals
            .flatMap { signal -> signal.output }
            .count { listOf(2, 3, 4, 7).contains(it.length) }
    }

}

data class SegmentPossibility(val character: Char, val possibilities: List<Char>);

object DayEightDelta {

    private fun reducePossibilities(signal: Entry, segments: List<SegmentPossibility>): List<SegmentPossibility> {
        return (signal.input + signal.output).fold(segments) { acc, s ->
            if (s.length == 2) {
                acc
                    .map {
                        if ("cf".contains(it.character)) {
                            SegmentPossibility(it.character, it.possibilities.filter { p -> s.contains(p) })
                        } else {
                            SegmentPossibility(it.character, it.possibilities.filter { p -> !s.contains(p) })
                        }
                    }
            } else if (s.length == 3) {
                acc
                    .map {
                        if ("acf".contains(it.character)) {
                            SegmentPossibility(it.character, it.possibilities.filter { p -> s.contains(p) })
                        } else {
                            SegmentPossibility(it.character, it.possibilities.filter { p -> !s.contains(p) })
                        }
                    }
            } else if (s.length == 4) {
                acc
                    .map {
                        if ("bcdf".contains(it.character)) {
                            SegmentPossibility(it.character, it.possibilities.filter { p -> s.contains(p) })
                        } else {
                            SegmentPossibility(it.character, it.possibilities.filter { p -> !s.contains(p) })
                        }
                    }
            } else {
                acc
            }
        }
    }

    fun solvePerSignal(signal: Entry): Int {
        val segments = ('a'..'g').map { SegmentPossibility(it, ('a'..'g').map { it }) }

        val initialReduction = reducePossibilities(signal, segments)

        val valid = listOf(
            "abcefg",
            "cf",
            "acdeg",
            "acdfg",
            "bcdf",
            "abdfg",
            "abdefg",
            "acf",
            "abcdefg",
            "abcdfg"
        )

        val combos = initialReduction
            .drop(1)
            .fold(initialReduction[0].possibilities.map { listOf(it) }) { acc, poss ->
                poss.possibilities.map { p -> acc.map { a -> a + p } }.flatten()
            }
            .filter { it.toSet().size == it.size }

        val validCombo = combos
            .first { c ->
                val convertedValids = valid
                    .map { v -> v.toCharArray().map { vc -> c[vc.code - 'a'.code] }.sorted() }
                    .map { it.joinToString("") }

                (signal.input + signal.output)
                    .map { it.toCharArray().sorted() }
                    .map { it.joinToString("") }
                    .all { convertedValids.contains(it) }
            }

        val convertedValids = valid
            .map { v -> v.toCharArray().map { vc -> validCombo[vc.code - 'a'.code] }.sorted() }
            .map { it.joinToString("") }

        val numbers = signal
            .output
            .map { it.toCharArray().sorted() }
            .map { it.joinToString("") }
            .map { convertedValids.indexOf(it) }

        return numbers[0] * 1000 + numbers[1] * 100 + numbers[2] * 10 + numbers[3]
    }

    fun solve(signals: List<Entry>): Int {
        return signals.map { solvePerSignal(it) }.sum()
    }

}