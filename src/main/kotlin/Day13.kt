package day01

import java.io.File

private val input = File("inputs/day13.txt").readLines().asSequence()

private val test = """
	6,10
	0,14
	9,10
	0,3
	10,4
	4,11
	6,0
	6,12
	4,1
	0,13
	10,12
	3,4
	3,0
	8,4
	1,10
	2,14
	8,10
	9,0

	fold along y=7
	fold along x=5
""".trimIndent().splitToSequence("\n").map { it.trim() }

fun main() {
	init()
	partOne(test)
	partOne(input)
	partTwo(test)
	partTwo(input)
}

private fun init() {
}

private fun partOne(w: Sequence<String>) {
	val xy = getMap(w)

	xy.fold(getCmds(w).first())

	val uniqueDots = buildSet { addAll(xy) }
	println("count: ${uniqueDots.size}")
}

private fun partTwo(w: Sequence<String>) {
	val xy = getMap(w)

	getCmds(w).forEach { xy.fold(it) }

	val uniqueDots = buildSet { addAll(xy) }

	val maxX = uniqueDots.maxOf { it.x }
	val maxY = uniqueDots.maxOf { it.y }
	for (y in 0..maxY) {
		for (x in 0..maxX) {
			print(
				if (uniqueDots.contains(P(x, y))) {
					'*'
				} else {
					' '
				}
			)
		}
		println()
	}
	/*
 **   **  ****   ** *  * ****  **  *  *
*  * *  * *       * *  *    * *  * * *
*    *  * ***     * ****   *  *    **
*    **** *       * *  *  *   *    * *
*  * *  * *    *  * *  * *    *  * * *
 **  *  * *     **  *  * ****  **  *  *
	 */

}

data class P(var x: Int, var y: Int)
data class C(val a: Char, val pos: Int)

private fun getCmds(w: Sequence<String>): List<C> =
	w.dropWhile { it.isNotBlank() }.drop(1).map { it.split('=') }.map { C(it[0].last(), it[1].toInt()) }.toList()

private fun getMap(w: Sequence<String>): List<P> =
	w.takeWhile { it.isNotBlank() }.map { it.split(',').map { it.toInt() } }.map { P(it[0], it[1]) }.toList()

private fun List<P>.fold(cmd: C) {
	forEach { p ->
		when (cmd.a) {
			'x' -> {
				if (p.x > cmd.pos) p.x = cmd.pos - (p.x - cmd.pos)
			}
			'y' -> {
				if (p.y > cmd.pos) p.y = cmd.pos - (p.y - cmd.pos)
			}
		}
	}
}
