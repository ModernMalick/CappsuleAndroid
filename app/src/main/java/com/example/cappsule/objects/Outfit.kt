package com.example.cappsule.objects

import android.graphics.Bitmap

class Outfit(val name: String, val layer: Bitmap, val top: Bitmap, val bottom: Bitmap, val shoes: Bitmap, val id: Int) {

    class OutfitNameComparator : Comparator<Outfit> {
        override fun compare(o1: Outfit, o2: Outfit): Int {
            return o1.name.compareTo(o2.name)
        }
    }
}