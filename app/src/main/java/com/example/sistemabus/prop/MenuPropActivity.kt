package com.example.sistemabus.prop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sistemabus.inicio.AuthActivity
import com.example.sistemabus.R
import com.example.sistemabus.cond.VerPuntActivity
import com.example.sistemabus.ofertaremple.OfertarEmpleoActivity
import com.example.sistemabus.unidad.EditProfileUniActivity
import com.example.sistemabus.unidad.ProfileUniActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_menu_prop.*

class MenuPropActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    var numuni = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("Conductores")
        FirebaseMessaging.getInstance().subscribeToTopic("Propietarios")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_prop)
        loadData()
        setup()
    }
    private fun setup(){
        title = "Menú Propietario"
        btnVerPerfil.setOnClickListener {
            finish()
            showProfileProp()
        }
        btnVerPerfilUni.setOnClickListener {
            validateUni()
        }
        btnCerrarSecion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
            showAuth()
        }
        btnOfEmpleo.setOnClickListener {
            loadDataEmp()
        }
        btnVerLeyTran.setOnClickListener {
            showLawT()
        }
        btnVerComenCond.setOnClickListener {
            validateCond()
        }
        btnCalfCond.setOnClickListener {
            validateCondCalf()
        }
        btnElimCuent.setOnClickListener {
            showAlertDeletAcount()
        }
    }
    private fun showAuth(){
        val AuthIntent = Intent(this, AuthActivity::class.java).apply{
        }
        startActivity(AuthIntent)
    }
    private fun showProfileProp(){
        val ProfilePropIntent = Intent(this, ProfilePropActivity::class.java).apply {
        }
        startActivity(ProfilePropIntent)
    }
    private fun showProfileUni(){
        val ProfileUniIntent = Intent(this, ProfileUniActivity::class.java).apply {
        }
        startActivity(ProfileUniIntent)
    }
    private fun showEditProfileUni(){
        val ProfileEditUniIntent = Intent(this, EditProfileUniActivity::class.java).apply {
        }
        startActivity(ProfileEditUniIntent)
    }
    private  fun showOfEmpleo(){
        val ProfileOfEmpleoIntent = Intent(this, OfertarEmpleoActivity::class.java).apply {
        }
        startActivity(ProfileOfEmpleoIntent)
    }
    private fun showLawT(){
        val viewLawofTIntent = Intent(this, LeydeTransitoActivity::class.java).apply {
        }
        startActivity(viewLawofTIntent)
    }
    private fun showViewComent(){
        val viewComentIntent = Intent(this, VerPuntActivity::class.java).apply {
        }
        startActivity(viewComentIntent)
    }
    private fun showComentCond(){
        val CalfCondIntent = Intent(this, CalfCondActivity::class.java).apply {
        }
        startActivity(CalfCondIntent)
    }
    private fun loadData(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get()
            .addOnSuccessListener { documento ->
                numuni = documento.getString("NumerodeUnidad").toString()
            }
    }
    private fun validateUni(){
        if(numuni.isEmpty()){
            finish()
            showEditProfileUni()
        }
        else{
            finish()
            showProfileUni()
        }
    }
    private fun validateCond(){
        db.collection("Unidad").document(numuni).get()
            .addOnSuccessListener { documento ->
                val cond = documento.getString("Conductor").toString()
                if(cond.isNullOrEmpty()){
                    shoeAlertUnAsigEm()
                }
                else{
                    showViewComent()
                }

            }
    }
    private fun validateCondCalf(){
        db.collection("Unidad").document(numuni).get()
            .addOnSuccessListener { documento ->
                val cond = documento.getString("Conductor").toString()
                if(cond.isNullOrEmpty()){
                    shoeAlertUnAsigEm()
                }
                else{
                    showComentCond()
                }

            }
    }
    private fun loadDataEmp(){
                if(numuni.isNullOrEmpty()){
                    showAlertUnit()
                }
                else{
                    validateEmple(numuni)
                }
    }
    private fun validateEmple(numuni: String){
        db.collection("Unidad").document(numuni).get()
            .addOnSuccessListener { documento ->
                val cond = documento.getString("Conductor").toString()
                showEmple(cond)
            }

    }
    private fun showEmple(cond : String){
        if(cond.isNullOrEmpty()){
            finish()
            showOfEmpleo()
        }
        else{
            showAlert()
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡ Alerta !!")
        builder.setMessage("No puede ofertar un empleo mientras tenga contratado un Conductor")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlertUnit(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡ Alerta !!")
        builder.setMessage("No puede ofertar un empleo mientras no tenga asiganada una Unidad")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun shoeAlertUnAsigEm(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡ Alerta !!")
        builder.setMessage("No puede realizar esta operacion mientras no tenga asiganado un Conductor")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlertDeletAcount(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡Alerta!!")
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
        builder.setTitle("¡¡Alerta!!")
        builder.setMessage("Una vez realizada esta operación no se podra recuperar ninguno de sus datos.\n\n ¿Desea continuar?")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            loadDataUser()
        }
        builder.setNegativeButton("Cancel", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun loadDataUser(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get().addOnSuccessListener { documento ->
            var numunidel : String = documento.getString("NumerodeUnidad").toString()
            var codeImgProp : String = documento.getString("CodeImg").toString()
            if(numunidel.isNullOrEmpty()){
                deletimgProp(codeImgProp)
            }
            else{
                deletimgUnid(numunidel, codeImgProp )
            }

        }

    }
    private fun deletimgUnid(numunidel : String, codeImgProp : String){
        db.collection("Unidad").document(numunidel).get().addOnSuccessListener { documento ->
            var codeImgUdel : String = documento.getString("CodeImg").toString()
            var conductor : String = documento.getString("Conductor").toString()
            if(codeImgUdel.isNullOrEmpty()){
                if(conductor.isNullOrEmpty()){
                    db.collection("Unidad").document(numunidel).delete().addOnSuccessListener {
                        deletofEmple(codeImgProp)
                    }.addOnFailureListener {
                        Toast.makeText(this,"Error al Eliminar Unidad", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    db.collection("Conductor").document(conductor).update(
                        hashMapOf(
                            "Contratado" to "No"
                        ) as Map<String, Any>
                    ).addOnCompleteListener {
                        db.collection("Unidad").document(numunidel).delete().addOnSuccessListener {
                            deletofEmple(codeImgProp)
                        }.addOnFailureListener {
                            Toast.makeText(this,"Error al Eliminar Unidad", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else{
                val user = Firebase.auth.currentUser
                val email = user?.email
                val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child("ProfilePictureUnidad/" + email.toString())
                val desertRef : StorageReference = storageRef.child(codeImgUdel)
                desertRef.delete().addOnSuccessListener {
                    FirebaseStorage.getInstance().reference.child("ProfilePictureUnidad/" + email.toString())
                        .delete().addOnCompleteListener {
                            if(conductor == ""){
                                db.collection("Unidad").document(numunidel).delete().addOnSuccessListener {
                                    deletofEmple(codeImgProp)
                                }.addOnFailureListener {
                                    Toast.makeText(this,"Error al Eliminar Unidad", Toast.LENGTH_LONG).show()
                                }
                            }
                            else{
                                db.collection("Conductor").document(conductor).update(
                                    hashMapOf(
                                        "Contratado" to "No"
                                    ) as Map<String, Any>
                                ).addOnCompleteListener {
                                    db.collection("Unidad").document(numunidel).delete().addOnSuccessListener {
                                        deletofEmple(codeImgProp)
                                    }.addOnFailureListener {
                                        Toast.makeText(this,"Error al Eliminar Unidad", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                }.addOnFailureListener {
                    Toast.makeText(this,"Error al Eliminar Imágen de la Unidad", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun deletofEmple(codeImgProp : String){
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
                            deletimgProp(codeImgProp)
                        } else {
                            deletimgProp(codeImgProp)
                        }
                    }
                } else {
                    Toast.makeText(this, "Error al eliminar oferta!!", Toast.LENGTH_LONG).show()
                }
            }

    }
    private fun deletimgProp(codeImgProp : String){
        val user = Firebase.auth.currentUser
        val email = user?.email
        if(codeImgProp.isNullOrEmpty()){
            db.collection("Propietario").document(email.toString()).delete().addOnSuccessListener {
                user!!.delete().addOnCompleteListener {
                    showDeleteUser()
                }
            }.addOnFailureListener {
                Toast.makeText(this,"Error al Eliminar Propietario", Toast.LENGTH_LONG).show()
            }
        }
        else{
            val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child("ProfilePicturePropietario/" + email.toString())
            val desertRef : StorageReference = storageRef.child(codeImgProp)
            desertRef.delete().addOnSuccessListener {
                        db.collection("Propietario").document(email.toString()).delete().addOnSuccessListener {
                            user!!.delete().addOnCompleteListener {
                                showDeleteUser()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Error al Eliminar Propietario", Toast.LENGTH_LONG)
                                .show()
                        }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al Eliminar la Imagen Propietario", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun showDeleteUser(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡Exito!!")
        builder.setMessage("Su cuenta de Usuario se ha Eliminado Exitosamente.")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            FirebaseAuth.getInstance().signOut()
            finish()
            showAuth()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}