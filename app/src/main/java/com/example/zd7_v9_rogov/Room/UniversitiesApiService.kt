package com.example.zd7_v9_rogov.Room

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UniversitiesApiService {

    @GET("search")
    suspend fun getUniversitiesByCountry(
        @Query("country") country: String = "Belarus"
    ): Response<List<UniversityApiResponse>>
}