fun main() {
    val fish = "1,1,1,1,1,5,1,1,1,5,1,1,3,1,5,1,4,1,5,1,2,5,1,1,1,1,3,1,4,5,1,1,2,1,1,1,2,4,3,2,1,1,2,1,5,4,4,1,4,1,1,1,4,1,3,1,1,1,2,1,1,1,1,1,1,1,5,4,4,2,4,5,2,1,5,3,1,3,3,1,1,5,4,1,1,3,5,1,1,1,4,4,2,4,1,1,4,1,1,2,1,1,1,2,1,5,2,5,1,1,1,4,1,2,1,1,1,2,2,1,3,1,4,4,1,1,3,1,4,1,1,1,2,5,5,1,4,1,4,4,1,4,1,2,4,1,1,4,1,3,4,4,1,1,5,3,1,1,5,1,3,4,2,1,3,1,3,1,1,1,1,1,1,1,1,1,4,5,1,1,1,1,3,1,1,5,1,1,4,1,1,3,1,1,5,2,1,4,4,1,4,1,2,1,1,1,1,2,1,4,1,1,2,5,1,4,4,1,1,1,4,1,1,1,5,3,1,4,1,4,1,1,3,5,3,5,5,5,1,5,1,1,1,1,1,1,1,1,2,3,3,3,3,4,2,1,1,4,5,3,1,1,5,5,1,1,2,1,4,1,3,5,1,1,1,5,2,2,1,4,2,1,1,4,1,3,1,1,1,3,1,5,1,5,1,1,4,1,2,1".split(",").map { Integer.valueOf(it) }
    println(DaySix.solve(fish, 80))
    println(DaySixDelta.solve(fish, 256))
}

object DaySix {
    fun solve(fish: List<Int>, daysLeft: Int): Int {
        return evolve(daysLeft, fish)
    }

    private fun evolve(daysLeft: Int, fish: List<Int>): Int {
        if (daysLeft == 0) {
            return fish.count()
        }

        val updatedFish = fish.map { f -> if (f == 0) 6 else f - 1 }

        val newFish = fish.filter { f -> f == 0 }.map { 8 }

        return evolve(daysLeft - 1, updatedFish + newFish)
    }
}

object DaySixDelta {
    fun solve(fish: List<Int>, daysLeft: Int): Long {
        val fishPerDaysLeft = (0..8).map { days -> fish.count { f -> f == days } }.map { it.toLong() }

        return evolve(daysLeft, fishPerDaysLeft)
    }

    private fun evolve(daysLeft: Int, fishPerDaysLeft: List<Long>): Long {
        if (daysLeft == 0) {
            return fishPerDaysLeft.reduce { one, two -> one.plus(two) }
        }

        val updated = fishPerDaysLeft.subList(1, 7) + listOf(fishPerDaysLeft[0].plus(fishPerDaysLeft[7]), fishPerDaysLeft[8], fishPerDaysLeft[0])

        return evolve(daysLeft - 1, updated)
    }
}