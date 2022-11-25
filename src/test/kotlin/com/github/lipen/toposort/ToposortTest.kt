package com.github.lipen.toposort

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ToposortTest {
    private val edges = listOf(
        3 to 10, 3 to 8,
        5 to 11,
        7 to 8, 7 to 11,
        8 to 9,
        11 to 2, 11 to 9, 11 to 10
    )
    private val graph = run {
        val graph = mutableMapOf<Int, List<Int>>()
        for ((a, b) in edges) {
            // Note: edge (a,b) adds `b` to the adjacency list of `a`.
            graph.merge(a, listOf(b), List<Int>::plus)
        }
        graph
    }
    private val deps = run {
        val deps = mutableMapOf<Int, List<Int>>()
        for ((a, b) in edges) {
            // Note: edge (a,b) adds `a` to the dependency list of `b`.
            deps.merge(b, listOf(a), List<Int>::plus)
        }
        deps
    }

    @Test
    fun `test toposort using Kahn algorithm`() {
        println("Graph: $graph")
        val toposort = toposortKahn(graph).toList()
        println("Topological sort (${toposort.size}): $toposort")
        assertEquals(listOf(3, 5, 7, 8, 11, 2, 9, 10), toposort)
    }

    @Test
    fun `test toposort by layers`() {
        println("Dependencies: $deps")
        val layers = toposortLayers(deps).map { it.sorted() }.toList()
        println("Layers (${layers.size}):")
        for (layer in layers) {
            println("  - $layer")
        }
        assertEquals(listOf(listOf(3, 5, 7), listOf(8, 11), listOf(2, 9, 10)), layers)
    }
}
