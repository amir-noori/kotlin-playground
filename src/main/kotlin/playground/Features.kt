package playground

import arrow.continuations.Effect
import arrow.continuations.generic.DelimitedScope
import arrow.core.*
import arrow.core.computations.EitherEffect
import arrow.core.computations.RestrictedEitherEffect
import arrow.core.computations.either
import arrow.fx.coroutines.CircuitBreaker
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import arrow.optics.optics
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime


// making the following object as a callable block
object callable {
    public inline operator fun invoke(i: Int, f: (i: Int) -> String) {
        println("calling ${f(i)}")
    }
}

fun testCallable() {
    callable(10) { i ->
        ""
    }
}


// extension method for any type
//fun <A, E> A.ensure(predicate: (A) -> Boolean, result: (s: String) -> E): E =
//    if (predicate(this)) result("OK.") else result("OOPS!")
//
//class Order(val id: Int) {
//
//    fun test() {
//        ensure({ o -> o.id == 100 }, { s -> s.length })
//    }
//
//}

@JvmInline
value class ProductId(val value: String)

@optics
data class Entry(val id: ProductId, val amount: Int) {
    companion object // required by @optics

    val asPair: Pair<String, Int>
        get() = Pair(id.value, amount)
}

@optics
data class Order(val entries: List<Entry>) {
    companion object // required by @optics
}

fun Order.flatten(): Order = Order(
    entries
        .groupBy(Entry::id)
        .map { (id, entries) -> Entry(id, entries.sumOf(Entry::amount)) }
)


enum class BillingResponse {
    OK, USER_ERROR, SYSTEM_ERROR
}

interface Billing {
    suspend fun processBilling(order: Map<String, Int>): BillingResponse
}

fun Billing.withBreaker(circuitBreaker: CircuitBreaker, retries: Int): Billing =
    BillingWithBreaker(this, circuitBreaker, retries)


@OptIn(ExperimentalTime::class)
private class BillingWithBreaker(
    private val underlying: Billing,
    private val circuitBreaker: CircuitBreaker,
    private val retries: Int,
) : Billing {
    override suspend fun processBilling(order: Map<String, Int>): BillingResponse =
        Schedule.recurs<BillingResponse>(retries)
            .zipRight(Schedule.doWhile { it == BillingResponse.SYSTEM_ERROR })
            .repeat {
                Schedule.recurs<Throwable>(retries)
                    .and(Schedule.exponential(20.milliseconds))
                    .retry {
                        circuitBreaker.protectOrThrow {
                            underlying.processBilling(order)
                        }
                    }
            }
}


enum class ValidateStructureProblem {
    EMPTY_ORDER,
    TOO_MUCH
}

suspend fun validateStructure(order: Order): ValidatedNel<ValidateStructureProblem, Order> =
    either.invoke<Nel<ValidateStructureProblem>, Order> {
        ensure(order.entries.isNotEmpty()) { ValidateStructureProblem.EMPTY_ORDER.nel() }
        ensure(order.entries.size > 10) { ValidateStructureProblem.TOO_MUCH.nel() }
        order.flatten()
    }.toValidated()


suspend fun validateStructure2(): ValidatedNel<Int, String> {

    val r: Either<NonEmptyList<Int>, String> = either.invoke<Nel<Int>, String> {
        2.nel()
        2.nel()
        ""
    }

    return r.toValidated()
}


public object either {
    public inline fun <E, A> eager(crossinline c: suspend RestrictedEitherEffect<E, *>.() -> A): Either<E, A> =
        Effect.restricted(eff = { RestrictedEitherEffect { it } }, f = c, just = { it.right() })

    public suspend inline operator fun <E, A> invoke(crossinline c: suspend EitherEffect<E, *>.() -> A): Either<E, A> =
        Effect.suspended(eff = { it: DelimitedScope<Either<E, A>> ->
            object : EitherEffect<E, A> {
                override fun control(): DelimitedScope<Either<E, A>> {
                    return it
                }
            }
        }, f = c, just = { it.right() })
}


/**
 * infix pipe function
 * example: listOf(1, 2, 3) pipe ::println
 */
infix fun <T> Iterable<T>.pipe(f: (T) -> Unit) =
    this.forEach { f(it) }




