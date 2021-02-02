package com.example.test1

data class APIInfo(val API: String, val Description: String, val Auth: String, val HTTPS: Boolean, val Cors: String, val Link: String, val Category: String)
data class Wrapper(val count: Int, val entries: List<APIInfo>)