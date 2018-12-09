package isel.pt.yama.kotlinx

import android.annotation.SuppressLint
import android.os.AsyncTask

class AsyncWork<T>(private val work: () -> T) {
    private var completion: MutableList<((T) -> Unit)?> = mutableListOf()
    init {
        @SuppressLint("StaticFieldLeak")
        val worker = object : AsyncTask<Unit, Unit, T>() {
            override fun doInBackground(vararg params: Unit?): T = work()
            override fun onPostExecute(result: T) { completion.forEach{
                completion -> completion?.let { it(result) } }
            }
        }
        worker.execute()
		/*
		launch {
			T res = work()
			completion?.let { it(result) }
		}
		*/
		
    }

    infix fun andThen(completion: (T) -> Unit): AsyncWork<T> {
        this.completion.add(completion)
        return this
    }
}

fun <T> runAsync(work: () -> T): AsyncWork<T> = AsyncWork(work)