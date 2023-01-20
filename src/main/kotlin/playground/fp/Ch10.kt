package playground.fp

import arrow.core.None
import arrow.core.Option
import arrow.core.orElse

interface Monoid<A> {
    fun combine(a: A, b: A): A
    val nil: A
}

val stringMonoid = object : Monoid<String> {
    override fun combine(a: String, b: String): String = a + b
    override val nil: String = ""
}

val intMonoid = object : Monoid<Int> {
    override fun combine(a: Int, b: Int): Int = a + b
    override val nil: Int = 0
}

fun <A> listMonoid(): Monoid<List<A>> = object : Monoid<List<A>> {
    override fun combine(a: List<A>, b: List<A>): List<A> = a + b
    override val nil: List<A> = emptyList()
}

fun intAddition(): Monoid<Int> {
    return object : Monoid<Int> {
        override fun combine(a: Int, b: Int): Int = a + b
        override val nil: Int = 0
    }
}

fun intMultiplication(): Monoid<Int> {
    return object : Monoid<Int> {
        override fun combine(a: Int, b: Int): Int = a * b
        override val nil: Int = 1
    }
}

fun booleanOr(): Monoid<Boolean> {
    return object : Monoid<Boolean> {
        override fun combine(a: Boolean, b: Boolean): Boolean = a || b
        override val nil: Boolean = false
    }
}

fun booleanAnd(): Monoid<Boolean> {
    return object : Monoid<Boolean> {
        override fun combine(a: Boolean, b: Boolean): Boolean = a && b
        override val nil: Boolean = true
    }
}

fun <A> optionMonoid(): Monoid<Option<A>> {
    return object : Monoid<Option<A>> {
        override fun combine(a: Option<A>, b: Option<A>): Option<A> = a.orElse { b }
        override val nil: Option<A> = None
    }
}

fun <A> dual(m: Monoid<A>): Monoid<A> = object : Monoid<A> {
    override fun combine(a: A, b: A): A = m.combine(b, a)
    override val nil: A = m.nil
}

fun <A> endoMonoid(): Monoid<(A) -> A> = object : Monoid<(A) -> A> {
    override fun combine(a: (A) -> A, b: (A) -> A): (A) -> A = { x: A -> a(b(x)) }
    override val nil: (A) -> A = { x -> x }
}



