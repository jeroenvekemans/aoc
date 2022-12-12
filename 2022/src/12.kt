import java.io.File

data class Location(val row: Int, val col: Int)
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
        if (this.marker == 'S') {
            return otherSquare.marker.code - 'a'.code <= 1
        }

        if (otherSquare.marker == 'E') {
            return 'z'.code - this.marker.code <= 1
        }

        return otherSquare.marker.code - marker.code <= 1
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

    val startSquare = squares.find { it.isStart() }!!
    val destinationSquare = squares.find { it.isDestination() }!!

    val result = travelTowardsDestination(listOf(startSquare), destinationSquare, squares.associateBy { it.location })

    println(result)

    println(result.size)

    println(result.reversed().map { it.marker }.joinToString(""))
}

fun travelTowardsDestination(
    path: List<Square>,
    destinationSquare: Square,
    squaresByLocation: Map<Location, Square>): List<Square> {

    println("iteration with path length ${path.size}")

    if (path.contains(destinationSquare)) {
        return path
    }

    val currentSquare = path.first()

    val nextSquareOptions = currentSquare
        .adjacentLocations()
        .filter { squaresByLocation.contains(it) }
        .map { squaresByLocation[it]!! }
        .filter { currentSquare.allowedToTravelTo(it) }
        .filter { !path.contains(it) }

    if (nextSquareOptions.isEmpty()) {
        return emptyList()
    }

    val recurse = nextSquareOptions
        .map { travelTowardsDestination(listOf(it) + path, destinationSquare, squaresByLocation) }
        .filter { it.isNotEmpty() }

    return if (recurse.isEmpty()) emptyList() else recurse.sortedBy { it.size }.first()
}
