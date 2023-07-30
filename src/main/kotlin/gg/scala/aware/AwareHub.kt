package gg.scala.aware

import gg.scala.aware.thread.AwareThreadContext
import gg.scala.aware.uri.WrappedAwareUri
import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.resource.DefaultClientResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.concurrent.Executors

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
object AwareHub
{
    internal val scheduler = Executors
        .newSingleThreadScheduledExecutor()

    private lateinit var wrappedUri: WrappedAwareUri
    private var client: RedisClient? = null

    var jsonInstance: Json = Json

    fun configure(
        wrappedUri: WrappedAwareUri
    )
    {
        this.wrappedUri = wrappedUri
    }

    fun client(): RedisClient
    {
        if (client == null)
        {
            client = RedisClient.create(
                DefaultClientResources.builder()
                    .ioThreadPoolSize(4)
                    .computationThreadPoolSize(4)
                    .build(),
                RedisURI.create(this.wrappedUri.build())
            )

            client!!.options = ClientOptions.builder()
                .timeoutOptions(
                    TimeoutOptions.builder()
                        .timeoutCommands(false)
                        .fixedTimeout(
                            Duration.ofSeconds(0L)
                        )
                        .build()
                )
                .autoReconnect(true)
                .build()
        }

        return client!!
    }

    fun close()
    {
        client?.shutdown()
        client = null
    }

    suspend fun <T : Any> publish(
        aware: Aware<T>,
        message: T,
        context: AwareThreadContext =
            AwareThreadContext.ASYNC,
        channel: String = aware.channel,
    )
    {
        if (
            context == AwareThreadContext.SYNC
        )
        {
            runBlocking {
                aware.publishConnection.sync()
                    .publish(channel, message)
            }
            return
        }

        coroutineScope {
            withContext(Dispatchers.IO) {
                aware.publishConnection.sync()
                    .publish(channel, message)
            }
        }
    }
}
