package day18

import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.ceil

private val input = File("inputs/day18.txt").readLines().asSequence()

private val test = """
	[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
	[[[5,[2,8]],4],[5,[[9,9],0]]]
	[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
	[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
	[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
	[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
	[[[[5,4],[7,7]],8],[[8,3],8]]
	[[9,3],[[9,9],[6,[4,9]]]]
	[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
	[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
""".trimIndent().splitToSequence("\n").map { it.trim() }

suspend fun main() = coroutineScope {
	init()
	partOne(test)
	partOne(input)
	partTwo(test)
	partTwo(input)
}

private fun init() {
}

private fun partOne(w: Sequence<String>) {
	val sn = w
		.map { parse(it) }
		.reduce { acc, v -> acc + v }
		as SN.N
	sn.reduce()
	println("fsum: $sn")
	println("magnitude: ${sn.magnitude()}")
}

private suspend fun partTwo(w: Sequence<String>) {
	val list = w.map { parse(it) }.toList()
	val max = sequence {
		repeat(list.size) { i ->
			repeat(list.size) { j ->
				if (i != j) yield(Pair(i, j))
			}
		}
	}
		.maxOf {
			(list[it.first] + list[it.second]).magnitude()
		}

	println("max:$max")
}

/**
 * [1,0]
 * [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
 */
private fun parse(
	s: String,
	outPos: AtomicInteger = AtomicInteger(-1),
	outPrev: AtomicReference<SN.V?> = AtomicReference(null)
): SN = with(s[outPos.incrementAndGet()]) {
	when {
		Character.isDigit(this) -> {
			var num = this - '0'
			while (Character.isDigit(s[outPos.incrementAndGet()])) {
				num = num * 10 + (s[outPos.get()] - '0')
			}
			SN.V(num).apply {
				connectPrev(outPrev.getAndSet(this))
			}
		}
		this == '[' -> SN.N(parse(s, outPos, outPrev), parse(s, outPos, outPrev))
		else -> parse(s, outPos, outPrev)
	}
}

sealed class SN {
	class V(var v: Int, var prev: V? = null, var next: V? = null) : SN() {
		override fun magnitude() = v
		override fun print() = v.toString()

		fun connectPrev(prev: V?) {
			prev?.let { it.next = this }
			this.prev = prev
		}

		override fun leftmostV(): V = this
		override fun rightmostV(): V = this
		override fun mustSplit() = v > 9

		override fun splitV() = N(V(v / 2), V(ceil(v.toDouble() / 2.0).toInt())).also {
			it.rightmostV().connectPrev(it.leftmostV())
			it.leftmostV().connectPrev(prev)
			next?.connectPrev(it.rightmostV())
		}

		@Suppress("unused")
		private fun printNext(): String = next?.let { "$v->${it.printNext()}" } ?: "$v"

		@Suppress("unused")
		private fun printPrev(): String = prev?.let { "${it.printPrev()}<-$v" } ?: "$v"
	}

	class N(var left: SN, var right: SN) : SN() {

		override fun magnitude() = 3 * left.magnitude() + 2 * right.magnitude()
		override fun print() = "[$left,$right]"
		override fun leftmostV(): V = left.leftmostV()
		override fun rightmostV(): V = right.rightmostV()

		override fun findNodeToExplodeParent(depth: Int, parent: N?): N? {
			if (depth > 3 && (left is V && right is V)) return parent
			left.findNodeToExplodeParent(depth + 1, this)?.let { return it }
			return right.findNodeToExplodeParent(depth + 1, this)
		}

		override fun findNodeToSplitParent(): N? {
			if (left.mustSplit()) return this
			left.findNodeToSplitParent()?.let { return it }
			if (right.mustSplit()) return this
			return right.findNodeToSplitParent()
		}

		private fun splitN(): Boolean {
			val splitParent = findNodeToSplitParent() ?: return false
			if (splitParent.left.mustSplit()) {
				var prev = (splitParent.left as V).prev
				while (prev != null) {
					if (prev.v > 9) throw Error("$prev")
					prev = prev.prev
				}
				splitParent.left = splitParent.left.splitV()
			} else {
				var prev = (splitParent.right as V).prev
				while (prev != null) {
					if (prev.v > 9) throw Error("$prev")
					prev = prev.prev
				}
				splitParent.right = splitParent.right.splitV()
			}
			return true
		}

		private fun explode(): Boolean {
			val explodeParent = findNodeToExplodeParent() ?: return false
			val newV = V(0)
			val explodedNode = when (explodeParent.left) {
				is N -> {
					explodeParent.left.also { explodeParent.left = newV }
				}
				else -> {
					explodeParent.right.also { explodeParent.right = newV }
				}
			}
			explodedNode.leftmostV().apply {
				prev?.let { it.v += v }
				newV.connectPrev(prev)
			}
			explodedNode.rightmostV().apply {
				next?.let { it.v += v }
				next?.connectPrev(newV)
			}
			return true
		}

		fun reduce() {
			while (explode() || splitN()) Unit
		}

	}

	abstract fun magnitude(): Int
	abstract fun print(): String
	abstract fun leftmostV(): V
	abstract fun rightmostV(): V

	operator fun plus(other: SN): SN {
		val thisClone = this.clone()
		val otherClone = other.clone()
		otherClone.leftmostV().connectPrev(thisClone.rightmostV())
		return N(thisClone, otherClone).apply { reduce() }
	}

	private fun clone(): SN = parse(print()) //I'm sorry

	open fun mustSplit(): Boolean = false
	open fun findNodeToExplodeParent(depth: Int = 0, parent: N? = null): N? = null
	open fun findNodeToSplitParent(): N? = null
	open fun splitV(): N = throw Error()
	override fun toString() = print()
}