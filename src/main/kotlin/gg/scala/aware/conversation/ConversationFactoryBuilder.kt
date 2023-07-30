package gg.scala.aware.conversation

import gg.scala.aware.codec.WrappedRedisCodec
import gg.scala.aware.conversation.messages.ConversationMessage
import gg.scala.aware.conversation.messages.ConversationMessageResponse
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * Allows for a builder-style method
 * of creating a new [ConversationFactory].
 *
 * @author GrowlyX
 * @since 3/10/2022
 */
class ConversationFactoryBuilder<T : ConversationMessage, U : ConversationMessageResponse>(
    private val messageType: KClass<T>, private val responseType: KClass<U>
)
{
    companion object
    {
        @JvmStatic
        inline fun <reified T : ConversationMessage, reified U : ConversationMessageResponse>
                of(): ConversationFactoryBuilder<T, U>
        {
            return ConversationFactoryBuilder(T::class, U::class)
        }
    }

    private lateinit var channel: String

    private lateinit var timeout: Pair<Long, TimeUnit>
    private lateinit var timeoutFunction: (T) -> Unit

    private lateinit var processorFunction: (T) -> U
    private lateinit var responseFunction: (T, U) -> ConversationContinuation

    private lateinit var resp: WrappedRedisCodec<U>
    private lateinit var req: WrappedRedisCodec<T>

    fun timeout(
        duration: Long, timeUnit: TimeUnit, lambda: (T) -> Unit
    ): ConversationFactoryBuilder<T, U>
    {
        this.timeout = Pair(duration, timeUnit)
        this.timeoutFunction = lambda
        return this
    }

    fun channel(
        channel: String
    ): ConversationFactoryBuilder<T, U>
    {
        this.channel = channel
        return this
    }

    fun reqRespCodex(
        req: WrappedRedisCodec<T>, resp: WrappedRedisCodec<U>
    ) = apply {
        this@ConversationFactoryBuilder.req = req
        this@ConversationFactoryBuilder.resp = resp
    }

    fun response(
        lambda: (T) -> U
    ): ConversationFactoryBuilder<T, U>
    {
        this.processorFunction = lambda
        return this
    }

    fun receive(
        lambda: (T, U) -> ConversationContinuation
    ): ConversationFactoryBuilder<T, U>
    {
        this.responseFunction = lambda
        return this
    }

    fun build(): ConversationFactory<T, U>
    {
        return ConversationFactory(
            messageType, responseType, channel, timeout.first,
            timeout.second, timeoutFunction, processorFunction, responseFunction,
            req, resp
        )
    }
}
