package dk.itu.moapd.scootersharing.jacj.feature_map.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

// Based on https://gist.github.com/Ozius/1ef2151908c701854736
fun getBitmapDescriptor(id: Int, context: Context): BitmapDescriptor? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val vectorDrawable = AppCompatResources.getDrawable(
            context,
            id
        ) as VectorDrawable
        val h = vectorDrawable.intrinsicHeight
        val w = vectorDrawable.intrinsicWidth
        vectorDrawable.setBounds(0, 0, w, h)
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        vectorDrawable.draw(canvas)
        BitmapDescriptorFactory.fromBitmap(bm)
    } else {
        BitmapDescriptorFactory.fromResource(id)
    }
}