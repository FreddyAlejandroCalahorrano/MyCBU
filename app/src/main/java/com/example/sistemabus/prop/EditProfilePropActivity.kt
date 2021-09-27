package com.example.sistemabus.prop


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.example.sistemabus.admin.PropAdminActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile_prop.*
import java.util.HashMap

class EditProfilePropActivity : AppCompatActivity() {
    private val GALLERY_INTENT = 1
    private val db = FirebaseFirestore.getInstance()
    var imgUrl = ""
    var codeImg = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_prop)
        setup()
    }
    private fun setup(){
        title = "Editar Perfil Propietario"
        loadData()
        btnCancel.setOnClickListener {
            finish()
            showProfile()
        }
        btnSave.setOnClickListener {
            sendData()
        }
        btnUploadImg.setOnClickListener {
            picUpload()
        }
    }
    private fun loadData(){
        ///ACCEDER A EL USUARIO Y LEER EL EMAIL/////////
        val user = Firebase.auth.currentUser
        val email = user?.email
        ///ABRIR LA BASE DE DATOS Y LLENAR LOS CAMPOS CON LOS DATOS GUARDADOS/////
        db.collection("Propietario").document(email.toString()).get().addOnSuccessListener { documento ->
            textNombre.setText(documento.getString("Nombre")).toString()
            textApellido.setText(documento.getString("Apellido")).toString()
            textCedula.setText(documento.getString("CI")).toString()
            textEdad.setText(documento.getString("Edad")).toString()
            textTelf.setText(documento.getString("Teléfono")).toString()
            textDescrp.setText(documento.getString("Descripcion"))
            imgUrl = documento.getString("Url").toString()
            codeImg = documento.getString("CodeImg").toString()
            ///////CARGAMOS LA IMAGEN/////////////////
            Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.ic_profile_unload)
                .fallback(R.drawable.ic_profile_error)
                .fitCenter()
                .centerCrop()
                .into(imageProfileEdit)
        }
    }
    //////SUBIR UNA IMAGEN A LA BASE DE DATOS//////////////
    private fun picUpload(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_INTENT) {
            if(resultCode == RESULT_OK){
                ////////////////ALERT DIALOG/////////////////
                val user = Firebase.auth.currentUser
                val email = user?.email
                deletimg()
                val fileUri = data!!.data
                val Storage: StorageReference = FirebaseStorage.getInstance().reference.child("ProfilePicturePropietario").child(email.toString())
                val fileName: StorageReference = Storage.child("file" + fileUri!!.lastPathSegment)
                fileName.putFile(fileUri).addOnSuccessListener {
                    fileName.downloadUrl.addOnSuccessListener {
                            uri -> val hashMap = HashMap<String, String>()
                        hashMap["url"] = java.lang.String.valueOf(uri)
                        val img = java.lang.String.valueOf(uri)
                        val codImg = "file"+fileUri!!.lastPathSegment
                        ////ENVIAR VALORES A LA BASE DE DATOS////
                        sendDataImg(img, codImg)
                        ///////////MOSTRAR NUEVA IMAGEN///////////////////
                        Glide.with(this)
                            .load(img)
                            .placeholder(R.drawable.ic_profile_unload)
                            .fallback(R.drawable.ic_profile_error)
                            .fitCenter()
                            .centerCrop()
                            .into(imageProfileEdit)

                        Toast.makeText(this, "Se subio Correctamente", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else{
                Toast.makeText(this, "Error Resultado", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this, "Error al selecionar archivo", Toast.LENGTH_SHORT).show()
        }
    }
    private fun valuesdatabase(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get().addOnSuccessListener { documento ->
            imgUrl = documento.getString("Url").toString()
            codeImg = documento.getString("CodeImg").toString()
        }
    }
    ///////BORRAR LA IMAGEN ANTIGUA DE LA BASE DE DATOS////////////
    private fun deletimg(){
        valuesdatabase()
        if(codeImg.isNullOrEmpty()){
        }
        else{
            val user = Firebase.auth.currentUser
            val email = user?.email
            val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child("ProfilePicturePropietario/" + email.toString())
            val desertRef : StorageReference = storageRef.child(codeImg)
            desertRef.delete().addOnSuccessListener {
            }.addOnFailureListener {
            }
        }

    }
    private fun sendDataImg(img:String, codImg:String){
        val user = Firebase.auth.currentUser
        val email = user?.email
        FirebaseFirestore.getInstance()
            .collection("Propietario").document(email.toString())
            .update(
                hashMapOf(
                    "Url" to img,
                    "CodeImg" to codImg
                ) as Map<String, Any>
            )
        valuesdatabase()
    }
    private fun sendData(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        FirebaseFirestore.getInstance()
            .collection("Propietario").document(email.toString())
            .update(
                hashMapOf(
                    "Nombre" to textNombre.text.toString(),
                    "Apellido" to textApellido.text.toString(),
                    "CI" to textCedula.text.toString(),
                    "Edad" to textEdad.text.toString(),
                    "Teléfono" to textTelf.text.toString(),
                    "Descripcion" to textDescrp.text.toString()
                ) as Map<String, Any>
            ).addOnCompleteListener {
                    if (it.isSuccessful){
                        showAlert()
                    }
                    else{
                        Toast.makeText(this,"Error al actualizar los Datos!!",Toast.LENGTH_LONG).show()
                    }
            }
    }
    private fun showProfile(){
        val ProfilePropIntent = Intent(this, ProfilePropActivity::class.java).apply {
        }
        startActivity(ProfilePropIntent)
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exito!!")
        builder.setMessage("Los Datos se han actualizado Correctamente")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            finish()
            showProfile()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}