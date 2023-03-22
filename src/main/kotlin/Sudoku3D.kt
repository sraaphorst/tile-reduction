// By Sebastian Raaphorst, 2023.

class Sudoku3D(private val cube: Cube, private val size: Int = 2) {
    private val size2 = size * size
    private val size3 = size2 * size

    private val tileSet = (0 until size3).flatMap { x ->
        (0 until size3).flatMap { y ->
            (0 until size3).map { z ->
                Triple(x, y, z)
            }
        }
    }.toSet()

    private val tilePossibilitySet = (1..size3).toSet()

    init {
        // Error handling of incorrect input.
        // This does not validate the grid.
        if (cube.size != size3)
            throw IllegalArgumentException("Cube has wrong length: should be $size3")

        val illegalPlane = cube.firstOrNull { grid -> grid.size != size3 }
        if (illegalPlane != null)
            throw IllegalArgumentException("A grid has wrong length: should be $size3")

        val illegalRow = cube.firstOrNull { grid ->
            grid.firstOrNull { row -> row.size != size3 } != null
        }
        if (illegalRow != null)
            throw IllegalArgumentException("A row has wrong length: should be $size3")
    }

    private val rules = tileSet.flatMap { t ->
        val (x, y, z) = t
        tilePossibilitySet.map { s ->
            val possibilities = tileSet.associateWith { tp ->
                val (xp, yp, zp) = tp
                run {
                    if (x == xp && y == yp && z == zp)
                        setOf(s)
                    else if ((x == xp && y == yp) ||
                        (x == xp && z == zp) ||
                        (y == yp && z == zp))
                        tilePossibilitySet - s
                    else if (x / size == xp / size && y / size == yp / size && z / size == zp / size)
                        tilePossibilitySet - s
                    else
                        tilePossibilitySet
                }.toSet()
            }
            Pair(t, s) to possibilities
        }
    }.toMap()

    fun solve(): SolvedCube? {
        val tileReduction = TileReduction(
            rules,
            tileSet,
            tilePossibilitySet
        )

        // Fix the elements that are non-null.
        val fixedTileReduction = cube.withIndex().fold(tileReduction) { acc, gridInfo ->
            val (x, grid) = gridInfo
            grid.withIndex().fold(acc) { acc2, rowInfo ->
                val (y, row) = rowInfo
                row.withIndex().fold(acc2) { acc3, colInfo ->
                    val (z, entry) = colInfo
                    if (entry != null)
                        acc3.fix(Triple(x, y, z), entry)
                    else
                        acc3
                }
            }
        }

        // Solve via backtracking.
        val solutions = fixedTileReduction.backtrack(true)
        return processSolutions(solutions)
    }

    private fun processSolutions(solutions: Set<TileReduction<Triple<Int, Int, Int>, Int>>?): SolvedCube? {
        val solution = solutions?.firstOrNull() ?: return null
        return (0 until size3).map { x ->
            (0 until size3).map { y ->
                (0 until size3).map { z ->
                    solution.possibilities.getValue(Triple(x, y, z)).first()
                }
            }
        }
    }

    companion object {
        fun display(solution: SolvedCube, translate: (Int) -> String = { it.toString() }) {
            solution.forEach { grid ->
                grid.forEach { row ->
                    row.forEach { print("${translate(it)} ") }
                    println()
                }
                println()
            }
        }
    }
}

fun main() {
    val n: Int? = null

    // From http://forum.enjoysudoku.com/3d-sudoku-t35836.html
    val cube1 = listOf(
        listOf(
            listOf(n, n, n, n, 4, 2, n, n),
            listOf(n, n, 8, n, n, n, n, n),
            listOf(6, n, n, n, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, 8, n, n, n),
            listOf(n, 1, n, 2, n, n, n, n),
            listOf(n, n, n, n, n, 8, n, n),
            listOf(n, n, n, n, 5, n, n, n),
        ),
        listOf(
            listOf(n, 2, n, 1, n, n, n, n),
            listOf(3, n, n, n, n, n, n, 6),
            listOf(n, n, n, n, n, n, 5, n),
            listOf(n, n, n, n, n, 7, n, n),
            listOf(n, n, n, n, n, n, 3, n),
            listOf(n, n, n, 6, n, n, n, n),
            listOf(n, n, 5, n, n, n, n, 3),
            listOf(n, n, n, 8, n, n, 2, n)
        ),
        listOf(
            listOf(7, 5, n, 6, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, 8, 1, 3, n, n),
            listOf(n, n, n, n, 2, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(4, n, n, n, n, n, n, 5),
            listOf(n, n, n, n, n, n, n, n),
            listOf(2, n, n, 3, n, 8, n, n)
        ),
        listOf(
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, 8, n, n, n),
            listOf(n, n, 2, n, n, 7, n, n),
            listOf(n, 4, 1, n, n, n, n, n),
            listOf(n, n, n, n, n, n, 4, n),
            listOf(n, n, 7, n, 4, 2, 3, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n)
        ),
        listOf(
            listOf(n, n, 5, n, n, n, n, 3),
            listOf(5, n, 6, n, n, n, n, n),
            listOf(n, n, n, n, n, n, n, 1),
            listOf(n, n, n, n, 3, n, 4, n),
            listOf(2, n, 1, n, 6, n, n, 7),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, 8, n, n, n),
            listOf(n, n, 4, 2, n, n, 8, n)
        ),
        listOf(
            listOf(n, n, n, n, n, 8, n, n),
            listOf(n, 3, n, 4, n, n, n, n),
            listOf(n, n, 3, n, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, 8, n, n, n, n, n, n),
            listOf(5, n, n, n, n, n, n, n),
            listOf(n, n, n, n, n, 2, 3, n),
            listOf(n, n, n, n, n, 1, n, n),
        ),
        listOf(
            listOf(n, n, n, n, 1, n, n, n),
            listOf(n, 8, n, 7, n, n, n, n),
            listOf(n, n, n, n, n, n, n, 2),
            listOf(8, 6, n, n, n, n, 3, n),
            listOf(1, n, 2, 4, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, 7, n, 8, n),
            listOf(n, n, n, n, n, n, n, 5)
        ),
        listOf(
            listOf(n, n, n, n, n, n, n, 8),
            listOf(2, n, n, n, 6, n, n, n),
            listOf(n, 1, n, n, n, 5, n, n),
            listOf(n, n, n, n, n, n, 7, n),
            listOf(5, n, n, n, n, 3, n, n),
            listOf(n, n, n, n, n, n, 1, n),
            listOf(n, n, 8, n, n, n, n, n),
            listOf(n, 6, n, n, n, n, n, n)
        )
    )

    Sudoku3D(cube1, size = 2).solve()?.also { solution ->
        Sudoku3D.display(solution)
    }

    println("----------------\n")

    val cube2 = listOf(
        listOf(
            listOf(n, n, n, n, 5, 6, n, n),
            listOf(n, n, 5, n, n, n, n, n),
            listOf(5, n, n, n, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, 6, n, n, n),
            listOf(n, 3, n, 1, n, n, n, n),
            listOf(n, n, n, n, n, 1, n, n),
            listOf(n, n, n, n, 4, n, n, n),
        ),
        listOf(
            listOf(5, 6, n, 8, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, 4, 5, 6, n, n),
            listOf(n, n, n, n, 7, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(8, n, n, n, n, n, n, 1),
            listOf(n, n, n, n, n, n, n, n),
            listOf(4, n, n, 1, n, 7, n, n)
        ),
        listOf(
            listOf(n, n, 4, n, n, n, n, 1),
            listOf(4, n, 6, n, n, n, n, n),
            listOf(n, n, n, n, n, n, n, 5),
            listOf(n, n, n, n, 4, n, 6, n),
            listOf(3, n, 1, n, 7, n, n, 4),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, 3, n, n, n),
            listOf(n, n, 7, 6, n, n, 3, n)
        ),
        listOf(
            listOf(n, n, n, n, 2, n, n, n),
            listOf(n, 1, n, 3, n, n, n, n),
            listOf(n, n, n, n, n, n, n, 1),
            listOf(4, 5, n, n, n, n, 2, n),
            listOf(7, n, 5, 4, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, 7, n, 5, n),
            listOf(n, n, n, n, n, n, n, 6)
        ),
        listOf(
            listOf(n, 4, n, 6, n, n, n, n),
            listOf(5, n, n, n, n, n, n, 4),
            listOf(n, n, n, n, n, n, 5, n),
            listOf(n, n, n, n, n, 6, n, n),
            listOf(n, n, n, n, n, n, 6, n),
            listOf(n, n, n, 3, n, n, n, n),
            listOf(n, n, 6, n, n, n, n, 1),
            listOf(n, n, n, 7, n, n, 4, n)
        ),
        listOf(
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, 5, n, n, n),
            listOf(n, n, 5, n, n, 8, n, n),
            listOf(n, 6, 7, n, n, n, n, n),
            listOf(n, n, n, n, n, n, 2, n),
            listOf(n, n, 8, n, 6, 5, 4, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n)
        ),
        listOf(
            listOf(n, n, n, n, n, 1, n, n),
            listOf(n, 7, n, 1, n, n, n, n),
            listOf(n, n, 2, n, n, n, n, n),
            listOf(n, n, n, n, n, n, n, n),
            listOf(n, 4, n, n, n, n, n, n),
            listOf(7, n, n, n, n, n, n, n),
            listOf(n, n, n, n, n, 4, 3, n),
            listOf(n, n, n, n, n, 6, n, n),
        ),
        listOf(
            listOf(n, n, n, n, n, n, n, 7),
            listOf(2, n, n, n, 6, n, n, n),
            listOf(n, 5, n, n, n, 1, n, n),
            listOf(n, n, n, n, n, n, 4, n),
            listOf(1, n, n, n, n, 4, n, n),
            listOf(n, n, n, n, n, n, 5, n),
            listOf(n, n, 3, n, n, n, n, n),
            listOf(n, 6, n, n, n, n, n, n)
        )
    )

    Sudoku3D(cube2, size = 2).solve()?.also { solution ->
        Sudoku3D.display(solution)
    }
}
