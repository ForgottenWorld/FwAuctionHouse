package me.kaotich00.fwauctionhouse.services

import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.ext.url
import me.kaotich00.fwauctionhouse.utils.launchAsync
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
class WebApiServiceImpl : WebApiService {

    private lateinit var apiHost: String

    private var apiPort by Delegates.notNull<Int>()

    private lateinit var apiKey: String


    override fun setWebApiHost(apiHost: String) {
        this.apiHost = apiHost
    }

    override fun setWebApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    override fun setWebApiPort(port: Int) {
        this.apiPort = port
    }

    override fun sendOnListingsChangedEvent() {
        launchAsync {
            httpGet {
                url("$apiHost/events/onListingsChanged?api_key=$apiKey")
                port = apiPort
            }
        }
    }

    override fun sendOnTokenConfirmedEvent(token: String) {
        launchAsync {
            httpGet {
                url("$apiHost/events/onTokenConfirmed?api_key=$apiKey&token=$token")
                port = apiPort
            }
        }
    }
}