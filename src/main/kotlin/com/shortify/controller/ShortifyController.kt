package com.shortify.controller

import com.shortify.usecase.ICreateShortUrlUseCase
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

@Controller("/shortify")
class ShortifyController(
    private val createShortUrlUseCase: ICreateShortUrlUseCase
) {
    @Post
    fun shortify(url: String?): HttpResponse<String> {
        try {
            java.net.URL(url).toURI()
        } catch (e: Exception) {
            return HttpResponse.badRequest("Invalid URL")
        }
        return HttpResponse.created(
            createShortUrlUseCase.execute(url!!)
        )
    }
}