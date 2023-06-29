package com.shortify.controller

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.shortify.usecase.ICreateShortUrlUseCase
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


@Client("/shortify")
interface ShortifyClient {
    @Post
    fun shortify(url: String): HttpResponse<String>
}

@MicronautTest
internal class ShortifyControllerTest{
    @Inject
    lateinit var client: ShortifyClient

    @get:MockBean(ICreateShortUrlUseCase::class)
    val mockCreateShortUrlUseCase = mock<ICreateShortUrlUseCase>()

    @Test
    internal fun `should return status 201 on success`() {
        val response = client.shortify("https://www.google.com")
        assertEquals(201, response.status.code)
    }

    @Test
    internal fun `should throw httpClientResponseException with status 400 when invalid url`() {
        val response = assertThrows<HttpClientResponseException>{
            client.shortify("invalid url")
        }
        assertEquals(400, response.status.code)
        assertEquals("Invalid URL", response.response.getBody(String::class.java).get())
    }

    @Test
    internal fun `should throw httpClientResponseException with status 400 when url is empty`() {
        val response = assertThrows<HttpClientResponseException>{
            client.shortify("")
        }
        assertEquals(400, response.status.code)
        assertEquals("Invalid URL", response.response.getBody(String::class.java).get())
    }

    @Test
    internal fun `should return shortUrl when ICreateShortUrlUseCase is called with longUrl`() {
        given {
            mockCreateShortUrlUseCase.execute("https://www.longurl.com/with/many/paths")
        }.willReturn("http://shorturl.com/abc123")

        val response = client.shortify("https://www.longurl.com/with/many/paths")
        assertEquals(201, response.status.code)
        assertEquals("http://shorturl.com/abc123", response.body())
    }
}