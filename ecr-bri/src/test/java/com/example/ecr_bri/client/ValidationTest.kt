package com.example.ecr_bri.client

import org.junit.Test
import org.junit.Assert.*
import java.util.regex.Matcher

class ValidationTest {

    @Test
    fun `verify if given ip address format is valid`() {
        val ipAddress = "127.0.0.1"
        val matcher: Matcher = PatternUtils.IP_ADDRESS.matcher(ipAddress)
        assertEquals(true, matcher.matches())
    }

}