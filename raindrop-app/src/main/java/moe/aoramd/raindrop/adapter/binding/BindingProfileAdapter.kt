package moe.aoramd.raindrop.adapter.binding

import android.widget.TextView
import androidx.databinding.*
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.repository.RaindropRepository
import moe.aoramd.raindrop.repository.entity.Account

object BindingProfileAdapter {

    @JvmStatic
    @BindingAdapter("nickname")
    fun setNickname(textView: TextView, text: String) {
        textView.text = when (text) {
            Account.LOADING_TAG -> RaindropRepository.resourceString(R.string.app_loading)
            Account.OFFLINE_TAG -> RaindropRepository.resourceString(R.string.profile_click_login)
            else -> text
        }
    }

    @JvmStatic
    @BindingAdapter("signature")
    fun setSignature(textView: TextView, text: String) {
        textView.text = when (text) {
            Account.LOADING_TAG -> RaindropRepository.resourceString(R.string.app_loading)
            Account.OFFLINE_TAG -> RaindropRepository.resourceString(R.string.profile_empty_status)
            else -> text
        }
    }
}