package day17

import java.io.File

private val input = File("inputs/day17.txt").readLines().asSequence()

private val test = """
	target area: x=20..30, y=-10..-5
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
	val xy = with(Regex("x=(-*\\d+)\\.\\.(-*\\d+), y=(-*\\d+)\\.\\.(-*\\d+)").find(w.first())!!) {
		println(groupValues)
		R(
			groupValues[1].toInt()..groupValues[2].toInt(),
			groupValues[3].toInt()..groupValues[4].toInt()
		)
	}
	println(xy)
	var styleY = 0
	var styleVx = 0
	var styleVy = 0
	var successCount = 0
	for (vxs in 1..1500)
		for (vys in -1500..1500) {
			var vx = vxs
			var vy = vys
			var x = 0
			var y = 0
			var ymax = 0
			var success = false
			while (x <= xy.dx.last && y >= xy.dy.first && !success) {
				x += vx
				y += vy
				if (y > ymax) ymax = y
				if (vx > 0) vx--
				vy--
				if (x in xy.dx && y in xy.dy) success = true
			}
			if (success) {
				successCount++
				if (ymax > styleY) {
					styleY = ymax
					styleVx = vxs
					styleVy = vys
				}
			}
		}
	println("vx: $styleVx vy: $styleVy h: $styleY count: $successCount")
}

private fun partTwo(w: Sequence<String>) {
	//see the first part $successCount
}

private data class R(val dx: IntRange, val dy: IntRange)

