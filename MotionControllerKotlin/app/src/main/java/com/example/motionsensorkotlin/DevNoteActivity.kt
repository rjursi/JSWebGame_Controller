package com.example.motionsensorkotlin

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_devnote.*
import kotlinx.android.synthetic.main.activity_devnote.view.*
import kotlinx.android.synthetic.main.dialog_devnote.view.*
import java.lang.StringBuilder


class DevNoteActivity : AppCompatActivity() {

    var noteList = ArrayList<String>()
    lateinit var arrayAdapter : ArrayAdapter<String>
    val devNoteRef = Firebase.database.reference.child("devNote")
    var popCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devnote)

        arrayAdapter = ArrayAdapter(this,R.layout.list_item,R.id.list_editor,noteList)
        devNote_list.adapter = arrayAdapter

        devNoteRef.orderByPriority().limitToLast(7).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var data = snapshot.getValue(NoteModel::class.java)!!.post()
                noteList.add(data)
                arrayAdapter.notifyDataSetChanged()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


        dev_txt.setOnClickListener {
            popCount ++
            Log.d("taggtag","^^^^^^^^^^^^^^^^^^^"+popCount)
            if(popCount == 5){
                val builder = AlertDialog.Builder(this)
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.dialog_devnote, null)

                builder.setView(view).setPositiveButton("작성", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                        var noteIns = Note(view.dlg_dev_content.editableText.toString(),view.dlg_dev_editor.editableText.toString())

                        devNoteRef.push().setValue(noteIns)
                    }
                }).show()
                popCount =0
            }
        }

    }


    data class Note(
        var content : String,
        var editor : String
    )


}