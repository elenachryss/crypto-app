package com.example.cryptoapp.data.models

import com.google.gson.annotations.SerializedName

data class CoinDetailResponse(
    val id: String,
    val description: Description,
    val links: Links
)

data class Description(
    @SerializedName("en")
    val en: String
)

data class Links(
    @SerializedName("homepage")
    val homepage: List<String>?,

    @SerializedName("whitepaper")
    val whitepaper: String?
)