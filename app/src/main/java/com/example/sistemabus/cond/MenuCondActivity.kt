package com.example.sistemabus.cond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sistemabus.inicio.AuthActivity
import com.example.sistemabus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_menu_cond.*


class MenuCondActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    val user = Firebase.auth.currentUser
    val emailC = user?.email
    var x = 0
    var prop = ""
    var numuni = ""
    var codeImgCond = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("Propietarios")
        FirebaseMessaging.getInstance().subscribeToTopic("Conductores")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_cond)

        loadUni()
        setup()
    }
    private fun setup(){
        title = "Menú Conductor"
        btnVerPerfilCond.setOnClickListener {
            finish()
            showViewProfileCond()
        }
        btnCerrarSecionCond.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
            showAuth()
        }
        btnRealizarTest.setOnClickListener {
            finish()
            showViewMenuTes()
        }
        btnVerLeyTranCond.setOnClickListener {
            finish()
            showLawofT()
        }
        btnPostEmpleo.setOnClickListener {
            finish()
            showPostEmp()
        }
        btnVerPunt.setOnClickListener {
            finish()
            showViewPunt()
        }
        btnCalfProp.setOnClickListener {
            validateConEmp()
        }
        btnElimCuentCond.setOnClickListener {
            showAlertDeletAcount()
        }
    }
    private fun showAuth(){
        val AuthIntent = Intent(this, AuthActivity::class.java).apply{
        }
        startActivity(AuthIntent)
    }
    private fun showViewMenuTes(){
        val MenuTestIntent = Intent(this, MenuTestActivity::class.java).apply {
        }
        startActivity(MenuTestIntent)
    }
    private fun showViewProfileCond(){
        val ProfCondIntent = Intent(this, ProfileCondActivity::class.java).apply {
        }
        startActivity(ProfCondIntent)
    }
    private fun showLawofT(){
        val LawTIntent = Intent(this, ViewLawActivity::class.java).apply {
        }
        startActivity(LawTIntent)
    }
    private fun showPostEmp(){
        val PosEmIntent = Intent(this, PostEmpleoActivity::class.java).apply {
        }
        startActivity(PosEmIntent)
    }
    private fun showViewPunt(){
        val ViewPunIntent = Intent(this, VerPuntActivity::class.java)
        ViewPunIntent.putExtra("cond", "1")
        startActivity(ViewPunIntent)
    }
    private fun loadUni() {
        db.collection("Unidad").get()
            .addOnSuccessListener { resultado ->
                for (unidad in resultado) {
                    db.collection("Unidad").document(unidad.id).get().addOnSuccessListener { data ->
                        val cond = data.getString("Conductor").toString()
                        if (cond == emailC.toString())
                        {
                            x = 1
                            prop = data.getString("Propietario").toString()
                            numuni = data.getString("NumerodeUnidad").toString()
                        }
                    }
                }
            }
    }
    private fun validateConEmp(){
        if (x == 1){
            finish()
            showPropEmp()
        }
        else{
            showAlert()
        }
    }
    private fun showPropEmp(){
        val ViewProfilePropCondIntent = Intent(this, ViewProfilePropCondActivity::class.java)
        ViewProfilePropCondIntent.putExtra("email", prop)
        startActivity(ViewProfilePropCondIntent)
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡ Alerta !!")
        builder.setMessage("No se encuentra asignado a una Unidad.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlertDeletAcount(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡ Alerta !!")
        builder.setMessage("¿Esta seguro que desea Eliminar por completo su Cuenta de Usuário?")
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
        builder.setTitle("¡¡ Alerta !!")
        builder.setMessage("Una vez realizada esta operación no se podra recuperar ninguno de sus datos.\n\n ¿Desea continuar?")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            validateDelet()
        }
        builder.setNegativeButton("Cancel", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun validateDelet(){
        if (x == 1){
            showDelUnit()
        }
        else{
            deletImg()
        }
    }
    private fun showDelUnit(){
        db.collection("Unidad").document(numuni)
            .update(
                hashMapOf(
                    "Conductor" to "",
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if(it.isSuccessful){
                    deletImg()
                }
                else{
                    Toast.makeText(this, "Error al realizar esta Operación", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun deletImg() {
        db.collection("Conductor").document(emailC.toString()).get()
            .addOnSuccessListener { documento ->
                codeImgCond = documento.getString("CodeImg").toString()
            }.addOnCompleteListener {
            if (it.isSuccessful) {
                if(codeImgCond.isNullOrEmpty()){
                    deleteComentsCond()
                }
                else{
                    val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child("ProfilePictureConductor/" + emailC.toString())
                    val desertRef : StorageReference = storageRef.child(codeImgCond)
                    desertRef.delete().addOnCompleteListener {
                        if(it.isSuccessful){
                            deleteComentsCond()
                        }
                        else{
                            Toast.makeText(this, "Error al Eliminar la Imagen del Conductor", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else{
                Toast.makeText(this, "Error al leer los datos del Conductor", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun deleteComentsCond(){
        db.collection("Conductor").document(emailC.toString()).collection("Comentarios").get()
            .addOnSuccessListener { resultado ->
                for(comentarios in resultado){
                    comentarios.reference.delete()
                }
            }.addOnCompleteListener {
                if(it.isSuccessful){
                    db.collection("Conductor").document(emailC.toString()).delete().addOnCompleteListener {
                        if (it.isSuccessful) {
                            deletUserAcount()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Error al eliminar los Comentarios del Usuario", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun deletUserAcount(){
        db.collection("Conductor").document(emailC.toString()).delete().addOnSuccessListener {
            user!!.delete().addOnCompleteListener {
                showDeleteUser()
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Error al Eliminar Propietario", Toast.LENGTH_LONG).show()
        }
    }
    private fun showDeleteUser(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡ Exito !!")
        builder.setMessage("Su cuenta de Usuario se a Eliminado Exitosamente.")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            FirebaseAuth.getInstance().signOut()
            finish()
            showAuth()
        }
        builder.setNegativeButton("Cancel", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

