# tile-reduction

Given:
* a set of "tiles" `T`;
* a set of possible values `S` to assign to `T`; and
* a set of rules:

```kotlin
Assignment = Pair(t, s),  t ∈ T, s ∈ S
Possibilities = Pair(t', S'), t' ∈ T, S' ⊆ S
Rules = Assignment → Possibilities
```

the `TileReduction` finds one (or all) assignments.

Each implementation derives the above three parameters, and then can fix tiles to certain values before running the
algorithm. This allows for a great deal of flexibility in the problems that can be solved, as per the below examples.

At each stage, the algorithm uses backtracking over the lexicographically first tile amongst the set of tiles with the
fewest remaining permitted assignments. It then fixes the tile to the first assignment, refines the possible assignments
for the other tiles, and recurses. If at any point, a complete solution is found, it is stored, and if any tile has
no available assignments left, the algorithm stops. In both cases, it backtracks, either to return the solution found
or to continue to generate the set of all possible solutions.

Example problems:

1. [Roads](#road-tiling)
2. [Sudoku Grids](#sudoku)
3. [Larger Sudoku Grids](#larger-sudoku)
4. [Sudoku Cubes and Hypercubes](#sudoku-cubes-and-hypercubes)
5. [Grid Partitioning](#grid-partitioning)

Note that the problems below are all based on grids, but there is no reason that this problem cannot be generalized.

**Status:** Complete.


## Road tiling

This algorithm can be used to, for example, given a set of road tiles as in a game such as Carcassonne, determine
all valid completions of the road. In this example, we only have the road tiles:

Up (U):
```text
━┻━
```

Down (D):
```text
━┳━
```

Left (L):
```text
━┫ 
```

Right (R):
```text
 ┣━
```

Empty (E):
```text
░░░
```

Given, for example, the following input:

```text
_ R _ _ _
D U _ _ L
_ D _ _ _
E R U L E
```

the following 12 solutions are output:

```text
░░░ ┣━━┳━━┳━━┳━
━┳━━┻━━┻━━┻━━┫ 
━┻━━┳━━┳━━┳━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┳━━┳━━┫ 
━┳━━┻━━┻━━┻━━┫ 
━┻━━┳━━┳━━┳━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┳━━┫  ┣━
━┳━━┻━━┻━━┻━━┫ 
━┻━━┳━━┳━━┳━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┫  ┣━━┳━
━┳━━┻━━┻━━┻━━┫ 
━┻━━┳━━┳━━┳━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┫  ┣━━┫ 
━┳━━┻━━┻━━┻━━┫ 
━┻━━┳━━┳━━┳━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┻━━┻━━┳━
━┳━━┻━━┳━━┳━━┫ 
━┻━━┳━━┫  ┣━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┻━━┻━━┫ 
━┳━━┻━━┳━━┳━━┫ 
━┻━━┳━━┫  ┣━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┳━━┳━━┳━
━┳━━┻━━┫  ┣━━┫ 
━┻━━┳━━┫  ┣━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┳━━┳━━┫ 
━┳━━┻━━┫  ┣━━┫ 
━┻━━┳━━┫  ┣━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┳━━┫  ┣━
━┳━━┻━━┫  ┣━━┫ 
━┻━━┳━━┫  ┣━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┫  ┣━━┳━
━┳━━┻━━┫  ┣━━┫ 
━┻━━┳━━┫  ┣━━┻━
░░░ ┣━━┻━━┫ ░░░


░░░ ┣━━┫  ┣━━┫ 
━┳━━┻━━┫  ┣━━┫ 
━┻━━┳━━┫  ┣━━┻━
░░░ ┣━━┻━━┫ ░░░
```

[Back to Top](#tile-reduction)

## Sudoku

Of course, this can easily be applied as a Sudoku solver by choosing the appropriate rules.

For example, if we have the Sudoku seed:

```text
1 _ _ 3 _ 4 8 _ 9
_ 2 4 _ _ _ _ _ 7
_ _ _ 9 6 _ 5 _ _
2 3 _ _ _ _ _ 5 _
5 _ 7 _ _ 8 _ _ _
_ _ _ _ _ _ 4 _ _
_ _ 9 5 _ _ _ _ _
3 _ _ 8 _ _ _ _ _
_ 5 _ 6 _ _ _ 3 _
```

The algorithm finds the unique solution:

```text
1 6 5 3 7 4 8 2 9
9 2 4 1 8 5 3 6 7
8 7 3 9 6 2 5 1 4
2 3 8 4 9 6 7 5 1
5 4 7 2 1 8 6 9 3
6 9 1 7 5 3 4 8 2
4 8 9 5 3 1 2 7 6
3 1 6 8 2 7 9 4 5
7 5 2 6 4 9 1 3 8
```

[Back to Top](#tile-reduction)


## Larger Sudoku

For any `k ≥ 1`, a `k^2 × k^2` Sudoku board exists over `k^2` symbols with the expected properties of the `k = 3`
typical Sudoku board. Here is a `k = 4` seed for a `16 × 16` board which we represent using hexadecimal:

```text
_ A 3 4 7 B _ _ 1 9 E _ _ 8 _ _
C 6 E 7 _ _ _ 1 _ 5 B 2 _ 9 _ _
_ _ B _ _ 6 _ 8 _ _ 4 D 5 _ _ F
1 _ _ _ 9 _ 0 _ C _ F 3 4 E _ _
_ _ 0 _ F _ _ 5 _ _ A 7 _ B 8 _
6 7 A _ 0 _ _ _ _ _ _ C _ _ 5 9
5 _ _ _ _ _ _ 9 B 3 _ _ _ 0 _ E
E 3 2 8 D _ A B 5 1 _ 9 _ _ F _
2 _ _ _ 8 _ C _ 4 _ _ F _ 3 6 _
3 B _ 1 _ _ _ 7 _ _ _ _ 9 F 4 2
_ _ 7 _ B 4 _ F _ 2 5 6 _ _ _ 0
9 _ 4 F _ 2 3 _ 7 D _ _ _ _ B _
8 2 1 0 C _ 7 E D 4 3 B _ _ A 6
_ E _ _ 5 8 _ _ _ _ _ A _ 1 _ 3
7 F 5 _ A 0 B _ E _ 2 _ _ _ _ _
4 D _ A 1 _ 2 _ _ _ _ 5 B 7 _ C
```

The algorithm finds the unique solution:

```text
F A 3 4 7 B 5 C 1 9 E 0 6 8 2 D 
C 6 E 7 4 D F 1 8 5 B 2 0 9 3 A 
0 9 B 2 3 6 E 8 A 7 4 D 5 C 1 F 
1 5 8 D 9 A 0 2 C 6 F 3 4 E 7 B 
D 4 0 9 F 3 6 5 2 E A 7 C B 8 1 
6 7 A B 0 E 1 4 F 8 D C 3 2 5 9 
5 1 F C 2 7 8 9 B 3 6 4 A 0 D E 
E 3 2 8 D C A B 5 1 0 9 7 6 F 4 
2 0 D 5 8 1 C A 4 B 9 F E 3 6 7 
3 B 6 1 E 5 D 7 0 A C 8 9 F 4 2 
A 8 7 E B 4 9 F 3 2 5 6 1 D C 0 
9 C 4 F 6 2 3 0 7 D 1 E 8 A B 5 
8 2 1 0 C 9 7 E D 4 3 B F 5 A 6 
B E C 6 5 8 4 D 9 F 7 A 2 1 0 3 
7 F 5 3 A 0 B 6 E C 2 1 D 4 9 8 
4 D 9 A 1 F 2 3 6 0 8 5 B 7 E C
```

[Back to Top](#tile-reduction)


## Sudoku Cubes and Hypercubes
For an integer `k ≥ 1` and `n ≥ 1`, an analogous Sudoku cube (hypercube) exists where:

* There are `n` dimensions of size `k^n` with `(k^n)^n` cells.
* We fill the board with `k^n` symbols such that:
 - Each line in each dimension includes the `k^n` symbols exactly once
 - Each of the `((k^n)^n)/(k^n)) = (k^n)^(n-1)` sub-cubes (sub-hypercubes) of side `k` in the standard way contain include the `k^n` symbols exactly once

The smallest example that deviates from a normal Sudoku grid has parameters `k=2`, `n=3`, which gives us a cube
of size `2^3` on each axis, so an `8 × 8 × 8` cube, which contains 512 cells and 64 subcubes.

To represent the problem, we demonstrate it as eight grids to be stacked on top of each other, from top-to-bottom,
and left-to-right:

```text
_ _ _ _ 5 6 _ _                _ 4 _ 6 _ _ _ _
_ _ 5 _ _ _ _ _                5 _ _ _ _ _ _ 4
5 _ _ _ _ _ _ _                _ _ _ _ _ _ 5 _
_ _ _ _ _ _ _ _                _ _ _ _ _ 6 _ _
_ _ _ _ 6 _ _ _                _ _ _ _ _ _ 6 _
_ 3 _ 1 _ _ _ _                _ _ _ 3 _ _ _ _
_ _ _ _ _ 1 _ _                _ _ 6 _ _ _ _ 1
_ _ _ _ 4 _ _ _                _ _ _ 7 _ _ 4 _
        

5 6 _ 8 _ _ _ _                _ _ _ _ _ _ _ _
_ _ _ _ _ _ _ _                _ _ _ _ 5 _ _ _
_ _ _ 4 5 6 _ _                _ _ 5 _ _ 8 _ _
_ _ _ _ 7 _ _ _                _ 6 7 _ _ _ _ _
_ _ _ _ _ _ _ _                _ _ _ _ _ _ 2 _
8 _ _ _ _ _ _ 1                _ _ 8 _ 6 5 4 _
_ _ _ _ _ _ _ _                _ _ _ _ _ _ _ _
4 _ _ 1 _ 7 _ _                _ _ _ _ _ _ _ _
        

_ _ 4 _ _ _ _ 1                _ _ _ _ _ 1 _ _
4 _ 6 _ _ _ _ _                _ 7 _ 1 _ _ _ _
_ _ _ _ _ _ _ 5                _ _ 2 _ _ _ _ _
_ _ _ _ 4 _ 6 _                _ _ _ _ _ _ _ _
3 _ 1 _ 7 _ _ 4                _ 4 _ _ _ _ _ _
_ _ _ _ _ _ _ _                7 _ _ _ _ _ _ _
_ _ _ _ 3 _ _ _                _ _ _ _ _ 4 3 _
_ _ 7 6 _ _ 3 _                _ _ _ _ _ 6 _ _
         

_ _ _ _ 2 _ _ _                _ _ _ _ _ _ _ 7
_ 1 _ 3 _ _ _ _                2 _ _ _ 6 _ _ _
_ _ _ _ _ _ _ 1                _ 5 _ _ _ 1 _ _
4 5 _ _ _ _ 2 _                _ _ _ _ _ _ 4 _
7 _ 5 4 _ _ _ _                1 _ _ _ _ 4 _ _
_ _ _ _ _ _ _ _                _ _ _ _ _ _ 5 _
_ _ _ _ 7 _ 5 _                _ _ 3 _ _ _ _ _
_ _ _ _ _ _ _ 6                _ 6 _ _ _ _ _ _
```

The algorithm finds the unique solution:

```text
3 2 7 4 5 6 1 8                7 4 5 6 1 8 3 2 
1 4 5 2 7 8 3 6                5 6 7 8 3 2 1 4 
5 6 3 8 1 2 7 4                3 8 1 2 7 4 5 6
7 8 1 6 3 4 5 2                1 2 3 4 5 6 7 8  
2 1 4 3 6 5 8 7                4 3 2 1 8 7 6 5
4 3 2 1 8 7 6 5                6 5 4 3 2 1 8 7 
6 5 8 7 2 1 4 3                8 7 6 5 4 3 2 1                 
8 7 6 5 4 3 2 1                2 1 8 7 6 5 4 3 


5 6 1 8 3 2 7 4                1 8 3 2 7 4 5 6 
7 8 3 6 1 4 5 2                3 2 1 4 5 6 7 8 
1 2 7 4 5 6 3 8                7 4 5 6 3 8 1 2 
3 4 5 2 7 8 1 6                5 6 7 8 1 2 3 4 
6 5 8 7 2 1 4 3                8 7 6 5 4 3 2 1 
8 7 6 5 4 3 2 1                2 1 8 7 6 5 4 3 
2 1 4 3 6 5 8 7                4 3 2 1 8 7 6 5 
4 3 2 1 8 7 6 5                6 5 4 3 2 1 8 7 


2 3 4 5 6 7 8 1                4 5 6 7 8 1 2 3 
4 5 6 7 8 1 2 3                6 7 8 1 2 3 4 5
6 7 8 1 2 3 4 5                8 1 2 3 4 5 6 7
8 1 2 3 4 5 6 7                2 3 4 5 6 7 8 1
3 6 1 2 7 8 5 4                5 4 3 6 1 2 7 8
1 2 3 8 5 4 7 6                7 6 5 2 3 8 1 4
7 8 5 4 3 6 1 2                1 2 7 8 5 4 3 6
5 4 7 6 1 2 3 8                3 8 1 4 7 6 5 2
 

6 7 8 1 2 3 4 5                8 1 2 3 4 5 6 7
8 1 2 3 4 5 6 7                2 3 4 5 6 7 8 1
2 3 4 5 6 7 8 1                4 5 6 7 8 1 2 3 
4 5 6 7 8 1 2 3                6 7 8 1 2 3 4 5 
7 8 5 4 3 6 1 2                1 2 7 8 5 4 3 6 
5 4 7 6 1 2 3 8                3 8 1 4 7 6 5 2
3 6 1 2 7 8 5 4                5 4 3 6 1 2 7 8 
1 2 3 8 5 4 7 6                7 6 5 2 3 8 1 4 
```

[Back to Top](#tile-reduction)


# Grid Partitioning

Given an `m × n` grid where the corners are set so that the partitioning is closed, the
algorithm can perform a generation of all partitions of an `(m-1) × (n-1)` grid, or stochastically search for the first
random partition, by tiling the larger grid with:

* the four corner tiles `" ┏━"`, `"━┓ "`, `" ┗━"`, and `"━┛ "`;
* the vertical tile `" ┃ "`; and
* the horizontal tile `"━━━"`.

Here are four partitions of an `7 × 7` grid.

```text
 ┏━━┓  ┏━━┓  ┏━━┓  ┏━━┓ 
 ┗━━┛  ┃  ┃  ┃  ┗━━┛  ┃ 
 ┏━━━━━┛  ┃  ┗━━┓  ┏━━┛ 
 ┗━━━━━━━━┛  ┏━━┛  ┗━━┓ 
 ┏━━┓  ┏━━┓  ┃  ┏━━━━━┛ 
 ┃  ┃  ┃  ┃  ┗━━┛  ┏━━┓ 
 ┃  ┃  ┃  ┃  ┏━━━━━┛  ┃ 
 ┗━━┛  ┗━━┛  ┗━━━━━━━━┛ 
```

```text
 ┏━━━━━┓  ┏━━┓  ┏━━━━━┓ 
 ┗━━━━━┛  ┗━━┛  ┃  ┏━━┛ 
 ┏━━━━━┓  ┏━━━━━┛  ┗━━┓ 
 ┗━━┓  ┃  ┗━━┓  ┏━━┓  ┃ 
 ┏━━┛  ┗━━━━━┛  ┗━━┛  ┃ 
 ┗━━━━━┓  ┏━━━━━┓  ┏━━┛ 
 ┏━━┓  ┃  ┗━━┓  ┃  ┗━━┓ 
 ┗━━┛  ┗━━━━━┛  ┗━━━━━┛ 
```

```text
 ┏━━━━━━━━━━━┓  ┏━━━━━┓ 
 ┗━━━━━━━━┓  ┗━━┛  ┏━━┛ 
 ┏━━━━━┓  ┃  ┏━━┓  ┗━━┓ 
 ┗━━┓  ┗━━┛  ┃  ┗━━┓  ┃ 
 ┏━━┛  ┏━━┓  ┃  ┏━━┛  ┃ 
 ┗━━┓  ┃  ┃  ┗━━┛  ┏━━┛ 
 ┏━━┛  ┃  ┃  ┏━━┓  ┗━━┓ 
 ┗━━━━━┛  ┗━━┛  ┗━━━━━┛ 
```

```text
 ┏━━━━━┓  ┏━━━━━┓  ┏━━┓ 
 ┗━━┓  ┃  ┗━━┓  ┃  ┃  ┃ 
 ┏━━┛  ┗━━┓  ┗━━┛  ┃  ┃ 
 ┗━━━━━┓  ┗━━┓  ┏━━┛  ┃ 
 ┏━━━━━┛  ┏━━┛  ┗━━┓  ┃ 
 ┃  ┏━━━━━┛  ┏━━━━━┛  ┃ 
 ┃  ┗━━┓  ┏━━┛  ┏━━┓  ┃ 
 ┗━━━━━┛  ┗━━━━━┛  ┗━━┛ 
```

Note that there is no set of rules that can force the algorithm to find a random Hamiltonian cycle, although it is
simple to take a partitioning and turn it into a Hamiltonian cycle by modifying the components to be connected.

For example, the previous partitioning above can be modified as followed:

```text
 ┏━━━━━━━━━━━━━━┓  ┏━━┓ 
 ┗━━┓  ┏━━━━━┓  ┃  ┃  ┃ 
 ┏━━┛  ┗━━┓  ┗━━┛  ┃  ┃ 
 ┗━━━━━┓  ┗━━┓  ┏━━┛  ┃ 
 ┏━━━━━┛  ┏━━┛  ┗━━┓  ┃ 
 ┃  ┏━━━━━┛  ┏━━━━━┛  ┃ 
 ┃  ┗━━━━━━━━┛  ┏━━┓  ┃ 
 ┗━━━━━━━━━━━━━━┛  ┗━━┛ 
```

The algorithm can also accept a seed for the partitioning as per the other examples above.
In this partioning of an `8 × 8` grid, a border is specified around the perimeter.

```text
 ┏━━━━━━━━━━━━━━━━━━━━┓ 
 ┃  ┏━━━━━━━━┓  ┏━━┓  ┃ 
 ┃  ┗━━━━━┓  ┗━━┛  ┃  ┃ 
 ┃  ┏━━┓  ┃  ┏━━━━━┛  ┃ 
 ┃  ┃  ┃  ┗━━┛  ┏━━┓  ┃ 
 ┃  ┃  ┗━━┓  ┏━━┛  ┃  ┃ 
 ┃  ┗━━━━━┛  ┗━━━━━┛  ┃ 
 ┗━━━━━━━━━━━━━━━━━━━━┛ 
```

Here are the 54 solutions for a `4 × 3` grid using a tiling of a `5 × 4` grid:

```text
 ┏━━━━━━━━┓ 
 ┃  ┏━━━━━┛ 
 ┃  ┃  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━━━━┛ 
 ┃  ┃  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━━━━┛ 
 ┃  ┗━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━━━━┛ 
 ┗━━┛  ┏━━┓ 
 ┏━━━━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━━━━┛ 
 ┗━━┛  ┏━━┓ 
 ┏━━┓  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┃  ┃  ┃  ┃ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┃  ┃  ┃  ┃ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┃  ┃  ┗━━┛ 
 ┃  ┃  ┏━━┓ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┃  ┃  ┗━━┛ 
 ┃  ┗━━━━━┓ 
 ┗━━━━━━━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┃  ┗━━┛  ┃ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┃  ┃ 
 ┏━━━━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┃  ┃ 
 ┏━━┓  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 
 ┏━━━━━━━━┓ 
 ┗━━━━━━━━┛ 

 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 
 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┗━━━━━━━━┛ 
 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┗━━━━━━━━┛ 
 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┗━━━━━━━━┛ 
 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━━━━━━━┓ 
 ┗━━━━━┓  ┃ 
 ┏━━━━━┛  ┃ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┗━━━━━┓  ┃ 
 ┏━━┓  ┃  ┃ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┗━━━━━┓  ┃ 
 ┏━━┓  ┃  ┃ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━━━━━━━┓ 
 ┗━━━━━┓  ┃ 
 ┏━━┓  ┗━━┛ 
 ┃  ┃  ┏━━┓ 
 ┗━━┛  ┗━━┛ 

 ┏━━━━━━━━┓ 
 ┗━━━━━┓  ┃ 
 ┏━━┓  ┗━━┛ 
 ┃  ┗━━━━━┓ 
 ┗━━━━━━━━┛ 

 ┏━━━━━━━━┓ 
 ┗━━┓  ┏━━┛ 
 ┏━━┛  ┗━━┓ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┃  ┃  ┃  ┃ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┃  ┃  ┃  ┃ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┃  ┃  ┗━━┛ 
 ┃  ┃  ┏━━┓ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┃  ┃  ┗━━┛ 
 ┃  ┗━━━━━┓ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┃  ┗━━┛  ┃ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┃  ┃ 
 ┏━━━━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┃  ┃ 
 ┏━━┓  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 
 ┏━━━━━━━━┓ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 
 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┗━━┛ 
 ┃  ┃  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┗━━┛ 
 ┃  ┃  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┗━━┛ 
 ┃  ┗━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┗━━┛ 
 ┗━━┛  ┏━━┓ 
 ┏━━━━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┗━━┛ 
 ┗━━┛  ┏━━┓ 
 ┏━━┓  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┃  ┏━━━━━┛ 
 ┃  ┃  ┏━━┓ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┃  ┏━━━━━┛ 
 ┃  ┗━━━━━┓ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┃  ┏━━┓  ┃ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┃  ┏━━┓  ┃ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 
 ┏━━━━━━━━┓ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 
 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━┓  ┃ 
 ┏━━━━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━┓  ┃ 
 ┏━━┓  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┗━━┓  ┏━━┛ 
 ┏━━┛  ┗━━┓ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┃  ┃ 
 ┏━━━━━┛  ┃ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┃  ┃ 
 ┏━━┓  ┃  ┃ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┃  ┃ 
 ┏━━┓  ┃  ┃ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┃  ┃ 
 ┏━━┓  ┗━━┛ 
 ┃  ┃  ┏━━┓ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┃  ┃ 
 ┏━━┓  ┗━━┛ 
 ┃  ┗━━━━━┓ 
 ┗━━━━━━━━┛ 

 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┗━━┛ 
 ┏━━━━━━━━┓ 
 ┃  ┏━━┓  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┗━━┛ 
 ┏━━┓  ┏━━┓ 
 ┃  ┃  ┃  ┃ 
 ┗━━┛  ┗━━┛ 

 ┏━━┓  ┏━━┓ 
 ┗━━┛  ┗━━┛ 
 ┏━━┓  ┏━━┓ 
 ┃  ┗━━┛  ┃ 
 ┗━━━━━━━━┛ 
```


[Back to Top](#tile-reduction)