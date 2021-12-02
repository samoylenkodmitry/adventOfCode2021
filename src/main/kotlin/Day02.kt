package day01

import java.io.File
import kotlin.math.max

private val input = File("inputs/day02.txt").readLines()
	.asSequence()
	.map { it.split(" ") }.map { Command(it[0], it[1].toInt()) }

private val test = """
	forward 5
	down 5
	forward 8
	up 3
	down 8
	forward 2
""".trimIndent().splitToSequence("\n").map { it.trim() }
	.map { it.split(" ") }.map { Command(it[0], it[1].toInt()) }

class Command(val what: String, val s: Int)

fun main() {
	partOne(test)
	partOne(input)
	partTwo(test)
	partTwo(input)
}

private fun partOne(w: Sequence<Command>) {
	var h = 0
	var d = 0
	w.forEach {
		when (it.what) {
			"forward" -> h += it.s
			"up" -> d = max(0, d - it.s)
			"down" -> d += it.s
		}
	}
	println("h:$h d:$d mul:${h * d}")
}

private fun partTwo(w: Sequence<Command>) {
	var h = 0
	var d = 0
	var aim = 0
	w.forEach {
		when (it.what) {
			"forward" -> {
				h += it.s
				d += aim * it.s
			}
			"up" -> aim -= it.s
			"down" -> aim += it.s
		}
	}
	println(" h:$h d:$d aim: $aim mul: ${h * d}")
}
