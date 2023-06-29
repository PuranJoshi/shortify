package com.shortify.usecase

interface ICreateShortUrlUseCase {
    fun execute(longUrl: String): String
}