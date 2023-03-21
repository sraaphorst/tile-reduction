typealias Row = List<Int?>
typealias Grid = List<Row>

typealias SolvedRow = List<Int>
typealias SolvedGrid = List<SolvedRow>

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
                val set = run {
                    if (r == rp && c == cp)
                        setOf(s)
                    else if (r == rp || c == cp)
                        tilePossibilitySet - s
                    else if (r / size == rp / size && c / size == cp / size)
                        tilePossibilitySet - s
                    else
                        tilePossibilitySet
                }.toSet()
                set
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
        fun display(solution: SolvedGrid) {
            solution.forEach { row ->
                row.forEach { print("$it ") }
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
        listOf(1, n, n,    3, n, 4,    8, n, 9),
        listOf(n, 2, 4,    n, n, n,    n, n, 7),
        listOf(n, n, n,    9, 6, n,    5, n, n),

        listOf(2, 3, n,    n, n, n,    n, 5, n),
        listOf(5, n, 7,    n, n, 8,    n, n, n),
        listOf(n, n, n,    n, n, n,    4, n, n),

        listOf(n, n, 9,    5, n, n,    n, n, n),
        listOf(3, n, n,    8, n, n,    n, n, n),
        listOf(n, 5, n,    6, n, n,    n, 3, n)
    )

    Sudoku2D(grid).solve()?.also { solved ->
        Sudoku2D.display(solved)
    }
}