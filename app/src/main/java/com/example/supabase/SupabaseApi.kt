package com.example.supabase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class SupabaseHabitDto(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "title") val title: String,
    @Json(name = "category") val category: String,
    @Json(name = "completed_today") val completedToday: Boolean,
    @Json(name = "current_streak") val currentStreak: Int,
    @Json(name = "star_reward") val starReward: Int,
    @Json(name = "icon_key") val iconKey: String,
    @Json(name = "external_tracker") val externalTracker: String? = null,
    @Json(name = "external_metric") val externalMetric: String? = null,
    @Json(name = "updated_at") val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class SupabaseTodoDto(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "name") val name: String,
    @Json(name = "is_completed") val isCompleted: Boolean = false,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseScreenTimeTargetDto(
    @Json(name = "user_id") val userId: String,
    @Json(name = "target_hours") val targetHours: Int,
    @Json(name = "target_minutes") val targetMinutes: Int,
    @Json(name = "updated_at") val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class SupabaseWalletDto(
    @Json(name = "user_id") val userId: String,
    @Json(name = "balance") val balance: Int,
    @Json(name = "lifetime_stars") val lifetimeStars: Int,
    @Json(name = "updated_at") val updatedAt: String
)

interface SupabaseRestApi {

    // Habits endpoints
    @GET("rest/v1/habits")
    suspend fun getHabits(
        @Query("user_id") userIdFilter: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseHabitDto>>

    @POST("rest/v1/habits")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun upsertHabits(
        @Body habits: List<SupabaseHabitDto>
    ): Response<Unit>

    // Todos endpoint (matching user code snippet)
    @GET("rest/v1/todos")
    suspend fun getTodos(
        @Query("select") select: String = "*"
    ): Response<List<SupabaseTodoDto>>

    @POST("rest/v1/todos")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun upsertTodos(
        @Body todos: List<SupabaseTodoDto>
    ): Response<Unit>

    // Screen Time Targets
    @GET("rest/v1/screen_time_targets")
    suspend fun getScreenTimeTarget(
        @Query("user_id") userIdFilter: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseScreenTimeTargetDto>>

    @POST("rest/v1/screen_time_targets")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun upsertScreenTimeTarget(
        @Body target: SupabaseScreenTimeTargetDto
    ): Response<Unit>

    // Wallet State
    @GET("rest/v1/wallets")
    suspend fun getWallet(
        @Query("user_id") userIdFilter: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseWalletDto>>

    @POST("rest/v1/wallets")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun upsertWallet(
        @Body wallet: SupabaseWalletDto
    ): Response<Unit>
}
