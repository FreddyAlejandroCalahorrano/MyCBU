  package com.example.sistemabus.cond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemabus.R
import com.example.sistemabus.adaptadorOferEmple.Empleos
import com.example.sistemabus.adaptadorOferEmple.EmpleosAdaptador
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_post_empleo.*
import java.util.ArrayList

class PostEmpleoActivity : AppCompatActivity(), EmpleosAdaptador.onClickListener {
      private lateinit var recyclerView: RecyclerView
      private lateinit var empArrayList: ArrayList<Empleos>
      private lateinit var myAdapter: EmpleosAdaptador
      private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_empleo)
        cardviewAdapter()
        setup()

    }
      private fun setup(){
          title = "Postular Empleo"
          btnReg.setOnClickListener {
              finish()
              showMenuCond()
          }

      }
    private fun cardviewAdapter(){
        recyclerView = findViewById(R.id.reciclerViewPostEmp)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        empArrayList= arrayListOf()

        myAdapter = EmpleosAdaptador(empArrayList, this)
        recyclerView.adapter = myAdapter

        EventChangeListener()
    }
    private fun EventChangeListener(){

        db = FirebaseFirestore.getInstance()
        db.collection("Empleo").addSnapshotListener(object:
            com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED ){
                        empArrayList.add(dc.document.toObject(Empleos::class.java))

                    }
                }
                myAdapter.notifyDataSetChanged()
            }

        })
    }


    override fun onDataclick(email: String?, emailP: String?) {
        sendData(email, emailP)
    }
    private fun sendData(email: String?, emailP: String?){
        db.collection("Empleo").document(emailP.toString())
            .collection("Postulantes").document(email.toString()).set(
            hashMapOf(
                "email" to email
            )
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Postulacion Exitosa!!!", Toast.LENGTH_LONG).show()
                setup()
            } else {
                Toast.makeText(this, "Error al postular!!", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showMenuCond(){
        val MenuCondIntent = Intent(this, MenuCondActivity::class.java).apply{
        }
        startActivity(MenuCondIntent)
    }
}