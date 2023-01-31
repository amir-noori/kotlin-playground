package playground.fptutorials


fun <A> id(a: A): A = a

fun <A, B, C> ((A, B) -> C).curry(): (A) -> (B) -> C {
    return { a -> { b -> this(a, b) } }
}


