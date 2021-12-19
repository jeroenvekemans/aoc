import java.io.File
import java.lang.RuntimeException
import kotlin.math.abs

data class Scanner(val scanner: String, val beacons: List<Beacon>) {
    fun permutations(): List<Scanner> {
        return (0 until 24).map { index ->
            Scanner(scanner, beacons.map { it.permutations()[index] })
        }
    }

    fun distanceMatchesBiggerThanTwelve(otherScanner: Scanner): Map<Beacon, List<Beacon>> = this.beacons
        .flatMap { b -> otherScanner.beacons.map { otherBeacon -> Pair(b, b.distance(otherBeacon)) } }
        .groupBy({ it.second }, { it.first })
        .filterValues { it.size >= 12 }

    fun hasOverlappingRegion(otherScanner: Scanner): Boolean {
        return distanceMatchesBiggerThanTwelve(otherScanner).isNotEmpty()
    }

    fun normalizeAccordingTo(otherScanner: Scanner): Scanner {
        val result = distanceMatchesBiggerThanTwelve(otherScanner)

        if (result.isEmpty()) {
            throw RuntimeException("No overlapping regions...")
        }

        val firstMatch = result.entries.first()

        return Scanner(scanner, beacons.map { it.distance(firstMatch.key) })
    }

    fun distance(otherScanner: Scanner): Int {
        val distance = distanceMatchesBiggerThanTwelve(otherScanner).entries

        if (distance.isEmpty()) {
            println(this.scanner + " - " + otherScanner.scanner)
        }

        return 0
    }

}

data class Origin(val x: Int, val y: Int, val z: Int) {

    fun distance(otherOrigin: Origin): Int {
        return abs(x - otherOrigin.x) + abs(y - otherOrigin.y) + abs(z - otherOrigin.z)
    }

}
data class Beacon(val x: Int, val y: Int, val z: Int) {

    fun permutations(): List<Beacon> {
        return listOf(
            Beacon(x, y, z),
            Beacon(y, z, x),
            Beacon(z, x, y),
            Beacon(-x, z, y),
            Beacon(z, y, -x),
            Beacon(y, -x, z),
            Beacon(x, z, -y),
            Beacon(z, -y, x),
            Beacon(-y, x, z),
            Beacon(x, -z, y),
            Beacon(-z, y, x),
            Beacon(y, x, -z),
            Beacon(-x, -y, z),
            Beacon(-y, z, -x),
            Beacon(z, -x, -y),
            Beacon(-x, y, -z),
            Beacon(y, -z, -x),
            Beacon(-z, -x, y),
            Beacon(x, -y, -z),
            Beacon(-y, -z, x),
            Beacon(-z, x, -y),
            Beacon(-x, -z, -y),
            Beacon(-z, -y, -x),
            Beacon(-y, -x, -z)
        )
    }

    override fun toString(): String {
        return "$x,$y,$z"
    }

    fun distance(otherBeacon: Beacon): Beacon {
        return Beacon(this.x - otherBeacon.x, this.y - otherBeacon.y, this.z - otherBeacon.z)
    }

}

fun main() {
    val beaconsPerScanner = File("2021/src/19.txt").readLines()
        .fold(emptyList<Scanner>()) { acc, next ->
            if (next.startsWith("---")) {
                listOf(Scanner(next.removePrefix("--- ").removeSuffix(" ---"), emptyList())) + acc
            } else if (next.isEmpty()) {
                acc
            } else {
                val match = """(-?\d+),(-?\d+),(-?\d+)""".toRegex().find(next)
                val (x, y, z) = match!!.destructured
                listOf(
                    Scanner(
                        acc.first().scanner,
                        acc.first().beacons + listOf(Beacon(x.toInt(), y.toInt(), z.toInt()))
                    )
                ) + acc.drop(1)
            }
        }.reversed()

    println(DayNineteen.solve(beaconsPerScanner))
}

object DayNineteen {

    fun solve(normalizedObservations: List<Scanner>, origins: List<Origin>,  observations: List<Scanner>): Pair<List<Scanner>, List<Origin>> {
        if (observations.isEmpty()) {
            return Pair(normalizedObservations, origins)
        }

        val matchingPermutation = normalizedObservations
            .flatMap { normalized ->
                observations.flatMap {
                    obs -> obs.permutations().map { Triple(obs, normalized, it) }
                }
            }
            .first { it.second.hasOverlappingRegion(it.third) }

        val newNormalizedScanner = matchingPermutation.third.normalizeAccordingTo(matchingPermutation.second)
        val origin = matchingPermutation.second.distanceMatchesBiggerThanTwelve(matchingPermutation.third).entries.first().key

        return solve(normalizedObservations + listOf(newNormalizedScanner), origins + listOf(Origin(origin.x, origin.y, origin.z)), observations.filter { it != matchingPermutation.first })
    }

    fun solve(observations: List<Scanner>): Pair<Int, Int> {
        val scannerInOrigin = observations[0]

        val normalizedScanners = solve(listOf(scannerInOrigin), listOf(Origin(0, 0, 0)), observations.drop(1))

        val distinctBeacons = normalizedScanners.first.flatMap { it.beacons }.toSet().size

        val origins = normalizedScanners.second

        val originPairs = origins.flatMap  { o1 -> origins.map { o2 -> Pair(o1, o2)  } }

        val biggestDistance =
            originPairs
                .filter { it.first != it.second }
                .maxOf { it.first.distance(it.second) }

        return Pair(distinctBeacons, biggestDistance)
    }

}