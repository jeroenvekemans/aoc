import java.io.File
import java.lang.IllegalStateException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

sealed class SnailFishNumber {
    abstract fun regular(): Boolean
    abstract fun value(): Int
    abstract fun addToLeft(add: Int): SnailFishNumber
    abstract fun addToRight(add: Int): SnailFishNumber
    abstract fun explode(level: Int): Pair<SnailFishNumber, ExplosionPropagation>
    abstract fun split(): SnailFishNumber
    abstract fun magnitude(): Int

    fun add(otherNumber: SnailFishNumber): SnailFishNumber {
        return ComposedSnailFishNumber(this, otherNumber)
    }

    fun reduce(): SnailFishNumber {
        val explode = this.explode(1)

        return if (explode.first != this) {
            explode.first.reduce()
        } else {
            val split = this.split()

            return if (split != this) {
                split.reduce()
            } else {
                this
            }
        }
    }

}

data class SimpleSnailFishNumber(val number: Int) : SnailFishNumber() {
    override fun regular(): Boolean {
        return true;
    }

    override fun value(): Int {
        return number
    }

    override fun addToLeft(add: Int): SnailFishNumber {
        return SimpleSnailFishNumber(number + add)
    }

    override fun addToRight(add: Int): SnailFishNumber {
        return SimpleSnailFishNumber(number + add)
    }

    override fun explode(level: Int): Pair<SnailFishNumber, ExplosionPropagation> {
        return Pair(this, ExplosionPropagation(0, 0));
    }

    override fun split(): SnailFishNumber {
        return if (number >= 10) {
            ComposedSnailFishNumber(
                SimpleSnailFishNumber(floor(number / 2.0).toInt()),
                SimpleSnailFishNumber(ceil(number / 2.0).toInt())
            )
        } else this
    }

    override fun magnitude(): Int {
        return number
    }

    override fun toString(): String {
        return number.toString()
    }
}

data class ComposedSnailFishNumber(val left: SnailFishNumber, val right: SnailFishNumber) : SnailFishNumber() {
    override fun regular(): Boolean {
        return false;
    }

    override fun value(): Int {
        throw IllegalStateException()
    }

    override fun addToLeft(add: Int): SnailFishNumber {
        return ComposedSnailFishNumber(this.left.addToLeft(add), this.right)
    }

    override fun addToRight(add: Int): SnailFishNumber {
        return ComposedSnailFishNumber(this.left, this.right.addToRight(add))
    }

    override fun explode(level: Int): Pair<SnailFishNumber, ExplosionPropagation> {
        if (level > 4 && left.regular() && right.regular()) {
            return Pair(SimpleSnailFishNumber(0), ExplosionPropagation(left.value(), right.value()))
        }

        val reductionLeft = left.explode(level + 1)
        val reductionRight = right.explode(level + 1)


        return if (left != reductionLeft.first) {
            val updateRight = if (reductionLeft.second.right != 0) right.addToLeft(reductionLeft.second.right) else right

            Pair(ComposedSnailFishNumber(reductionLeft.first, updateRight), ExplosionPropagation(reductionLeft.second.left, 0))
        } else {
            if (right != reductionRight.first) {
                val updateLeft = if (reductionRight.second.left != 0) left.addToRight(reductionRight.second.left) else left
                Pair(ComposedSnailFishNumber(updateLeft, reductionRight.first), ExplosionPropagation(0, reductionRight.second.right))
            } else {
                Pair(this, ExplosionPropagation(0, 0))
            }
        }
    }

    override fun split(): SnailFishNumber {
        val leftSplit = left.split()

        return if (leftSplit != left) {
            ComposedSnailFishNumber(leftSplit, right)
        } else {
            val rightSplit = right.split()

            if (rightSplit != right) {
                ComposedSnailFishNumber(left, rightSplit)
            } else {
                this
            }
        }
    }

    override fun magnitude(): Int {
        return left.magnitude() * 3 + right.magnitude() * 2
    }

    override fun toString(): String {
        return "[$left,$right]"
    }
}

data class ExplosionPropagation(val left: Int, val right: Int) {
}

fun main() {
    val snailFishNumbers = File("2021/src/18.txt").readLines()

    println(DayEighteen.solve(snailFishNumbers))
    println(DayEighteen.solveDelta(snailFishNumbers))
}

object DayEighteen {

    fun solve(snailFishNumbers: List<String>): Int {
        val numbers = snailFishNumbers.map { parseNumber(it) }

        val sum = numbers.reduce { acc, next -> acc.add(next).reduce() }

        return sum.magnitude()
    }

    fun solveDelta(snailFishNumbers: List<String>): Int {
        val numbers = snailFishNumbers.map { parseNumber(it) }

        val pairs = numbers.flatMap {
            n1 -> numbers.map {
                n2 -> Pair(n1, n2)
            }
        }.filter { it.first != it.second }

        return pairs.fold(0) { acc, next ->
            max(acc, next.first.add(next.second).reduce().magnitude())
        }
    }

    private fun parseNumber(raw: String): SnailFishNumber {
        val withoutBrackets = raw.removeSurrounding("[", "]")

        val divider = withoutBrackets.foldIndexed(Pair(0, -1)) { index, acc, next ->
            if (next == '[') {
                Pair(acc.first + 1, acc.second)
            } else if (next == ']') {
                Pair(acc.first - 1, acc.second)
            } else if (next == ',' && acc.first == 0) {
                Pair(acc.first, index)
            } else {
                acc
            }
        }.second


        val first = withoutBrackets.substring(0, divider)
        val second = withoutBrackets.substring(divider + 1)

        return ComposedSnailFishNumber(parseSimpleOrRecurse(first), parseSimpleOrRecurse(second))
    }

    private fun parseSimpleOrRecurse(part: String): SnailFishNumber {
        if (part.startsWith("[")) {
            return parseNumber(part)
        } else {
            return SimpleSnailFishNumber(part.toInt())
        }
    }

}