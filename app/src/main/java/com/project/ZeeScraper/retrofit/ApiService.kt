package com.project.ZeeScraper.retrofit

import com.project.ZeeScraper.data.ApiResponse
import com.project.ZeeScraper.data.ApiResponseDetail
import com.project.ZeeScraper.data.CharacterDetail
import com.project.ZeeScraper.data.CharacterList
import retrofit2.Response
import retrofit2.http.*


interface ApiService{
    @POST("scrape/characters/")
    suspend fun getAllCharacters(): Response<ApiResponse<List<CharacterList>>>

    @POST("scrape/character/{id}/build/")
    suspend fun getCharacterById(@Path("id") id: Int): Response<ApiResponseDetail<CharacterDetail>>
}