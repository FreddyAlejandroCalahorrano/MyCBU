package com.example.sistemabus.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemabus.R
import com.example.sistemabus.adaptadorProp.Propietarios
import com.example.sistemabus.adaptadorProp.PropietariosAdaptador
import com.example.sistemabus.adaptadorUnidad.Unidades
import com.example.sistemabus.adaptadorUnidad.UnidadesAdaptador
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_unidad_admin.*
import java.util.ArrayList

class UnidadAdminActivity : AppCompatActivity(), UnidadesAdaptador.onClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var unidArrayList: ArrayList<Unidades>
    private lateinit var myAdapter: UnidadesAdaptador
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unidad_admin)
        setup()
    }

    private fun setup(){
        title = "Unidades"
        cardviewAdapter()
        btnReturAdmU.setOnClickListener {
            finish()
            showMenuAdmin()
        }
    }
    private fun cardviewAdapter(){
        recyclerView = findViewById(R.id.reciclerViewUnidAd)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        unidArrayList= arrayListOf()

        myAdapter = UnidadesAdaptador(unidArrayList, this)
        recyclerView.adapter = myAdapter

        EventChangeListener()
    }
    private fun EventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("Unidad").addSnapshotListener(object:
            com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED ){
                        unidArrayList.add(dc.document.toObject(Unidades::class.java))

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

    override fun onDataclick(NumerodeUnidad: String?) {
            val showEditUnid = Intent(this, EditUnitAdminActivity::class.java)
        showEditUnid.putExtra("NumerodeUnidad", NumerodeUnidad)
        startActivity(showEditUnid)
    }
}