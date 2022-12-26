import java.io.File
import java.lang.Integer.valueOf

data class Blueprint(
    val number: Int,
    val oreCost: List<Int>,
    val clayCost: List<Int>,
    val obsidianCost: List<Int>,
    val geodeCost: List<Int>,
)

data class RobotState(
    val blueprint: Blueprint,
    val minutesRemaining: Int,
    val robots: List<Int>,
    val budget: List<Int>
)

fun main() {
    val blueprints: List<Blueprint> = File("2022/src/19.txt").readLines()
        .map { text ->
            val match =
                """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()
                    .find(text)
            val (bpNumber, oreCost, clayCost, obsidianRobotCost1, obsidianRobotCost2, geodeRobotCost1, geodeRobotCost2) = match!!.destructured
            Blueprint(
                valueOf(bpNumber),
                listOf(valueOf(oreCost), 0, 0, 0),
                listOf(valueOf(clayCost), 0, 0, 0),
                listOf(valueOf(obsidianRobotCost1), valueOf(obsidianRobotCost2), 0, 0),
                listOf(valueOf(geodeRobotCost1), 0, valueOf(geodeRobotCost2), 0),
            )
        }

    println(blueprints[0])

    val result = maximizeGeodes(listOf(RobotState(
        blueprints[0],
        24,
        listOf(1,0,0,0),
        listOf(0,0,0,0)
    )))

    println(result)
}

fun maximizeGeodes(states: List<RobotState>): RobotState? {
    return null
}
