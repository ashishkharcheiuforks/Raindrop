package moe.aoramd.raindrop.netease.api

import moe.aoramd.raindrop.netease.repo.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SourceApi {

    @GET("/login/cellphone")
    suspend fun login(@Query("phone") phone: Long, @Query("password") password: String): Response<LoginRepo>

    @GET("/login/refresh")
    suspend fun updateLoginState(): Response<StateRepo>

    @GET("/user/playlist")
    suspend fun loadPlaylists(@Query("uid") uid: Long): Response<PlaylistsRepo>

    @GET("/playlist/detail")
    suspend fun loadSongs(@Query("id") playlistId: Long): Response<PlaylistRepo>

    @GET("/song/url")
    suspend fun loadUrl(@Query("id") songId: Long, @Query("br") bitRate: Int): Response<UrlRepo>

    @GET("/search")
    suspend fun searchSongs(@Query("keywords") keywords: String, @Query("offset") offset: Int): Response<SearchRepo>
}