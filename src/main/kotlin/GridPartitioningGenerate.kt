// By Sebastian Raaphorst, 2023.

class GridPartitioningGenerate(
    private val rows: Int,
    private val cols: Int,
    override val grid: GridPartition = defaultSeed(rows, cols))
    : GridPartitioning<Set<SolvedGridPartition>>(rows, cols) {
    private val processor = GenerationProcessing<Companion.Paths>(rows, cols)
    override val oneSolution: Boolean = false
    override val stochastic: Boolean = false

    override fun processSolutions(solutions: Set<TileReduction<Pair<Int, Int>, Companion.Paths>>?): Set<SolvedGridPartition> =
        processor.processSolutions(solutions)
}

fun main() {
    GridPartitioningGenerate(5, 4).solve()
        .also { println("${it.size} solutions:\n") }
        .forEach { solution ->
            GridPartitioning.display(solution)
            println()
        }
}