package com.example.sistemabus.cond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemabus.R
import com.example.sistemabus.adaptadorComentUsuarios.Comentarios
import com.example.sistemabus.adaptadorComentUsuarios.ComentariosAdaptador
import com.example.sistemabus.prop.MenuPropActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_ver_punt.*

class VerPuntActivity : AppCompatActivity(),ComentariosAdaptador.onClickListener {
    private val db2 = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var comentArrayList: ArrayList<Comentarios>
    private lateinit var myAdapter: ComentariosAdaptador
    private lateinit var db : FirebaseFirestore
    var Email = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_punt)
        if(intent.getStringExtra("cond").toString()  == "1"){
            val user = Firebase.auth.currentUser
            val email = user?.email
            Email = email.toString()
            loadData()
            setup()
        }
        else{
            loadUni()
        }

    }
    private fun loadUni(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db2.collection("Propietario").document(email.toString()).get().addOnSuccessListener { documento ->
            var numuni = documento.getString("NumerodeUnidad").toString()
            loadProp(numuni)
        }
    }
    private fun loadProp(numuni : String){
        db2.collection("Unidad").document(numuni).get().addOnSuccessListener { documento ->
            Email = documento.getString("Conductor").toString()
            loadData()
            setup()
        }
    }
    private fun setup(){
        title = "Comentarios"
        recyclerView = findViewById(R.id.reciclerVierComentVP)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        comentArrayList= arrayListOf()

        myAdapter = ComentariosAdaptador(comentArrayList, this)
        recyclerView.adapter = myAdapter

        EventChangeListener()
        btnreturnMenu.setOnClickListener {
            finish()
            returnt()
        }
    }
    private fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("Conductor").document(Email)
            .collection("Comentarios").addSnapshotListener(object :
                com.google.firebase.firestore.EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            comentArrayList.add(dc.document.toObject(Comentarios::class.java))
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
            )
    }
    private fun loadData(){
        db2.collection("Conductor").document(Email).get().addOnSuccessListener { documento ->
            txtNomCondVP.setText(documento.getString("Nombre") + " " + documento.getString("Apellido")).toString()
            txtPuntTotal.setText(documento.getString("puntaje")).toString()
        }
    }
    private fun returnt(){
        if(intent.getStringExtra("cond").toString()  == "1"){
            val MenuCondIntent = Intent(this, MenuCondActivity::class.java).apply{
            }
            startActivity(MenuCondIntent)
        }
        else{
            val MenuPropIntent = Intent(this, MenuPropActivity::class.java).apply{
            }
            startActivity(MenuPropIntent)
        }
    }
    override fun onDataclick(ID: String?) {
    }
}