package com.example.sistemabus.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemabus.R
import com.example.sistemabus.adaptadoCond.Conductores
import com.example.sistemabus.adaptadoCond.ConductoresAdaptador
import com.example.sistemabus.adaptadorQuestions.EditCuestActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_cond_admin.*

class CondAdminActivity : AppCompatActivity(), ConductoresAdaptador.onClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var condArrayList: ArrayList<Conductores>
    private lateinit var myAdapter: ConductoresAdaptador
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cond_admin)
        setup()
    }
    private fun setup(){
        title = "Unidades"
        cardviewAdapter()
        btnReturAdmC.setOnClickListener {
            finish()
            showMenuAdmin()
        }
    }
    private fun cardviewAdapter(){
        recyclerView = findViewById(R.id.reciclerViewCondAd)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        condArrayList= arrayListOf()

        myAdapter = ConductoresAdaptador(condArrayList, this)
        recyclerView.adapter = myAdapter

        EventChangeListener()
    }
    private fun EventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("Conductor").addSnapshotListener(object:
            com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED ){
                        condArrayList.add(dc.document.toObject(Conductores::class.java))

                    }
                }
                myAdapter.notifyDataSetChanged()
            }

        })
    }
    private fun showMenuAdmin(){
        val showMenuIntent = Intent(this, adminActivity::class.java).apply{
        }
        startActivity(showMenuIntent)
    }
    override fun onDataclick(email: String?) {
        val showEditCond = Intent(this, EditCondActivity::class.java)
        showEditCond.putExtra("email", email)
        startActivity(showEditCond)
    }
}