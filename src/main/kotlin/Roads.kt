// By Sebastian Raaphorst, 2023.

typealias RoadRow = List<Roads.Companion.Blocks?>
typealias RoadGrid = List<RoadRow>

typealias SolvedRoadRow = List<Roads.Companion.Blocks>
typealias SolvedRoadGrid = List<SolvedRoadRow>

class Roads(override val grid: RoadGrid): GridProblem<Roads.Companion.Blocks, Set<SolvedRoadGrid>>() {
    private val height = grid.size
    private val width = grid.maxOf { it.size }
    override val oneSolution: Boolean = false

    override val tileSet = (0 until height).flatMap { row ->
        (0 until width).map { col ->
            Pair(row, col)
        }
    }.toSet()

    override val tilePossibilitySet = Blocks.values().toSet()

    init {
        if (height < 1)
            throw IllegalArgumentException("Height was $height, must be at least 1.")

        val minWidth = grid.minOf { it.size }
        if (width < 1 || minWidth < width)
            throw IllegalArgumentException("Illegal widths found: must all be consistent and at least 1.")
    }

    override val rules = tileSet.flatMap { t ->
        val (r, c) = t
        tilePossibilitySet.map { s ->
            val possibilities = tileSet.associateWith { tp ->
                val (rp, cp) = tp
                run {
                    if (r == rp && c == cp)
                        setOf(s)
                    else if (rp == r && cp == c + 1) when (s) {
                        Blocks.UP,  Blocks.DOWN, Blocks.RIGHT -> setOf(Blocks.UP, Blocks.DOWN, Blocks.LEFT)
                        Blocks.LEFT, Blocks.EMPTY -> setOf(Blocks.RIGHT, Blocks.EMPTY)
                    } else if (rp == r && cp == c - 1) when (s) {
                        Blocks.UP,  Blocks.DOWN, Blocks.LEFT -> setOf(Blocks.UP, Blocks.DOWN, Blocks.RIGHT)
                        Blocks.RIGHT, Blocks.EMPTY -> setOf(Blocks.LEFT, Blocks.EMPTY)
                    } else if (cp == c && rp == r + 1) when (s) {
                        Blocks.DOWN, Blocks.LEFT, Blocks.RIGHT -> setOf(Blocks.UP, Blocks.LEFT, Blocks.RIGHT)
                        Blocks.UP, Blocks.EMPTY -> setOf(Blocks.DOWN, Blocks.EMPTY)
                    } else if (cp == c && rp == r - 1) when (s) {
                        Blocks.UP, Blocks.LEFT, Blocks.RIGHT -> setOf(Blocks.DOWN, Blocks.LEFT, Blocks.RIGHT)
                        Blocks.DOWN, Blocks.EMPTY -> setOf(Blocks.UP, Blocks.EMPTY)
                    } else
                        tilePossibilitySet
                }
            }
            Pair(t, s) to possibilities
        }
    }.toMap()

    override fun processSolutions(solutions: Set<TileReduction<Pair<Int, Int>, Blocks>>?): Set<SolvedRoadGrid> {
        if (solutions == null)
            return emptySet()
        return solutions.map {solution ->
            (0 until height).map { r ->
                (0 until width).map { c ->
                    solution.possibilities.getValue(Pair(r, c)).first()
                }
            }
        }.toSet()
    }

    companion object {
        enum class Blocks(val representation: List<String>) {
            UP   (listOf("..||..", "======", "......")),
            DOWN (listOf("......", "======", "..||..")),
            RIGHT(listOf("..||..", "..||==", "..||..")),
            LEFT (listOf("..||..", "==||..", "..||..")),
            EMPTY(listOf("......", "......", "......"))
        }

        fun displayAsLetters(solution: SolvedRoadGrid) {
            solution.forEach { row ->
                row.forEach { b ->
                    print(b.name[0])
                }
                println()
            }
        }

        fun display(solution: SolvedRoadGrid) {
            solution.forEach { row ->
                (0 until 3).forEach { idx ->
                    row.forEach { b ->
                        print(b.representation[idx])
                    }
                    println()
                }
            }
        }
    }
}

fun main() {
    val n: Roads.Companion.Blocks? = null
    val u = Roads.Companion.Blocks.UP
    val d = Roads.Companion.Blocks.DOWN
    val r = Roads.Companion.Blocks.RIGHT
    val l = Roads.Companion.Blocks.LEFT
    val e = Roads.Companion.Blocks.EMPTY

    val grid = listOf(
        listOf(n, r, n, n, n),
        listOf(d, u, n, n, l),
        listOf(n, d, n, n, n),
        listOf(e, r, u, l, e)
    )

    val solutions = Roads(grid).solve()
    solutions.forEach {
        Roads.display(it)
        println()
        println()
    }
}
