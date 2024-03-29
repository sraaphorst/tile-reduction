import kotlin.streams.asStream

// By Sebastian Raaphorst, 2023.

// Note that we call this a "Tile Reduction" problem, but any graph would do, where the nodes would be considered
// the "tiles."

// We need:
// A tile placement identifier type, T.
// A type of tile, S.
// A map of rules r = Map<(T, S), Map<T, Set<S>>> meaning if t is set to s, then for any other tile t', its current
//    tile possibility set is intersected with r((s, t))(t') to get the new tile possibility set.
typealias Assignment<T, S> = Pair<T, S>
typealias Possibilities<T, S> = Map<T, Set<S>>
typealias Rules<T, S> = Map<Assignment<T, S>, Possibilities<T, S>>

// Keep track of which cells are fixed so when picking a cell of lowest entropy, we do not pick a fixed cell.
typealias Fixed<T> = Map<T, Boolean>

data class TileReduction<T, S>(
    val rules: Rules<T, S>,
    val tileSet: Set<T>,
    val tilePossibilitySet: Set<S>,
    val possibilities: Possibilities<T, S> = tileSet.associateWith { tilePossibilitySet },
    val fixed: Fixed<T> = tileSet.associateWith { false }
) {
    /**
     * Calculate the average entropy of the TileReduction.
     * If it is null, then we are in an invalid state where a tile exists with no possibilities.
     * If it is 1.0, then we have a full assignment of tiles to values.
     */
    val averageEntropy: Double? by lazy {
        val tilePossibilities = tileSet.asSequence().map { t -> possibilities[t]?.size ?: 0 }
        if (0 in tilePossibilities)
            null
        else tilePossibilities.sum().toDouble() / tileSet.size
    }

    /**
     * Given an assignment of a tile to a value, calculate the new TileReduction.
     */
    fun fix(assignment: Assignment<T, S>): TileReduction<T, S> {
        val rule = rules.getValue(assignment)
        val newPossibilities = possibilities.map { (t, tPossibilities) ->
            t to tPossibilities.intersect(rule.getValue(t))
        }.toMap()
        val newFixed = fixed.map { (t, b) -> t to (t == assignment.first || b) }.toMap()
        return TileReduction(rules, tileSet, tilePossibilitySet, newPossibilities, newFixed)
    }

    /**
     * Given an assignment of tile t to value s, calculate the new TileReduction.
     */
    fun fix(t: T, s: S): TileReduction<T, S> =
        fix(Assignment(t, s))

    /**
     * Determine if this TileReduction is complete, i.e. has entropy 1, meaning that
     * every tile has exactly one value associated with it.
     */
    val complete: Boolean by lazy {
        possibilities.asSequence().all { it.value.size == 1 }
    }

    /**
     * Try to solve by picking a tile of lowest entropy and selecting a value.
     * If first is true, we return the first TileReduction that satisfies if there is one.
     * If first is false, we return the full set of TileReduction that satisfies if there are any.
     * If there are no answers, null is returned.
     */
    fun backtrack(first: Boolean, stochastic: Boolean = false): Set<TileReduction<T, S>>? {
        /**
         * The parameters should be:
         * 1. the tileIdx we are currently processing
         * 2. the possibilities of the tiles for this tile
         * 3. the last tried index of the possibilities for this tile (or null)
         * 4. the current board (before tileIdx was modified)
         * 5. whether we are backtracking or not
         */
        fun aux(currentBoard: TileReduction<T, S> = this): Set<TileReduction<T, S>>? {
            // Find the list of empty tiles.
            val emptyTiles = currentBoard.fixed.filterValues { !it }.keys

            // If the lowest entropy of the puzzle is 0, we have made a mistake somewhere.
            if (currentBoard.possibilities.any { it.value.isEmpty() })
                return null

            // If there are no empty tiles, then we have a complete board.
            if (emptyTiles.isEmpty())
                return setOf(currentBoard)

            // Pick the first unfixed tile of the lowest entropy to try next.
            val lowestEntropy = emptyTiles.minOf { t -> currentBoard.possibilities.getValue(t).size }

            val lowestEntropyPossibilities = if (stochastic)
                currentBoard.possibilities.asSequence()
                    .filter { (t, p) -> t in emptyTiles && p.size == lowestEntropy }
                    .shuffled()
                    .first()
            else
                currentBoard.possibilities.asSequence()
                    .filter { (t, p) -> t in emptyTiles && p.size == lowestEntropy }
                    .first()

            val t = lowestEntropyPossibilities.key
            val ps = if (stochastic)
                currentBoard.possibilities.getValue(t).shuffled()
            else
                currentBoard.possibilities.getValue(t)

            return if (first)
                ps.asSequence()
                    .mapNotNull { s ->
                        aux(currentBoard.fix(t, s))
                    }.firstOrNull()
//                ps.asSequence()
//                    .asStream().map { s ->
//                        aux(currentBoard.fix(t, s))
//                    }.filter { it != null }.findAny().orElseGet(null)
            else
                ps.fold(emptySet()) { set, s ->
                    val newBoard = currentBoard.fix(t, s)
                    set + (aux(newBoard) ?: emptySet())
                }
        }

        return aux()
    }
}
