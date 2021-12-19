package day01

import java.io.File

private val input = File("inputs/day12.txt").readLines()
	.asSequence()

private val test = """
	start-A
	start-b
	A-c
	A-b
	b-d
	A-end
	b-end
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
	val adj = adj(w)
	val queue = ArrayDeque<Pair<String, List<String>>>()
	queue.add(Pair("start", mutableListOf("start")))
	var endCount = 0
	while (queue.isNotEmpty()) {
		val curr = queue.removeFirst()
		if (curr.first == "end") {
			endCount++
		} else
			adj[curr.first]?.forEach { x ->
				if (x.uppercase() == x || !curr.second.contains(x))
					queue += Pair(x, curr.second + x)
			}
	}
	println("paths count: $endCount")
}

class Entry(val v: String, val visited: Map<String, Int>, val canVisit: Boolean, val path: String)

private fun partTwo(w: Sequence<String>) {
	val adj = adj(w)
	val queue = ArrayDeque<Entry>()
	queue.add(Entry("start", mapOf("start" to 1), true, "start"))
	var endCount = 0
	while (queue.isNotEmpty()) {
		val curr = queue.removeFirst()
		if (curr.v == "end") {
			endCount++
		} else
			adj[curr.v]?.forEach { x ->
				val visitedCount = curr.visited[x] ?: 0
				val isUpper = x[0].isUpperCase()
				if (x != "start" && (isUpper || (visitedCount == 0 || curr.canVisit && visitedCount < 2)))
					queue += Entry(
						x, curr.visited + (x to (visitedCount + 1)),
						curr.canVisit && (isUpper || visitedCount == 0),
						curr.path + ", $x"
					)
			}
	}
	println("2: paths count: $endCount")
}

private fun adj(w: Sequence<String>): MutableMap<String, MutableSet<String>> {
	val adj = mutableMapOf<String, MutableSet<String>>()
	w.map { it.split('-') }
		.forEach {
			adj.connect(it[0], it[1])
		}
	return adj
}

fun MutableMap<String, MutableSet<String>>.connect(s1: String, s2: String) {
	computeIfAbsent(s1, { mutableSetOf() }).add(s2)
	computeIfAbsent(s2, { mutableSetOf() }).add(s1)
}

