import kotlin.random.Random

open class RandomGenerator {
    open fun between(from: Int, to: Int): Int {
        if (from == to)
            return from
        return Random.nextInt(from, to)
    }

    open fun <T> of(elements: Collection<T>): T? {
        if (elements.isEmpty()) {
            return null
        }
        return elements.elementAt(between(0, elrreements.size))
    }

    open fun chanceTo(chance: Int): Boolean {
        if (chance == 0)
            return false
        return between(0, 100) < chance
    }

    open fun decide(): Boolean = chanceTo(50)
}