package isel.pt.yama.dataAccess

import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader

fun getDefaultLruCacheSize(): Int {
    val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    return maxMemory / 8
}
class LruBitmapCache(maxSize: Int) : LruCache<String, Bitmap>(maxSize), ImageLoader.ImageCache{

    constructor() : this(getDefaultLruCacheSize())

    override fun getBitmap(url: String?): Bitmap {
        val get = get(url)
        if(get==null){

        }//TODO how to do this...returning null???
        return get
    }

    override fun putBitmap(url: String?, bitmap: Bitmap?) {
        put(url, bitmap)
    }

    override fun sizeOf(key: String?, value: Bitmap?): Int {
        return value?.byteCount!!
    }




}



