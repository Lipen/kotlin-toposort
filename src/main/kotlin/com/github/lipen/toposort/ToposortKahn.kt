package com.github.lipen.toposort

private val logger = mu.KotlinLogging.logger {}

fun <T : Any> toposortKahn(graph: Map<T, List<T>>): Sequence<T> = sequence {
    logger.debug { "Performing a topological sort using Khan algorithm" }

    // In-degree of each vertex:
    val indegree: MutableMap<T, Int> = mutableMapOf()

    // Compute in-degree:
    for ((item, ds) in graph) {
        indegree.putIfAbsent(item, 0)
        for (d in ds) {
            indegree.merge(d, 1, Int::plus)
        }
    }

    // Traversal queue:
    val queue = ArrayDeque<T>()

    // Initially, put into queue all vertices without incoming edges:
    for ((item, degree) in indegree) {
        if (degree == 0) {
            queue.add(item)
        }
    }

    // Number of produced elements:
    var cnt = 0

    while (queue.isNotEmpty()) {
        val x = queue.removeFirst()
        ++cnt
        yield(x)

        for (d in graph[x] ?: emptyList()) {
            indegree[d] = indegree[d]!! - 1
            if (indegree[d]!! == 0) {
                queue.add(d)
            }
        }
    }

    // If not all elements were emitted yet, we have a cycle:
    check(cnt == indegree.size) { "Cycle of size ${indegree.size - cnt} detected" }
}
