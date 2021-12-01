package day01

import java.io.File

val input = File("inputs/day01.txt").readLines().map { it.toInt() }
val test = """
    199
    200
    208
    210
    200
    207
    240
    269
    260
    263
""".trimIndent().splitToSequence("\n").map { it.trim() }.map { it.toInt() }

fun main() {
	println("for test: " + test
		.windowed(2)
		.count { (a, b) -> b > a }
	)
	println(
		input
			.windowed(2)
			.count { (a, b) -> b > a }
	)
	//part two
	println("for test: " + test
		.windowed(3)
		.map { it.sum() }
		.windowed(2)
		.count { (a, b) -> b > a }
	)
	println(
		input
			.windowed(3)
			.map { it.sum() }
			.windowed(2)
			.count { (a, b) -> b > a }
	)
}