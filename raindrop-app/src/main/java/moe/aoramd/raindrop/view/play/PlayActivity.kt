package moe.aoramd.raindrop.view.play

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.adapter.binding.BindingBaseAdapter
import moe.aoramd.raindrop.adapter.list.PlayPlayingAdapter
import moe.aoramd.raindrop.databinding.ActivityPlayBinding
import moe.aoramd.raindrop.service.PlayService
import moe.aoramd.raindrop.view.base.bind.PlayerBindActivity
import moe.aoramd.raindrop.view.base.bind.PlayerBindViewModel

class PlayActivity : PlayerBindActivity() {

    companion object {
        fun start(activity: FragmentActivity) {
            val intent = Intent(activity, PlayActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityPlayBinding

    private val viewModel: PlayViewModel by viewModels()
    override val binder: PlayerBindViewModel by lazy { viewModel }


    // Override Functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Playing List
        binding.adapter = PlayPlayingAdapter(this)
        binding.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Event Listener
        viewModel.event.observe(this, Observer {
            when (it) {
                PlayService.EVENT_LOAD_SONG_FAILED -> onLoadSongFailedEvent()
            }
        })
    }

    // Click Listener
    val rootClickListener = object : BindingBaseAdapter.IndexClickListener {
        override fun onClick(view: View, index: Int) {
            viewModel.selectSong(index)
        }
    }

    // Event Operations
    private fun onLoadSongFailedEvent() {
        Snackbar.make(binding.root, R.string.snackbar_load_song_failed, Snackbar.LENGTH_SHORT)
            .show()
    }
}
