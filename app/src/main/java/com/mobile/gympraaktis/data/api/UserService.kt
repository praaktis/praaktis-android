package com.mobile.gympraaktis.data.api

import com.mobile.gympraaktis.domain.entities.*
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {

    @GET("user/getDashboardData/")
    fun getDashboardData(): Single<DashboardDTO>

    @GET("user/getProfile/")
    fun getProfile(): Single<UserDTO>

    @GET("user/TimelineData/")
    fun getTimelineData(): Single<TimelineDTO>

    @GET("user/logout/")
    fun logout(): Single<ResponseBody>

    @GET("user/getDetailResults/{attempt_id}/")
    fun getDetailResult(
        @Path("attempt_id") attempt_id: Int
    ): Single<List<DetailScoreDTO>>

    @POST("user/storeResults/")
    fun storeResult(
        @Body storeResult: StoreResultDTO
    ): Single<ResponseBody>

    @GET("getChallenges/")
    fun getChallenges(): Single<List<ChallengeDTO>>

    @GET("user/getComparison")
    fun getComparison(): Single<ComparisonDTO>

    @POST("user/inviteFriend/")
    fun inviteFriend(
        @Body inviteFriend: InviteFriendRequest
    ): Single<UserMessage>

    @POST("user/deleteFriend/")
    fun deleteFriend(
        @Body deleteFriendRequest: DeleteFriendRequest
    ): Single<ResponseBody>

    @POST("user/confirmFriend/")
    fun confirmFriend(
        @Body confirmFriendRequest: ConfirmFriendRequest
    ): Single<ResponseBody>

    @GET("user/getFriends/")
    fun getFriends(): Single<List<FriendDTO>>

    @GET("user/getFriendRequests/")
    fun getFriendRequest(): Single<List<FriendDTO>>

    @POST("user/refuseFriend/")
    fun refuseFriend(
        @Body request: DeleteFriendRequest
    ): Single<ResponseBody>

    @POST("user/registerDevice/")
    fun registerDevice(
        @Body request: RegisterDeviceDTO
    ): Single<ResponseBody>
}