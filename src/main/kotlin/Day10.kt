package day01

import java.io.File
import java.util.*

private val input = File("inputs/day10.txt").readLines()
	.asSequence()

private val test = """
	[({(<(())[]>[[{[]{<()<>>
	[(()[<>])]({[<{<<[]>>(
	{([(<{}[<>[]}>{[]{[(<()>
	(((({<>}<{<{<>}{[]{[]{}
	[[<[([]))<([[{}[[()]]]
	[{[{({}]{}}([{[{{{}}([]
	{<[[]]>}<{[{[{[]{()[[[]
	[<(<(<(<{}))><([]([]()
	<{([([[(<>()){}]>(<<{{
	<{([{{}}[<[[[<>{}]]]>[]]
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
	val total = w.sumOf {
		val c = findErrChar(it)
		//println("for $it err expected ${c.first}, found ${c.second}")
		points(c.second)
	}

	println(total)
}

private fun partTwo(w: Sequence<String>) {

	val result = w.map {
		val suffix = findContinue(it)
		val lineValue = suffix
			.asSequence()
			.fold(0L) { acc, chr ->
				acc * 5L + points2(chr)
			}
		//println("for $it suffix $suffix score $lineValue")
		lineValue
	}
		.filter { it > 0 }
		.sorted()
		.toList()
		.let { it[it.size / 2] }

	println(result)
}

fun findErrChar(inp: String): Pair<Char, Char> {
	val st = Stack<Char>()

	for (i in 0..inp.lastIndex) {
		val c = inp[i]
		if (st.isNotEmpty()) {
			if (invalid(st.peek(), c)) return Pair(expected(st.peek()), c)
			if (closed(st.peek(), c)) st.pop()
			else st.push(c)
		} else {
			st.push(c)
		}
	}
	return Pair('x', 'x')
}

fun findContinue(inp: String): String {
	val st = Stack<Char>()

	for (i in 0..inp.lastIndex) {
		val c = inp[i]
		if (st.isNotEmpty()) {
			if (invalid(st.peek(), c)) return ""
			if (closed(st.peek(), c)) st.pop()
			else st.push(c)
		} else {
			st.push(c)
		}
	}
	return buildString {
		while (st.isNotEmpty()) {
			append(expected(st.pop()))
		}
	}
}

/*
    ): 3 points.
    ]: 57 points.
    }: 1197 points.
    >: 25137 points.
 */
fun points(c: Char) = when (c) {
	')' -> 3
	']' -> 57
	'}' -> 1197
	'>' -> 25137
	'x' -> 0
	else -> throw Error("can't be a reward $c")
}

/*

    ): 1 point.
    ]: 2 points.
    }: 3 points.
    >: 4 points.

 */
fun points2(c: Char) = when (c) {
	')' -> 1
	']' -> 2
	'}' -> 3
	'>' -> 4
	'x' -> 0
	else -> throw Error("can't be a reward $c")
}

fun invalid(c1: Char, c2: Char): Boolean =
	(c2 == ']' && c1 != '[') ||
		(c2 == '}' && c1 != '{') ||
		(c2 == ')' && c1 != '(') ||
		(c2 == '>' && c1 != '<')

fun closed(c1: Char, c2: Char): Boolean =
	(c2 == ']' && c1 == '[') ||
		(c2 == '}' && c1 == '{') ||
		(c2 == ')' && c1 == '(') ||
		(c2 == '>' && c1 == '<')

fun expected(c: Char): Char = when (c) {
	'(' -> ')'
	'[' -> ']'
	'{' -> '}'
	'<' -> '>'
	else -> throw Error("can't be $c")
}

