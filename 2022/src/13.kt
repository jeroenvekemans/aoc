import java.io.File
import java.lang.Integer.valueOf

sealed class PacketItem {
    abstract fun isSmallerThanOrEqual(other: PacketItem): Boolean

    data class ScalarItem(val number: Int) : PacketItem() {
        override fun isSmallerThanOrEqual(other: PacketItem): Boolean {
            return when (other) {
                is ScalarItem -> number <= other.number
                is ListItem -> other.members.isNotEmpty() && isSmallerThanOrEqual(other.members.first())
            }
        }

        override fun toString(): String {
            return number.toString()
        }
    }

    data class ListItem(val members: List<PacketItem>) : PacketItem() {
        override fun isSmallerThanOrEqual(other: PacketItem): Boolean {
            return when (other) {
                is ScalarItem -> this.isSmallerThanOrEqual(ListItem(listOf(other)))
                is ListItem -> {
                    if (this.members.isEmpty()) {
                        return true
                    } else if (other.members.isEmpty()) {
                        return false
                    } else {
                        val first = this.members[0]
                        val otherFirst = other.members[0]

                        if (first.isSmallerThanOrEqual(otherFirst) && !otherFirst.isSmallerThanOrEqual(first)) {
                            return true
                        }

                        if (!first.isSmallerThanOrEqual(otherFirst)) {
                            return false;
                        }

                        return ListItem(this.members.drop(1)).isSmallerThanOrEqual(ListItem(other.members.drop(1)))
                    }
                }
            }
        }

        override fun toString(): String {
            return "[" + members.joinToString(",") + "]"
        }
    }
}

fun main() {
    val packetPairs: List<Pair<String, String>> = File("2022/src/13.txt")
        .readLines()
        .chunked(3)
        .map { raw ->
            Pair(raw[0], raw[1])
        }

    val pairs = packetPairs.map { Pair(parse(it.first), parse(it.second)) }


    val result = pairs.mapIndexed { index, pair ->
        Pair(index + 1, pair.first.isSmallerThanOrEqual(pair.second))
    }

    val first = result.filter { it.second }.sumOf { it.first }

    println("first $first")

    val dividerPacket = PacketItem.ListItem(listOf(PacketItem.ListItem(listOf(PacketItem.ScalarItem(2)))))
    val dividerPacket2 = PacketItem.ListItem(listOf(PacketItem.ListItem(listOf(PacketItem.ScalarItem(6)))))
    val allPackets = pairs.flatMap { listOf(it.first, it.second) } + listOf(dividerPacket, dividerPacket2)


    val sorted = allPackets.drop(1).fold(listOf(allPackets.first())) { acc, next ->
        val smaller = acc.takeWhile { sortedPacketValue -> sortedPacketValue.isSmallerThanOrEqual(next) }

        smaller + listOf(next) + acc.drop(smaller.size)
    }

    println(sorted.joinToString("\n"))

    val second = (sorted.indexOf(dividerPacket) + 1) * (sorted.indexOf(dividerPacket2) + 1)
    println("second $second")
}

fun parse(representation: String): PacketItem {
    return if (representation.startsWith('[')) {
        parseList(representation)
    } else {
        PacketItem.ScalarItem(valueOf(representation))
    }
}

fun parseList(representation: String): PacketItem {
    val withoutOuterBrackets = representation.removeSurrounding("[", "]")

    val subs = mutableListOf<PacketItem>()
    var depth = 0
    var part = ""

    for (character in withoutOuterBrackets.toCharArray()) {
        if (depth == 0 && character == ',') {
            subs.add(parse(part))
            part = ""
        } else {
            if (character == '[') {
                depth++
            } else if (character == ']') {
                depth--;
            }
            part += character
        }
    }

    if (part.isNotEmpty()) {
        subs.add(parse(part))
    }

    return PacketItem.ListItem(subs.toList())
}
