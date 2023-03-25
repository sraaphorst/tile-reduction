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

fun main() {
    GridPartitioningSearch(8, 8).solve()?.also { solution ->
        GridPartitioning.display(solution)
    }
}
