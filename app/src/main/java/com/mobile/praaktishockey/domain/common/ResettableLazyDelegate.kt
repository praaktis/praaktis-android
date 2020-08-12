package com.mobile.praaktishockey.domain.common

interface ResettableLazy<out T> : Lazy<T> {

    fun reset()
}

private class ResettableSynchronizedLazyImpl<out T>(initializer: () -> T, lock: Any? = null) : ResettableLazy<T> {

    private val initializer: (() -> T)? = initializer

    private object UNINITIALIZED_VALUE

    @Volatile private var _value: Any? = UNINITIALIZED_VALUE

    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: this

    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    @Suppress("UNCHECKED_CAST") (_v2 as T)
                } else {
                    val typedValue = initializer!!()
                    _value = typedValue
                    typedValue
                }
            }
        }

    override fun reset() {
        synchronized(lock) {
            _value = UNINITIALIZED_VALUE
        }
    }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
}

fun <T> resettableLazy(initializer: () -> T): ResettableLazy<T> = ResettableSynchronizedLazyImpl(initializer)
