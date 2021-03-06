package moe.aoramd.raindrop.view.base.player

import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.aoramd.raindrop.IPlayListener
import moe.aoramd.raindrop.IPlayService
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.service.SongMedium

/**
 *  media play service controllable view model
 *
 *  @author M.D.
 *  @version dev 1
 */
abstract class PlayerControlViewModel : ViewModel() {

    // whether add play listener
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

        override fun onPlayingProgressChanged(progress: Float) {
            viewModelScope.launch(Dispatchers.Main) {
                playingProgressChanged(progress)
            }
        }

        override fun onPlayingStateChanged(state: Int) {
            viewModelScope.launch(Dispatchers.Main) {
                playingStateChanged(state)
            }
        }

        override fun onPlayingShuffleModeChanged(mode: Int) {
            viewModelScope.launch(Dispatchers.Main) {
                playingShuffleModeChanged(mode)
            }
        }

        override fun eventListener(event: String) {
            viewModelScope.launch(Dispatchers.Main) {
                eventListener(event)
            }
        }
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

    internal fun removePlayingListenerIfNeed() {
        service?.removePlayingListener(this.toString())
    }

    protected open fun playingSongChanged(song: Song, index: Int) {}

    protected open fun playingListChanged(songs: List<Song>) {}

    protected open fun playingProgressChanged(progress: Float) {}

    protected open fun playingStateChanged(state: Int) {}

    protected open fun playingShuffleModeChanged(mode: Int) {}

    protected open fun eventListener(event: String) {}
}
