import java.io.File

data class Location(val row: Int, val col: Int) {}

data class Square(val marker: Char, val location: Location) {
    fun isStart(): Boolean {
        return marker == 'S'
    }

    fun isDestination(): Boolean {
        return marker == 'E'
    }

    fun adjacentLocations(): List<Location> {
        return listOf(
            this.location.copy(row = this.location.row + 1),
            this.location.copy(row = this.location.row - 1),
            this.location.copy(col = this.location.col + 1),
            this.location.copy(col = this.location.col - 1)
        )
    }

    fun allowedToTravelTo(otherSquare: Square): Boolean {
        return markerWeight(marker) - markerWeight(otherSquare.marker) <= 1
    }

    private fun markerWeight(marker: Char): Int {
        return when(marker) {
            'S' -> 'a'.code
            'E' -> 'z'.code
            else -> marker.code
        }
    }
}

data class Path(val squares: List<Square>) {
    fun head(): Square {
        return squares[0]
    }

    fun contains(square: Square): Boolean {
        return squares.contains(square)
    }

    fun extend(square: Square): Path {
        return this.copy(squares = listOf(square) + this.squares)
    }
}

fun main() {
    val squares: List<Square> = File("2022/src/12.txt")
        .readLines()
        .flatMapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, marker ->
                Square(marker, Location(rowIndex, colIndex))
            }
        }

    val startSquare = squares.find { it.isDestination() }!!

    val result = travel(mapOf(Pair(startSquare, Path(listOf(startSquare)))), squares.associateBy { it.location })

    println(result)
    println(result.squares.size - 1)
}

tailrec fun travel(paths: Map<Square, Path>, grid: Map<Location, Square>): Path {
    if (paths.keys.any { p -> p.marker == 'a' || p.isStart() }) {
        return paths[paths.keys.first { p -> p.marker == 'a' || p.isStart() }]!!
    }

    val newPaths = paths.values.flatMap { p ->
        p.head().adjacentLocations()
            .filter { grid.contains(it) }
            .map { grid[it]!! }
            .filter { p.head().allowedToTravelTo(it) }
            .map { p.extend(it) }
    }.associateBy { it.head() }

    return travel(newPaths, grid)
}
