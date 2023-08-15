package com.example.ecr_bri

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testCopyObject() {
        val configuration1 = Configuration("123456", 12345)
        val configuration2 = configuration1.copy("11111")
        print(configuration2.port)
    }
}