import java.io.File
import java.lang.Integer.valueOf
import java.lang.RuntimeException


data class Operation(val raw: String) {
    fun apply(oldValue: Int): Int {
        val operator = raw.substring(21, 22)
        val operandRaw = raw.substring(23)

        val operand = if (operandRaw == "old") oldValue else valueOf(operandRaw)

        return if (operator == "*") {
            oldValue * operand
        } else if (operator == "+") {
            oldValue + operand
        } else {
            throw RuntimeException("operator not valid")
        }
    }
}

data class Test(val raw: String, val targetIfTrue: Int, val targetIfFalse: Int) {
    fun apply(current: Int): Int {
        val operand = valueOf(raw.substring(19))

        return if (current % operand == 0) targetIfTrue else targetIfFalse
    }
}

data class Monkey(val items: List<Int>, val operation: Operation, val test: Test, val inspections: Int = 0)

fun main() {
    val monkeys: List<Monkey> = File("2022/src/11.txt")
        .readLines()
        .chunked(7)
        .map { rawMonkey ->
            Monkey(
                rawMonkey[1].trim().substring(16).split(", ").map { valueOf(it) },
                Operation(rawMonkey[2].trim()),
                Test(
                    rawMonkey[3].trim(),
                    valueOf(rawMonkey[4].trim().substring(25)),
                    valueOf(rawMonkey[5].trim().substring(26))
                )
            )
        }

    val res = (0 until 20).fold(monkeys) { acc, _ ->
        playRound(acc)
    }

    printState(res)

    val monkeyBusiness = res.sortedByDescending { it.inspections }.take(2).map { it.inspections }
    println(monkeyBusiness[0] * monkeyBusiness[1])
}

fun playRound(monkeys: List<Monkey>): List<Monkey> {
    return (monkeys.indices).fold(monkeys) { acc, next -> takeTurn(next, acc) }
}

fun takeTurn(current: Int, monkeys: List<Monkey>): List<Monkey> {
    val m = monkeys[current]

    val diffs = m.items.map {
        val newWorryLevel = m.operation.apply(it) / 3

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