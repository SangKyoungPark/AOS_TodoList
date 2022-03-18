package com.psk.note

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

// Lint 라는 것이 성능상 문제가 있을 수 있는 코드를 관리해줌
// AsyncTask -> 메모리누수날수도 있다.
@SuppressLint("StaticFieldLeak")
class MainActivity : AppCompatActivity() , OnDeleteListener{

    // 나중에 초기화할 때 lateinit
    lateinit var db : MemoDatabase
    var memoList = listOf<MemoEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = MemoDatabase.getInstance(this)!!

        button_add.setOnClickListener {
            val memo = MemoEntity(null, edittext_memo.text.toString())
            insertMemo(memo)
            edittext_memo.setText("")
        }
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 다시 화면에 보여줌
        getAllMemo()
    }

    fun insertMemo(memo : MemoEntity){
        // 1. MainTread(모든UI관련일) vs WorkerThread(Background Thread)(Data 관련일처리)

        val insertTask = object : AsyncTask<Unit,Unit,Unit>(){
            override fun doInBackground(vararg params: Unit?) {
                db.memoDAO().insert(memo)
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                getAllMemo()
            }

        }
        insertTask.execute()
    }
    fun getAllMemo(){
        val getTask = (object : AsyncTask<Unit,Unit,Unit>(){

            override fun doInBackground(vararg params: Unit?) {
                memoList = db.memoDAO().getAll()
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                setRecycleView(memoList)
            }
        }).execute()

        // getTask.execute()
    }
    fun deleteMemo(memo : MemoEntity){
        val deleteTask = object : AsyncTask<Unit,Unit,Unit>(){
            override fun doInBackground(vararg params: Unit?) {
                db.memoDAO().delete(memo)
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                getAllMemo()
            }
        }
        deleteTask.execute()
    }
    fun setRecycleView(memoList : List<MemoEntity>){
        recyclerView.adapter = MyAdapter(this,memoList, this)
    }

    override fun onDeleteListener(memo: MemoEntity) {
        deleteMemo(memo)
    }
}