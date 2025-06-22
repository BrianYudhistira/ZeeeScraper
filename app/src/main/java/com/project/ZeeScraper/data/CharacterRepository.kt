package com.project.ZeeScraper.data

import com.project.ZeeScraper.retrofit.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CharacterRepository {
    private val apiService = NetworkModule.apiService

    suspend fun getAllCharacters(): Result<List<CharacterList>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllCharacters()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "success" && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception("API Error: ${body?.message ?: "Unknown error"}"))
                    }
                } else {
                    Result.failure(Exception("HTTP Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun getCharacterById(id: Int): Result<CharacterDetail> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getCharacterById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.status == "success" && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("API Error: ${body?.message ?: "Unknown error"}"))
                }
            } else {
                    Result.failure(Exception("HTTP Error: ${response.code()} ${response.message()}"))
                }
        }
    }
}

