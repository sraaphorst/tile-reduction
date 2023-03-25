// By Sebastian Raaphorst, 2023.

class GridPartitioningSearch(
    private val rows: Int,
    private val cols: Int,
    override val grid: GridPartition = defaultSeed(rows, cols))
    : GridPartitioning<SolvedGridPartition?>(rows, cols) {
    override val oneSolution: Boolean = true
    override val stochastic: Boolean = true

    override fun processSolutions(solutions: Set<TileReduction<Pair<Int, Int>, Companion.Paths>>?): SolvedGridPartition? {
        val solution = solutions?.firstOrNull() ?: return null
        return (0 until rows).map { x ->
            (0 until cols).map { y ->
                solution.possibilities.getValue(Pair(x, y)).first()
            }
        }
    }
}

fun seedWithBorder(rows: Int, cols: Int): GridPartition =
    (0 until rows).map { x ->
        (0 until cols).map { y ->
            // Fix the corners.
            if (x == 0 && y == 0) GridPartitioning.Companion.Paths.SOUTHEAST
            else if (x == 0 && y == cols - 1) GridPartitioning.Companion.Paths.SOUTHWEST
            else if (x == rows - 1 && y == 0) GridPartitioning.Companion.Paths.NORTHEAST
            else if (x == rows - 1 && y == cols - 1) GridPartitioning.Companion.Paths.NORTHWEST
            else if (x == 0 || x == rows - 1) GridPartitioning.Companion.Paths.HORIZONTAL
            else if (y == 0 || y == cols - 1) GridPartitioning.Companion.Paths.VERTICAL
            else null
        }
    }

fun main() {
    println("7 x 7 grid partition using an 8 x 8 grid tiling with border seed:")
    GridPartitioningSearch(8, 8, seedWithBorder(8, 8)).solve()?.also { solution ->
        GridPartitioning.display(solution)
    }
    println()

    println("7 x 7 grid partition using an 8 x 8 grid tiling with default corner seed:")
    GridPartitioningSearch(8, 8).solve()?.also { solution ->
        GridPartitioning.display(solution)
    }
}
