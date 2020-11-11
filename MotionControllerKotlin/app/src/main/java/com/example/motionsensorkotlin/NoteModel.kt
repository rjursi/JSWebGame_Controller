package com.example.motionsensorkotlin

class NoteModel {

    var content : String = "content"
    var editor : String ="editor"



    fun post() :String {
        val str = "작성자 : " + editor + "\n\n" +
                    "변경 내용 : " + content
        return str
    }

}