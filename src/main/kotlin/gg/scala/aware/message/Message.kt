package gg.scala.aware.message

import gg.scala.aware.Aware
import gg.scala.aware.AwareHub
import gg.scala.aware.thread.AwareThreadContext
import kotlinx.serialization.Serializable
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

    inline fun <reified T> get(id: String) = Json
        .decodeFromJsonElement<T>(
            checkNotNull(content[id]) {
                "Message content by id $id is not available"
            }
        )

    operator fun set(key: String, value: Any)
    {
        content[key] = Json.encodeToJsonElement(value)
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
