package com.example.cappsule

import java.net.URL

const val apiKey = "d6ec42cf87a93d3d280eb8b3e98d8436"

fun getData(city: String, unit: String): String {
    return URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$unit&appid=$apiKey").readText(Charsets.UTF_8)
}