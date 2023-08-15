package com.example.ecr_bri.client

import java.util.regex.Matcher

object Validation {

    fun isIpAddressValid(ipAddress: String) : Boolean {
        val matcher: Matcher = PatternUtils.IP_ADDRESS.matcher(ipAddress)
        if (matcher.matches()) {
            return true
        }
        return false
    }
}