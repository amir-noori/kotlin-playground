package playground.fptutorials

import kotlin.reflect.KProperty


// Lambda Inheritance

/**
 *      A → B
 *      ↓   ↑
 *      C → D
 *
 */
class ClassA : ClassC()
open class ClassB
open class ClassC
class ClassD : ClassB()

fun lambdaInheritance() {
    var lambda: (ClassA) -> ClassB = { a -> ClassB() }
    // lambdas inputs are contravariant and output are covariant
    lambda = { c: ClassC -> ClassD() }
}

// Lazy Evaluation

infix fun <A> ((A) -> Boolean).AND(predicate: (A) -> Boolean): (A) -> Boolean = { a ->
    if (this(a)) predicate(a)
    else false
}

fun lazyEval() {

    val isLagerThan10 = { i: Int ->
        println("isLagerThan10 is called")
        i > 10
    }

    val test = { i: Int ->
        println("test is called")
        true
    }

    println((isLagerThan10 AND test)(5))
    println((isLagerThan10 AND test)(15))

}


fun byDelegateTest() {

    data class X(val x: Int)

    var variable by object {
        var localValue: X? = null
        operator fun getValue(
            thisRef: Any?,
            property: KProperty<*>,
        ): X? {
            println("Getter Invoked returning $localValue")
            return localValue
        }

        operator fun setValue(
            thisRef: Any?,
            property: KProperty<*>,
            value: X?,
        ) {
            println("Setter Invoked with value $value")
            localValue = value
        }
    }

    variable = X(10)
    println("variable is: $variable")

}

fun lazyDelegateTest() {
    val data = 20
    val lazyData by lazy {
        val x = 10
        println("value $x will be returned")
        x
    }

    if (data > 100 && lazyData == 10) {
        println("condition evaluated")
    } else {
        println("less than 100")
    }
}

fun <A> myLazy(fn: () -> A): () -> A = { fn() }

fun myLazyDelegateTest() {
    val data = 20
    val lazyData = myLazy {
        val x = 10
        println("value $x will be returned")
        x
    }

    if (data > 100 && lazyData() == 10) {
        println("condition evaluated")
    } else {
        println("less than 100")
    }
}


// Memoization


fun fib(n: Int): Int {
    return if (n == 0 || n == 1) n
    else fib(n - 1) + fib(n - 2)
}

fun <A, B> ((A) -> B).memoize(): (A) -> B {
    val cache by lazy { mutableMapOf<A, B>() }
    return { a: A ->
        if (cache.containsKey(a)) {
            val value = cache[a]!!
            println("key $a is cached: $value")
            value
        } else {
            println("computing for $a")
            val b = this(a)
            cache[a] = b
            b
        }
    }
}

fun testMemoize() {
    val fibMemoized = ::fib.memoize()
    fibMemoized(0)
    fibMemoized(1)
    fibMemoized(2)
    fibMemoized(1)
}

// Lazy Stream

fun eagerEvenIntegers(n: Int): List<Int> = List(n) { i -> i * 2 }

fun lazyIntegers(): () -> Int {
    var cache = 0
    return { cache++ }
}

fun testStreams() {

    println(eagerEvenIntegers(10))

    val ints = lazyIntegers()
    println(ints())
    println(ints())
    println(ints())
}


fun <A> myNullableLazy(fn: () -> A?): () -> A? {
    var evaluated = false // HERE
    var result: A? = null
    return { ->
        if (!evaluated) {
            evaluated = true
            result = fn()
        }
        result
    }
}

fun testMyNullableLazy() {
    val myNullableLazy: () -> Int? = myNullableLazy {
        println("I'm nullable lazy!")
        null
    }
    repeat(10) {
        println(myNullableLazy())
    }
}

fun main() {

}


