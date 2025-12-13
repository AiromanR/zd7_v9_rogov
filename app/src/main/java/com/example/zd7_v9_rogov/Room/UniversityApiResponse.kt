package com.example.zd7_v9_rogov.Room

data class UniversityApiResponse(
    val name: String,
    val web_pages: List<String>,
    val domains: List<String>? = null,
    val alpha_two_code: String? = null,
    val country: String,
    val state_province: String? = null
)