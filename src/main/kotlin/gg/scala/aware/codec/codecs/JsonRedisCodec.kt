package gg.scala.aware.codec.codecs

import gg.scala.aware.codec.WrappedRedisCodec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.reflect.KClass

/**
 * Encodes/decodes an object from/to
 * a valid Json formatted string.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
abstract class JsonRedisCodec<V : Any>(
    codec: KClass<V>
) : WrappedRedisCodec<V>(codec)
{
    companion object
    {
        @JvmStatic
        inline fun <reified V : Any> of(
            noinline packet: (V) -> String
        ) : JsonRedisCodec<V>
        {
            return object : JsonRedisCodec<V>(V::class)
            {
                override fun getPacketId(v: V) = packet.invoke(v)

                override fun encodeToString(v: V) = Json
                    .encodeToJsonElement(v)
                    .toString()

                override fun decodeFromString(string: String, codec: KClass<V>) =
                    Json.decodeFromString<V>(string)
            }
        }
    }

    abstract fun getPacketId(v: V): String

    override fun interpretPacketId(v: V): String
    {
        return getPacketId(v)
    }
}
