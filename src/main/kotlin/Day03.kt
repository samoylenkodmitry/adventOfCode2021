package day01

import java.io.File

private val input = File("inputs/day03.txt").readLines()
	.asSequence()

private val test = """
00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010
""".trimIndent().splitToSequence("\n").map { it.trim() }

fun main() {
	partOne(test)
	partOne(input)
	partTwo(test)
	partTwo(input)
}

private fun partOne(w: Sequence<String>) {
	val len = w.first().length
	val r = freq(w, len)
	var e = 0
	var g = 0
	val big = big(r, len)

	r.forEachIndexed { index, v ->
		if (index < len) {
			val isCommon = v >= big
			if (isCommon) {
				e += 1 shl (len - index - 1)
			} else {
				g += 1 shl (len - index - 1)
			}
		}
	}
	println("arr: ${r.joinToString(",")} e:${e.toString(2)} g:${g.toString(2)} eg:${e * g}")
}

private fun big(r: Array<Int>, len: Int): Int {
	val big = if (r[len] % 2 == 0) r[len] / 2 else 1 + r[len] / 2
	return big
}

private fun freq(w: Sequence<String>, len: Int): Array<Int> {
	val r = w.fold(Array(len + 1) { 0 }, operation = { acc, s ->
		acc[len]++ //size
		s.forEachIndexed { index, c ->
			acc[index] += c - '0'
		}
		acc
	})
	return r
}

private fun partTwo(w: Sequence<String>) {
	val len = w.first().length
	var x = w
	var y = w
	for (bit in 0 until len) {
		x = x.filterByBit(bit) { a, b -> a >= b }
		y = y.filterByBit(bit) { a, b -> a < b }
	}
	val ox = x.first()
	val co = y.first()

	println("ox: $ox co: $co ox: ${ox.toInt(2)}, co: ${co.toInt(2)}, mul: ${ox.toInt(2) * co.toInt(2)}")
}

fun Sequence<String>.filterByBit(index: Int, compare: (Int, Int) -> Boolean): Sequence<String> {
	val counts = this.map { it[index] }.groupingBy { it }.eachCount()
	if (counts.size <= 1) {
		return this
	}
	return if (compare(counts.getOrDefault('1', 0), counts.getOrDefault('0', 0))) {
		this.filter { it[index] == '1' }
	} else {
		this.filter { it[index] == '0' }
	}
}
