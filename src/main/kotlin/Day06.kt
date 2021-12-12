package day01

import java.io.File

private val input = File("inputs/day06.txt").readLines()
	.asSequence()

private val test = """
	3,4,3,1,2
""".trimIndent().splitToSequence("\n").map { it.trim() }

fun main() {
	initCache()
	partOne(test)
	partOne(input)
	partTwo(test)
	partTwo(input)
}

val cache = Array(9) { Array(257) { -1L } }

private fun partOne(w: Sequence<String>) {
	println(getInitailFishes(w).sumOf { cache[it.cycleDay][80] })
}

private fun partTwo(w: Sequence<String>) {
	println(getInitailFishes(w).sumOf { cache[it.cycleDay][256] })
}

private fun initCache() {
	for (cycle in 0..8) {
		for (daysOfSimulation in 0..256) {
			var count = 0L
			val fishes = mutableListOf(Fish(cycle))
			repeat(daysOfSimulation) { day ->
				if (fishes.isNotEmpty()) {
					//println("$count, fishes before: ${fishes.joinToString { it.cycleDay.toString() }}")
					fishes.removeIf { fish ->
						val cached = cache[fish.cycleDay][daysOfSimulation - day]
						if (cached > 0) {
							count += cached
							true
						} else {
							false
						}
					}
					//println("$count, fishes after: ${fishes.joinToString { it.cycleDay.toString() }}")
					val children = simulateOneDay(fishes)
					children.forEach { fish ->
						val cached = cache[fish.cycleDay][daysOfSimulation - day]
						if (cached > 0) {
							count += cached
						} else {
							fishes.add(fish)
						}
					}
				}

			}
			count += fishes.count()
			cache[cycle][daysOfSimulation] = count
			//println("$cycle, $daysOfSimulation, ${count}")
		}
	}
}

private fun getInitailFishes(w: Sequence<String>): MutableList<Fish> =
	w.first().split(",").map { Fish(it.toInt()) }.toMutableList()

private fun simulateOneDay(fishes: MutableList<Fish>): MutableList<Fish> {
	val fishChildren = mutableListOf<Fish>()
	fishes.forEach { it.liveAndBreed(fishChildren) }
	return fishChildren
}

class Fish(var cycleDay: Int) {
	fun liveAndBreed(newFish: MutableList<Fish>) {
		when (cycleDay) {
			0 -> {
				newFish.add(Fish(8))
				cycleDay = 6
			}
			else -> cycleDay--
		}
	}
}


