package day15

import java.io.ByteArrayOutputStream
import java.io.File

private val input = File("inputs/day16.txt").readLines().asSequence()

private val test = """
	D2FE28
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
	println(readPackets(w).sumOf { it.versionSum() })
}

private fun partTwo(w: Sequence<String>) {
	println(readPackets(w).sumOf { it.calc() })
}

private fun readPackets(w: Sequence<String>): List<Packet> = buildList {
	with(
		Parser(
			ByteArrayOutputStream().apply {
				w.first().forEach {
					write(it.digitToInt(16))
				}
			}.toByteArray()
		)
	) {
		while (!finished()) {
			mark()
			add(Packet(this))
			skipToPad4()
		}
	}
}

private class Packet(parser: Parser) {

	val subPackets = mutableListOf<Packet>()
	val version: Int
	val typeId: Int
	var value = -1L
	var lenTypeId = -1
	var bitsLen = -1
	var numSub = -1

	init {
		try {
			version = parser.read3()
			typeId = parser.read3()
			if (typeId == 4) {
				value = 0L
				while (true) {
					value = value shl 4
					val bits5 = parser.read5()
					val isEnd = (bits5 and (1 shl 4)) == 0
					val mask4 = (1 shl 4) - 1
					value = value or (bits5 and mask4).toLong()
					if (isEnd) {
						//end
						break
					}
				}
			} else {
				lenTypeId = parser.read1()
				when (lenTypeId) {
					0 -> {
						bitsLen = parser.read15()
						var bits = 0
						while (bits < bitsLen) {
							val posStart = parser.pos()
							subPackets.add(Packet(parser))
							bits += parser.pos() - posStart
						}
					}
					1 -> {
						numSub = parser.read11()
						repeat(numSub) {
							subPackets.add(Packet(parser))
						}
					}
				}
			}
		} catch (e: Throwable) {
			throw Error("can't continue to parse $this", e)
		}
	}

	override fun toString(): String {
		return "Packet(subPackets=$subPackets, version=$version, typeId=$typeId, value=$value, lenTypeId=$lenTypeId, bitsLen=$bitsLen, numSub=$numSub)"
	}

	fun calc(): Long = when (typeId) {
		0 -> subPackets.sumOf { it.calc() }
		1 -> subPackets.fold(1) { acc, packet -> acc * packet.calc() }
		2 -> subPackets.minOf { it.calc() }
		3 -> subPackets.maxOf { it.calc() }
		4 -> value
		5 -> if (subPackets[0].calc() > subPackets[1].calc()) 1 else 0
		6 -> if (subPackets[0].calc() < subPackets[1].calc()) 1 else 0
		7 -> if (subPackets[0].calc() == subPackets[1].calc()) 1 else 0
		else -> 0
	}

	fun versionSum(): Int = version + subPackets.sumOf { it.versionSum() }
}

private class Parser(private val bytes: ByteArray) {
	private val bitString by lazy {
		bytes.joinToString("") { it.toInt().toString(2).padStart(4, '0') }
	}
	private var curr = bytes[0].toInt()
	private val endPos = bytes.size * 4
	private var bytePos = 0//0..size
	private var bitPos = 0//0..3 0123
	private var totalPos = 0
	private var markPos = -1

	fun pos() = totalPos

	fun finished() = bytePos == bytes.size || totalPos >= endPos

	fun mark() {
		if (markPos != -1) throw Error("forget to skip?")
		markPos = totalPos
	}

	fun skipToPad4() {
		if (markPos != -1) {
			val skipCount = 4 - (totalPos - markPos) % 4
			if (skipCount < 4)
				readNBits(skipCount)
		}
		markPos = -1
	}

	fun read1() = readNBits(1)
	fun read3() = readNBits(3)
	fun read5() = (read4() shl 1) or read1()
	fun read11() = (read8() shl 2) or read3()
	fun read15() = (read8() shl 7) or read7()

	private fun read4() = readNBits(4)
	private fun read7() = (read4() shl 3) or read3()
	private fun read8() = (read4() shl 4) or read4()

	private fun buf() {
		if (bitPos == 4) {
			bitPos = 0
			bytePos++
			curr = bytes[bytePos].toInt()
		}
		if (bytePos * 4 + bitPos != totalPos)
			throw Error("wrong pos: $bytePos $bitPos $totalPos")
	}

	fun print(): String = buildString {
		appendLine()
		appendLine(bitString)
		repeat(totalPos) { append('^') }
	}

	private fun readNBits(n: Int): Int {
		buf()
		if (finished()) {
			throw Error("can't be finished here $bitPos $bytePos $totalPos $endPos $n ${bytes.size} ${print()}")
		}
		if (bitPos + n > 4) {
			//0123
			//   ^^^
			//   b  b
			bytePos++
			if (bytePos == bytes.size) {
				throw Error("can't be here $bitPos $bytePos $totalPos $endPos $n ${bytes.size} ${print()}")
			}
			val next = bytes[bytePos].toInt()
			val bits1Count = 4 - bitPos
			val bits2Count = n - bits1Count
			val mask1 = (1 shl bits1Count) - 1
			val bits1 = curr and mask1
			val bits2 = next shr (4 - bits2Count)
			bitPos = bits2Count
			curr = next
			totalPos += n
			return (bits1 shl bits2Count) or bits2
		} else {
			totalPos += n
			bitPos += n
			//0123
			// ^^^
			// b  b
			val mask = (1 shl n) - 1
			return (curr shr (4 - bitPos)) and mask
		}
	}
}
