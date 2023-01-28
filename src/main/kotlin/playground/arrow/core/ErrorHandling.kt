package playground.arrow.core

import arrow.core.*
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.computations.either
import arrow.core.computations.nullable
import arrow.typeclasses.Semigroup

/**
 *
 * functional error handling
 *
 */

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


/**
 *
 * error handling strategy
 *
 */
sealed class ErrorHandlingStrategy {
    object FailFast : ErrorHandlingStrategy()
    object AccumulateError : ErrorHandlingStrategy()
}

sealed class ValidationError(message: String) {
    data class DoesNotContain(val text: String) : ValidationError("does not contain $text")
    data class MaxLength(val length: Int) : ValidationError("exceeds length $length")
    data class NotEmail(val reasons: Nel<ValidationError>) : ValidationError("invalid email")
}

data class FormField(val value: String)
data class EmailField(val value: String)

object Rules {

    private fun FormField.contains(text: String): ValidatedNel<ValidationError, FormField> {
        return if (!this.value.contains(text))
            ValidationError.DoesNotContain(text).invalidNel()
        else validNel()
    }

    private fun FormField.maxLength(length: Int): ValidatedNel<ValidationError, FormField> {
        return if (this.value.length > length)
            ValidationError.MaxLength(length).invalidNel()
        else validNel()
    }

    private fun FormField.validateErrorAccumulate(): ValidatedNel<ValidationError, EmailField> =
        contains("@").zip(
            Semigroup.nonEmptyList(),
            maxLength(250)
        ) { _, _ -> EmailField(value) }.handleErrorWith { ValidationError.NotEmail(it).invalidNel() }


    private fun FormField.validateFailFast(): Either<Nel<ValidationError>, EmailField> =
        either.eager {
            contains("@").bind()
            maxLength(250).bind()
            EmailField(value)
        }

    operator fun invoke(strategy: ErrorHandlingStrategy, fields: List<FormField>):
            Either<Nel<ValidationError>, List<EmailField>> =
        when (strategy) {
            ErrorHandlingStrategy.FailFast ->
                fields.traverseEither { it.validateFailFast() }
            ErrorHandlingStrategy.AccumulateError ->
                fields.traverseValidated(Semigroup.nonEmptyList()) {
                    it.validateErrorAccumulate()
                }.toEither()
        }
}






