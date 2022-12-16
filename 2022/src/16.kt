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

    val result = findMaximumPressure(valves, directPathCosts, 30, listOf(start), 0)

    println(result)
}

fun findMaximumPressure(
    valves: Map<String, Valve>,
    directPathCosts: Map<Valve, Map<String, Int>>,
    remainingMinutes: Int,
    path: List<Valve>,
    acc: Int
): Int {
    if (remainingMinutes <= 2) {
        return acc
    }

    val current = path.first()
    val options = directPathCosts[current]!!

    val results = options
        .filter { !path.contains(valves[it.key]!!) }
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

    return if (results.isEmpty()) acc else results.maxBy { it }
}

// https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
fun computeDirectPathCosts(valves: Collection<Valve>): Map<Valve, Map<String, Int>> {
    val costs = valves.associateBy({ it }, { it.pathTo.associateBy({ it }, { 1 }).toMutableMap() }).toMutableMap()

    costs.keys.forEach { k ->
        costs.keys.forEach { i ->
            costs.keys.forEach { j ->
                val costIk = costs[i]!!.getOrDefault(k.name, 99999)
                val costKj = costs[k]!!.getOrDefault(j.name, 99999)
                val costIj = costs[i]!!.getOrDefault(j.name, 99999)

                val costViaMiddle = costIk + costKj
                if (costViaMiddle < costIj) {
                    costs[i]!![j.name] = costViaMiddle
                }
            }
        }
    }

    val valvesWithZeroFlow = valves.filter { it.flow == 0 }.toSet()

    costs.forEach { entry ->
        valvesWithZeroFlow.forEach { valveToRemove ->
            if (entry.value.containsKey(valveToRemove.name)) {
                entry.value.remove(valveToRemove.name)
            }
        }
    }


    return costs
}
