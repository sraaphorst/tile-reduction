//// We need:
//// A tile placement identifier type, T.
//// A type of tile, S.
//// A map of rules r = Map<(T, S), Map<T, Set<S>>> meaning if t is set to s, then for any other tile t', its current
////    tile possibility set is intersected with r((s, t))(t') to get the new tile possibility set.
//typealias Assignment<T, S> = Pair<T, S>
//typealias Possibilities<T, S> = Map<T, Set<S>>
//typealias Rules<T, S> = Map<Assignment<T, S>, Possibilities<T, S>>
//typealias Fixed<T> = Map<T, Boolean>
//
//data class TileReduction<T, S>(
//    val rules: Rules<T, S>,
//    val tileSet: Set<T>,
//    val tilePossibilitySet: Set<S>,
//    val possibilities: Possibilities<T, S> = tileSet.associateWith { tilePossibilitySet },
//    val fixed: Fixed<T> = tileSet.associateWith { false }
//) {
//    /**
//     * Calculate the average entropy of the TileReduction.
//     * If it is null, then we are in an invalid state where a tile exists with no possibilities.
//     * If it is 1.0, then we have a full assignment of tiles to values.
//     */
//    val averageEntropy: Double? by lazy {
//        val tilePossibilities = tileSet.asSequence().map { t -> possibilities[t]?.size ?: 0 }
//        if (0 in tilePossibilities)
//            null
//        else tilePossibilities.sum().toDouble() / tileSet.size
//    }
//
//    /**
//     * Given an assignment of a tile to a value, calculate the new TileReduction.
//     */
//    fun fix(assignment: Assignment<T, S>): TileReduction<T, S> {
//        val rule = rules.getValue(assignment)
//        val newPossibilities = possibilities.map { (t, tPossibilities) ->
//            t to tPossibilities.intersect(rule.getValue(t))
//        }.toMap()
//
//        val check = newPossibilities.getValue(assignment.first)
//        if (check.size != 1)
//            println("Uh oh: $assignment -> $check")
//
//        val newFixed = fixed.map { (t, b) -> t to (t == assignment.first || b) }.toMap()
//        return TileReduction(rules, tileSet, tilePossibilitySet, newPossibilities, newFixed)
//    }
//
//    /**
//     * Given an assignment of tile t to value s, calculate the new TileReduction.
//     */
//    fun fix(t: T, s: S): TileReduction<T, S> =
//        fix(Assignment(t, s))
//
////    /**
////     * Find the lowest entropy amongst the tiles.
////     */
////    val lowestEntropy: Int by lazy {
////        tileSet
////            .filterNot { fixed.getValue(it) }
////            .minOf { t -> possibilities.getValue(t).size }
////    }
////
////    /**
////     * Get the tiles with the lowest entropy.
////     */
////    val lowestEntropyTiles: List<T> by lazy {
////        val le = lowestEntropy
////        possibilities
//////            .asSequence()
//////            .filterNot { (t, _) -> fixed.getValue(t) }
////            .filter { (t, s) -> s.size == le && !fixed.getValue(t) }
////            .map { (t, _) -> t }
////    }
//
//    /**
//     * Determine if this TileReduction is complete, i.e. has entropy 1, meaning that
//     * every tile has exactly one value associated with it.
//     */
//    val complete: Boolean by lazy {
//        !possibilities.asSequence().filter { it.value.size != 1 }.any()
//    }
//
//    /**
//     * Try to solve by picking a tile of lowest entropy and selecting a value.
//     * If first is true, we return the first TileReduction that satisfies if there is one.
//     * If first is false, we return the full set of TileReduction that satisfies if there are any.
//     * If there are no answers, null is returned.
//     */
//    fun backtrack(first: Boolean): Set<TileReduction<T, S>>? {
//        /**
//         * The parameters should be:
//         * 1. the tileIdx we are currently processing
//         * 2. the possibilities of the tiles for this tile
//         * 3. the last tried index of the possibilities for this tile (or null)
//         * 4. the current board (before tileIdx was modified)
//         * 5. whether we are backtracking or not
//         */
//        fun aux(currentBoard: TileReduction<T, S> = this, depth: Int = 0): Set<TileReduction<T, S>>? {
//            // Find the list of empty tiles.
//            val emptyTiles = currentBoard.fixed.filterValues { !it }.keys
//
//            // If there are no empty tiles, then we have a complete board.
//            if (emptyTiles.isEmpty())
//                return setOf(this)
//
//            // Pick the first tile of the lowest entropy to try next.
//            val lowestEntropy = emptyTiles.minOfOrNull { t -> currentBoard.possibilities.getValue(t).size } ?: 0
//            if (lowestEntropy == 0)
//                return null
//
//            val lowestEntropyT = currentBoard.possibilities.filter { (t, p) ->
//                t in emptyTiles && p.size == lowestEntropy
//            }
//
////            val let = lowestEntropyTiles.toList()
////            val t = lowestEntropyTiles.first()
//
//            val t = lowestEntropyT.keys.first()
//            val ps = currentBoard.possibilities.getValue(t)
//            println("${"\t".repeat(depth)}$t -> $ps")
//            // Try all the possible values.
//            return if (first) {
//                val answers = ps.mapNotNull { s ->
//                    val newBoard = currentBoard.fix(t, s)
//                    aux(newBoard, depth + 1)
//                }
//                answers.firstOrNull()
//                // If first is set, then we use a sequence and stop as soon as we have an answer.
////                ps.asSequence().mapNotNull { s ->
////                    val newBoard = currentBoard.fix(t, s)
////                    aux(newBoard, depth + 1)
////                }.firstOrNull()
//            }
//            else
//            // We continue through all possible values.
//                ps.fold(emptySet()) { set, s ->
//                    val newBoard = currentBoard.fix(t, s)
////                    set + (aux(this.fix(t, s), depth + 1) ?: emptySet())
//                    set + (aux(newBoard, depth + 1)?.toList() ?: emptySet())
//                }
//        }
//
//        return aux()
//    }
//
////    fun backtrack2(first: Boolean): Set<TileReduction<T, S>>? {
////        tailrec fun aux(
////            currentBoard: TileReduction<T, S> = this,
////            emptyTiles: Set<T> = currentBoard.fixed.filterValues { !it }.keys,
////            acc: Set<TileReduction<T, S>> = emptySet(),
////            currentPossibilities: List<S> = emptyList(),
////            currentIndex: Int = 0
////        ): Set<TileReduction<T, S>>? {
////            // If there are no empty tiles, then we have a complete board.
////            if (emptyTiles.isEmpty())
////                return acc + currentBoard
////
////            // Pick the first tile of the lowest entropy to try next.
////            val t = currentBoard.lowestEntropyTiles.first()
////
////            if (currentPossibilities.isEmpty()) {
////                // If currentPossibilities is empty, get the possibilities for the first tile.
////                val ps = currentBoard.possibilities.getValue(t)
////                return aux(currentBoard, emptyTiles, acc, ps.toList(), 0)
////            }
////
////            if (currentIndex >= currentPossibilities.size) {
////                // If all possibilities have been tried, return the accumulator (acc) or null.
////                return acc.ifEmpty { null }
////            }
////
////            // Try the next possibility.
////            val s = currentPossibilities[currentIndex]
////            val newBoard = currentBoard.fix(t, s)
////            val newEmptyTiles = emptyTiles - t
////
////            return if (first && acc.isNotEmpty()) {
////                // If first is set, stop as soon as we have an answer.
////                acc
////            } else {
////                // Continue with the next possibility or the next tile.
////                aux(newBoard, newEmptyTiles, acc, currentPossibilities, currentIndex + 1)
////            }
////        }
////
////        return aux()
////    }
//}


// We need:
// A tile placement identifier type, T.
// A type of tile, S.
// A map of rules r = Map<(T, S), Map<T, Set<S>>> meaning if t is set to s, then for any other tile t', its current
//    tile possibility set is intersected with r((s, t))(t') to get the new tile possibility set.
typealias Assignment<T, S> = Pair<T, S>
typealias Possibilities<T, S> = Map<T, Set<S>>
typealias Rules<T, S> = Map<Assignment<T, S>, Possibilities<T, S>>
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

        val check = newPossibilities.getValue(assignment.first)
        if (check.size != 1)
            println("Uh oh: $assignment -> $check")

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
        !possibilities.asSequence().filter { it.value.size != 1 }.any()
    }

    /**
     * Try to solve by picking a tile of lowest entropy and selecting a value.
     * If first is true, we return the first TileReduction that satisfies if there is one.
     * If first is false, we return the full set of TileReduction that satisfies if there are any.
     * If there are no answers, null is returned.
     */
    fun backtrack(first: Boolean): Set<TileReduction<T, S>>? {
        /**
         * The parameters should be:
         * 1. the tileIdx we are currently processing
         * 2. the possibilities of the tiles for this tile
         * 3. the last tried index of the possibilities for this tile (or null)
         * 4. the current board (before tileIdx was modified)
         * 5. whether we are backtracking or not
         */
        fun aux(currentBoard: TileReduction<T, S> = this, depth: Int = 0): Set<TileReduction<T, S>>? {
            // Find the list of empty tiles.
            val emptyTiles = currentBoard.fixed.filterValues { !it }.keys

            // If there are no empty tiles, then we have a complete board.
            if (emptyTiles.isEmpty())
                return setOf(currentBoard)

            // Pick the first tile of the lowest entropy to try next.
            val lowestEntropy = emptyTiles.minOfOrNull { t -> currentBoard.possibilities.getValue(t).size } ?: 0
            if (lowestEntropy == 0)
                return null

            val lowestEntropyPossibilities = currentBoard.possibilities.asSequence()
                .filter { (t, p) -> t in emptyTiles && p.size == lowestEntropy }
                .first()

            val t = lowestEntropyPossibilities.key
            val ps = currentBoard.possibilities.getValue(t)
//            println("${"\t".repeat(depth)}$t -> $ps")
            // Try all the possible values.

//            if (first) {
//                val answers = ps.mapNotNull { s ->
//                    val newBoard = currentBoard.fix(t, s)
//                    val retval = aux(newBoard, depth + 1)
//                    retval
//                }
//                val answer = answers.firstOrNull()
//                return answer
            return if (first) {
                val answers = ps.mapNotNull { s ->
                    val newBoard = currentBoard.fix(t, s)
                    aux(newBoard, depth + 1)
                }
                answers.firstOrNull()
                // If first is set, then we use a sequence and stop as soon as we have an answer.
//                ps.asSequence().mapNotNull { s ->
//                    val newBoard = currentBoard.fix(t, s)
//                    aux(newBoard, depth + 1)
//                }.firstOrNull()
            } else
            // We continue through all possible values.
                ps.fold(emptySet()) { set, s ->
                    val newBoard = currentBoard.fix(t, s)
//                    set + (aux(this.fix(t, s), depth + 1) ?: emptySet())
                    set + (aux(newBoard, depth + 1)?.toList() ?: emptySet())
                }


        }

        return aux()
    }

//    fun backtrack2(first: Boolean): Set<TileReduction<T, S>>? {
//        tailrec fun aux(
//            currentBoard: TileReduction<T, S> = this,
//            emptyTiles: Set<T> = currentBoard.fixed.filterValues { !it }.keys,
//            acc: Set<TileReduction<T, S>> = emptySet(),
//            currentPossibilities: List<S> = emptyList(),
//            currentIndex: Int = 0
//        ): Set<TileReduction<T, S>>? {
//            // If there are no empty tiles, then we have a complete board.
//            if (emptyTiles.isEmpty())
//                return acc + currentBoard
//
//            // Pick the first tile of the lowest entropy to try next.
//            val t = currentBoard.lowestEntropyTiles.first()
//
//            if (currentPossibilities.isEmpty()) {
//                // If currentPossibilities is empty, get the possibilities for the first tile.
//                val ps = currentBoard.possibilities.getValue(t)
//                return aux(currentBoard, emptyTiles, acc, ps.toList(), 0)
//            }
//
//            if (currentIndex >= currentPossibilities.size) {
//                // If all possibilities have been tried, return the accumulator (acc) or null.
//                return acc.ifEmpty { null }
//            }
//
//            // Try the next possibility.
//            val s = currentPossibilities[currentIndex]
//            val newBoard = currentBoard.fix(t, s)
//            val newEmptyTiles = emptyTiles - t
//
//            return if (first && acc.isNotEmpty()) {
//                // If first is set, stop as soon as we have an answer.
//                acc
//            } else {
//                // Continue with the next possibility or the next tile.
//                aux(newBoard, newEmptyTiles, acc, currentPossibilities, currentIndex + 1)
//            }
//        }
//
//        return aux()
//    }
}
