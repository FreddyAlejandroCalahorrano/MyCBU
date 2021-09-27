package com.example.sistemabus.cond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit_profile_uni.*
import kotlinx.android.synthetic.main.activity_view_profile_prop_cond.*

class ViewProfilePropCondActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    var unidadProp = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile_prop_cond)
        setup()
    }
    private fun setup(){
        title = "Perfil Propietario"
        loadData()
        btnRenunC.setOnClickListener{
            showAlertRenunProp()
        }
        btnRegresarCC.setOnClickListener{
            finish()
            showMenuCond()
        }
    }
    private fun loadData(){
        db.collection("Propietario").document(intent.getStringExtra("email").toString())
            .get().addOnSuccessListener { documento ->
            val url = documento.getString("Url")
            txtNombreCC.setText(documento.getString("Nombre") + " " + documento.getString("Apellido")).toString()
            txtCiCC.setText(documento.getString("CI")).toString()
            txtEdadCC.setText(documento.getString("Edad")).toString()
            txtNumUniCC.setText(documento.getString("NumerodeUnidad")).toString()
            txtTelfCC.setText(documento.getString("Teléfono")).toString()
            txtMailCC.setText(documento.getString("email")).toString()
            txtDescCC.setText(documento.getString("Descripcion")).toString()
            unidadProp = documento.getString("NumerodeUnidad").toString()
            ///////Cargar Imagen/////////////////
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_profile_unload)
                .fallback(R.drawable.ic_profile_error)
                .circleCrop()
                .into(imageProfileCC)
        }
    }
    private fun showMenuCond(){
        val MenuCondIntent = Intent(this, MenuCondActivity::class.java).apply{
        }
        startActivity(MenuCondIntent)
    }
    private fun showRenunProp(){

        db.collection("Unidad").document(unidadProp)
            .update(
                hashMapOf(
                    "Conductor" to "",
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if(it.isSuccessful){
                    val user = Firebase.auth.currentUser
                    val email = user?.email
                    db.collection("Conductor").document(email.toString()).update(
                        hashMapOf(
                            "Contratado" to "No",
                        ) as Map<String, Any>
                    ).addOnCompleteListener {
                        if(it.isSuccessful){
                            AlertShowMenuCond()
                        }
                        else{
                            Toast.makeText(this, "Error al realizar esta Operación", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Error al realizar esta Operación", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun showAlertRenunProp(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡Alerta!!")
        builder.setMessage("¿Esta seguro que desea Renunciar a su puesto de Trabajo en la unidad "+ unidadProp +"?")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            ShowAlertConfirm()
        }
        builder.setNegativeButton("Cancel", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun ShowAlertConfirm(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡Alerta!!")
        builder.setMessage("Una vez realizada esta operación no podra continuar Trabajando en la unidad "+unidadProp+".\n\n ¿Desea continuar?")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            showRenunProp()
        }
        builder.setNegativeButton("Cancel", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun AlertShowMenuCond(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡ Exito !!")
        builder.setMessage("Se han eliminado sus datos de la Unidad "+ unidadProp + ".")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            finish()
            showMenuCond()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}