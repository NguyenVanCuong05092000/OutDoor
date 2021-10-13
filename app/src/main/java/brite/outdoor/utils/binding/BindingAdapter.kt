package brite.outdoor.utils.binding

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import brite.outdoor.R
import brite.outdoor.data.entities.ObjectSearch


class BindingAdapter {
    companion object {
        @BindingAdapter("android:setMyTextSpan")
        @JvmStatic
        fun setCustomTextSpan(view: TextView, value: String) {
            val content = SpannableString(value)
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            view.text = content
        }

        @BindingAdapter("android:setSelectedImageView")
        @JvmStatic
        fun setSelectedImageView(view: ImageView, isSelected: MutableLiveData<Boolean>) {
            view.isSelected = isSelected.value == true
        }


        @BindingAdapter("android:changeToBrowser")
        @JvmStatic
        fun changeToBrowser(view: TextView, url: String) {
            view.setOnClickListener {
                view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }

        @BindingAdapter("android:actionGlobal2Home")
        @JvmStatic
        fun actionGlobal2Home(view: Button, url: Int) {
            view.setOnClickListener {
//                view.findNavController().navigate(R.id.action_global_frmHome)
            }
        }

        @BindingAdapter("android:showOrHideLoading")
        @JvmStatic
        fun showOrHideLoading(view: ProgressBar, isShowLoading: MutableLiveData<Boolean>) {
            if (isShowLoading.value == true) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }

}