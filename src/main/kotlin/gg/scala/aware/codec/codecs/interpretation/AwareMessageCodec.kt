package gg.scala.aware.codec.codecs.interpretation

import gg.scala.aware.AwareHub.jsonInstance
import gg.scala.aware.codec.codecs.JsonRedisCodec
import gg.scala.aware.message.Message
import kotlinx.serialization.encodeToString
import kotlin.reflect.KClass

/**
 * A default implementation for [JsonRedisCodec]
 * providing an [AwareMessage] value type.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
object AwareMessageCodec : JsonRedisCodec<Message>(Message::class)
{
    override fun encodeToString(v: Message) = jsonInstance.encodeToString(v)

    override fun decodeFromString(string: String, codec: KClass<Message>) =
        jsonInstance.decodeFromString<Message>(string)

    override fun getPacketId(v: Message) = v.id
}
