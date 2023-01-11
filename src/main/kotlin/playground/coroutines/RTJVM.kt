package playground.coroutines

import kotlinx.coroutines.*


suspend fun takeBath() {
    println("start bath")
    delay(2000)
    println("bath is done")
}

suspend fun boilWater() {
    println("start boiling water")
    delay(2000)
    println("boiling done")
}

suspend fun haveBreakfast() {
    println("start eating")
    delay(1000)
    println("eating done")
}

suspend fun morningRoutinesSequential() {
    coroutineScope {
        takeBath()
        boilWater()
    }
}

suspend fun morningRoutinesParallel() {
    coroutineScope {
        launch { takeBath() }
        launch { boilWater() }
    }
}

suspend fun fullMorningRoutine() {
    coroutineScope {
        coroutineScope {
            launch { takeBath() }
            launch { boilWater() }
        }

        launch { haveBreakfast() }
    }
}

suspend fun fullMorningRoutineAsync() {
    coroutineScope {
        coroutineScope {
            val bath: Deferred<Unit> = async { takeBath() }
            val boil: Deferred<Unit> = async { boilWater() }

            bath.await()
            boil.await()
        }

        launch { haveBreakfast() }
    }
}

suspend fun main(args: Array<String>) {
    println("lets begin")
    fullMorningRoutineAsync()
    println("all good")
}
