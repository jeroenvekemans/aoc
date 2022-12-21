import java.io.File
import java.lang.Integer.valueOf

fun main() {
    val numbersWithIndex: List<Pair<Long, Int>> =
        File("2022/src/20.txt").readLines().map { valueOf(it) }.mapIndexed { index, value -> Pair(value * 811589153L, index) }

    val source = (0 until 10).flatMap { numbersWithIndex }
    val result = source.fold(numbersWithIndex) { acc, next -> move(next, acc) }

    val zeroIndex = result.mapIndexed { index, value -> Pair(value.first, index) }.find { it.first==0L }!!.second
    val one = result[(1000 + zeroIndex) % result.size]
    val two = result[(2000 + zeroIndex) % result.size]
    val three = result[(3000 + zeroIndex) % result.size]

    val coordinates = one.first + two.first + three.first

    println(coordinates)
}

fun move(number: Pair<Long,Int>, numbers: List<Pair<Long,Int>>): List<Pair<Long,Int>> {
    if (number.first == 0L) {
        return numbers
    }

    val origIndex = numbers.indexOf(number)

    val lengthAfterRemove = numbers.size - 1
    val result = ((origIndex + number.first % lengthAfterRemove) % lengthAfterRemove).toInt()
    val newIndex = if (result <= 0) result + lengthAfterRemove else result

    val mutableList = numbers.toMutableList()
    mutableList.add((if (newIndex > origIndex) newIndex + 1 else newIndex), number)
    mutableList.removeAt(if (newIndex < origIndex) origIndex + 1 else origIndex)
    return mutableList.toList()
}
