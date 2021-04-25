package me.kaotich00.fwauctionhouse.ktor

import io.ktor.http.*
import io.ktor.server.testing.*
import me.kaotich00.fwauctionhouse.ktor.plugins.configureRouting
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello World!", response.content)
            }
        }
    }
}