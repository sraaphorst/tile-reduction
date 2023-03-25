// By Sebastian Raaphorst, 2023.
// Find a single partitioning of P_x

typealias RowPartition2 = List<GridPartitioning2.Companion.Paths?>
typealias GridPartition2 = List<RowPartition2>

typealias SolvedRowPartition2 = List<GridPartitioning2.Companion.Paths>
typealias SolvedGridPartition2 = List<SolvedRowPartition2>

class GridPartitioning2(
    private val rows: Int, private val cols: Int,
    override val grid: GridPartition2 = (0 until rows).map { x -> (0 until cols).map { y ->
        // Fix the corners.
        if (x == 0 && y == 0) Paths.SOUTHEAST
        else if (x == 0 && y == cols - 1) Paths.SOUTHWEST
        else if (x == rows - 1 && y == 0) Paths.NORTHEAST
        else if (x == rows - 1 && y == cols - 1) Paths.NORTHWEST
        else null
    } }):
    GridProblem<GridPartitioning2.Companion.Paths, SolvedGridPartition2?>() {

    override val oneSolution: Boolean = true
    override val stochastic: Boolean = true

    override val tileSet = (0 until rows).flatMap { x ->
        (0 until cols).map { y ->
            Pair(x, y)
        }
    }.toSet()
    override val tilePossibilitySet = Paths.values().toSet()

    override val rules = tileSet.flatMap { t ->
        val (x, y) = t
        tilePossibilitySet.map { s ->
            val possibilities = tileSet.associateWith { tp ->
                val (xp, yp) = tp
                run {
                    if (x == xp && y == yp)
                        setOf(s)

                    // Left border
                    else if (xp == x && yp == y - 1 && yp == 0) when (s) {
                        Paths.HORIZONTAL, Paths.NORTHWEST, Paths.SOUTHWEST ->
                            setOf(Paths.NORTHEAST, Paths.SOUTHEAST)
                        else -> setOf(Paths.VERTICAL)
                    }

                    // Right border
                    else if (xp == x && yp == y + 1 && yp == cols - 1) when (s) {
                        Paths.HORIZONTAL, Paths.NORTHEAST, Paths.SOUTHEAST ->
                            setOf(Paths.NORTHWEST, Paths.SOUTHWEST)
                        else -> setOf(Paths.VERTICAL)
                    }

                    // Top border
                    else if (xp == x - 1 && yp == y && xp == 0) when (s) {
                        Paths.VERTICAL, Paths.NORTHWEST, Paths.NORTHEAST ->
                            setOf(Paths.SOUTHWEST, Paths.SOUTHEAST)
                        else -> setOf(Paths.HORIZONTAL)
                    }

                    // Bottom border
                    else if (xp == x + 1 && yp == y && xp == rows - 1) when (s) {
                        Paths.VERTICAL, Paths.SOUTHWEST, Paths.SOUTHEAST ->
                            setOf(Paths.NORTHWEST, Paths.NORTHEAST)
                        else -> setOf(Paths.HORIZONTAL)
                    }

                    else if (xp == x && yp == y + 1) when (s) {
                        Paths.HORIZONTAL, Paths.NORTHEAST, Paths.SOUTHEAST ->
                            setOf(Paths.HORIZONTAL, Paths.NORTHWEST, Paths.SOUTHWEST)
                        Paths.VERTICAL, Paths.NORTHWEST, Paths.SOUTHWEST ->
                            setOf(Paths.VERTICAL, Paths.NORTHEAST, Paths.SOUTHEAST)
                    }

                    else if (xp == x && yp == y - 1) when (s) {
                        Paths.HORIZONTAL, Paths.NORTHWEST, Paths.SOUTHWEST ->
                            setOf(Paths.HORIZONTAL, Paths.NORTHEAST, Paths.SOUTHEAST)
                        Paths.VERTICAL, Paths.NORTHEAST, Paths.SOUTHEAST ->
                            setOf(Paths.VERTICAL, Paths.NORTHWEST, Paths.SOUTHWEST)
                    }

                    else if (xp == x - 1 && yp == y) when (s) {
                        Paths.HORIZONTAL, Paths.SOUTHWEST, Paths.SOUTHEAST ->
                            setOf(Paths.HORIZONTAL, Paths.NORTHWEST, Paths.NORTHEAST)
                        Paths.VERTICAL, Paths.NORTHWEST, Paths.NORTHEAST ->
                            setOf(Paths.VERTICAL, Paths.SOUTHWEST, Paths.SOUTHEAST)
                    }

                    else if (xp == x + 1 && yp == y) when (s) {
                        Paths.HORIZONTAL, Paths.NORTHWEST, Paths.NORTHEAST ->
                            setOf(Paths.HORIZONTAL, Paths.SOUTHWEST, Paths.SOUTHEAST)
                        Paths.VERTICAL, Paths.SOUTHWEST, Paths.SOUTHEAST ->
                            setOf(Paths.VERTICAL, Paths.NORTHWEST, Paths.NORTHEAST)
                    }

                    else
                        tilePossibilitySet
                }
            }
            Pair(t, s) to possibilities
        }
    }.toMap()

    override fun processSolutions(solutions: Set<TileReduction<Pair<Int, Int>, Paths>>?): SolvedGridPartition2? {
        val solution = solutions?.firstOrNull() ?: return null
        return (0 until rows).map { x ->
            (0 until cols).map { y ->
                solution.possibilities.getValue(Pair(x, y)).first()
            }
        }
    }

    companion object {
        enum class Paths(val representation: Char) {
            VERTICAL('║'),
            HORIZONTAL('═'),
            NORTHEAST('╚'),
            SOUTHWEST('╗'),
            NORTHWEST('╝'),
            SOUTHEAST('╔')
        }

        fun display(solution: SolvedGridPartition2) {
            solution.forEach { row ->
                row.forEach { value ->
                    print(value.representation)
                }
                println()
            }
        }

    }
}

fun main() {
    GridPartitioning2(8, 8).solve()?.also { solution ->
        GridPartitioning2.display(solution)
    }
}
