// By Sebastian Raaphorst, 2023.

class GenerationProcessing<S>(private val rows: Int, private val cols: Int) {
    fun processSolutions(solutions: Set<TileReduction<Pair<Int, Int>, S>>?): Set<List<List<S>>> {
        if (solutions == null)
            return emptySet()
        return solutions.map { solution ->
            (0 until rows).map { x ->
                (0 until cols).map { y ->
                    solution.possibilities.getValue(Pair(x, y)).first()
                }
            }
        }.toSet()
    }
}