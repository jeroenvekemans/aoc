import java.io.File
import java.lang.Integer.*

data class SectionRange(val start: Int, val end: Int) {

    fun fullyContains(otherSectionRange: SectionRange) : Boolean {
        return otherSectionRange.start >= this.start && otherSectionRange.end <= this.end
    }

    fun overlaps(otherSectionRange: SectionRange): Boolean {
        return (otherSectionRange.start >= this.start && otherSectionRange.start <= this.end)
    }

}

fun main() {
    val assignmentPairs: List<Pair<SectionRange, SectionRange>> = File("2022/src/04.txt")
        .readLines()
        .map {
            val digits = it.split("-", ",")

            Pair(
                SectionRange(valueOf(digits[0]), valueOf(digits[1])),
                SectionRange(valueOf(digits[2]), valueOf(digits[3])),
            )
        }
//        .filter { it.first.fullyContains(it.second) || it.second.fullyContains(it.first) }
        .filter { it.first.overlaps(it.second) || it.second.overlaps(it.first) }


    println(assignmentPairs.size)
}