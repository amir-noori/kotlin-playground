package playground.fp

class Ch03 {

    sealed class List<out A> {
        companion object {
            fun <A> of(vararg i: A): List<A> {
                val tail = i.sliceArray(1 until i.size)
                return if (i.isEmpty()) Nil else Cons(i[0], of(*tail))
            }

            fun sum(xs: List<Int>): Int = doWork(xs, 0) { x, acc -> x + acc }

            fun product(xs: List<Double>): Double = doWork(xs, 1.0) { x, acc -> x * acc }

            fun <A> doWork(xs: List<A>, initial: A, f: (x: A, acc: A) -> A): A {
                return when (xs) {
                    is Nil -> initial
                    is Cons -> f(xs.head, doWork(xs.tail, initial, f))
                }
            }
        }
    }

    object Nil : List<Nothing>()

    data class Cons<out A>(val head: A, val tail: List<A>) : List<A>()


    companion object {
        fun test() {
            val myList: List<String> = List.of("a", "b", "c")
            val myIntList: List<Int> = List.of(10, 20, 30)
            val myDoubleList: List<Double> = List.of(10.0, 20.0, 30.0)

            when (myList) {
                is Cons -> println("list head: ${myList.head}")
                is Nil -> print("empty list has no head")
            }
            println("sum ->: ${List.sum(myIntList)}")
            println("product ->: ${List.product(myDoubleList)}")
        }
    }
}


fun main() {
    Ch03.test()
}



