package com.mobile.gympraaktis.data.repository

import com.mobile.gympraaktis.BuildConfig
import com.mobile.gympraaktis.data.api.UserService
import com.mobile.gympraaktis.domain.common.ASyncTransformer
import com.mobile.gympraaktis.domain.common.Constants.createService
import com.mobile.gympraaktis.domain.entities.*
import com.mobile.gympraaktis.ui.subscription_plans.vm.UpdatePurchaseBody
import io.reactivex.Single
import okhttp3.ResponseBody

interface UserServiceRepository {

    fun getDashboardData(): Single<DashboardDTO>
    fun getProfile(): Single<UserDTO>
    fun getPlayerProfile(playerId: Long): Single<PlayerDTO>
    fun getTimelineData(): Single<TimelineDTO>
    suspend fun getAttemptHistory(page: Int, playerId: Long? = null): AttemptHistoryResponse
    fun getDetailResult(attemptId: Int): Single<List<DetailScoreDTO>>
    fun storeResult(storeResultModel: StoreResultModel): Single<ResponseBody>
    suspend fun storeResultCoroutines(storeResultModel: StoreResultModel): ResponseBody
    fun getChallenges(): Single<List<ChallengeDTO>>
    fun getComparison(playerId: Long): Single<ComparisonDTO>
    fun inviteFriend(email: String): Single<UserMessage>
    fun confirmFriend(email: String): Single<ResponseBody>
    fun getFriends(): Single<List<FriendDTO>>
    fun getFriendRequest(): Single<List<FriendDTO>>
    fun deleteFriend(email: String): Single<ResponseBody>
    fun refuseFriend(email: String): Single<ResponseBody>
    fun registerDevice(deviceId: String): Single<ResponseBody>
    fun logout(): Single<ResponseBody>
    fun createPlayer(playerCreateModel: PlayerCreateModel): Single<ResponseBody>
    fun updatePlayer(playerCreateModel: PlayerUpdateModel): Single<ResponseBody>
    fun fetchHeightOptions(): Single<List<KeyValueDTO>>
    fun fetchWeightOptions(): Single<List<KeyValueDTO>>
    fun fetchAbilityOptions(): Single<List<KeyValueDTO>>
    fun fetchAgeOptions(): Single<List<KeyValueDTO>>
    fun fetchGenderOptions(): Single<List<GenderDTO>>
    fun updatePurchase(planId: Int): Single<ResponseBody>

    class UserServiceRepositoryImpl : UserServiceRepository {

        companion object {
            var INSTANCE: UserServiceRepository? = null
            fun getInstance(): UserServiceRepository {
                if (INSTANCE == null) INSTANCE =
                    UserServiceRepositoryImpl()
                return INSTANCE!!
            }
        }

        var userService: UserService = createService()

        override fun getDashboardData(): Single<DashboardDTO> {
            return userService.getDashboardData().compose(ASyncTransformer<DashboardDTO>())
        }

        override fun getProfile(): Single<UserDTO> {
            return userService.getProfile().compose(ASyncTransformer<UserDTO>())
        }

        override fun getPlayerProfile(playerId: Long): Single<PlayerDTO> {
            return userService.fetchPlayerProfile(playerId);
        }

        override fun getTimelineData(): Single<TimelineDTO> {
            return userService.getTimelineData().compose(ASyncTransformer<TimelineDTO>())
        }

        override suspend fun getAttemptHistory(page: Int, playerId: Long?): AttemptHistoryResponse {
            return userService.getAttemptHistory(page, playerId)
        }

        override fun getDetailResult(attemptId: Int): Single<List<DetailScoreDTO>> {
            return userService.getDetailResult(attemptId)
                .compose(ASyncTransformer<List<DetailScoreDTO>>())
        }

        override fun storeResult(storeResultModel: StoreResultModel): Single<ResponseBody> {
            return userService.storeResult(storeResultModel)
                .compose(ASyncTransformer<ResponseBody>())
        }

        override suspend fun storeResultCoroutines(storeResultModel: StoreResultModel): ResponseBody {
            return userService.storeResultCoroutines(storeResultModel)
        }

        override fun getChallenges(): Single<List<ChallengeDTO>> {
            return userService.getChallenges(appId = BuildConfig.APPLICATION_ID).compose(ASyncTransformer<List<ChallengeDTO>>())
        }

        override fun getComparison(playerId: Long): Single<ComparisonDTO> {
            return userService.getComparison(playerId).compose(ASyncTransformer<ComparisonDTO>())
        }

        override fun inviteFriend(email: String): Single<UserMessage> {
            val request = InviteFriendRequest(email)
            return userService.inviteFriend(request).compose(ASyncTransformer<UserMessage>())
        }

        override fun confirmFriend(email: String): Single<ResponseBody> {
            val confirmFriendRequest = ConfirmFriendRequest(email)
            return userService.confirmFriend(confirmFriendRequest)
                .compose(ASyncTransformer<ResponseBody>())
        }

        override fun getFriends(): Single<List<FriendDTO>> {
            return userService.getFriends().compose(ASyncTransformer<List<FriendDTO>>())
        }

        override fun getFriendRequest(): Single<List<FriendDTO>> {
            return userService.getFriendRequest().compose(ASyncTransformer<List<FriendDTO>>())
        }

        override fun deleteFriend(email: String): Single<ResponseBody> {
            val request = DeleteFriendRequest(email)
            return userService.deleteFriend(request).compose(ASyncTransformer<ResponseBody>())
        }

        override fun refuseFriend(email: String): Single<ResponseBody> {
            val request = DeleteFriendRequest(email)
            return userService.refuseFriend(request).compose(ASyncTransformer<ResponseBody>())
        }

        override fun registerDevice(deviceId: String): Single<ResponseBody> {
            val request = RegisterDeviceDTO(deviceId)
            return userService.registerDevice(request).compose(ASyncTransformer<ResponseBody>())
        }

        override fun logout(): Single<ResponseBody> {
            return userService.logout().compose(ASyncTransformer<ResponseBody>())
        }

        override fun createPlayer(playerCreateModel: PlayerCreateModel): Single<ResponseBody> {
            return userService.createPlayer(playerCreateModel)
                .compose(ASyncTransformer<ResponseBody>())
        }

        override fun updatePlayer(playerCreateModel: PlayerUpdateModel): Single<ResponseBody> {
            return userService.updatePlayer(playerCreateModel)
                .compose(ASyncTransformer<ResponseBody>())
        }

        override fun fetchHeightOptions(): Single<List<KeyValueDTO>> {
            return userService.fetchHeightOptions().compose(ASyncTransformer<List<KeyValueDTO>>())
        }

        override fun fetchWeightOptions(): Single<List<KeyValueDTO>> {
            return userService.fetchWeightOptions().compose(ASyncTransformer<List<KeyValueDTO>>())
        }

        override fun fetchAbilityOptions(): Single<List<KeyValueDTO>> {
            return userService.fetchAbilityOptions().compose(ASyncTransformer<List<KeyValueDTO>>())
        }

        override fun fetchAgeOptions(): Single<List<KeyValueDTO>> {
            return userService.fetchAgeOptions().compose(ASyncTransformer<List<KeyValueDTO>>())
        }

        override fun fetchGenderOptions(): Single<List<GenderDTO>> {
            return userService.fetchGenderOptions().compose(ASyncTransformer<List<GenderDTO>>())
        }

        override fun updatePurchase(planId: Int): Single<ResponseBody> {
            return userService.updatePurchase(UpdatePurchaseBody(planId))
                .compose(ASyncTransformer<ResponseBody>())
        }

    }
}