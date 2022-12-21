import java.io.File
import java.lang.Integer.valueOf

data class Cube(val x: Int, val y: Int, val z: Int) {
    fun adjacentCubes(): Set<Cube> {
        return setOf(
            Cube(x, y, z - 1),
            Cube(x, y, z + 1),
            Cube(x, y - 1, z),
            Cube(x, y + 1, z),
            Cube(x + 1, y, z),
            Cube(x - 1, y, z),
        )
    }

    fun withinSpace(min: Cube, max: Cube): Boolean {
        return x in min.x..max.x && y in min.y..max.y && z in min.z..max.z
    }
}

fun main() {
    val cubes: Set<Cube> = File("2022/src/18.txt")
        .readLines()
        .map { it.split(",") }
        .map { Cube(valueOf(it[0]), valueOf(it[1]), valueOf(it[2])) }
        .toSet()

    val exposedSides = exposedSides(cubes)
    println(exposedSides)

    val minSpace = Cube(cubes.minOf { it.x - 1 }, cubes.minOf { it.y - 1 }, cubes.minOf { it.z - 1 })
    val maxSpace = Cube(cubes.maxOf { it.x + 1 }, cubes.maxOf { it.y + 1 }, cubes.maxOf { it.z + 1 })

    val result = searchSides(cubes, listOf(minSpace), emptyList(), 0, Pair(minSpace, maxSpace))
    println(result)
}

tailrec fun searchSides(
    cubes: Set<Cube>,
    potentials: List<Cube>,
    evaluated: List<Cube>,
    acc: Int,
    space: Pair< Cube, Cube>
): Int {
    if (potentials.isEmpty()) {
        return acc
    }

    val cubePotentiallyTouchingDropletFromOutside = potentials[0]

    return if (evaluated.contains(cubePotentiallyTouchingDropletFromOutside)) {
        searchSides(cubes, potentials.drop(1), evaluated + cubePotentiallyTouchingDropletFromOutside, acc, space)
    } else {
        val result = cubePotentiallyTouchingDropletFromOutside.adjacentCubes().filter { it.withinSpace(space.first, space.second) }

        val sidesTouchingCubes = result.count { it in cubes }
        val toValidateIfTouchingCubes = result.filter { it !in cubes }
        searchSides(
            cubes,
            potentials.drop(1) + toValidateIfTouchingCubes,
            evaluated + cubePotentiallyTouchingDropletFromOutside,
            acc + sidesTouchingCubes,
            space
        )
    }
}

fun exposedSides(cubes: Set<Cube>): Int {
    return cubes.sumOf { cube ->
        6 - cubes.filter { it != cube }.count { it in cube.adjacentCubes() }
    }
}