package gg.scala.aware.message

import gg.scala.aware.Aware
import gg.scala.aware.AwareHub
import gg.scala.aware.AwareHub.jsonInstance
import gg.scala.aware.thread.AwareThreadContext
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

/**
 * @author GrowlyX
 * @since 7/29/2023
 */
@Serializable
class Message(
    @Transient
    private val aware: Aware<Message>? = null,
    val id: String
)
{
    val content = mutableMapOf<String, JsonElement>()

    inline fun <reified T> get(id: String) = jsonInstance
        .decodeFromJsonElement<T>(
            checkNotNull(content[id]) {
                "Message content by id $id is not available"
            }
        )

    inline fun <reified T : Any> set(
        key: String, value: T,
        serializer: SerializationStrategy<T>
    ) = apply {
        content[key] = jsonInstance
            .encodeToJsonElement(
                serializer, value
            )
    }

    inline operator fun <reified T> set(key: String, value: T) =
        apply {
            content[key] = jsonInstance.encodeToJsonElement(value)
        }

    suspend fun publish(
        context: AwareThreadContext =
            AwareThreadContext.ASYNC,
        channel: String = aware?.channel
            ?: throw IllegalStateException(
                "Aware is not initialized"
            )
    )
    {
        AwareHub.publish(
            checkNotNull(aware),
            this, context, channel
        )
    }
}
