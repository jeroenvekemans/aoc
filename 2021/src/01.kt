import java.io.File
import java.lang.Integer.valueOf

fun main() {
    val measurements: List<Int> = File("2021/src/01.txt").readLines().map { valueOf(it) }

    val res = measurements
        .zipWithNext()
        .filter { p -> p.first < p.second }
        .count()

    println(res)

    val res2 = measurements.windowed(3, 1)
        .zipWithNext()
        .filter { (p1, p2) -> p1.sum() < p2.sum() }
        .count()

    println(res2)
}