package com.tendertool.app.src

import com.tendertool.app.models.*
import retrofit2.http.*

interface APIService
{
    //GET all tenders
    @GET("tender/fetch")
    suspend fun fetchTenders(): List<BaseTender>

    //GET a single tender
    @GET("tender/fetch/{id}")
    suspend fun fetchTenderByID(@Path("id") id: String): BaseTender

    //GET watchlist for a specific user
    @GET("watchlist/{user}")
    suspend fun getWatchlist(@Path("user") userID: String): List<BaseTender>

    @POST("watchlist/togglewatch/{user}/{id}")
    suspend fun toggleWatchlist(@Path("user") userID: String, @Path("id") id: String): BaseTender
}