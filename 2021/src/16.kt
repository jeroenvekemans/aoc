import java.lang.RuntimeException

fun main() {
    val input =
        "020D708041258C0B4C683E61F674A1401595CC3DE669AC4FB7BEFEE840182CDF033401296F44367F938371802D2CC9801A980021304609C431007239C2C860400F7C36B005E446A44662A2805925FF96CBCE0033C5736D13D9CFCDC001C89BF57505799C0D1802D2639801A900021105A3A43C1007A1EC368A72D86130057401782F25B9054B94B003013EDF34133218A00D4A6F1985624B331FE359C354F7EB64A8524027D4DEB785CA00D540010D8E9132270803F1CA1D416200FDAC01697DCEB43D9DC5F6B7239CCA7557200986C013912598FF0BE4DFCC012C0091E7EFFA6E44123CE74624FBA01001328C01C8FF06E0A9803D1FA3343E3007A1641684C600B47DE009024ED7DD9564ED7DD940C017A00AF26654F76B5C62C65295B1B4ED8C1804DD979E2B13A97029CFCB3F1F96F28CE43318560F8400E2CAA5D80270FA1C90099D3D41BE00DD00010B893132108002131662342D91AFCA6330001073EA2E0054BC098804B5C00CC667B79727FF646267FA9E3971C96E71E8C00D911A9C738EC401A6CBEA33BC09B8015697BB7CD746E4A9FD4BB5613004BC01598EEE96EF755149B9A049D80480230C0041E514A51467D226E692801F049F73287F7AC29CB453E4B1FDE1F624100203368B3670200C46E93D13CAD11A6673B63A42600C00021119E304271006A30C3B844200E45F8A306C8037C9CA6FF850B004A459672B5C4E66A80090CC4F31E1D80193E60068801EC056498012804C58011BEC0414A00EF46005880162006800A3460073007B620070801E801073002B2C0055CEE9BC801DC9F5B913587D2C90600E4D93CE1A4DB51007E7399B066802339EEC65F519CF7632FAB900A45398C4A45B401AB8803506A2E4300004262AC13866401434D984CA4490ACA81CC0FB008B93764F9A8AE4F7ABED6B293330D46B7969998021C9EEF67C97BAC122822017C1C9FA0745B930D9C480"

    println(DaySixteen.solve(input))
    println(DaySixteen.solveDelta(input))
}

object DaySixteen {

    private fun solveRecursively(binaryRepresentation: String): Pair<Int, String> {
        if (binaryRepresentation.isEmpty()) {
            return Pair(0, "")
        }

        val packetVersion = Integer.valueOf(binaryRepresentation.substring(0, 3), 2)
        val packetId = Integer.valueOf(binaryRepresentation.substring(3, 6), 2)

        if (packetId == 4) {
            val groups = binaryRepresentation.substring(6).windowed(5, 5)

            val firstZeroIndex = groups.map { it.first() }.indexOfFirst { it == '0' }

            return Pair(packetVersion, binaryRepresentation.substring(6 + 5 * (1 + firstZeroIndex)))
        } else {
            val operatorPacket = binaryRepresentation.substring(6, 7)

            val subPacketLengthLength = if (operatorPacket == "0") 15 else 11
            val subPacketLength = Integer.valueOf(binaryRepresentation.substring(7, 7 + subPacketLengthLength), 2)

            if (operatorPacket == "0") {
                val limitedSubPacketsByLength = binaryRepresentation.substring(
                    7 + subPacketLengthLength,
                    7 + subPacketLengthLength + subPacketLength
                )

                var remainder = limitedSubPacketsByLength
                var result = 0

                while (remainder.isNotEmpty()) {
                    val solution = solveRecursively(remainder)

                    result += solution.first
                    remainder = solution.second
                }

                return Pair(
                    packetVersion + result,
                    binaryRepresentation.substring(7 + subPacketLengthLength + subPacketLength)
                )
            } else {
                val subPackets = binaryRepresentation.substring(7 + subPacketLengthLength)

                var count = subPacketLength
                var remainder = subPackets
                var result = 0

                while (count-- > 0) {
                    val solution = solveRecursively(remainder)

                    result += solution.first
                    remainder = solution.second
                }

                return Pair(packetVersion + result, remainder)
            }
        }
    }

    private fun solveDeltaRecursively(binaryRepresentation: String, ): Pair<Long, String> {
        if (binaryRepresentation.isEmpty()) {
            return Pair(0, "")
        }

        val packetId = Integer.valueOf(binaryRepresentation.substring(3, 6), 2)

        if (packetId == 4) {
            val groups = binaryRepresentation.substring(6).windowed(5, 5)

            val firstZeroIndex = groups.map { it.first() }.indexOfFirst { it == '0' }

            val literalValue =
                groups
                    .take(firstZeroIndex + 1)
                    .joinToString("") { it.substring(1) }
                    .toLong(2)

            return Pair(literalValue, binaryRepresentation.substring(6 + 5 * (1 + firstZeroIndex)))
        } else {
            val operatorPacket = binaryRepresentation.substring(6, 7)

            val subPacketLengthLength = if (operatorPacket == "0") 15 else 11
            val subPacketLength = Integer.valueOf(binaryRepresentation.substring(7, 7 + subPacketLengthLength), 2)

            if (operatorPacket == "0") {
                val limitedSubPacketsByLength = binaryRepresentation.substring(
                    7 + subPacketLengthLength,
                    7 + subPacketLengthLength + subPacketLength
                )

                var remainder = limitedSubPacketsByLength
                val results = listOf<Long>().toMutableList()

                while (remainder.isNotEmpty()) {
                    val solution = solveDeltaRecursively(remainder)

                    results.add(solution.first)
                    remainder = solution.second
                }

                return Pair(
                    computeResult(results, packetId),
                    binaryRepresentation.substring(7 + subPacketLengthLength + subPacketLength)
                )
            } else {
                val subPackets = binaryRepresentation.substring(7 + subPacketLengthLength)

                var count = subPacketLength
                var remainder = subPackets
                val results = listOf<Long>().toMutableList()

                while (count-- > 0) {
                    val solution = solveDeltaRecursively(remainder)

                    results.add(solution.first)
                    remainder = solution.second
                }

                return Pair(computeResult(results, packetId), remainder)
            }
        }
    }

    private fun computeResult(results: List<Long>, packetId: Int): Long {
        return when (packetId) {
            0 -> results.sum()
            1 -> results.reduce { acc, next -> acc * next }
            2 -> results.minOf { it }
            3 -> results.maxOf { it }
            5 -> if (results[0] > results[1]) 1 else 0
            6 -> if (results[0] < results[1]) 1 else 0
            7 -> if (results[0] == results[1]) 1 else 0
            else -> throw RuntimeException("I don't know what to do with packet id $packetId")
        }
    }

    fun solve(encodedTransmission: String): Int {
        val binaryRepresentation = hexToBinary(encodedTransmission)

        return solveRecursively(binaryRepresentation).first
    }

    fun solveDelta(encodedTransmission: String): Long {
        val binaryRepresentation = hexToBinary(encodedTransmission)

        return solveDeltaRecursively(binaryRepresentation).first
    }

    private fun hexToBinary(encodedTransmission: String) = encodedTransmission
        .toCharArray()
        .map { Integer.parseInt(it.toString(), 16) }
        .map { Integer.toBinaryString(it) }
        .joinToString("") { "0000".substring(it.length) + it }


}