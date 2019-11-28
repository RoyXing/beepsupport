package tech.beepbeep.support.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import androidx.databinding.BindingAdapter

/**
 *desc:
 *Author: roy
 *Date:2019-11-25
 */


@BindingAdapter("bindingIcon")
fun setIcon(view: ImageView, iconAddress: String) {
    if (iconAddress.isNotEmpty()) {
        view.setImageBitmap(string2Bitmap(address = iconAddress))
    }
}

fun string2Bitmap(address: String): Bitmap {
    val decode = Base64.decode(address, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decode, 0, decode.size)
}

