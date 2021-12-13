package day01

import java.io.File

private val input = File("inputs/day09.txt").readLines()
	.asSequence()

private val test = """
	2199943210
	3987894921
	9856789892
	8767896789
	9899965678
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
	val map = createMap(w)
	val r = findRiskPoints(map)
		.sumOf { map[it.second][it.first] + 1 }

	println("total risk: $r")
}

private fun partTwo(w: Sequence<String>) {
	val map = createMap(w)
	val riskPoints = findRiskPoints(map)
	val visited = Array(map.size) { Array(map[0].size) { false } }
	val result = riskPoints
		.asSequence()
		.map { countDfs(it.first, it.second, visited, map) }
		.sortedDescending()
		.take(3)
		.reduce { acc, i -> acc * i }
	println("part 2: $result")
}

fun countDfs(
	x: Int, y: Int,
	visited: Array<Array<Boolean>>,
	map: List<List<Int>>
): Int {
	if (visited[y][x]) return 0
	if (map[y][x] == 9) return 0

	visited[y][x] = true
	var res = 1
	if (x > 0) res += countDfs(x - 1, y, visited, map)
	if (y > 0) res += countDfs(x, y - 1, visited, map)
	if (x < map[y].lastIndex) res += countDfs(x + 1, y, visited, map)
	if (y < map.lastIndex) res += countDfs(x, y + 1, visited, map)

	return res
}

private fun findRiskPoints(map: List<List<Int>>): MutableList<Pair<Int, Int>> =
	map.foldIndexed(mutableListOf()) { y, list, row ->
		row.foldIndexed(list) { x, sameList, v ->
			if (lower(map, x, y, v)) sameList.add(Pair(x, y))
			sameList
		}
	}

private fun createMap(w: Sequence<String>): List<List<Int>> =
	w.map { it.map { it - '0' } }.toList()

fun lower(map: List<List<Int>>, x: Int, y: Int, v: Int): Boolean {
	var res = true
	if (x > 0) res = res and (map[y][x - 1] > v)
	if (y > 0) res = res and (map[y - 1][x] > v)
	if (x < map[y].lastIndex) res = res and (map[y][x + 1] > v)
	if (y < map.lastIndex) res = res and (map[y + 1][x] > v)
	return res
}

