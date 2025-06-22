package com.project.zeescraper.retrofit

import com.project.zeescraper.data.ApiResponse
import com.project.zeescraper.data.ApiResponseDetail
import com.project.zeescraper.data.CharacterDetail
import com.project.zeescraper.data.CharacterList
import retrofit2.Response
import retrofit2.http.*


interface ApiService{
    @POST("scrape/characters/")
    suspend fun getAllCharacters(): Response<ApiResponse<List<CharacterList>>>

    @POST("scrape/character/{id}/build/")
    suspend fun getCharacterById(@Path("id") id: Int): Response<ApiResponseDetail<CharacterDetail>>
}