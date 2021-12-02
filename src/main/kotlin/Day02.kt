package day01

import java.io.File

private val input = File("inputs/day02.txt").readLines()
	.asSequence()
	.map { it.split(" ") }

private val test = """
	forward 5
	down 5
	forward 8
	up 3
	down 8
	forward 2
""".trimIndent().splitToSequence("\n").map { it.trim() }
	.map { it.split(" ") }

fun <Context> List<String>.newCommand(f: Exe<Context>, u: Exe<Context>, d: Exe<Context>): Command<Context> {
	val s = this[1].toInt()
	return when (this[0]) {
		"forward" -> Command(s, f)
		"up" -> Command(s, u)
		else -> Command(s, d)
	}
}

fun interface Exe<Context> {
	fun exe(s: Int, ctx: Context)
}

class Command<Context>(val s: Int, val f: Exe<Context>) {
	fun exe(c: Context) = f.exe(s, c)
}

fun main() {
	partOne(test)
	partOne(input)
	partTwo(test)
	partTwo(input)
}

private fun partOne(w: Sequence<List<String>>) {
	data class Ctx1(var h: Int, var d: Int)

	val f = Exe { s: Int, ctx: Ctx1 -> ctx.h += s }
	val u = Exe { s: Int, ctx: Ctx1 -> ctx.d -= s }
	val d = Exe { s: Int, ctx: Ctx1 -> ctx.d += s }
	val ctx = Ctx1(0, 0)
	w
		.map { it.newCommand(f, u, d) }
		.forEach { it.exe(ctx) }

	println("h:${ctx.h} d:${ctx.d} mul:${ctx.h * ctx.d}")
}

private fun partTwo(w: Sequence<List<String>>) {
	data class Ctx2(var h: Int, var d: Int, var aim: Int)

	val f = Exe { s: Int, ctx: Ctx2 ->
		ctx.apply {
			h += s
			d += aim * s
		}
	}
	val u = Exe { s: Int, ctx: Ctx2 -> ctx.aim -= s }
	val d = Exe { s: Int, ctx: Ctx2 -> ctx.aim += s }
	val ctx = Ctx2(0, 0, 0)
	w
		.map { it.newCommand(f, u, d) }
		.forEach { it.exe(ctx) }

	println("h:${ctx.h} d:${ctx.d} mul:${ctx.h * ctx.d}")
}
