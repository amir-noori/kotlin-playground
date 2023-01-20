package playground.arrow.core

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.computations.either
import arrow.core.computations.nullable
import arrow.core.flatMap


object Lettuce
object Knife
object Salad

interface Err
object NoKnife : Err
object NoLettuce : Err

object NullableLunch {
    private fun takeFoodFromRefrigerator(): Lettuce? = null
    private fun getKnife(): Knife? = null
    private fun prepare(tool: Knife, ingredient: Lettuce): Salad? = Salad

    fun prepareLunch(): Salad? {
        val lettuce = takeFoodFromRefrigerator()
        val knife = getKnife()
        return lettuce?.let {
            knife?.let {
                prepare(knife, lettuce)
            }
        }
    }

    suspend fun prepareLunch2(): Salad? =
        nullable {
            val lettuce = takeFoodFromRefrigerator().bind()
            val knife = getKnife().bind()
            val salad = prepare(knife, lettuce).bind()
            salad
        }
}

object FunctionalLunch {

    private fun takeFoodFromRefrigerator(): Either<Err, Lettuce> = Right(Lettuce)
    private fun getKnife(): Either<Err, Knife> = Left(NoKnife)
    private fun prepare(tool: Knife, ingredient: Lettuce): Salad = Salad

    fun prepareLunch(): Either<Err, Salad> {
        val lettuceEither = takeFoodFromRefrigerator()
        val knifeEither = getKnife()

        return lettuceEither.flatMap { l ->
            knifeEither.map { k ->
                prepare(k, l)
            }
        }
    }

    suspend fun prepareLunch2(): Either<Err, Salad> =
        either {
            val lettuce = takeFoodFromRefrigerator().bind()
            val knife = getKnife().bind()
            val salad = prepare(knife, lettuce)
            salad
        }
}


fun main(args: Array<String>) {
    val nullLunch = NullableLunch.prepareLunch()
    println("launch: $nullLunch")

    when (val lunch = FunctionalLunch.prepareLunch()) {
        is Left -> {
            when (lunch.value) {
                is NoKnife -> println("Go get a knife")
                is NoLettuce -> println("Go get a lettuce")
            }
        }
        is Right -> println("lunch is ready: ${lunch.value}")
    }

}




