package playground.fptutorials

import kotlin.random.Random


/**
 *
 *  Monoid properties:
 *      a op (b op c) = (a op b) op c
 *      a op unit = unit op a = a
 *
 */

interface Monoid<A> {
    fun unit(): A
    fun combine(a: A, b: A): A
}

object IntAdditionMonoid : Monoid<Int> {
    override fun unit(): Int = 0
    override fun combine(a: Int, b: Int): Int = a + b
}

object IntMultiplicationMonoid : Monoid<Int> {
    override fun unit(): Int = 1
    override fun combine(a: Int, b: Int): Int = a * b
}

object StringConcatMonoid : Monoid<String> {
    override fun unit(): String = ""
    override fun combine(a: String, b: String): String = a + b
}


/**
 *
 *   Property Based Testing
 *
 *
 */


/**
 * to do the property based testing on sum first the sum rules should be derived:
 *  sum is commutative:
 *      a + b = b + a // combination
 *      a + 0 = a // unit
 *      a + 2x = (a + x) + x
 *
 *
 */
fun sum(a: Int, b: Int): Int = a + b


fun interface Generator<T> {
    fun generate(n: Int): List<T>
}

object IntGenerator : Generator<Int> {
    override fun generate(n: Int): List<Int> {
        return List(n) { Random.nextInt() }
    }
}

interface Property<T> {
    operator fun invoke(
        generator: Generator<T>,
        f: (List<T>) -> T,
    ): Boolean
}

infix fun <T> Property<T>.and(p: Property<T>) = object : Property<T> {
    override fun invoke(
        generator: Generator<T>,
        f: (List<T>) -> T,
    ): Boolean = this@and(generator, f) && p(generator, f)
}

class IdentityProperty<T>(
    private val unit: T,
) : Property<T> {
    override fun invoke(
        generator: Generator<T>,
        f: (List<T>) -> T,
    ): Boolean {
        val value: T = generator.generate(1)[0]
        return f(listOf(value, unit)) == f(listOf(unit, value)) == f(listOf(value))
    }
}

class CommutativeProperty<T> : Property<T> {
    override fun invoke(
        generator: Generator<T>,
        f: (List<T>) -> T,
    ): Boolean {
        val values: List<T> = generator.generate(2)
        return f(listOf(values[0], values[1])) == f(listOf(values[1], values[0]))
    }
}

class AssociativeProperty<T> : Property<T> {
    override fun invoke(
        generator: Generator<T>,
        f: (List<T>) -> T,
    ): Boolean {
        val values: List<T> = generator.generate(2)
        val r1 = f(
            listOf(
                f(listOf(values[0], values[1])),
                values[2]
            )
        )
        val r2 = f(
            listOf(
                values[0],
                f(listOf(values[1], values[2]))
            )
        )
        return r1 == r2
    }
}


