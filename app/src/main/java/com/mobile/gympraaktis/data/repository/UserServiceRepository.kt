package com.mobile.gympraaktis.data.repository

import com.mobile.gympraaktis.data.api.UserService
import com.mobile.gympraaktis.domain.common.ASyncTransformer
import com.mobile.gympraaktis.domain.common.Constants.createService
import com.mobile.gympraaktis.domain.entities.*
import io.reactivex.Single
import okhttp3.ResponseBody

interface UserServiceRepository {

    fun getDashboardData(): Single<DashboardDTO>
    fun getProfile(): Single<UserDTO>
    fun getTimelineData(): Single<TimelineDTO>
    suspend fun getAttemptHistory(page: Int): AttemptHistoryResponse
    fun getDetailResult(attemptId: Int): Single<List<DetailScoreDTO>>
    fun storeResult(storeResultDTO: StoreResultDTO): Single<ResponseBody>
    fun getChallenges(): Single<List<ChallengeDTO>>
    fun getComparison(): Single<ComparisonDTO>
    fun inviteFriend(email: String): Single<UserMessage>
    fun confirmFriend(email: String): Single<ResponseBody>
    fun getFriends(): Single<List<FriendDTO>>
    fun getFriendRequest(): Single<List<FriendDTO>>
    fun deleteFriend(email: String): Single<ResponseBody>
    fun refuseFriend(email: String): Single<ResponseBody>
    fun registerDevice(deviceId: String): Single<ResponseBody>
    fun logout(): Single<ResponseBody>

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

        override fun getTimelineData(): Single<TimelineDTO> {
            return userService.getTimelineData().compose(ASyncTransformer<TimelineDTO>())
        }

        override suspend fun getAttemptHistory(page: Int): AttemptHistoryResponse {
            return userService.getAttemptHistory(page)
        }

        override fun getDetailResult(attemptId: Int): Single<List<DetailScoreDTO>> {
            return userService.getDetailResult(attemptId)
                .compose(ASyncTransformer<List<DetailScoreDTO>>())
        }

        override fun storeResult(storeResultDTO: StoreResultDTO): Single<ResponseBody> {
            return userService.storeResult(storeResultDTO).compose(ASyncTransformer<ResponseBody>())
        }

        override fun getChallenges(): Single<List<ChallengeDTO>> {
            return userService.getChallenges().compose(ASyncTransformer<List<ChallengeDTO>>())
        }

        override fun getComparison(): Single<ComparisonDTO> {
            return userService.getComparison().compose(ASyncTransformer<ComparisonDTO>())
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

    }
}