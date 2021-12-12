package day01

import java.io.File
import kotlin.math.abs

private val input = File("inputs/day07.txt").readLines()
	.asSequence()

private val test = """
	16,1,2,0,4,2,7,1,2,14
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
	val nums = nums(w)
	var min = Int.MAX_VALUE
	for (i in 0..nums.lastIndex) {
		var fuel = 0
		for (j in 0..nums.lastIndex) {
			if (i != j) {
				fuel += abs(nums[i] - nums[j])
			}
		}
		if (fuel < min) min = fuel
	}
	println("min: $min")
}

private fun partTwo(w: Sequence<String>) {
	val nums = nums(w)
	val smallest = nums.minOf { it }
	val highest = nums.maxOf { it }
	var min = Int.MAX_VALUE
	var minJ = -1
	for (j in smallest..highest) {
		var fuel = 0
		for (i in 0..nums.lastIndex) {
			fuel += fuel2(abs(nums[i] - j))
		}
		if (fuel < min) {
			min = fuel
			minJ = j
		}
	}
	println("min: $min minJ: $minJ")
}

fun fuel2(n: Int): Int {
	//1 2 3 4 5
	//1  =1, n=1
	//1 2 =3, n=2
	//1 2 3 = 6, n=3
	//1 2 3 4 = 10, n=4 f(4)=f(3)+4
	//f(n)=n*(n+1)/2
	return n * (n + 1) / 2
}

private fun nums(w: Sequence<String>): List<Int> = w.first().splitToSequence(",").map { it.toInt() }.toList()




