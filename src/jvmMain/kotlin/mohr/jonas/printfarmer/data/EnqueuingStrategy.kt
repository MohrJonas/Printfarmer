package mohr.jonas.printfarmer.data

enum class EnqueuingStrategy {
    SPECIFIC,
    RANDOM,
    SHORTEST_QUEUE;

    companion object {
        fun EnqueuingStrategy.name() = when (this) {
            SPECIFIC -> "Specific Printer"
            RANDOM -> "Random Printer"
            SHORTEST_QUEUE -> "Shortest Queue"
        }
    }
}