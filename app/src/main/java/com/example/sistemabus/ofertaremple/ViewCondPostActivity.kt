package com.example.sistemabus.ofertaremple


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.example.sistemabus.adaptadorComentUsuarios.ComentariosAdaptador
import com.example.sistemabus.adaptadorComentUsuarios.Comentarios
import com.example.sistemabus.prop.MenuPropActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_view_cond_post.*
import java.util.ArrayList

class ViewCondPostActivity : AppCompatActivity(), ComentariosAdaptador.onClickListener  {
    private val db2 = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var comentArrayList: ArrayList<Comentarios>
    private lateinit var myAdapter: ComentariosAdaptador
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_cond_post)
        loadData()
        setup()
        btnContra.setOnClickListener {
            showAlert()
        }
        btnRegresarCP.setOnClickListener {
            finish()
            showOfEmplo()
        }
    }
    private fun loadData(){
        db2.collection("Conductor").document(intent.getStringExtra("email").toString()).get().addOnSuccessListener { documento ->
            val url = documento.getString("Url")
            txtNombreCP.setText(documento.getString("Nombre") + " " + documento.getString("Apellido")).toString()
            txtEdadCP.setText(documento.getString("Edad")).toString()
            txtCICP.setText(documento.getString("CI")).toString()
            txtTipLCP.setText(documento.getString("Tipo de Licencia")).toString()
            txtNumCP.setText(documento.getString("Teléfono")).toString()
            txtEmailCP.setText(documento.getString("email")).toString()
            txtDescrCP.setText(documento.getString("Descripcion"))
            txtPtjeCP.setText(documento.getString("puntaje")).toString()
            ///////Cargar Imagen/////////////////
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_profile_unload)
                .fallback(R.drawable.ic_profile_error)
                .into(imageViewCP)
        }
    }
    private fun setup(){
        title = "Perfil Postulante"

        //textEmail.setText(intent.getStringExtra("email")).toString()
        recyclerView = findViewById(R.id.reciclerViewComent)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        comentArrayList= arrayListOf()

        myAdapter = ComentariosAdaptador(comentArrayList, this)
        recyclerView.adapter = myAdapter

        EventChangeListener()
    }
    private fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("Conductor").document(intent.getStringExtra("email").toString())
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
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡ Alerta !!")
        builder.setMessage("Esta seguro de que desea contratar a este Conductor?")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            loadUni()
        }
        builder.setNegativeButton("Cancelar")
        { dialog, id ->
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun loadUni(){
        db2.collection("Conductor").document(intent.getStringExtra("email").toString())
            .get().addOnSuccessListener { documento ->
                if(documento.getString("Contratado").toString() == "Si"){
                    Toast.makeText(this, "Este conductor ya se encuentra Contratado en otra Unidad", Toast.LENGTH_SHORT).show()
                }
                else{
                    val user = Firebase.auth.currentUser
                    val email = user?.email
                    db2.collection("Propietario").document(email.toString()).get().addOnSuccessListener { documento ->
                        addCond(documento.getString("NumerodeUnidad").toString())
                    }
                }
            }

    }
    private fun addCond(unidad : String){
        db2.collection("Unidad").document(unidad)
            .update(
                hashMapOf(
                "Conductor" to intent.getStringExtra("email").toString(),
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if(it.isSuccessful){
                    showAlertAddCond()
                }
                else{
                    Toast.makeText(this, "Error al contratar a este conductor", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun showAlertAddCond(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡ Exito !!")
        builder.setMessage("Conductor Contratado")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            deleteOf()
            asingcond()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showMenu(){
        val MenuIntent = Intent(this, MenuPropActivity::class.java).apply{
        }
        startActivity(MenuIntent)
    }
    private fun deleteOf() {
        val user = Firebase.auth.currentUser
        val email = user?.email
        db2.collection("Empleo").document(email.toString()).collection("Postulantes").get()
            .addOnSuccessListener { resultado ->
                for (documentos in resultado) {
                    documentos.reference.delete()
                }
            }.addOnCompleteListener {
                db.collection("Empleo").document(email.toString()).delete().addOnCompleteListener {
                    finish()
                    showMenu()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al eliminar Empleo", Toast.LENGTH_LONG).show()
            }
    }
    private fun asingcond(){
        db2.collection("Conductor").document(intent.getStringExtra("email").toString()).update(
            hashMapOf(
                "Contratado" to "Si",
            ) as Map<String, Any>
        )
    }
    private fun showOfEmplo(){
        val showOfEmIntent = Intent(this, OfertarEmpleoActivity::class.java).apply{
        }
        startActivity(showOfEmIntent)
    }
    override fun onDataclick(email: String?) {
            /*val ViewCondPost = Intent(this,ViewCondPostActivity::class.java)
            ViewCondPost.putExtra("email", email)
            startActivity(ViewCondPost)*/
    }
}