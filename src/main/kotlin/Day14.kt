package day14

import java.io.File

private val input = File("inputs/day14.txt").readLines().asSequence()

private val test = """
	NNCB

	CH -> B
	HH -> N
	CB -> H
	NH -> C
	HB -> C
	HC -> B
	HN -> C
	NN -> C
	BH -> H
	NC -> B
	NB -> B
	BN -> B
	BB -> N
	BC -> B
	CC -> N
	CN -> C
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
	simulate(w, 10)
}

private fun partTwo(w: Sequence<String>) {
	simulate(w, 40)
}

private data class Rule(val ft: String, val ins: Char) {
	fun make(rules: Map<String, Rule>): Collection<Rule> = buildList {
		rules["${ft[0]}$ins"]?.let { add(it) }
		rules["$ins${ft[1]}"]?.let { add(it) }
	}
}

private data class Simulation(val steps: Int, val what: Rule)

private fun simulate(w: Sequence<String>, steps: Int) {
	val rules = getRules(w)

	val cache = mutableMapOf<Simulation, LongArray>()

	val counts = initialRules(w.first(), rules)
		.map { simulate(Simulation(steps, it), cache, rules) }
		.fold(LongArray(26), { acc, ints -> acc.sum(ints) })
		.sum(initialCounts(w.first()))

	val max = counts.maxOf { it }
	val min = counts.filter { it > 0 }.minOf { it }
	println("max=$max min=$min diff=${max - min}")
}

private fun simulate(
	simulation: Simulation,
	cache: MutableMap<Simulation, LongArray>,
	rules: Map<String, Rule>
): LongArray {
	val otherSimulations = ArrayDeque<Simulation>()

	val counts = LongArray(26)
	val queue = ArrayDeque<Rule>()
	queue.addLast(simulation.what)
	repeat(simulation.steps) { step ->
		val sz = queue.size
		repeat(sz) {
			val rule = queue.removeFirst()
			val c = rule.ins
			counts[c - 'A']++
			rule.make(rules).forEach { newRule ->
				val newSimulation = Simulation(simulation.steps - step - 1, newRule)
				val cached = cache[newSimulation]
				if (cached == null)
					otherSimulations.addLast(newSimulation)
				else
					counts.sum(cached)

			}
		}
	}
	while (otherSimulations.isNotEmpty()) {
		counts.sum(simulate(otherSimulations.removeFirst(), cache, rules))
	}
	cache[simulation] = counts
	return counts
}

private fun LongArray.sum(cached: LongArray): LongArray {
	for (i in 0..25) this[i] += cached[i]
	return this
}


private fun initialCounts(what: String) =
	LongArray(26).apply {
		what.forEach {
			this[it - 'A']++
		}
	}

private fun initialRules(
	what: String,
	rules: Map<String, Rule>
): ArrayDeque<Rule> {
	val queue = ArrayDeque<Rule>()
	what.asSequence().windowed(2)
		.forEach {
			val c1 = it[0]
			val c2 = it[1]
			val key = "$c1$c2"
			rules[key]?.let { queue.addLast(it) }
		}
	return queue
}

private fun LongArray.print() = asSequence()
	.withIndex()
	.filter { it.value > 0 }
	.map {
		"${Char(it.index + 'A'.code)}:${it.value}"
	}
	.joinToString(",")

private fun getRules(w: Sequence<String>): Map<String, Rule> =
	w
		.drop(2)
		.map { it.split(" -> ") }
		.map { Rule(it[0], it[1][0]) }
		.associateBy { it.ft }

@Suppress("unused")
private fun naiveImplementation(w: Sequence<String>, steps: Int, tmp: String) {
	val rules = getRules(w)

	val counts = initialCounts(tmp)
	val queue = initialRules(tmp, rules)
	repeat(steps) {
		val sz = queue.size
		repeat(sz) {
			val rule = queue.removeFirst()
			val c = rule.ins
			counts[c - 'A']++
			queue.addAll(rule.make(rules))
		}
	}
	println(
		"counts: ${counts.print()}"
	)
	val max = counts.maxOf { it }
	val min = counts.filter { it > 0 }.minOf { it }
	println("max=$max min=$min diff=${max - min}")
}
