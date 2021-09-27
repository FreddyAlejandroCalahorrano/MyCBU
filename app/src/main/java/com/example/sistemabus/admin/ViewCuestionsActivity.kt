package com.example.sistemabus.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemabus.R
import com.example.sistemabus.adaptadorQuestions.EditCuestActivity
import com.example.sistemabus.adaptadorQuestions.Questions
import com.example.sistemabus.adaptadorQuestions.QuestionsAdaptador
import com.example.sistemabus.cond.MenuTestActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_view_cuestions.*
import java.util.ArrayList

class ViewCuestionsActivity : AppCompatActivity() , QuestionsAdaptador.onClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var questArrayList: ArrayList<Questions>
    private lateinit var myAdapter: QuestionsAdaptador
    private lateinit var db : FirebaseFirestore
    var condExs = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_cuestions)
        btnAddQuest.visibility = View.GONE
        validate()
    }
    private fun setup(){
        title = "Preguntas"
        btnAddQuest.setOnClickListener {
            finish()
            showEditQuest()
        }
        btnReturAdm.setOnClickListener {
            finish()
            if(condExs.equals(1)){
                showMenuCond()
            }
            else{
                showMenuAdmin()
            }
        }
    }
    private fun showMenuAdmin(){
        val showMenuIntent = Intent(this, adminActivity::class.java).apply{
        }
        startActivity(showMenuIntent)
    }
    private fun showMenuCond(){
        val showMenuCIntent = Intent(this, MenuTestActivity::class.java).apply{
        }
        startActivity(showMenuCIntent)
    }
    private fun showEditQuest(){
        val showEditQuest = Intent(this, EditCuestActivity::class.java).apply {

        }
        startActivity(showEditQuest)
    }
    private fun cardviewAdapter(){
        recyclerView = findViewById(R.id.reciclerViewQuestAd)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        questArrayList= arrayListOf()

        myAdapter = QuestionsAdaptador(questArrayList, this)
        recyclerView.adapter = myAdapter

        EventChangeListener()
    }
    private fun EventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("Quizz").addSnapshotListener(object:
            com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED ){
                        questArrayList.add(dc.document.toObject(Questions::class.java))

                    }
                }
                myAdapter.notifyDataSetChanged()
            }

        })
    }
    private fun validate(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db = FirebaseFirestore.getInstance()
        db.collection("Conductor")
            .document(email.toString()).get().addOnSuccessListener {
                if (it.exists()) {
                    condExs = 1
                    cardviewAdapter()
                    setup()
                }
                else{
                    btnAddQuest.visibility = View.VISIBLE
                    cardviewAdapter()
                    setup()
                }
            }
    }
    override fun onDataclick(numQuest: String?) {
        if(condExs.equals(0)){
            val showEditQuest = Intent(this, EditCuestActivity::class.java)
            showEditQuest.putExtra("numQuest", numQuest)
            startActivity(showEditQuest)
        }
    }
}