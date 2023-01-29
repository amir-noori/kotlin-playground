package playground.fptutorials

sealed class FList<out T> {
    companion object {
        @JvmStatic
        fun <T> of(vararg items: T): FList<T> {
            val tail = items.sliceArray(1 until items.size)
            return if (items.isEmpty()) empty() else FCons(items[0], of(*tail))
        }

        @JvmStatic
        fun <T> empty(): FList<T> = Nil
    }
}

object Nil : FList<Nothing>()
data class FCons<T>(
    val head: T,
    val tail: FList<T> = Nil,
) : FList<T>()

fun <T> fListOf(vararg items: T): FList<T> {
    return FList.of(*items)
}

fun <T> FList<T>.size(): Int = when (this) {
    is Nil -> 0
    is FCons<T> -> 1 + tail.size()
}

fun <T, S> FList<T>.match(
    whenNil: () -> S,
    whenCons: (head: T, tail: FList<T>) -> S,
) = when (this) {
    is Nil -> whenNil()
    is FCons<T> -> whenCons(head, tail)
}

tailrec fun <T> FList<T>.forEach(f: (T) -> Unit) {
    when (this) {
        is FCons<T> -> {
            f(head); tail.forEach(f)
        }
        is Nil -> {}
    }
}

fun <T> FList<T>.each(f: (T) -> Unit) {
    this.match(whenNil = {}, whenCons = { head, tail ->
        f(head)
        tail.each(f)
    })
}


