package com.iav.contestdataprovider.Api.Model

data class RandomText(
    val value: String,
    val length: Int,
    val created: String
)

data class RandomTextResponse(
    val randomText: RandomText
)
