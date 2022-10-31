package playground.fp

class Ch02 {

    companion object {
        fun factorial(i: Int): Int {
            tailrec fun go(currentInteger: Int, result: Int): Int {
                return if (currentInteger == 1) result
                else go(currentInteger - 1, result * currentInteger)
            }

            if (i <= 1) return 1
            return go(i, 1)
        }
    }
}

object Example1 {
    private fun abs(n: Int): Int =
        if (n < 0) -n
        else n

    private fun factorial(i: Int): Int {
        fun go(n: Int, acc: Int): Int =
            if (n <= 0) acc
            else go(n - 1, n * acc)
        return go(i, 1)
    }

    fun <A> format(name: String, data: A, f: (A) -> A): String {
        return "The $name of $data is ${f(data)}."
    }

    fun formatAbs(x: Int): String {
        return format("absolute", x, ::abs)
    }

    fun formatFactorial(x: Int): String {
        return format("absolute", x, ::factorial)
    }

    fun formatSquare(x: Int): String {
        return format("absolute", x) { i: Int -> i * i }
    }
}


fun main() {

}