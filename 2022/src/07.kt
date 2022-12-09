import java.io.File as Faal

sealed class Cmd {
    data class ListCmd(val output: List<String>): Cmd()
    data class ChangeDirCmd(val dir: String): Cmd()
}

data class File(val name: List<String>, val size: Int)

fun main() {
    val terminalOutput: List<String> = Faal("2022/src/07.txt").readLines()

    val commands = terminalOutput
        .mapIndexed { index, value -> Pair(index, value) }
        .filter { it.second.startsWith("$") }
        .map {
            if (it.second == "$ ls") {
                Cmd.ListCmd(terminalOutput.drop(it.first + 1).takeWhile { !it.startsWith("$") })
            } else {
                Cmd.ChangeDirCmd(it.second.drop(5))
            }
        }

    val files  = process(listOf("/"), commands.drop(1))

    println(files.joinToString("\n") { f -> f.name.joinToString(" > ") + " (" + f.size + ")" })

    val folderSizes = mutableMapOf<String, Int>()

    for (file in files) {
        val dirs = file.name.dropLast(1)

        for (i in 1 .. dirs.size) {
            val dir = dirs.take(i).joinToString("")
            val newSize = file.size + if (folderSizes.contains(dir)) folderSizes.get(dir)!! else 0
            folderSizes[dir] = newSize
        }
    }

    println(folderSizes)

    println(folderSizes.entries.filter { it.value <= 100000 }.sumOf { it.value })

    val sizeOfRoot = folderSizes["/"]!!
    println(folderSizes.entries
        .filter { 70000000 - (sizeOfRoot - it.value) >= 30000000 }
        .minBy { it.value })
}

fun process(path: List<String>, commands: List<Cmd>): List<File> {
    if (commands.isEmpty()) {
        return emptyList();
    }

    val cmd = commands.first()
    val remainder = commands.drop(1)

    return when(cmd) {
        is Cmd.ChangeDirCmd -> {
            if (cmd.dir == "..") {
                process(path.dropLast(1), remainder);
            } else {
                process(path + listOf(cmd.dir), remainder)
            }
        }
        is Cmd.ListCmd -> {
            val files = cmd.output.filter { !it.startsWith("dir") }.map { File(path + it.split(" ")[1], Integer.valueOf(it.split(" ")[0])) }

            return files + process(path, remainder)
        }
    }
}
