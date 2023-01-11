class Person {
    val name: String? = "Márton"
    val surname: String = "Braun"
    val fullName: String?
        get() = name?.let { "$it $surname" }

    val fullName2: String? = name?.let { "$it $surname" }

    fun printInfo() {
        if (fullName != null) {
            // println(fullName.length) // ERROR
            println(fullName?.length) // ERROR
        }

        val a: Int

        if (fullName2 != null) {
            println(fullName2.length) // Márton Braun
        }
    }

}


fun main(args: Array<String>) {
    val person = Person()
    person.printInfo()
    println("item 01")
}
