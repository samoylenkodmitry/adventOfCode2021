package day05

import java.io.File

private val input = File("inputs/day05.txt").readLines()
	.asSequence()

private val test = """
	0,9 -> 5,9
	8,0 -> 0,8
	9,4 -> 3,4
	2,2 -> 2,1
	7,0 -> 7,4
	6,4 -> 2,0
	0,9 -> 2,9
	3,4 -> 1,4
	0,0 -> 8,8
	5,5 -> 8,2
""".trimIndent().splitToSequence("\n").map { it.trim() }

fun main() {
	partOne(test)
	partOne(input)
	partTwo(test)
	partTwo(input)
}

private fun partOne(w: Sequence<String>) {
	val lines = allLines(w).filter { it.isHorizontal() || it.isVertical() }

	val count = calcCount(lines)
	println("count = $count")
}

private fun partTwo(w: Sequence<String>) {
	val lines = allLines(w).filter { it.isHorizontal() || it.isVertical() || it.isDiagonal() }

	val count = calcCount(lines)
	println("count = $count")
}

private fun allLines(w: Sequence<String>) = w
	.map {
		Line(it, it
			.split(" -> ")
			.map { nums ->
				nums
					.split(",")
					.map { snum -> snum.toInt() }
			}.map { num -> Point(num) }
		)
	}

private fun calcCount(lines: Sequence<Line>): Int {
	val map = Array(1000) { Array(1000) { 0 } }
	lines.forEach { it.drawTo(map) }
	return map.sumOf { it.count { num -> num > 1 } }
}

private class Line(line: String, private val points: List<Point>) {
	init {
		if (points.size < 2) throw Error("wtf: $line")
	}

	private val lr = points.sortedBy { it.xy[0] }
	private val tb = points.sortedBy { it.xy[1] }
	fun isHorizontal() = points[0].xy[1] == points[1].xy[1]
	fun isVertical() = points[0].xy[0] == points[1].xy[0]
	fun isDiagonal() = isDiagonal1() || isDiagonal2()

	private fun isDiagonal1(): Boolean {
		val dx = lr[1].xy[0] - lr[0].xy[0]
		val dy = lr[1].xy[1] - lr[0].xy[1]
		return dx == dy
	}

	private fun isDiagonal2(): Boolean {
		val dx = lr[1].xy[0] - lr[0].xy[0]
		val dy = lr[1].xy[1] - lr[0].xy[1]
		return dx == -dy
	}

	fun drawTo(map: Array<Array<Int>>) {
		if (isHorizontal()) {
			val y = lr[0].xy[1]
			for (x in lr[0].xy[0]..lr[1].xy[0]) {
				map[y][x] += 1
			}
		} else if (isVertical()) {
			val x = tb[0].xy[0]
			for (y in tb[0].xy[1]..tb[1].xy[1]) {
				map[y][x] += 1
			}
		} else if (isDiagonal1()) {
			var y = lr[0].xy[1]
			for (x in lr[0].xy[0]..lr[1].xy[0]) {
				map[y][x] += 1
				y++
			}

		} else if (isDiagonal2()) {
			var y = lr[0].xy[1]
			for (x in lr[0].xy[0]..lr[1].xy[0]) {
				map[y][x] += 1
				y--
			}
		}
	}
}

private class Point(val xy: List<Int>)


