package com.mobile.gympraaktis.data.api

import com.mobile.gympraaktis.domain.entities.*
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface UserService {

    @GET("user/getDashboardData/")
    fun getDashboardData(): Single<DashboardDTO>

    @GET("user/getProfile/")
    fun getProfile(): Single<UserDTO>

    @GET("user/TimelineData/")
    fun getTimelineData(): Single<TimelineDTO>

    @GET("user/getAttemptHistory/")
    suspend fun getAttemptHistory(
        @Query("page") page: Int,
        @Query("player_id") playerId: Long? = null
    ): AttemptHistoryResponse

    @GET("user/logout/")
    fun logout(): Single<ResponseBody>

    @GET("user/getDetailResults/{attempt_id}/")
    fun getDetailResult(
        @Path("attempt_id") attempt_id: Int
    ): Single<List<DetailScoreDTO>>

    @POST("user/storeResults/")
    fun storeResult(
        @Body storeResult: StoreResultModel
    ): Single<ResponseBody>

    @POST("user/storeResults/")
    suspend fun storeResultCoroutines(
        @Body storeResult: StoreResultModel
    ): ResponseBody

    @GET("getRoutines/")
    fun getChallenges(): Single<List<ChallengeDTO>>

    @GET("user/getComparison/{player_id}")
    fun getComparison(
        @Path("player_id") playerId: Long,
    ): Single<ComparisonDTO>

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

    @POST("user/createPlayer/")
    fun createPlayer(
        @Body playerModel: PlayerCreateModel
    ): Single<ResponseBody>

    @GET("getHeights/")
    fun fetchHeightOptions(): Single<List<KeyValueDTO>>

    @GET("getWeights/")
    fun fetchWeightOptions(): Single<List<KeyValueDTO>>

    @GET("getAbilities/")
    fun fetchAbilityOptions(): Single<List<KeyValueDTO>>

    @GET("getAgeGroups/")
    fun fetchAgeOptions(): Single<List<KeyValueDTO>>

    @GET("getGenders/")
    fun fetchGenderOptions(): Single<List<GenderDTO>>

}