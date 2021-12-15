package day01

import java.io.File

private val input = File("inputs/day11.txt").readLines()
	.asSequence()

private val test = """
	5483143223
	2745854711
	5264556173
	6141336146
	6357385478
	4167524645
	2176841721
	6882881134
	4846848554
	5283751526
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
	val map = w.map { it.map { it - '0' }.toMutableList() }.toList()
	var count = 0
	repeat(100) {
		count += step(map, Array(10) { Array(10) { false } })
	}
	println(count)
}

private fun partTwo(w: Sequence<String>) {
	val map = w.map { it.map { it - '0' }.toMutableList() }.toList()
	repeat(1000) {
		step(map, Array(10) { Array(10) { false } })
		if (map.fold(0) { acc, arr ->
				acc + arr.fold(0) { acc2, v ->
					acc2 + v
				}
			} == 0) {
			println("flashes at step ${it + 1}")
			return
		}
	}
}

fun step(map: List<MutableList<Int>>, visited: Array<Array<Boolean>>): Int {
	for (y in 0..9) {
		for (x in 0..9) {
			map[y][x]++
		}
	}
	var count = 0
	for (y in 0..9) {
		for (x in 0..9) {
			val v = map[y][x]
			if (v > 9) {
				map[y][x]--
				count += flash(map, y, x, visited)
			}
		}
	}

	return count
}

fun flash(map: List<MutableList<Int>>, y: Int, x: Int, flashed: Array<Array<Boolean>>): Int {
	if (flashed[y][x]) return 0
	map[y][x]++
	val v = map[y][x]
	var count = 0
	if (v > 9) {
		flashed[y][x] = true
		map[y][x] = 0
		count = 1
		if (x > 0) count += flash(map, y, x - 1, flashed)
		if (y > 0) count += flash(map, y - 1, x, flashed)
		if (x < 9) count += flash(map, y, x + 1, flashed)
		if (y < 9) count += flash(map, y + 1, x, flashed)
		if (x > 0 && y > 0) count += flash(map, y - 1, x - 1, flashed)
		if (x < 9 && y < 9) count += flash(map, y + 1, x + 1, flashed)
		if (x > 0 && y < 9) count += flash(map, y + 1, x - 1, flashed)
		if (y > 0 && x < 9) count += flash(map, y - 1, x + 1, flashed)
	}
	return count
}

