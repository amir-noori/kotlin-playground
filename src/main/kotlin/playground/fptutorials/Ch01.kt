package playground.fptutorials

// HOF

fun isValidNum(s: String): Boolean =
    try {
        s.toInt(); true
    } catch (e: NumberFormatException) {
        false
    }

fun sumInRange(values: List<String>, range: IntRange): Int =
    values
        .filter(::isValidNum)
        .map(String::toInt)
        .filter { range.contains(it) }
        .sum()

fun chrono(f: () -> Unit): Long {
    val from = System.currentTimeMillis()
    f()
    return System.currentTimeMillis() - from
}


// Composition


infix fun <A, B, C> ((A) -> B).compose(b: (B) -> C): (A) -> C = { b(this(it)) }

fun double(x: Int): Int = x * 2
fun square(x: Int): Int = x * x
fun doubleSquare(x: Int) = ::double compose ::square
fun squareDouble(x: Int) = ::square compose ::double


// Exception handling

fun steToInt(s: String): Result<Int> =
    try {
        Result.success(s.toInt())
    } catch (e: NumberFormatException) {
        Result.failure(e)
    }


fun main(args: Array<String>): Unit {
    chrono { sumInRange(listOf(""), 5..10) }
}






