package day15

import java.io.File
import java.util.*

private val input = File("inputs/day15.txt").readLines().asSequence()

private val test = """
	1163751742
	1381373672
	2136511328
	3694931569
	7463417111
	1319128137
	1359912421
	3125421639
	1293138521
	2311944581
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
	w.initialXy().findPath()
}

private fun partTwo(w: Sequence<String>) {
	w.initialXy().expand(5).findPath()
}

private fun Sequence<String>.initialXy(): List<List<Int>> =
	map { it.map { c -> c - '0' }.toList() }
		.toList()

private fun List<List<Int>>.expand(
	times: Int
): List<List<Int>> {
	val xxy = map { xx ->
		buildList {
			repeat(times) { rep ->
				xx.map { inc(it, rep) }.forEach { this.add(it) }
			}
		}
	}
	return buildList {
		repeat(times) { rep ->
			xxy.forEach { xx ->
				add(xx.map { inc(it, rep) }.toList())
			}
		}
	}
}

private fun inc(v: Int, rep: Int) = 1 + ((v + rep - 1) % 9)

private data class P(val x: Int, val y: Int, val parent: P?, val pathRisk: Int, val h: Int) : Comparable<P> {
	override fun compareTo(other: P): Int {
		if (this.pathRisk == other.pathRisk)
			return this.h - other.h
		return this.pathRisk - other.pathRisk
	}
}

private fun List<List<Int>>.findPath() {
	val finalP = runAStar(this)
	var p = finalP
	val path = LinkedList<P>()
	var riskSum = 0
	while (p != null) {
		riskSum += this[p.y][p.x]
		path.addFirst(p)
		p = p.parent
	}
	riskSum -= this[0][0]
	print(path, riskSum)
}

/*
1#1 6 3 7 5 1 7 4 2
1#3 8 1 3 7 3 6 7 2
2#1#3#6#5#1#1#3 2 8
3 6 9 4 9 3 1#5#6 9
7 4 6 3 4 1 7 1#1#1
1 3 1 9 1 2 8 1 3#7
1 3 5 9 9 1 2 4 2#1
3 1 2 5 4 2 1 6 3#9
1 2 9 3 1 3 8 5 2#1#
2 3 1 1 9 4 4 5 8 1#
path: 1->1->2->1->3->6->5->1->1->1->5->1->1->3->2->3->2->1->1
path risk sum: 40
 */
private fun List<List<Int>>.print(
	path: LinkedList<P>,
	riskSum: Int
) {
	println(mapIndexed { y, xx ->
		xx.mapIndexed { x, risk ->
			if (path.find { it.x == x && it.y == y } != null) "$risk#" else "$risk "

		}.joinToString("")
	}.joinToString("\n"))
	println("path: ${path.joinToString("->") { "" + this[it.y][it.x] }}")
	println("path risk sum: $riskSum")
}

private fun runAStar(xy: List<List<Int>>): P? {
	val xlen = xy[0].size
	val ylen = xy.size
	val queue = PriorityQueue<P>()
	queue.add(P(0, 0, null, 0, 0))
	var finalP: P? = null
	val visited = Array(ylen) { Array(xlen) { false } }
	while (queue.isNotEmpty()) {
		val p = queue.poll()
		if (p.x == xlen - 1 && p.y == ylen - 1) {
			finalP = p
			break
		}
		if (visited[p.y][p.x]) continue
		visited[p.y][p.x] = true
		if (p.x > 0) queue.addp(p.x - 1, p.y, p, xy)
		if (p.y > 0) queue.addp(p.x, p.y - 1, p, xy)
		if (p.x < xlen - 1) queue.addp(p.x + 1, p.y, p, xy)
		if (p.y < ylen - 1) queue.addp(p.x, p.y + 1, p, xy)
	}
	return finalP
}

private fun PriorityQueue<P>.addp(x: Int, y: Int, p: P, xy: List<List<Int>>) =
	add(P(x, y, p, p.pathRisk + xy[y][x], heuristic(x, y, xy)))

private fun heuristic(x: Int, y: Int, xy: List<List<Int>>): Int {
	val dx = x - xy[0].size
	val dy = y - xy.size

	return dx * dx + dy * dy
}

