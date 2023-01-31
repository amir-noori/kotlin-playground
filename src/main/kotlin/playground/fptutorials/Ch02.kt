package playground.fptutorials


// Category Theory

/**
 *
 * a category follow these rules:
 *
 * 1- Composition
 * 2- Associativity
 * 3- Identity
 *
 */


/**
 *
 *     Composition
 *
 */

fun <A, B, C> `gof`(
    f: (A) -> B,
    g: (B) -> C,
): (A) -> C = { a -> g(f(a)) }


/**
 *
 *     Associativity
 *
 */

fun <A, B, C, D> `h◦(g◦f)`(
    f: (A) -> B,
    g: (B) -> C,
    h: (C) -> D,
): (A) -> D = { a -> h(gof(f, g)(a)) }

fun <A, B, C, D> `(h◦g)◦f`(
    f: (A) -> B,
    g: (B) -> C,
    h: (C) -> D,
): (A) -> D = { a -> gof(g, h)(f(a)) }

fun <A, B, C, D> checkAssociativityLaw(
    f: (A) -> B,
    g: (B) -> C,
    h: (C) -> D,
): (A) -> Boolean = { a ->
    `h◦(g◦f)`(f, g, h)(a) == `(h◦g)◦f`(f, g, h)(a)
}


/**
 *
 *     Identity
 *
 */


fun <A, B> idLaw1(f: (A) -> B): (A) -> Boolean = { a ->
    /*
        the problem with ::id is that it is not specific for any type, like what if type A is a monoid
        and the identity function for each monoid type would be different for example:
            monoid on (integers, *) is id(a) = a * 1
            monoid on (integers, +) is id(a) = a + 0
    */
    checkAssociativityLaw(::id, f, ::id)(a)
}

interface Identity<A> {
    fun id(a: A): A
}

fun <A, B> idLaw2(f: (A) -> B): (A) -> Boolean
        where A : Identity<A>, B : Identity<B> = { a ->
    checkAssociativityLaw(a::id, f, f(a)::id)(a)
}

// if we can provide the inverse function then the next identity law is the strongest.
fun <A, B> ((A) -> B).inverse(): (B) -> A {
    TODO()
}

fun <A, B> idLaw3(f: (A) -> B): (A) -> Boolean
        where A : Identity<A>, B : Identity<B> = { a ->
    checkAssociativityLaw(a::id, f, f(a)::id)(a) && checkAssociativityLaw(f(a)::id, f.inverse(), f(a)::id)(f(a))
}



/**
 *
 *    Category of types and functions
 *
 */


// Fun is a morphism from A to B
typealias Fun<A, B> = (A) -> B

inline infix fun <A, B, C> Fun<B, C>.after(crossinline f: Fun<A, B>): Fun<A, C> =
    { a -> this(f(a)) }



/**
 *
 *    Types and Sets
 *
 */

data class EVEN(val i: Int) {
    fun next(): EVEN {
        return EVEN(i + 2)
    }
}

val TWO = EVEN(2)
val FOUR = TWO.next()
val SIX = FOUR.next()




fun main() {
    fun double(x: Int): Int = x * 2
    fun triple(x: Int): Int = x * 3
    fun square(x: Int): Int = x * x

    val theLaw: (Int) -> Boolean = checkAssociativityLaw(::double, ::square, ::triple)

    for (i in 1..10) println("the law for $i -> ${theLaw(i)}")

}




