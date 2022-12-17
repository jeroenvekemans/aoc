import java.io.File
import java.lang.Integer.valueOf
import kotlin.reflect.jvm.internal.ReflectProperties.Val

data class Valve(val name: String, val flow: Int, val pathTo: List<String>)

fun main() {
    val valves: Map<String, Valve> = File("2022/src/16.txt").readLines()
        .map { raw ->
            val match = Regex("Valve ([a-zA-z]+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)").find(raw)!!
            val (name, rate, paths) = match.destructured
            Valve(
                name,
                valueOf(rate),
                paths.split(", ")
            )
        }
        .associateBy { it.name }

    val directPathCosts: Map<Valve, Map<String, Int>> = computeDirectPathCosts(valves.values)
    val start = valves["AA"]!!

    println(directPathCosts.entries.joinToString("\n"))

    val amountOfNonZeroValves = valves.values.count { it.flow > 0 }

    val individualRuns = findMaximumPressure(valves, directPathCosts, 26, listOf(start), 0).filter {
        it.first.size in amountOfNonZeroValves/2-3..amountOfNonZeroValves/2+3
    }.sortedByDescending { it.second }.take(1000)

    println(individualRuns.size)

    val result = individualRuns
        .flatMap { run1 -> individualRuns.map { run2 -> Pair(run1, run2) }}
        .filter { (run1, run2) ->
            run1.first.isNotEmpty() && run2.first.isNotEmpty()
        }
        .filter { (run1, run2) ->
            val pathElementsRunOneWithoutStart = run1.first.dropLast(1).toSet()
            val pathElementsRunTwoWithoutStart = run2.first.dropLast(1).toSet()

            (pathElementsRunOneWithoutStart - pathElementsRunTwoWithoutStart).size == pathElementsRunOneWithoutStart.size
        }
        .maxBy { (run1, run2) -> run1.second + run2.second }

    println(result)
    println(result.first.second + result.second.second)
}

fun findMaximumPressure(
    valves: Map<String, Valve>,
    directPathCosts: Map<Valve, Map<String, Int>>,
    remainingMinutes: Int,
    path: List<Valve>,
    acc: Int
): List<Pair<List<String>, Int>> {
    if (remainingMinutes <= 2) {
        return listOf(Pair(path.map { it.name }, acc))
    }

    val current = path.first()
    val options = directPathCosts[current]!!

    val results = options
        .filter { !path.contains(valves[it.key]!!) }
        .filter { valves[it.key]!!.flow > 0 }
        .map { (valveName, travelCost) ->
            val next = valves[valveName]!!

            findMaximumPressure(
                valves,
                directPathCosts,
                remainingMinutes - travelCost - 1,
                listOf(next) + path,
                acc + (next.flow * (remainingMinutes - travelCost - 1))
            )
        }

    return listOf(Pair(path.map { it.name }, acc)) + if (results.isEmpty()) emptyList() else results.flatten()
}

// https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
fun computeDirectPathCosts(valves: Collection<Valve>): Map<Valve, Map<String, Int>> {
    val costs = valves.associateBy({ it }, { it.pathTo.associateBy({ it }, { 1 }).toMutableMap() }).toMutableMap()

    costs.keys.forEach { k ->
        costs.keys.forEach { i ->
            costs.keys.forEach { j ->
                val costIk = costs[i]!!.getOrDefault(k.name, 1_000_000)
                val costKj = costs[k]!!.getOrDefault(j.name, 1_000_000)
                val costIj = costs[i]!!.getOrDefault(j.name, 1_000_000)

                val costViaMiddle = costIk + costKj
                if (costViaMiddle < costIj) {
                    costs[i]!![j.name] = costViaMiddle
                }
            }
        }
    }

    return costs
}
