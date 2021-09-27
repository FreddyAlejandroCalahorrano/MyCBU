package com.example.sistemabus.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.example.sistemabus.prop.ProfilePropActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_prop_admin.*
import java.util.HashMap

class EditPropAdminActivity : AppCompatActivity() {
    private val GALLERY_INTENT = 1
    private val db = FirebaseFirestore.getInstance()
    var imgUrl = ""
    var codeImg = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_prop_admin)
        setup()
    }
    private fun setup(){
        title = "Editar Perfil Propietario"
        loadData()
        btnCancelPAD.setOnClickListener {
            finish()
            showProfile()
        }
        btnSavePAD.setOnClickListener {
            sendData()
        }
        btnUploadImgPAD.setOnClickListener {
            picUpload()
        }
    }
    private fun loadData(){
        ///ABRIR LA BASE DE DATOS Y LLENAR LOS CAMPOS CON LOS DATOS GUARDADOS/////
        db.collection("Propietario").document((intent.getStringExtra("email")).toString()).get().addOnSuccessListener { documento ->
            textNombrePAD.setText(documento.getString("Nombre")).toString()
            textApellidoPAD.setText(documento.getString("Apellido")).toString()
            textCedulaPAD.setText(documento.getString("CI")).toString()
            textEdadPAD.setText(documento.getString("Edad")).toString()
            textTelfPAD.setText(documento.getString("Teléfono")).toString()
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
                .into(imageProfileEditPAD)
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
                deletimg()
                val fileUri = data!!.data
                val Storage: StorageReference = FirebaseStorage.getInstance().reference.child("ProfilePicturePropietario")
                    .child((intent.getStringExtra("email")).toString())
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
                            .into(imageProfileEditPAD)

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
        db.collection("Propietario").document((intent.getStringExtra("email")).toString())
            .get().addOnSuccessListener { documento ->
            imgUrl = documento.getString("Url").toString()
            codeImg = documento.getString("CodeImg").toString()
        }
    }
    ///////BORRAR LA IMAGEN ANTIGUA DE LA BASE DE DATOS////////////
    private fun deletimg(){
        valuesdatabase()
        val storageRef : StorageReference = FirebaseStorage.getInstance().reference
            .child("ProfilePicturePropietario/" + (intent.getStringExtra("email")).toString())
        val desertRef : StorageReference = storageRef.child(codeImg)
        desertRef.delete().addOnSuccessListener {
        }.addOnFailureListener {
        }
    }
    private fun sendDataImg(img:String, codImg:String){
        FirebaseFirestore.getInstance()
            .collection("Propietario").document((intent.getStringExtra("email")).toString())
            .update(
                hashMapOf(
                    "Url" to img,
                    "CodeImg" to codImg
                ) as Map<String, Any>
            )
        valuesdatabase()
    }
    private fun sendData(){
        FirebaseFirestore.getInstance()
            .collection("Propietario").document((intent.getStringExtra("email")).toString())
            .update(
                hashMapOf(
                    "Nombre" to textNombrePAD.text.toString(),
                    "Apellido" to textApellidoPAD.text.toString(),
                    "CI" to textCedulaPAD.text.toString(),
                    "Edad" to textEdadPAD.text.toString(),
                    "Teléfono" to textTelfPAD.text.toString(),
                    "Descripcion" to textDescrp.text.toString()
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    showAlert()
                }
                else{
                    Toast.makeText(this,"Error al actualizar los Datos!!", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun showProfile(){
        val ProfilePropIntent = Intent(this, PropAdminActivity::class.java).apply {
        }
        startActivity(ProfilePropIntent)
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exito!!")
        builder.setMessage("Los Datos se han actualizado Correctamente")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            showProfile()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}