package brite.outdoor.utils.compressor

import android.graphics.Bitmap
import brite.outdoor.utils.compressFormat
import brite.outdoor.utils.loadBitmap
import brite.outdoor.utils.overWrite

import java.io.File

class FormatConstraint(private val format: Bitmap.CompressFormat) : Constraint {

    override fun isSatisfied(imageFile: File): Boolean {
        return format == imageFile.compressFormat()
    }

    override fun satisfy(imageFile: File): File {
        return overWrite(imageFile, loadBitmap(imageFile), format)
    }
}

fun Compression.format(format: Bitmap.CompressFormat) {
    constraint(FormatConstraint(format))
}