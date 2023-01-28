package playground.fpkotlin

class Ch03 {

    sealed class List<out A> {

        companion object {
            fun <A> of(vararg i: A): List<A> {
                val tail = i.sliceArray(1 until i.size)
                return if (i.isEmpty()) Nil else Cons(i[0], of(*tail))
            }

            fun <A> empty(): List<A> = Nil

            fun <A> tail(l: List<A>): List<A> =
                when (l) {
                    is Cons -> l.tail
                    is Nil -> Nil
                }

            fun <A> setHead(l: List<A>, h: A): List<A> =
                when (l) {
                    is Cons -> Cons(h, l.tail)
                    is Nil -> Cons(h, Nil)
                }

            tailrec fun <A> drop(l: List<A>, n: Int): List<A> {
                if (n <= 0) return l
                return when (l) {
                    is Cons -> drop(l.tail, n - 1)
                    is Nil -> Nil
                }
            }

            tailrec fun <A> dropWhile(l: List<A>, f: (A) -> Boolean): List<A> {
                return when (l) {
                    is Nil -> Nil
                    is Cons -> {
                        if (f(l.head)) l
                        else dropWhile(l.tail, f)
                    }
                }
            }

            fun sumR(xs: List<Int>): Int = foldRight(xs, 0) { x, acc -> x + acc }

            fun productR(xs: List<Double>): Double = foldRight(xs, 1.0) { x, acc -> x * acc }

            fun sumL(xs: List<Int>): Int = foldLeft(xs, 0) { x, acc -> x + acc }

            fun productL(xs: List<Double>): Double = foldLeft(xs, 1.0) { x, acc -> x * acc }

            fun <A> reverse(xs: List<A>): List<A> = foldLeft(xs, empty()) { x, l: List<A> -> Cons(x, l) }

            /**
             * This is not tail recursive and not stack safe!
             *
             * sum of List(1, 2, 3, 4) ->
             *      f(1, foldRight(List(2, 3, 4), 0, +))
             *      f(2, foldRight(List(3, 4), 0, +))
             *      f(3, foldRight(List(4), 0, +))
             *      f(4, foldRight(Nil, 0, +)) -> actual computation (+) is started here which will produce stackoverflow
             *                                    for large lists.
             *
             *      f(10, foldRight((20, 30), 0, f))
             *      f(20, foldRight((30), 0, f))
             *      f(30, foldRight(, 0, f))
             *      sumR ->: 60
             *
             */
            fun <A, B> foldRight(xs: List<A>, acc: B, f: (A, B) -> B): B {
                return when (xs) {
                    is Nil -> acc
                    is Cons -> {
                        println("f(${xs.head}, foldRight(${xs.tail}, $acc, f))")
                        f(xs.head, foldRight(xs.tail, acc, f))
                    }
                }
            }

            fun <A, B> foldRightUsingFoldLeft(xs: List<A>, acc: B, f: (A, B) -> B): B {
                return foldLeft(xs, acc, f)
            }

            fun <A, B> foldLeftUsingFoldRight(xs: List<A>, acc: B, f: (A, B) -> B): B {
                return foldRight(xs, acc, f)
            }

            fun sumX(xs: List<Int>): Int = foldRightUsingFoldLeft(xs, 0) { x, acc -> x + acc }

            fun sumY(xs: List<Int>): Int = foldLeftUsingFoldRight(xs, 0) { x, acc -> x + acc }

            /**
             * This a tail recursive function
             *
             * sum of List(1, 2, 3, 4) ->
             *      foldLeft(List(2, 3, 4), f(1, 0, +)) -> computation starts from here
             *      foldLeft(List(3, 4), f(2, 1, +))
             *      foldLeft(List(4), f(3, 3, +))
             *      foldLeft(Nil, f(4, 6, +))
             *
             *
             *      foldLeft((20, 30), f(10, 0), f)
             *      foldLeft((30), f(20, 10), f)
             *      foldLeft(, f(30, 30), f)
             *      sumL ->: 60
             *
             */
            tailrec fun <A, B> foldLeft(xs: List<A>, acc: B, f: (A, B) -> B): B {
                return when (xs) {
                    is Nil -> acc
                    is Cons -> {
                        println("foldLeft(${xs.tail}, f(${xs.head}, $acc), f)")
                        foldLeft(xs.tail, f(xs.head, acc), f)
                    }
                }
            }

            fun <A> prepend(a: A, l: List<A>): List<A> {
                return Cons(a, l)
            }

            fun <A> append(a: A, l: List<A>): List<A> {
                return foldLeft(l, of(a)) { x, y -> Cons(x, y) }
            }

            fun <A> concat(l: List<List<A>>): List<A> {
                return Nil // TODO
            }



        }
    }

    object Nil : List<Nothing>() {
        override fun toString(): String = ""
    }

    data class Cons<out A>(val head: A, val tail: List<A>) : List<A>() {
        override fun toString(): String {

            tailrec fun str(l: List<A>, result: String): String {
                return when (l) {
                    is Nil -> result
                    is Cons -> {
                        str(l.tail, "$result, ${l.head}")
                    }
                }
            }

            return "(" + str(this, "").removePrefix(", ") + ")"
        }
    }


    companion object {
        fun test() {
            val myList: List<String> = List.of("a", "b", "c")

//            val asc = Array(1000) { i -> i }
            val myIntList: List<Int> = List.of(10, 20, 30)

            val myDoubleList: List<Double> = List.of(10.0, 20.0, 30.0)

//            when (myList) {
//                is Cons -> println("list head: ${myList.head}")
//                is Nil -> print("empty list has no head")
//            }

//            println("sumX ->: ${List.sumX(myIntList)}")
//            println("sumL ->: ${List.sumL(myIntList)}")
//
//            println("sumY ->: ${List.sumY(myIntList)}")
//            println("sumR ->: ${List.sumR(myIntList)}")

            val list1 = List.append(22, myIntList)
            val list2 = List.prepend(11, myIntList)

            println("list1 ->: $list1")
            println("list2 ->: $list2")


//            println("sumR ->: ${List.sumR(myIntList)}")
//            println("sumL ->: ${List.sumL(myIntList)}")
//            println("product ->: ${List.productR(myDoubleList)}")
        }
    }
}


fun main() {
    Ch03.test()
}



