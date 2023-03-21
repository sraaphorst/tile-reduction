typealias Row = List<Int?>
typealias Grid = List<Row>

typealias SolvedRow = List<Int>
typealias SolvedGrid = List<SolvedRow>

class Sudoku2D(private val grid: Grid, private val size: Int = 3) {
    private val size2 = size * size

    private val tileSet = (0 until size2).flatMap {
            row -> (0 until size2).map {
            col -> Pair(row, col)
    }
    }.toSet()

    private val tilePossibilitySet = (1..size2).toSet()

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

    private val rules = tileSet.flatMap { t ->
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

//    private fun validateSolvedGrid(solvedGrid: SolvedGrid): Boolean =

    fun solve(): SolvedGrid? {
        val tileReduction = TileReduction(
            rules,
            tileSet,
            tilePossibilitySet
        )

        // Fix the elements that are non-null.
        val fixedTileReduction = grid.withIndex().fold(tileReduction) { acc, rowInfo ->
            val (rowIdx, row) = rowInfo
            row.withIndex().fold(acc) { acc2, colInfo ->
                val (colIdx, entry) = colInfo
                if (entry != null)
                    acc2.fix(Pair(rowIdx, colIdx), entry)
                else
                    acc2
            }
        }

        // Solve via backtracking.
        val solution = fixedTileReduction.backtrack(true)?.firstOrNull() ?: return null

        // Convert to a SolvedGrid.
        return (0 until size2).map { r ->
            (0 until size2).map { c ->
                solution.possibilities.getValue(Pair(r, c)).first()
            }
        }
    }
}

fun main() {
    /**
     * Extremely difficult Sudoku puzzle:
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
    val n: Int? = null

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

    val solvedGrid = Sudoku2D(grid).solve() ?: return
    solvedGrid.forEach { row ->
        row.forEach{ print("$it ")}
        println()
    }

    val grid2 = listOf(
        listOf(1, 2, 3, n),
        listOf(n, n, n, n),
        listOf(n, n, n, n),
        listOf(n, n, n, n)
    )

    println()
    val solved2 = Sudoku2D(grid2, size=2).solve() ?: return
    solved2.forEach { row ->
        row.forEach{ print("$it ")}
        println()
    }
}