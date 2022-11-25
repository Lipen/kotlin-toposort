package com.github.lipen.toposort

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun <T : Any> toposortLayers(deps: Map<out T, Iterable<T>>): Sequence<List<T>> = sequence {
    logger.debug { "Performing a topological sort by layers" }

    // Local mutable data:
    val data: MutableMap<T, MutableSet<T>> = mutableMapOf()

    // Add all deps to the map:
    for ((k, xs) in deps) {
        data[k] = xs.toMutableSet()
    }

    // Find all items without deps and add them explicitly to the map:
    for (item in deps.values.asSequence().flatten()) {
        if (item !in data) {
            data[item] = mutableSetOf()
        }
    }

    while (true) {
        // New layer is a list of items without dependencies:
        val layer = data.mapNotNull { (item, deps) ->
            if (deps.isEmpty()) item else null
        }

        // New layer can be empty in two cases:
        //  (1) `data` is empty (this is OK)
        //  (2) there is a circular dependency in `data` (we check for it outside the loop)
        if (layer.isEmpty()) break

        // Return non-empty layer:
        yield(layer)

        // Remove keys without deps (the last produced layer):
        data.keys.removeAll(layer)
        // Reduce deps:
        data.values.forEach { it.removeAll(layer) }
    }

    // If `data` is not empty, we have a cycle:
    check(data.isEmpty()) { "Circular dependency detected: $data" }
}
