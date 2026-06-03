package com.example.myattendanceapp.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserData
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

data class CheckInResponse(
    val message: String,
    val type: String,
    val time: String?,
    val status: String?
)

data class AttendanceItem(
    val id: Int,
    val user_id: Int,
    val date: String,
    val check_in_time: String?,
    val check_out_time: String?,
    val status: String
)

data class AttendanceListResponse(
    val attendances: List<AttendanceItem>
)

data class TodayResponse(
    val attendance: AttendanceItem?
)

data class LeaveItem(
    val id: Int,
    val user_id: Int,
    val reason: String,
    val start_date: String,
    val end_date: String,
    val status: String
)

data class LeaveListResponse(
    val leaves: List<LeaveItem>
)

data class LeaveRequest(
    val reason: String,
    val start_date: String,
    val end_date: String
)

data class LeaveResponse(
    val message: String
)

data class StatsResponse(
    val present: Int,
    val late: Int,
    val absent: Int,
    val leave: Int
)

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/checkin")
    suspend fun checkIn(@Header("Authorization") token: String): Response<CheckInResponse>

    @GET("api/attendance")
    suspend fun myAttendance(@Header("Authorization") token: String): Response<AttendanceListResponse>

    @GET("api/attendance/today")
    suspend fun today(@Header("Authorization") token: String): Response<TodayResponse>

    @POST("api/leave")
    suspend fun submitLeave(
        @Header("Authorization") token: String,
        @Body request: LeaveRequest
    ): Response<LeaveResponse>

    @GET("api/leave")
    suspend fun myLeaves(
        @Header("Authorization") token: String
    ): Response<LeaveListResponse>
}