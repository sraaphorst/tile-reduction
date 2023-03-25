// By Sebastian Raaphorst, 2023.

abstract class GridProblem<S, R> {
    protected abstract val grid: List<List<S?>>
    protected abstract val rules: Rules<Pair<Int, Int>, S>
    protected abstract val tileSet: Set<Pair<Int, Int>>
    protected abstract val tilePossibilitySet: Set<S>
    protected abstract val oneSolution: Boolean
    protected abstract val stochastic: Boolean

    protected abstract fun processSolutions(solutions: Set<TileReduction<Pair<Int, Int>, S>>?): R

    fun solve(): R {
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
        val solutions = fixedTileReduction.backtrack(oneSolution, stochastic)
        return processSolutions(solutions)
    }
}
