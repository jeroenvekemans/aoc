import java.io.File
import java.util.*
import kotlin.math.min

fun main() {
    val raw = File("2021/src/23.txt").readLines()

    println(DayTwentyThree.solve(raw))
}

object DayTwentyThree {
    enum class Species(val sign: Char, val stepCost: Int) {
        Amber('A', 1), Bronze('B', 10), Copper('C', 100), Desert('D', 1000);

        companion object {
            fun getBySign(sign: Char): Species {
                return values().first { it.sign == sign }
            }
        }
    }

    data class Node(val number: Int)
    data class Edge(val first: Int, val second: Int) {
        fun contains(number: Int): Boolean {
            return number == first || number == second
        }

        fun other(number: Int): Int {
            return if (number == first) second else first
        }
    }

    data class Animal(val id: Number, val color: Species)
    data class Room(val color: Species, val node1: Int, val node2: Int)

    class GameState(val cost: Int, val animals: Map<Animal, Int>) {

        private val rooms = listOf(
            Room(Species.Amber, 12, 13),
            Room(Species.Bronze, 14, 15),
            Room(Species.Copper, 16, 17),
            Room(Species.Desert, 18, 19),
        )

        private fun isRoom(number: Int): Boolean {
            return rooms.any { it.node1 == number || it.node2 == number }
        }

        private fun getRoom(number: Int): Room {
            return rooms.first { it.node1 == number || it.node2 == number }
        }

        private val nodes = (1..19).map { Node(it) }
        private val edges = nodes.take(11).zipWithNext().map {
            Edge(it.first.number, it.second.number)
        } + listOf(
            Edge(3, 12), Edge(12, 13), Edge(5, 14), Edge(14, 15),
            Edge(7, 16), Edge(16, 17), Edge(9, 18), Edge(18, 19),
        )

        private fun connectsWith(step: Int, acc: Map<Int, Int>): Map<Int, Int> {
            val distance = step + 1

            val nextNodes = acc.flatMap { node ->
                edges.filter { it.contains(node.key) }.map { it.other(node.key) }
            }.filter { !animals.containsValue(it) }.filter { !acc.containsKey(it) }.associateWith { distance }

            if (nextNodes.isEmpty()) {
                return acc
            }

            return connectsWith(distance, acc + nextNodes)
        }

        private val animalsAtDestinationInBackOfRoom = animals.filter {
            rooms.any { r -> r.color == it.key.color && r.node2 == it.value }
        }

        val animalsAtDestination = animalsAtDestinationInBackOfRoom + animals.filterKeys { animalsAtDestinationInBackOfRoom.any { ga -> ga.key.color == it.color } }.filter {
            rooms.any { r -> r.color == it.key.color && r.node1 == it.value }
        }

        fun isComplete(): Boolean {
            return animalsAtDestination.size == 8
        }

        fun generateNextStates(): List<GameState> {
            val animalsToMove = animals.filterKeys { !animalsAtDestination.containsKey(it) }

            return animalsToMove.flatMap { a ->
                val currentNode = a.value
                val nextNodes = connectsWith(0, mapOf(Pair(currentNode, 0))).minus(currentNode)
                    .filter { !animals.containsValue(it.key) }

                nextNodes
                    .filter { nextNode ->
                        !listOf(3, 5, 7, 9).contains(nextNode.key)
                    }
                    .filter { nextNode ->
                        isRoom(currentNode) || isRoom(nextNode.key)
                    }
                    .filter { nextNode ->
                        val isRoom = isRoom(nextNode.key)

                        if (isRoom) {
                            val r = getRoom(nextNode.key)

                            r.color == a.key.color && ((nextNode.key == r.node2) || animalsAtDestination.containsValue(r.node2))
                        } else {
                            true
                        }
                    }
                    .map { nextNode ->
                        GameState(
                            cost + nextNode.value * a.key.color.stepCost,
                            animals + mapOf(Pair(a.key, nextNode.key))
                        )
                    }
            }
        }
    }

    fun solve(raw: List<String>): Int {
        val beginState = mapOf(
            Pair(Animal(1, Species.getBySign(raw[2][3])), 12),
            Pair(Animal(2, Species.getBySign(raw[3][3])), 13),
            Pair(Animal(3, Species.getBySign(raw[2][5])), 14),
            Pair(Animal(4, Species.getBySign(raw[3][5])), 15),
            Pair(Animal(5, Species.getBySign(raw[2][7])), 16),
            Pair(Animal(6, Species.getBySign(raw[3][7])), 17),
            Pair(Animal(7, Species.getBySign(raw[2][9])), 18),
            Pair(Animal(8, Species.getBySign(raw[3][9])), 19),
        )

        val initial = GameState(0, beginState)

        val queue = PriorityQueue<GameState> { s1, s2 ->
            if (s1.animalsAtDestination.size > s2.animalsAtDestination.size) {
                -1
            } else if (s1.animalsAtDestination.size < s2.animalsAtDestination.size) {
                1
            } else {
                s1.cost - s2.cost
            }
        }
        queue.add(initial)

        var minimum = Int.MAX_VALUE

        while (queue.isNotEmpty()) {
            val next = queue.poll()

            if (next.isComplete()) {
                minimum = min(minimum, next.cost)

                println("local minimum $minimum")
                queue.removeIf { it.cost >= minimum }
            } else {
                queue.addAll(next.generateNextStates())
            }
        }

        return -1
    }
}
