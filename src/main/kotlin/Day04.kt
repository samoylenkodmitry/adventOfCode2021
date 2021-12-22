package day04

import java.io.File

private val input = File("inputs/day04.txt").readLines()
	.asSequence()

private val test = """
7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7
""".trimIndent().splitToSequence("\n").map { it.trim() }

fun main() {
	partOne(test)
	partOne(input)
	partTwo(test)
	partTwo(input)
}

private fun partOne(w: Sequence<String>) {
	val numbers = numsSeq(w)
	val boards = boardSeq(w)

	numbers.first { num ->
		boards.any { it.nextNum(num) }
	}
}

private fun partTwo(w: Sequence<String>) {
	val numbers = numsSeq(w)
	val boards = boardSeq(w)

	numbers.forEach { num ->
		boards.forEach { it.nextNum(num) }
	}
}

private fun boardSeq(w: Sequence<String>): List<Board> = w.drop(1).chunked(6).map { Board(it) }.toList()

private fun numsSeq(w: Sequence<String>): Sequence<Int> = w.first().splitToSequence(",").map { it.toInt() }

private class Board(val lines: List<String>) {
	val rows = lines
		.drop(1)
		.map { line ->
			line.split(Regex("\\W+"))
				.filter { it != "" }
				.map { snum ->
					if (snum == "") {
						throw Error("wtf: line:`$line`, snum: `$snum` ${lines.joinToString("\n")}")
					}
					snum.toInt()
				}
				.toMutableList()
		}
		.toMutableList()
	var sum = rows.sumOf { it.sum() }

	var alreadySolved = false
	fun nextNum(num: Int): Boolean {
		if (alreadySolved) return false
		for (x in 0..4) {
			for (y in 0..4) {
				val n = rows[y][x]
				if (n == num) {
					rows[y][x] = -n - 1
					sum -= n
					break
				}
			}
		}
		val s = solved()
		if (s) {
			alreadySolved = true
			val score = sum * num
			println("score = $score at num $num for board ${lines.joinToString(separator = "\n")}")

		}
		return s
	}

	private fun solved(): Boolean {
		if (rows.any { it.all { it < 0 } }) return true
		for (x in 0..4) {
			var allVisited = true
			for (y in 0..4) {
				if (rows[y][x] >= 0) {
					allVisited = false
					break
				}
			}
			if (allVisited) return true
		}
		return false
	}
}

