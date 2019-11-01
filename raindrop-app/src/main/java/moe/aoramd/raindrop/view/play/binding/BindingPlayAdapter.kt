package moe.aoramd.raindrop.view.play.binding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import moe.aoramd.raindrop.view.play.PlayPlayingAdapter
import moe.aoramd.raindrop.repository.entity.Song
import moe.aoramd.raindrop.widget.MusicProgressBar

/**
 *  music play interface data binding adapter
 *
 *  @author M.D.
 *  @version dev 1
 */
object BindingPlayAdapter {

    /*
        sliding up panel layout
     */

    @JvmStatic
    @BindingAdapter("showPanel")
    fun setPanelState(
        slidingUpPanelLayout: SlidingUpPanelLayout,
        enable: Boolean
    ) {
        if (slidingUpPanelLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
            || slidingUpPanelLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED
        ) {
            slidingUpPanelLayout.panelState =
                if (enable)
                    SlidingUpPanelLayout.PanelState.EXPANDED
                else
                    SlidingUpPanelLayout.PanelState.COLLAPSED
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "showPanel")
    fun isPanelEnable(slidingUpPanelLayout: SlidingUpPanelLayout) =
        slidingUpPanelLayout.panelState != SlidingUpPanelLayout.PanelState.COLLAPSED

    @JvmStatic
    @BindingAdapter("showPanelAttrChanged", requireAll = false)
    fun panelStateListener(
        slidingUpPanelLayout: SlidingUpPanelLayout,
        listener: InverseBindingListener?
    ) {
        listener?.let {
            slidingUpPanelLayout.addPanelSlideListener(
                object : SlidingUpPanelLayout.PanelSlideListener {
                    override fun onPanelSlide(panel: View?, slideOffset: Float) {}

                    override fun onPanelStateChanged(
                        panel: View?,
                        previousState: SlidingUpPanelLayout.PanelState?,
                        newState: SlidingUpPanelLayout.PanelState?
                    ) {
                        it.onChange()
                    }
                }
            )
        }
    }

    /*
        music progress bar
     */

    @JvmStatic
    @BindingAdapter("progress")
    fun setProgress(
        progressBar: MusicProgressBar,
        progress: Int
    ) {
        progressBar.playProgress = progress
    }

    @JvmStatic
    @BindingAdapter("progressStartChangeListener")
    fun setProgressStartChangeListener(
        progressBar: MusicProgressBar,
        listener: MusicProgressBar.ProgressStartChangeListener
    ) {
        progressBar.progressStartChangeListener = listener
    }

    @JvmStatic
    @BindingAdapter("progressChangedListener")
    fun setProgressChangedListener(
        progressBar: MusicProgressBar,
        listener: MusicProgressBar.ProgressChangedListener
    ) {
        progressBar.progressChangedListener = listener
    }

    /*
        data list recycler view
     */

    @JvmStatic
    @BindingAdapter("playAdapter")
    fun setPlayPlayingAdapter(
        recyclerView: RecyclerView,
        adapter: PlayPlayingAdapter
    ) {
        recyclerView.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("playingSongs")
    fun setPlayingSongs(
        recyclerView: RecyclerView,
        songs: List<Song>
    ) {
        val adapter = recyclerView.adapter as PlayPlayingAdapter
        adapter.data = songs
        adapter.notifyDataSetChanged()
    }

    @JvmStatic
    @BindingAdapter("playingIndex")
    fun setPlayingIndex(
        recyclerView: RecyclerView,
        index: Int
    ) {
        val adapter = recyclerView.adapter as PlayPlayingAdapter
        if (adapter.currentIndex != index) {
            val oldIndex = adapter.currentIndex
            adapter.currentIndex = index
            if (oldIndex != -1) adapter.notifyItemChanged(oldIndex)
            if (adapter.currentIndex != -1) adapter.notifyItemChanged(adapter.currentIndex)
        }
    }
}