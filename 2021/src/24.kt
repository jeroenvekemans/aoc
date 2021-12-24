import java.io.File
import java.lang.RuntimeException

fun main() {
    fun parseOperand(rawOperand: String): Operand {
        val numeric = rawOperand.toIntOrNull() ?: return Variable(rawOperand[0])

        return Constant(numeric)
    }

    val instructions = File("2021/src/24.txt").readLines()
        .map { instruction ->
            val match = """([a-z]+) ([a-z]+)\s?(-?[a-z0-9]+)?""".toRegex().find(instruction)
            val (operator, op1, op2) = match!!.destructured

            when (operator) {
                "inp" -> InputInstruction(Variable(op1[0]))
                "add" -> AddInstruction(Variable(op1[0]), parseOperand(op2))
                "mul" -> MulInstruction(Variable(op1[0]), parseOperand(op2))
                "div" -> DivInstruction(Variable(op1[0]), parseOperand(op2))
                "mod" -> ModInstruction(Variable(op1[0]), parseOperand(op2))
                "eql" -> EqlInstruction(Variable(op1[0]), parseOperand(op2))
                else -> throw RuntimeException("don't know how to parse $instruction")
            }
        }

    println(DayTwentyFour.solve(instructions))
}

sealed class Operand
data class Constant(val value: Int) : Operand()
data class Variable(val value: Char) : Operand()

sealed class Instruction
data class InputInstruction(val op1: Variable) : Instruction()
data class AddInstruction(val op1: Variable, val op2: Operand) : Instruction()
data class MulInstruction(val op1: Variable, val op2: Operand) : Instruction()
data class DivInstruction(val op1: Variable, val op2: Operand) : Instruction()
data class ModInstruction(val op1: Variable, val op2: Operand) : Instruction()
data class EqlInstruction(val op1: Variable, val op2: Operand) : Instruction()

object DayTwentyFour {

    data class State(
        val line: Int,
        val variableState: Map<Variable, Int>,
        val processedMonad: List<Int>
    )

    private fun assignNextInput(
        instructions: List<Instruction>,
        variables: Map<Variable, Int>,
        variable: Variable,
        monad: List<Int>
    ): Boolean {
        if (monad.isEmpty()) {
            throw RuntimeException("monad is empty")
        }

        val nextNumber = monad.first()
        val monadRest = monad.drop(1)

        return evaluateInstructions(instructions, variables + mapOf(Pair(variable, nextNumber)), monadRest)
    }

    private fun value(operand: Operand, variables: Map<Variable, Int>): Int {
        return when (operand) {
            is Constant -> operand.value
            is Variable -> variables[operand]!!
        }
    }

    private fun evaluateInstructions(
        instructions: List<Instruction>,
        variables: Map<Variable, Int>,
        monad: List<Int>
    ): Boolean {
        if (instructions.isEmpty()) {
            return variables[Variable('z')] == 0
        }

        val ins = instructions.first()
        val nextInstructions = instructions.drop(1)

        val result = when (ins) {
            is InputInstruction -> assignNextInput(nextInstructions, variables, ins.op1, monad)
            is AddInstruction -> evaluateInstructions(
                nextInstructions,
                variables + mapOf(Pair(ins.op1, variables[ins.op1]!! + value(ins.op2, variables))),
                monad
            )
            is MulInstruction -> evaluateInstructions(
                nextInstructions,
                variables + mapOf(Pair(ins.op1, variables[ins.op1]!! * value(ins.op2, variables))),
                monad
            )
            is DivInstruction -> evaluateInstructions(
                nextInstructions,
                variables + mapOf(Pair(ins.op1, variables[ins.op1]!! / value(ins.op2, variables))),
                monad
            )
            is ModInstruction -> evaluateInstructions(
                nextInstructions,
                variables + mapOf(Pair(ins.op1, variables[ins.op1]!! % value(ins.op2, variables))),
                monad
            )
            is EqlInstruction -> evaluateInstructions(
                nextInstructions,
                variables + mapOf(Pair(ins.op1, if (variables[ins.op1]!! == value(ins.op2, variables)) 1 else 0)),
                monad
            )
        }

        return result
    }

    fun solve(instructions: List<Instruction>) {
        val init = mapOf(
            Pair(Variable('w'), 0),
            Pair(Variable('x'), 0),
            Pair(Variable('y'), 0),
            Pair(Variable('z'), 0)
        )

//        val result = (99999999999999 downTo 88888888888888 step 1)
//            .asSequence()
//            .map { it.toString().toCharArray().map { it.digitToInt() } }
//            .filter { !it.contains(0) }
//            .first { evaluateInstructions(instructions, init, it) }

        val result = evaluateInstructions(instructions, init, "13579246899998".toCharArray().map { it.digitToInt() })

        println(result)
    }


}