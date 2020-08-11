package mazer.arthur.gamingshop.data.remote

import mazer.arthur.gamingshop.domain.models.Banner
import mazer.arthur.gamingshop.domain.models.GameDetails
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiNetwork {

    @GET(ApiConstants.BANNER)
    suspend fun fetchBanners(): ArrayList<Banner>

    @GET(ApiConstants.SPOTLIGHT)
    suspend fun fetchSpotlight(): ArrayList<GameDetails>

    @GET(ApiConstants.GAME)
    suspend fun fetchGameDetails(@Path("id") id: Int): GameDetails

}