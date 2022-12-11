import java.io.File
import java.lang.Integer.valueOf


data class Operation(val raw: String) {
    fun apply(oldValue: Map<Int, Int>): Map<Int,Int> {
        val operator = raw.substring(21, 22)
        val operandRaw = raw.substring(23)

        return if (operandRaw == "old") {
            oldValue
                .entries
                .map {
                    Pair(it.key, (it.value * it.value) % it.key)
                }
                .associateBy({ it.first }, { it.second })
        } else {
            val operand = valueOf(operandRaw)

            return if (operator == "*") {
                oldValue
                    .entries
                    .map {
                        Pair(it.key, (it.value * operand) % it.key)
                    }
                    .associateBy({ it.first }, { it.second })
            } else {
                oldValue
                    .entries
                    .map {
                        Pair(it.key, (it.value + operand) % it.key)
                    }
                    .associateBy({ it.first }, { it.second })
            }
        }
    }
}

data class Test(val raw: String, val targetIfTrue: Int, val targetIfFalse: Int) {
    fun apply(current: Map<Int, Int>): Int {
        val operand = valueOf(raw.substring(19))

        val curr = current.get(operand)!!

        return if (curr % operand == 0) targetIfTrue else targetIfFalse
    }
}

data class Monkey(val items: List<Map<Int, Int>>, val operation: Operation, val test: Test, val inspections: Int = 0)

fun main() {
    val rawMonkeys: List<List<String>> = File("2022/src/11.txt")
        .readLines()
        .chunked(7)

    val dividers = rawMonkeys.map { valueOf(it[3].trim().substring(19)) }
    println("dividers $dividers")

    val monkeys: List<Monkey> = File("2022/src/11.txt")
        .readLines()
        .chunked(7)
        .map { rawMonkey ->
            val rawItems = rawMonkey[1].trim().substring(16).split(", ")

            val remaindersByDivs = rawItems
                .map { valueOf(it) }
                .map { item ->
                    dividers.map { Pair(it, item % it) }.associateBy ({it.first}, {it.second})
                }

            Monkey(
                remaindersByDivs,
                Operation(rawMonkey[2].trim()),
                Test(
                    rawMonkey[3].trim(),
                    valueOf(rawMonkey[4].trim().substring(25)),
                    valueOf(rawMonkey[5].trim().substring(26))
                )
            )
        }

    val res = (0 until 10_000).fold(monkeys) { acc, _ ->
        playRound(acc)
    }

    printState(res)

    val monkeyBusiness = res.sortedByDescending { it.inspections }.take(2).map { it.inspections }
    println(monkeyBusiness[0])
    println(monkeyBusiness[1])
    println(monkeyBusiness[0].toLong() * monkeyBusiness[1].toLong())


}

fun playRound(monkeys: List<Monkey>): List<Monkey> {
    return (monkeys.indices).fold(monkeys) { acc, next -> takeTurn(next, acc) }
}

fun takeTurn(current: Int, monkeys: List<Monkey>): List<Monkey> {
    val m = monkeys[current]

    val diffs = m.items.map {
        val newWorryLevel = m.operation.apply(it)

        Pair(m.test.apply(newWorryLevel), newWorryLevel)
    }.groupBy({ it.first }, { it.second })

    return monkeys
        .mapIndexed { index, monkey ->
            if (index == current) {
                monkey.copy(items = emptyList(), inspections = monkey.inspections + monkey.items.size)
            } else {
                monkey.copy(items = monkey.items + diffs.getOrDefault(index, emptyList()))
            }
        }
}

fun printState(monkeys: List<Monkey>) {
    monkeys.forEachIndexed { index, m ->
        println("Monkey $index: ${m.items} + (evaluated ${m.inspections})")
    }
}