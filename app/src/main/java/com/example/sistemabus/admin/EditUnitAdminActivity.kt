package com.example.sistemabus.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.example.sistemabus.unidad.ProfileUniActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_unit_admin.*
import java.util.HashMap

class EditUnitAdminActivity : AppCompatActivity() {
    private val GALLERY_INTENT = 1
    private val db = FirebaseFirestore.getInstance()
    var imgUrl = ""
    var codeImg = ""
    var propie = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_unit_admin)
        setup()
    }
    private fun setup(){
        title = "Editar Perfil Unidad"
        loadData((intent.getStringExtra("NumerodeUnidad")).toString())
        btnCancelUA.setOnClickListener {
            finish()
            showUnids()
        }
        btnSaveUA.setOnClickListener {
            if(textNumUniUA.text.isEmpty()){
                Toast.makeText(this,"Este Campo no puede estar vacio", Toast.LENGTH_SHORT).show()
            }
            else{
                checkUnitExist()
            }
        }
        btnUploadImgUA.setOnClickListener {
            validateUpImage((intent.getStringExtra("NumerodeUnidad")).toString())
        }
        btnDeletUA.setOnClickListener {
            showAlertDeletBtn()
        }
    }
    private fun loadData(numuni: String){
        ///ABRIR LA BASE DE DATOS Y LLENAR LOS CAMPOS CON LOS DATOS GUARDADOS/////
        db.collection("Unidad").document(numuni).get().addOnSuccessListener { documento ->
            textMarcaUA.setText(documento.getString("Marca")).toString()
            textCooperativaUA.setText(documento.getString("Cooperativa")).toString()
            textNumUniUA.setText(documento.getString("NumerodeUnidad")).toString()
            textAnioUA.setText(documento.getString("Anio")).toString()
            textPlacaUA.setText(documento.getString("Placa")).toString()
            textDescrpUA.setText(documento.getString("Descripcion"))
            imgUrl = documento.getString("Url").toString()
            codeImg = documento.getString("CodeImg").toString()
            propie = documento.getString("Propietario").toString()
            ///////CARGAMOS LA IMAGEN/////////////////
            Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.ic_profile_unload)
                .fallback(R.drawable.ic_profile_error)
                .fitCenter()
                .centerCrop()
                .into(imageProfileEditUA)
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
                val Storage: StorageReference = FirebaseStorage.getInstance().reference.child("ProfilePictureUnidad").child(propie)
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
                            .into(imageProfileEditUA)

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
        db.collection("Propietario").document(propie).get()
            .addOnSuccessListener { documento ->
                val numuni = documento.getString("NumerodeUnidad").toString()
                if(numuni.isEmpty()){
                    Toast.makeText(this, "Erro al leer Usuario", Toast.LENGTH_SHORT).show()
                }
                else{
                    db.collection("Unidad").document(numuni).get().addOnSuccessListener { documento ->
                        imgUrl = documento.getString("Url").toString()
                        codeImg = documento.getString("CodeImg").toString()
                    }
                }

            }
    }
    ///////BORRAR LA IMAGEN ANTIGUA DE LA BASE DE DATOS////////////
    private fun deletimg(){
        valuesdatabase()
        if(codeImg == ""){ }
        else{
            val user = Firebase.auth.currentUser
            val email = user?.email
            val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child("ProfilePictureUnidad/" + email.toString())
            val desertRef : StorageReference = storageRef.child(codeImg)
            desertRef.delete().addOnSuccessListener {
            }.addOnFailureListener {
            }
        }

    }
    private fun sendDataImg(img:String, codImg:String,){
        db.collection("Propietario").document(propie).get()
            .addOnSuccessListener { documento ->
                val numuni = documento.getString("NumerodeUnidad").toString()
                if(numuni.isEmpty()){
                    Toast.makeText(this, "Erro al leer Usuario", Toast.LENGTH_SHORT).show()
                }
                else{
                    FirebaseFirestore.getInstance()
                        .collection("Unidad").document(numuni)
                        .update(
                            hashMapOf(
                                "Url" to img,
                                "CodeImg" to codImg
                            ) as Map<String, Any>
                        )
                    valuesdatabase()
                }
            }
    }
    private fun sendData(){

        FirebaseFirestore.getInstance()
            .collection("Unidad").document(textNumUniUA.text.toString())
            .set(
                hashMapOf(
                    "Marca" to textMarcaUA.text.toString(),
                    "Cooperativa" to textCooperativaUA.text.toString(),
                    "NumerodeUnidad" to textNumUniUA.text.toString(),
                    "Anio" to textAnioUA.text.toString(),
                    "Placa" to textPlacaUA.text.toString(),
                    "Descripcion" to textDescrpUA.text.toString(),
                    "Url" to imgUrl,
                    "CodeImg" to codeImg,
                    "Propietario" to propie
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    FirebaseFirestore.getInstance()
                        .collection("Propietario").document(propie)
                        .update(
                            hashMapOf(
                                "NumerodeUnidad" to textNumUniUA.text.toString(),
                            ) as Map<String, Any>
                        )
                    showAlert()
                }
                else{
                    Toast.makeText(this,"Error al actualizar los Datos!!", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun showProfile(){
        val ProfileUnipIntent = Intent(this, ProfileUniActivity::class.java).apply {
        }
        startActivity(ProfileUnipIntent)
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exito!!")
        builder.setMessage("Los Datos se han actualizado Correctamente")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            showUnids()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlertImage(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡Alerta!!")
        builder.setMessage("Por Favor debe llenar los Campos Primero")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            showProfile()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun validateUpImage(numuni: String){
        if(numuni.isEmpty()){
            showAlertImage()
        }
        else{
            picUpload()

        }
    }

    private fun  checkUnitExist() {
        db.collection("Propietario").document(propie).get()
            .addOnSuccessListener { documento ->
                val numuni = documento.getString("NumerodeUnidad").toString()
                if(numuni.isEmpty()){
                    Toast.makeText(this, "Erro al leer Usuario", Toast.LENGTH_SHORT).show()
                }
                else if(numuni.equals(textNumUniUA.text.toString())){
                    sendData()
                }
                else{
                    var x = false
                    db.collection("Unidad").get().addOnSuccessListener { resultado->
                        for(documentos in resultado){
                            if((documentos.id).equals(textNumUniUA.text.toString())){
                                x = true
                                Toast.makeText(this,"Esta Unidad ya se encuentra Registrada", Toast.LENGTH_LONG).show()
                            }
                        }
                        if(x.equals(false)){
                            showAlertNewUnit(numuni)
                        }
                    }
                }
            }
    }
    private fun showAlertNewUnit(numuni: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta")
        builder.setMessage("¿Esta seguro que desea Cambiar el número de esta Unidad?")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            deleteUnit(numuni)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlertDeletBtn(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta")
        builder.setMessage("¿Esta seguro que desea Eliminar esta Unidad?")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            deleteUnitBtn((intent.getStringExtra("NumerodeUnidad").toString()))
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun deleteUnit(numuni: String) {
        db.collection("Unidad").document(numuni).delete().addOnSuccessListener {
            sendData()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al Eliminar Unidad", Toast.LENGTH_LONG).show()
        }
    }
    private fun deleteUnitBtn(numuni: String) {
        db.collection("Unidad").document(numuni).delete().addOnSuccessListener {
            db.collection("Propietario").document(propie).update(
                hashMapOf(
                    "NumerodeUnidad" to "",
                ) as Map<String, Any>
            ).addOnCompleteListener {
                deletimg()
                finish()
                showUnids()

            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al Eliminar Unidad", Toast.LENGTH_LONG).show()
        }
    }
        private fun showUnids(){
        val UnidIntent = Intent(this, UnidadAdminActivity::class.java).apply {
        }
        startActivity(UnidIntent)
    }
}