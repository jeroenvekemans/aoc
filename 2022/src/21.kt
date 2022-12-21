import java.io.File
import java.lang.Integer.valueOf

sealed class ComparisonExpression {
    abstract fun simplify(): ComparisonExpression
    abstract fun toText(): String

    data class Scalar(val number: Long) : ComparisonExpression() {
        override fun simplify(): ComparisonExpression {
            return this
        }

        override fun toText(): String {
            return "$number"
        }
    }

    data class Equals(val op1: ComparisonExpression, val op2: ComparisonExpression) : ComparisonExpression() {
        override fun simplify(): ComparisonExpression {
            val simplifiedOp1 = op1.simplify()
            val simplifiedOp2 = op2.simplify()

//            val reduction =
//                if (simplifiedOp1 is Division && simplifiedOp2 is Scalar) {
//                    if (simplifiedOp1.op1 is Scalar) {
//                        Equals(
//                            simplifiedOp1.op2,
//                            Multiplication(simplifiedOp1.op1, simplifiedOp2).simplify()
//                        ).simplify()
//                    } else if (simplifiedOp1.op2 is Scalar) {
//                        Equals(
//                            simplifiedOp1.op1,
//                            Multiplication(simplifiedOp1.op2, simplifiedOp2).simplify()
//                        ).simplify()
//                    } else {
//                        Equals(simplifiedOp1, simplifiedOp2)
//                    }
//                } else if (simplifiedOp1 is Addition && simplifiedOp2 is Scalar) {
//                    if (simplifiedOp1.op1 is Scalar) {
//                        Equals(simplifiedOp1.op2, Subtraction(simplifiedOp2, simplifiedOp1.op1).simplify()).simplify()
//                    } else if (simplifiedOp1.op2 is Scalar) {
//                        Equals(simplifiedOp1.op1, Subtraction(simplifiedOp2, simplifiedOp1.op2).simplify()).simplify()
//                    } else {
//                        Equals(simplifiedOp1, simplifiedOp2)
//                    }
//                } else if (simplifiedOp1 is Multiplication && simplifiedOp2 is Scalar) {
//                    if (simplifiedOp1.op1 is Scalar) {
//                        Equals(simplifiedOp1.op2, Division(simplifiedOp2, simplifiedOp1.op1).simplify()).simplify()
//                    } else if (simplifiedOp1.op2 is Scalar) {
//                        Equals(simplifiedOp1.op1, Division(simplifiedOp2, simplifiedOp1.op2).simplify()).simplify()
//                    } else {
//                        Equals(simplifiedOp1, simplifiedOp2)
//                    }
//                } else if (simplifiedOp1 is Subtraction && simplifiedOp2 is Scalar) {
//                    if (simplifiedOp1.op1 is Scalar) {
//                        Equals(simplifiedOp1.op2, Addition(simplifiedOp2, simplifiedOp1.op1).simplify()).simplify()
//                    } else if (simplifiedOp1.op2 is Scalar) {
//                        Equals(simplifiedOp1.op1, Addition(simplifiedOp2, simplifiedOp1.op2).simplify()).simplify()
//                    } else {
//                        Equals(simplifiedOp1, simplifiedOp2)
//                    }
//                } else {
//                    Equals(simplifiedOp1, simplifiedOp2)
//                }
//
//            return if (reduction != Equals(simplifiedOp1, simplifiedOp2)) {
//                reduction
//            } else {
                return Equals(simplifiedOp1, simplifiedOp2)
//            }
        }

        override fun toText(): String {
            return "${op1.toText()}=${op2.toText()}"
        }

    }

    data class Unknown(val x: String) : ComparisonExpression() {
        override fun simplify(): ComparisonExpression {
            return this
        }

        override fun toText(): String {
            return "x"
        }

    }

    data class Multiplication(val op1: ComparisonExpression, val op2: ComparisonExpression) : ComparisonExpression() {
        override fun simplify(): ComparisonExpression {
            val one = op1.simplify()
            val two = op2.simplify()

            return if (one is Scalar && two is Scalar) {
                return Scalar(one.number * two.number)
            } else {
                Multiplication(one, two)
            }
        }

        override fun toText(): String {
            return "${op1.toText()}*${op2.toText()}"
        }

    }

    data class Division(val op1: ComparisonExpression, val op2: ComparisonExpression) : ComparisonExpression() {
        override fun simplify(): ComparisonExpression {
            val one = op1.simplify()
            val two = op2.simplify()

            return if (one is Scalar && two is Scalar && one.number % two.number == 0L) {
                Scalar(one.number / two.number)
            } else {
                Division(one, two)
            }
        }

        override fun toText(): String {
            return "${op1.toText()}/${op2.toText()}"
        }

    }

    data class Addition(val op1: ComparisonExpression, val op2: ComparisonExpression) : ComparisonExpression() {
        override fun simplify(): ComparisonExpression {
            val one = op1.simplify()
            val two = op2.simplify()

            return if (one is Scalar && two is Scalar) {
                return Scalar(one.number + two.number)
            } else {
                Addition(one, two)
            }
        }

        override fun toText(): String {
            return "(${op1.toText()}+${op2.toText()})"
        }

    }

    data class Subtraction(val op1: ComparisonExpression, val op2: ComparisonExpression) : ComparisonExpression() {
        override fun simplify(): ComparisonExpression {
            val one = op1.simplify()
            val two = op2.simplify()

            return if (one is Scalar && two is Scalar) {
                return Scalar(one.number - two.number)
            } else {
                Subtraction(one, two)
            }
        }

        override fun toText(): String {
            return "(${op1.toText()}-${op2.toText()})"
        }

    }
}

sealed class MonkeyJob {
    abstract fun resolve(dictionary: Map<String, MonkeyJob>):Long
    abstract fun formula(dictionary: Map<String, MonkeyJob>): ComparisonExpression

    data class EqualityJob(val operand1: String, val operand2: String) : MonkeyJob() {
        override fun resolve(dictionary: Map<String, MonkeyJob>): Long {
            throw UnsupportedOperationException("cannot")
        }

        override fun formula(dictionary: Map<String, MonkeyJob>): ComparisonExpression {
            val one = dictionary[operand1]!!.formula(dictionary)
            val two = dictionary[operand2]!!.formula(dictionary)
            return ComparisonExpression.Equals(one, two)
        }
    }

    data class UnknownJob(val name: String) : MonkeyJob() {
        override fun resolve(dictionary: Map<String, MonkeyJob>): Long {
            throw UnsupportedOperationException("cannot")
        }

        override fun formula(dictionary: Map<String, MonkeyJob>): ComparisonExpression {
            return ComparisonExpression.Unknown(name)
        }
    }

    data class YellJob(val number: Long) : MonkeyJob() {
        override fun resolve(dictionary: Map<String, MonkeyJob>): Long {
            return number
        }

        override fun formula(dictionary: Map<String, MonkeyJob>): ComparisonExpression {
            return ComparisonExpression.Scalar(number)
        }
    }

    data class MultiplicationJob(val operand1: String, val operand2: String) : MonkeyJob() {
        override fun resolve(dictionary: Map<String, MonkeyJob>): Long {
            val one = dictionary[operand1]!!.resolve(dictionary)
            val two = dictionary[operand2]!!.resolve(dictionary)
            return one * two
        }

        override fun formula(dictionary: Map<String, MonkeyJob>): ComparisonExpression {
            val one = dictionary[operand1]!!.formula(dictionary)
            val two = dictionary[operand2]!!.formula(dictionary)
            return ComparisonExpression.Multiplication(one, two)
        }
    }

    data class AdditionJob(val operand1: String, val operand2: String) : MonkeyJob() {
        override fun resolve(dictionary: Map<String, MonkeyJob>): Long {
            val one = dictionary[operand1]!!.resolve(dictionary)
            val two = dictionary[operand2]!!.resolve(dictionary)
            return one + two
        }

        override fun formula(dictionary: Map<String, MonkeyJob>): ComparisonExpression {
            val one = dictionary[operand1]!!.formula(dictionary)
            val two = dictionary[operand2]!!.formula(dictionary)
            return ComparisonExpression.Addition(one, two)
        }
    }

    data class DivisionJob(val operand1: String, val operand2: String) : MonkeyJob() {
        override fun resolve(dictionary: Map<String, MonkeyJob>): Long {
            val one = dictionary[operand1]!!.resolve(dictionary)
            val two = dictionary[operand2]!!.resolve(dictionary)
            return one / two
        }

        override fun formula(dictionary: Map<String, MonkeyJob>): ComparisonExpression {
            val one = dictionary[operand1]!!.formula(dictionary)
            val two = dictionary[operand2]!!.formula(dictionary)
            return ComparisonExpression.Division(one, two)
        }
    }

    data class SubtractionJob(val operand1: String, val operand2: String) : MonkeyJob() {
        override fun resolve(dictionary: Map<String, MonkeyJob>): Long {
            val one = dictionary[operand1]!!.resolve(dictionary)
            val two = dictionary[operand2]!!.resolve(dictionary)
            return one - two
        }

        override fun formula(dictionary: Map<String, MonkeyJob>): ComparisonExpression {
            val one = dictionary[operand1]!!.formula(dictionary)
            val two = dictionary[operand2]!!.formula(dictionary)
            return ComparisonExpression.Subtraction(one, two)
        }
    }
}

fun main() {
    val monkeyJobs: Map<String, MonkeyJob> = File("2022/src/21.txt")
        .readLines()
        .map { raw ->
            val name = raw.take(4)

            val job = if (name == "root") {
                MonkeyJob.EqualityJob(raw.substring(6, 10), raw.substring(13))
            } else if (name == "humn") {
                MonkeyJob.UnknownJob(name)
            } else if (raw.contains('+')) {
                MonkeyJob.AdditionJob(raw.substring(6, 10), raw.substring(13))
            } else if (raw.contains('-')) {
                MonkeyJob.SubtractionJob(raw.substring(6, 10), raw.substring(13))
            } else if (raw.contains('/')) {
                MonkeyJob.DivisionJob(raw.substring(6, 10), raw.substring(13))
            } else if (raw.contains('*')) {
                MonkeyJob.MultiplicationJob(raw.substring(6, 10), raw.substring(13))
            } else {
                MonkeyJob.YellJob((valueOf(raw.substring(6)).toLong()))
            }

            Pair(name, job)
        }.associateBy({ it.first }, { it.second })

    val result = monkeyJobs["root"]!!.formula(monkeyJobs)

    val res = result.simplify()
    println(res.toText())
}
