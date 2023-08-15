package com.example.ecrintegrationapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun retryConnection() = runBlocking {
        startTask()
            .retry(3) {
                println("retrying...")
                delay(2000)
                it is IndexOutOfBoundsException
            }.catch {
                print(it.toString())
            }
            .collect {
                println(it)
            }
    }

    private fun startTask(): Flow<Int> {
        return flow {
            for (i in 1..4) {
                val randomInt = (0..2).random()
                if (randomInt == 0) {
                    throw IndexOutOfBoundsException()
                } else if (randomInt == 2) {
                    throw  IOException()
                }
                emit(i)
            }
        }.flowOn(Dispatchers.IO)
    }
}