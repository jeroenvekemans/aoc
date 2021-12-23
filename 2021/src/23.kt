import java.io.File
import java.util.*

fun main() {
    val state = File("2021/src/23.txt").readLines()

    println(DayTwentyThree.solve(state))
}

object DayTwentyThree {

    val priorityQueue = PriorityQueue<State> { s1, s2 ->
        if (s1.todoScore() < s2.todoScore()) {
            -1
        } else if (s1.todoScore() > s2.todoScore()) {
            1
        } else {
            s1.cost - s2.cost
        }
    }

    fun solve(raw: List<String>): Int {
        val state = parseState(raw)

        priorityQueue.add(state)

        while (priorityQueue.isNotEmpty()) {
            val s = priorityQueue.poll()

            println(s.lockedAmphipods)

            if (s.isCompleted()) {
                println("found something")
                println(s)
                break
            }

            priorityQueue.addAll(state.possibleNextStates())
        }

        return -1
    }

    private fun parseState(state: List<String>): State {
        val hallwayNodes = (1..11).map { identifier ->
            Node(identifier.toString(), true, !listOf(3, 5, 7, 9).contains(identifier))
        }

        val destinationNode = listOf(
            Node("12", true, true),
            Node("13", true, true),
            Node("14", true, true),
            Node("15", true, true),
            Node("16", true, true),
            Node("17", true, true),
            Node("18", true, true),
            Node("19", true, true),
        )

        val hallwayConnections = hallwayNodes.zipWithNext().map { Edge(it.first.identifier, it.second.identifier) }

        val destinationEdges = listOf(
            Edge("3", "12"),
            Edge("12", "13"),
            Edge("5", "14"),
            Edge("14", "15"),
            Edge("7", "16"),
            Edge("16", "17"),
            Edge("9", "18"),
            Edge("18", "19"),
        )

        val fish = listOf(
            state[2][3],
            state[2][5],
            state[2][7],
            state[2][9],
            state[3][3],
            state[3][5],
            state[3][7],
            state[3][9]
        )

        val pods = fish.zip(listOf("12", "14", "16", "18", "13", "15", "17", "19"))
            .mapIndexed { index, p ->
                Amphipod(
                    p.first + index.toString(),
                    AmphipodType.values().first { it.id == p.first },
                    p.second
                )
            }

        val nodes = (hallwayNodes + destinationNode).map { it.identifier to it }.toMap()

        return State(0, nodes, hallwayConnections + destinationEdges, pods, emptySet())
    }

    data class State(
        val cost: Int,
        val nodes: Map<String, Node>,
        val edges: List<Edge>,
        val amphipods: List<Amphipod>,
        val lockedAmphipods: Set<String>
    ) {
        fun todoScore(): Int {
            return amphipods.filter { !lockedAmphipods.contains(it.identifier) }.sumOf { it.type.stepCost }
        }

        fun isCompleted(): Boolean {
            val currentPositions = amphipods.groupBy({ it.type }, { nodes[it.position]!! })

            return currentPositions.all {
                edges.contains(Edge(it.value[0].identifier, it.value[1].identifier)) ||
                        edges.contains(Edge(it.value[1].identifier, it.value[0].identifier))
            }
        }

        private fun connectedWith(step: Int, acc: Map<Node, Int>): Map<Node, Int> {
            val distance = step + 1

            val connectedWith = acc.keys.flatMap { canConnectAlready ->
                edges.filter { it.contains(canConnectAlready.identifier) }
                    .map { it.other(canConnectAlready.identifier) }
            }.map { nodes[it]!! }.filter { amphipods.none { a -> a.position == it.identifier } }

            if (acc.keys.containsAll(connectedWith)) {
                return acc
            }

            val newConnections = connectedWith.filter { !acc.keys.contains(it) }.associateWith { step }

            return connectedWith(distance, acc + newConnections)
        }

        private fun canMoveTo(pod: Amphipod): Map<Node, Int> {
            val currentPosition = nodes[pod.position]

            return connectedWith(0, mapOf(Pair(currentPosition!!, 0)))
                .filterKeys { it.identifier != pod.position }
                .filterKeys { it.canStop }

        }

        fun possibleNextStates(): List<State> {
            val allPossibleMoves = amphipods.filter { !lockedAmphipods.contains(it.identifier) }.flatMap { pod ->
                canMoveTo(pod).map { newNode ->
                    this.copy(
                        cost = cost + newNode.value * pod.type.stepCost,
                        amphipods = amphipods.filter { it.identifier != pod.identifier } + listOf(pod.copy(position = newNode.key.identifier)),
                        lockedAmphipods = lockedAmphipods + if (newNode.key.destination) setOf(pod.identifier) else emptySet()
                    )
                }
            }


            return allPossibleMoves
        }

    }

    data class Node(val identifier: String, val destination: Boolean, val canStop: Boolean)
    data class Edge(val first: String, val second: String) {
        fun contains(oneOf: String): Boolean {
            return oneOf == first || oneOf == second
        }

        fun other(oneOf: String): String {
            return if (oneOf == first) second else first
        }
    }

    enum class AmphipodType(val id: Char, val stepCost: Int) {
        Amber('A', 1),
        Bronze('B', 10),
        Copper('C', 100),
        Desert('D', 1000)
    }

    data class Amphipod(val identifier: String, val type: AmphipodType, val position: String)

}