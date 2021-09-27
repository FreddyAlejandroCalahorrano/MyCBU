package com.example.sistemabus.ofertaremple

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemabus.R
import com.example.sistemabus.adapterpostulantes.Postulantes
import com.example.sistemabus.adapterpostulantes.PostulantesAdaptor
import com.example.sistemabus.prop.MenuPropActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit_profile_uni.*
import kotlinx.android.synthetic.main.activity_ofertar_empleo.*
import java.util.*

class OfertarEmpleoActivity : AppCompatActivity(), PostulantesAdaptor.onClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var postArrayList: ArrayList<Postulantes>
    private lateinit var myAdapter: PostulantesAdaptor
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ofertar_empleo)
        setup()
        btnPostular.setOnClickListener {
            generateEmpleo()
        }
        btnActualizar.setOnClickListener {
            updateData()
        }
        btnEliminar.setOnClickListener {
            delete()
        }
        btnReturn.setOnClickListener {
            finish()
            shorMenuProp()
        }


    }
    private fun setup(){
        title = "Ofertar Emmpleo"
        btnEliminar.visibility = View.GONE
        btnPostular.visibility = View.GONE
        btnActualizar.visibility = View.GONE
        lySpace.visibility = View.GONE
        cardviewAdapter()
        validateEmpleo()
    }
    private fun validateEmpleo(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Empleo").document(email.toString()).get().addOnSuccessListener { document ->
                if((document.getString("ID").toString()).equals("1")){
                    btnActualizar.visibility = View.VISIBLE
                    lySpace.visibility = View.VISIBLE
                    btnEliminar.visibility = View.VISIBLE
                    loadData()
                }
                else{
                    btnPostular.visibility = View.VISIBLE
                }
            }
    }
    private fun generateEmpleo(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Empleo").document(email.toString()).set(
                hashMapOf(
                    "ID" to "1",
                    "Titulo" to txtTitle.text.toString(),
                    "Salario" to txtSalario.text.toString(),
                    "Horas" to txtHoras.text.toString(),
                    "Descripcion" to txtDescrp.text.toString(),
                    "emailP" to email,
                )
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Oferta exitosa!!", Toast.LENGTH_LONG).show()
                    setup()
                } else {
                    Toast.makeText(this, "Error al crear oferta!!", Toast.LENGTH_LONG).show()
                }
            }

    }
    private fun loadData(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Empleo").document(email.toString()).get().addOnSuccessListener { document ->
                txtTitle.setText(document.getString("Titulo")).toString()
                txtSalario.setText(document.getString("Salario")).toString()
                txtHoras.setText(document.getString("Horas")).toString()
                txtDescrp.setText(document.getString("Descripcion")).toString()
                //"ID" to "1",
            }
    }
    private fun updateData(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Empleo").document(email.toString()).update(
                hashMapOf(
                    "ID" to "1",
                    "Titulo" to txtTitle.text.toString(),
                    "Salario" to txtSalario.text.toString(),
                    "Horas" to txtHoras.text.toString(),
                    "Descripcion" to txtDescrp.text.toString()
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Datos Actualizados Correctamente", Toast.LENGTH_LONG).show()
                    setup()
                } else {
                    Toast.makeText(this, "Error al actualizar los Datos!!", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun delete(){
        val user = Firebase.auth.currentUser
        val email = user?.email

            db.collection("Empleo").document(email.toString()).collection("Postulantes").get()
                .addOnSuccessListener { resultado ->
                    for(documentos in resultado){
                        documentos.reference.delete()
                    }
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        db.collection("Empleo").document(email.toString()).delete().addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "La oferta se a Eliminado", Toast.LENGTH_LONG).show()
                                txtTitle.setText("")
                                txtSalario.setText("")
                                txtHoras.setText("")
                                txtDescrp.setText("")
                                setup()
                            } else {
                                Toast.makeText(this, "Error al eliminar oferta!!", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error al eliminar oferta!!", Toast.LENGTH_LONG).show()
                    }
                }

    }
    private fun cardviewAdapter(){
        recyclerView = findViewById(R.id.reciclerViewPostu)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        postArrayList= arrayListOf()

        myAdapter = PostulantesAdaptor(postArrayList, this)
        recyclerView.adapter = myAdapter

        EventChangeListener()
    }
    private fun EventChangeListener(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db = FirebaseFirestore.getInstance()
        db.collection("Empleo").document(email.toString()).collection("Postulantes").addSnapshotListener(object:
            com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED ){
                        postArrayList.add(dc.document.toObject(Postulantes::class.java))

                    }
                }
                myAdapter.notifyDataSetChanged()
            }

        })
    }
    private fun shorMenuProp(){
        val viewMenuPIntent = Intent(this, MenuPropActivity::class.java).apply {
        }
        startActivity(viewMenuPIntent)
    }

    override fun onDataclick(email: String?) {
        finish()
        val ViewCondPost = Intent(this,ViewCondPostActivity::class.java)
        ViewCondPost.putExtra("email", email)
        startActivity(ViewCondPost)
    }
}