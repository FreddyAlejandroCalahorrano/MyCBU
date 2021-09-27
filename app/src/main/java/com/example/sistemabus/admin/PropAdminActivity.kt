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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_prop_admin.*
import java.util.ArrayList

class PropAdminActivity : AppCompatActivity(), PropietariosAdaptador.onClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var propArrayList: ArrayList<Propietarios>
    private lateinit var myAdapter: PropietariosAdaptador
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prop_admin)
        setup()
    }
    private fun setup(){
        title = "Propietarios"
        cardviewAdapter()
        btnReturAdmP.setOnClickListener {
            finish()
            showMenuAdmin()
        }
    }
    private fun cardviewAdapter(){
        recyclerView = findViewById(R.id.reciclerViewPropAd)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        propArrayList= arrayListOf()

        myAdapter = PropietariosAdaptador(propArrayList, this)
        recyclerView.adapter = myAdapter

        EventChangeListener()
    }
    private fun EventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("Propietario").addSnapshotListener(object:
            com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED ){
                        propArrayList.add(dc.document.toObject(Propietarios::class.java))

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
        val showEditProp = Intent(this, EditPropAdminActivity::class.java)
        showEditProp.putExtra("email", email)
        startActivity(showEditProp)
    }
}