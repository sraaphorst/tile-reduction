// By Sebastian Raaphorst, 2023.

class Sudoku2D(override val grid: Grid, private val size: Int = 3): GridProblem<Int, SolvedGrid?>() {
    private val size2 = size * size
    override val oneSolution: Boolean = true

    override val tileSet = (0 until size2).flatMap { row ->
        (0 until size2).map { col ->
            Pair(row, col)
        }
    }.toSet()

    override val tilePossibilitySet = (1..size2).toSet()

    init {
        // Error handling of incorrect input.
        // This does not validate the board.
        if (grid.size != size2)
            throw IllegalArgumentException("Grid has wrong length: expected=$size2, actual=${grid.size}")

        val illegalRow = grid.withIndex().asSequence()
            .filter { (_, row) -> row.size != size2 }
            .map { it.index }
            .firstOrNull()
        if (illegalRow != null)
            throw IllegalArgumentException("Row $illegalRow has wrong length: expected=$size2 " +
                "actual=${grid[illegalRow].size}")

        val illegalRowEntry = grid.flatten().asSequence()
            .filterNot { it == null || it in tilePossibilitySet }
            .firstOrNull()
        if (illegalRowEntry != null)
            throw IllegalArgumentException("Illegal entry in grid: $illegalRowEntry")
    }

    override val rules = tileSet.flatMap { t ->
        val (r, c) = t
        tilePossibilitySet.map { s ->
            val possibilities = tileSet.associateWith { tp ->
                val (rp, cp) = tp
                run {
                    if (r == rp && c == cp)
                        setOf(s)
                    else if (r == rp || c == cp)
                        tilePossibilitySet - s
                    else if (r / size == rp / size && c / size == cp / size)
                        tilePossibilitySet - s
                    else
                        tilePossibilitySet
                }.toSet()
            }
            Pair(t, s) to possibilities
        }
    }.toMap()

    override fun processSolutions(solutions: Set<TileReduction<Pair<Int, Int>, Int>>?): SolvedGrid? {
        val solution = solutions?.firstOrNull() ?: return null
        return (0 until size2).map { r ->
            (0 until size2).map { c ->
                solution.possibilities.getValue(Pair(r, c)).first()
            }
        }
    }

    companion object {
        fun display(solution: SolvedGrid, translate: (Int) -> String = { it.toString() }) {
            solution.forEach { row ->
                row.forEach { print("${translate(it)} ") }
                println()
            }
        }
    }
}

fun main() {
    val n: Int? = null

    /**
     * Example mini-Sudoku:
     * +-----+-----+
     * | 1 2 | 3   |
     * |     |     |
     * +-----+-----+
     * |     |     |
     * |     |     |
     * +-----+-----+
     */
    val smallGrid = listOf(
        listOf(1, 2, 3, n),
        listOf(n, n, n, n),
        listOf(n, n, n, n),
        listOf(n, n, n, n)
    )

    Sudoku2D(smallGrid, size = 2).solve()?.also { solution ->
        Sudoku2D.display(solution)
        println()
    }

    /**
     * Example Sudoku puzzle:
     * +-------+-------+-------+
     * | 1     | 3   4 | 8   9 |
     * |   2 4 |       |     7 |
     * |       | 9 6   | 5     |
     * +-------+-------+-------+
     * | 2 3   |       |   5   |
     * | 5   7 |     8 |       |
     * |       |       | 4     |
     * +-------+-------+-------+
     * |     9 | 5     |       |
     * | 3     | 8     |       |
     * |   5   | 6     |   3   |
     * +-------+-------+-------+
     */
    val grid = listOf(
        listOf(1, n, n, 3, n, 4, 8, n, 9),
        listOf(n, 2, 4, n, n, n, n, n, 7),
        listOf(n, n, n, 9, 6, n, 5, n, n),

        listOf(2, 3, n, n, n, n, n, 5, n),
        listOf(5, n, 7, n, n, 8, n, n, n),
        listOf(n, n, n, n, n, n, 4, n, n),

        listOf(n, n, 9, 5, n, n, n, n, n),
        listOf(3, n, n, 8, n, n, n, n, n),
        listOf(n, 5, n, 6, n, n, n, 3, n)
    )

    Sudoku2D(grid).solve()?.also { solved ->
        Sudoku2D.display(solved)
    }

    val a = 0xa
    val b = 0xb
    val c = 0xc
    val d = 0xd
    val e = 0xe
    val f = 0xf
    val g = 0x10

    val big = listOf(
        listOf(n, a, 3, 4, 7, b, n, n, 1, 9, e, n, n, 8, n, n),
        listOf(c, 6, e, 7, n, n, n, 1, n, 5, b, 2, n, 9, n, n),
        listOf(n, n, b, n, n, 6, n, 8, n, n, 4, d, 5, n, n, f),
        listOf(1, n, n, n, 9, n, g, n, c, n, f, 3, 4, e, n, n),

        listOf(n, n, g, n, f, n, n, 5, n, n, a, 7, n, b, 8, n),
        listOf(6, 7, a, n, g, n, n, n, n, n, n, c, n, n, 5, 9),
        listOf(5, n, n, n, n, n, n, 9, b, 3, n, n, n, g, n, e),
        listOf(e, 3, 2, 8, d, n, a, b, 5, 1, n, 9, n, n, f, n),

        listOf(2, n, n, n, 8, n, c, n, 4, n, n, f, n, 3, 6, n),
        listOf(3, b, n, 1, n, n, n, 7, n, n, n, n, 9, f, 4, 2),
        listOf(n, n, 7, n, b, 4, n, f, n, 2, 5, 6, n, n, n, g),
        listOf(9, n, 4, f, n, 2, 3, n, 7, d, n, n, n, n, b, n),

        listOf(8, 2, 1, g, c, n, 7, e, d, 4, 3, b, n, n, a, 6),
        listOf(n, e, n, n, 5, 8, n, n, n, n, n, a, n, 1, n, 3),
        listOf(7, f, 5, n, a, g, b, n, e, n, 2, n, n, n, n, n),
        listOf(4, d, n, a, 1, n, 2, n, n, n, n, 5, b, 7, n, c)
    )
    println()
    Sudoku2D(big, size = 4).solve()?.also { solution ->
        Sudoku2D.display(solution, translate = { n ->
            when {
                n < 0xa -> n.toString()
                n == 0xa -> "A"
                n == 0xb -> "B"
                n == 0xc -> "C"
                n == 0xd -> "D"
                n == 0xe -> "E"
                n == 0xf -> "F"
                n == 0x10 -> "G"
                else -> "X"
            }
        })
    }
}
