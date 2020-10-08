package com.mobile.gympraaktis.data

data class Result<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val code: Int?,
    val throwable: Throwable? = null
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        EMPTY
    }

    companion object {
        fun <T> success(): Result<T> {
            return Result(Status.SUCCESS, null, null, null)
        }

        fun <T> success(data: T, message: String? = null): Result<T> {
            return Result(Status.SUCCESS, data, message, null)
        }

        fun <T> error(
            message: String? = null,
            code: Int? = null,
            data: T? = null,
            throwable: Throwable
        ): Result<T> {
            return Result(Status.ERROR, data, message, code, throwable)
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(Status.LOADING, data, null, null)
        }

        fun <T> empty(): Result<T> {
            return Result(Status.EMPTY, null, null, null)
        }
    }

}