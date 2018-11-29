package isel.pt.yama.kotlinx

import android.annotation.SuppressLint
import android.os.AsyncTask

class AsyncWork<T>(private val work: () -> T) {
    private var completion: ((T) -> Unit)? = null
    init {
        @SuppressLint("StaticFieldLeak")
        val worker = object : AsyncTask<Unit, Unit, T>() {
            override fun doInBackground(vararg params: Unit?): T = work()
            override fun onPostExecute(result: T) { completion?.let { it(result) } }
        }
        worker.execute()
		/*
		launch {
			T res = work()
			completion?.let { it(result) }
		}
		*/
		
    }

    infix fun andThen(completion: (T) -> Unit) {
        this.completion = completion
    }
}

fun <T> runAsync(work: () -> T): AsyncWork<T> = AsyncWork(work) //TODO: why is it not necessary AsyncWork<T>