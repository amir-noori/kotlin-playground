package playground.fptutorials


/**
 *
 * A functor is basically a way to map one category in another while preserving the structure
 * So a functor must preserve:
 *  - Composition, which means that F (g ◦ f) = Fg ◦ Ff.
 *  - Identity, which means that F ia = i Fa
 *
 */

// Optional

sealed class Optional<out T> {

    companion object {
        fun <T> lift(value: T): Optional<T> = Some(value)

        fun <T> empty(): Optional<T> = None
    }
}

data class Some<T>(val value: T) : Optional<T>()
object None : Optional<Nothing>()

fun <A, B> Optional<A>.map(f: (A) -> B): Optional<B> {
    return when (this) {
        is Some<A> -> Optional.lift(f(value))
        is None -> Optional.empty()
    }
}


// Either

sealed class KEither<out L, out R>
class KRight<T>(val value: T) : KEither<Nothing, T>()
class KLeft<T>(val value: T) : KEither<T, Nothing>()

fun <L, R, L2> KEither<L, R>.mapL(f: (L) -> L2): KEither<L2, R> {
    return when (this) {
        is KRight<R> -> this
        is KLeft<L> -> KLeft(f(value))
    }
}

fun <L, R, R2> KEither<L, R>.mapR(f: (R) -> R2): KEither<L, R2> {
    return when (this) {
        is KRight<R> -> KRight(f(value))
        is KLeft<L> -> this
    }
}

fun <L, R, L2, R2> KEither<L, R>.map(f: (L) -> L2, g: (R) -> R2): KEither<L2, R2> {
    return this.mapL(f).mapR(g)
}




