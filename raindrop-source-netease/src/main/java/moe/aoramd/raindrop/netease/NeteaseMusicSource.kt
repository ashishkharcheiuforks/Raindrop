package moe.aoramd.raindrop.netease

import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.aoramd.raindrop.netease.adapter.AccountTypeAdapter
import moe.aoramd.raindrop.netease.adapter.AlbumTypeAdapter
import moe.aoramd.raindrop.netease.adapter.PlaylistTypeAdapter
import moe.aoramd.raindrop.netease.adapter.SongTypeAdapter
import moe.aoramd.raindrop.netease.interceptor.GetCookieInterceptor
import moe.aoramd.raindrop.netease.interceptor.PutCookieInterceptor
import moe.aoramd.raindrop.netease.connection.SourceConnection
import moe.aoramd.raindrop.repository.entity.Account
import moe.aoramd.raindrop.repository.entity.Album
import moe.aoramd.raindrop.repository.entity.Playlist
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.repository.source.MusicSource
import moe.aoramd.raindrop.repository.source.SourceResult
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedInputStream
import java.io.IOException
import java.io.OutputStream

class NeteaseMusicSource : MusicSource {

    private val _tag = "NeteaseMusicSource"

    companion object {
        private const val BASE_URL = "http://193.112.99.234:3000/"
    }

    private val gson = GsonBuilder()
        .registerTypeAdapter(Account::class.java, AccountTypeAdapter())
        .registerTypeAdapter(Album::class.java, AlbumTypeAdapter())
        .registerTypeAdapter(Playlist::class.java, PlaylistTypeAdapter())
        .registerTypeAdapter(Song::class.java, SongTypeAdapter())
        .create()

    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))

    private val retrofit =
        builder.client(
            OkHttpClient.Builder()
                .addInterceptor(PutCookieInterceptor())
                .build()
        ).build()

    private val retrofitLogin =
        builder.client(
            OkHttpClient.Builder()
                .addInterceptor(GetCookieInterceptor())
                .build()
        ).build()

    private val connection = retrofit.create(SourceConnection::class.java)

    private val connectionLogin = retrofitLogin.create(SourceConnection::class.java)

    override val downloadPath = "/netease"

    override suspend fun login(phone: Long, password: String): SourceResult<Account> =
        withContext(Dispatchers.IO) {
            val response = connectionLogin.login(phone, password)

            if (response.code() != 200)
                return@withContext SourceResult<Account>(
                    false,
                    resultErrorMsg = MusicSource.EVENT_NETWORK_ERROR
                )

            return@withContext response.body()?.run {
                if (code == 200)
                    SourceResult(true, resultContent = account)
                else
                    SourceResult(false, resultErrorMsg = MusicSource.EVENT_REQUEST_ERROR)
            } ?: SourceResult(false, resultErrorMsg = MusicSource.EVENT_NETWORK_ERROR)
        }

    override suspend fun updateLoginState(): Boolean =
        withContext(Dispatchers.IO) {
            val response = connection.updateLoginState()

            if (response.code() != 200)
                return@withContext false

            return@withContext response.body()?.code == 200
        }

    override suspend fun loadPlaylists(accountId: Long): SourceResult<List<Playlist>> =
        withContext(Dispatchers.IO) {

            val response = connection.loadPlaylists(accountId)

            if (response.code() != 200)
                return@withContext SourceResult<List<Playlist>>(
                    false,
                    resultErrorMsg = MusicSource.EVENT_NETWORK_ERROR
                )

            return@withContext response.body()?.run {
                if (code == 200)
                    SourceResult(true, resultContent = playlist)
                else
                    SourceResult(false, resultErrorMsg = MusicSource.EVENT_REQUEST_ERROR)
            } ?: SourceResult(false, resultErrorMsg = MusicSource.EVENT_NETWORK_ERROR)
        }

    override suspend fun loadSongs(playlistId: Long): SourceResult<List<Song>> =
        withContext(Dispatchers.IO) {
            val response = connection.loadSongs(playlistId)

            if (response.code() != 200)
                return@withContext SourceResult<List<Song>>(
                    false,
                    resultErrorMsg = MusicSource.EVENT_NETWORK_ERROR
                )

            return@withContext response.body()?.run {
                if (code == 200)
                    SourceResult(true, resultContent = playlist.tracks)
                else
                    SourceResult(false, MusicSource.EVENT_REQUEST_ERROR)
            } ?: SourceResult(false, MusicSource.EVENT_NETWORK_ERROR)
        }

    override suspend fun loadUrl(songId: Long): String {
        return "https://music.163.com/song/media/outer/url?id=$songId.mp3"
    }

    override suspend fun downloadSong(
        songId: Long,
        stream: OutputStream
    ): String? {
        val url = loadUrl(songId)
        val request = Request.Builder().url(url).build()
        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful)
            return MusicSource.EVENT_NETWORK_ERROR

        if (response.body != null) {
            val inputStream = BufferedInputStream(response.body!!.byteStream())
            return try {
                val data = ByteArray(1024)
                var length: Int
                while (true) {
                    length = inputStream.read(data)
                    if (length == -1) break
                    stream.write(data, 0, length)
                }
                stream.flush()
                stream.close()
                inputStream.close()
                null
            } catch (e: IOException) {
                MusicSource.MSG_IO_ERROR
            }
        } else
            return MusicSource.EVENT_REQUEST_ERROR
    }
}