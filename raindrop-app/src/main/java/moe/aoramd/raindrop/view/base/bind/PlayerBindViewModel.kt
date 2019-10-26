package moe.aoramd.raindrop.view.base.bind

import android.os.Bundle
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.aoramd.raindrop.IPlayListener
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.service.PlayService
import moe.aoramd.raindrop.service.SongMedium

abstract class PlayerBindViewModel : ViewModel() {

    protected abstract val listenPlayingDataChanged: Boolean

    internal var service: IPlayService? = null

    internal var controller: MediaControllerCompat? = null

    private val playListener = object : IPlayListener.Stub() {
        override fun onPlayingSongChanged(songMedium: SongMedium, index: Int) {
            val song = SongMedium.toSong(songMedium)
            viewModelScope.launch(Dispatchers.Main) {
                playingSongChanged(song, index)
            }
        }

        override fun onPlayingListChanged(songMediums: MutableList<SongMedium>) {
            val songs = SongMedium.toSongs(songMediums)
            viewModelScope.launch(Dispatchers.Main) {
                playingListChanged(songs)
            }
        }

        override fun onMessageReceive(message: String, bundle: Bundle) {
            viewModelScope.launch(Dispatchers.Main) {
                when (message) {
                    PlayService.MESSAGE_PROGRESS_CHANGED ->
                        playingProgressChanged(bundle.getFloat(PlayService.MESSAGE_BUNDLE_KEY))
                    PlayService.MESSAGE_STATE_CHANGED ->
                        playingStateChanged(bundle.getInt(PlayService.MESSAGE_BUNDLE_KEY))
                    PlayService.MESSAGE_SHUFFLE_MODE_CHANGED ->
                        playingShuffleModeChanged(bundle.getInt(PlayService.MESSAGE_BUNDLE_KEY))
                }
            }
        }
    }

    internal fun removePlayingListenerIfNeed() {
        service?.removePlayingListener(this.toString())
    }

    internal fun addPlayingListenerIfNeed() {
        if (listenPlayingDataChanged) {
            try {
                service?.addPlayingListener(this.toString(), playListener)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    protected open fun playingSongChanged(song: Song, index: Int) {}

    protected open fun playingListChanged(songs: List<Song>) {}

    protected open fun playingProgressChanged(progress: Float) {}

    protected open fun playingStateChanged(state: Int) {}

    protected open fun playingShuffleModeChanged(shuffleMode: Int) {}
}
