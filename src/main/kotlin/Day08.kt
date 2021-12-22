package day08

import java.io.File

private val input = File("inputs/day08.txt").readLines()
	.asSequence()

private val test = """
	be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
	edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
	fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
	fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
	aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
	fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
	dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
	bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
	egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
	gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
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
	val nums = intArrayOf(2, 3, 4, 7)
	println("count=" +
		w.sumOf {
			it
				.substringAfter("|")
				.split(" ").filter { it != "" }.count { it.length in nums }
		}
	)

}

private fun partTwo(w: Sequence<String>) {
	val sum = w.map { TaskAndTest(it) }
		.map { it.result }
		.sum()
	println("result : $sum")
}

private class TaskAndTest(text: String) {
	val result: Int

	init {
		val spl = text.split("|")
		val task = spl[0].split(" ").filter { it != "" }.map { Num(it) }
		val test = spl[1].split(" ").filter { it != "" }.map { Num(it) }
		val mapping = Mapping()
		mapping.analyze(task)

		result = test.map { mapping.decode(it.value) }.joinToString("") { it }.toInt()
		//println("curr result: " + result)
	}

}

private class Mapping {

	val segmentToChar = mutableMapOf<Char, Char>()

	fun analyze(task: List<Num>) {
		val counts = "abcdefg".associateWith { 0 }.toMutableMap()
		task.forEach {
			it.value.forEach { chr ->
				counts[chr] = counts[chr]!! + 1
			}
		}
		segmentToChar['e'] = counts.filter { it.value == 4 }.keys.first()
		segmentToChar['b'] = counts.filter { it.value == 6 }.keys.first()
		segmentToChar['f'] = counts.filter { it.value == 9 }.keys.first()
		segmentToChar['c'] = task.first { it.value.length == 2 }.value.replace(segmentToChar['f']!!.toString(), "").first()
	}

	fun decode(str: String) =
		when {
			zero(str) -> "0"
			one(str) -> "1"
			two(str) -> "2"
			three(str) -> "3"
			four(str) -> "4"
			five(str) -> "5"
			six(str) -> "6"
			seven(str) -> "7"
			eight(str) -> "8"
			nine(str) -> "9"
			else -> "[$str, map=${segmentToChar.map { it.key + " -> " + it.value }.joinToString { it }}]"
		}

	fun String.has(chr: Char) = this.contains(segmentToChar[chr]!!)
	fun String.has(chr1: Char, chr2: Char) = has(chr1) && has(chr2)
	fun String.has(chr1: Char, chr2: Char, chr3: Char) = has(chr1) && has(chr2) && has(chr3)
	fun String.not(chr: Char) = !has(chr)
	fun String.not(chr1: Char, chr2: Char) = not(chr1) && not(chr2)
	fun String.not(chr1: Char, chr2: Char, chr3: Char) = not(chr1) && not(chr2) && not(chr3)
	fun String.len(cmp: Int) = this.length == cmp

	/*
	 0:      1:      2:      3:      4:
 aaaa    ....    aaaa    aaaa    ....
b    c  .    c  .    c  .    c  b    c
b    c  .    c  .    c  .    c  b    c
 ....    ....    dddd    dddd    dddd
e    f  .    f  e    .  .    f  .    f
e    f  .    f  e    .  .    f  .    f
 gggg    ....    gggg    gggg    ....

  5:      6:      7:      8:      9:
 aaaa    aaaa    aaaa    aaaa    aaaa
b    .  b    .  .    c  b    c  b    c
b    .  b    .  .    c  b    c  b    c
 dddd    dddd    ....    dddd    dddd
.    f  e    f  .    f  e    f  .    f
.    f  e    f  .    f  e    f  .    f
 gggg    gggg    ....    gggg    gggg
 //known = e,b,f,c
	 */
	fun zero(inp: String) = inp.len(6) && inp.has('c', 'e') && inp.has('b', 'f')
	fun one(inp: String) = inp.len(2)
	fun two(inp: String) = inp.len(5) && inp.has('c', 'e') && inp.not('b', 'f')
	fun three(inp: String) = inp.len(5) && inp.has('c', 'f') && inp.not('e', 'b')
	fun four(inp: String) = inp.len(4)
	fun five(inp: String) = inp.len(5) && inp.has('b', 'f') && inp.not('e', 'c')
	fun six(inp: String) = inp.len(6) && inp.has('b', 'e', 'f') && inp.not('c')
	fun seven(inp: String) = inp.len(3)
	fun eight(inp: String) = inp.len(7)
	fun nine(inp: String) = inp.len(6) && inp.has('b', 'f', 'c') && inp.not('e')
}

private class Num(val value: String) {
	/*
 aaaa
b    c
b    c
 dddd
e    f
e    f
 gggg
	 */
}
