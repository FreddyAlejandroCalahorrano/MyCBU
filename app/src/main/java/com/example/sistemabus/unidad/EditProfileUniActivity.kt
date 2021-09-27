package com.example.sistemabus.unidad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.example.sistemabus.prop.MenuPropActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile_prop.*
import kotlinx.android.synthetic.main.activity_edit_profile_uni.*
import kotlinx.android.synthetic.main.activity_edit_profile_uni.btnCancel
import kotlinx.android.synthetic.main.activity_edit_profile_uni.btnSave
import kotlinx.android.synthetic.main.activity_edit_profile_uni.btnUploadImg
import kotlinx.android.synthetic.main.activity_edit_profile_uni.imageProfileEdit
import kotlinx.android.synthetic.main.activity_edit_profile_uni.textDescrp
import java.util.HashMap

class EditProfileUniActivity : AppCompatActivity() {
    private val GALLERY_INTENT = 1
    private val db = FirebaseFirestore.getInstance()
    var imgUrl = ""
    var codeImg = ""
    var cond = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_uni)
        loadDataProp()
        setup()
    }
    private fun setup(){
        title = "Editar Perfil Unidad"
        btnCancel.setOnClickListener {
            validateCncl()
        }
        btnSave.setOnClickListener {
            if(textNumUni.text.isEmpty()){
                Toast.makeText(this,"Debe ingresar un número de Unidad", Toast.LENGTH_SHORT).show()
            }
            else{
                checkUnitExist()
            }
        }
        btnUploadImg.setOnClickListener {
            val user = Firebase.auth.currentUser
            val email = user?.email
            db.collection("Propietario").document(email.toString()).get()
                .addOnSuccessListener { documento ->
                    val numuni = documento.getString("NumerodeUnidad").toString()
                    validateUpImage(numuni)
                }
        }
    }
    private fun loadDataProp(){
        ///ACCEDER A EL USUARIO Y LEER EL EMAIL/////////
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get()
            .addOnSuccessListener { documento ->
                val numuni = documento.getString("NumerodeUnidad").toString()
                validate(numuni)
            }
    }
    private fun loadData(numuni: String){
        ///ABRIR LA BASE DE DATOS Y LLENAR LOS CAMPOS CON LOS DATOS GUARDADOS/////
        db.collection("Unidad").document(numuni).get().addOnSuccessListener { documento ->
            textMarca.setText(documento.getString("Marca")).toString()
            textCooperativa.setText(documento.getString("Cooperativa")).toString()
            textNumUni.setText(documento.getString("NumerodeUnidad")).toString()
            textAnio.setText(documento.getString("Anio")).toString()
            textPlaca.setText(documento.getString("Placa")).toString()
            textDescrp.setText(documento.getString("Descripcion"))
            imgUrl = documento.getString("Url").toString()
            codeImg = documento.getString("CodeImg").toString()
            cond = documento.getString("Conductor").toString()
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
                val user = Firebase.auth.currentUser
                val email = user?.email
                deletimg()
                val fileUri = data!!.data
                val Storage: StorageReference = FirebaseStorage.getInstance().reference
                    .child("ProfilePictureUnidad").child(email.toString())
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
        db.collection("Propietario").document(email.toString()).get()
            .addOnSuccessListener { documento ->
               var numuni = documento.getString("NumerodeUnidad").toString()
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
        if(codeImg.isNullOrEmpty()){ }
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
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get()
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
        val user = Firebase.auth.currentUser
        val email = user?.email
        FirebaseFirestore.getInstance()
            .collection("Unidad").document(textNumUni.text.toString())
            .set(
                hashMapOf(
                    "Propietario" to email.toString(),
                    "Marca" to textMarca.text.toString(),
                    "Cooperativa" to textCooperativa.text.toString(),
                    "NumerodeUnidad" to textNumUni.text.toString(),
                    "Anio" to textAnio.text.toString(),
                    "Placa" to textPlaca.text.toString(),
                    "Descripcion" to textDescrp.text.toString(),
                    "Conductor" to cond,
                    "Url" to imgUrl,
                    "CodeImg" to codeImg
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    FirebaseFirestore.getInstance()
                        .collection("Propietario").document(email.toString())
                        .update(
                            hashMapOf(
                                "NumerodeUnidad" to textNumUni.text.toString(),
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
            finish()
            showProfile()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlertImage(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡Alerta!!")
        builder.setMessage("Por Favor debe llenar y guardar los Campos antes de agregar una Imágen")
        builder.setPositiveButton("Aceptar" , null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun validate(numuni:String){
        if(numuni.isEmpty()){
            showAlertFirst()
        }
        else{
            loadData(numuni)
        }
    }
    private fun validateCncl(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get()
            .addOnSuccessListener { documento ->
                val numuni = documento.getString("NumerodeUnidad").toString()
                if(numuni.isEmpty()){
                    finish()
                    showMenuProp()
                }
                else{
                    finish()
                    showProfile()
                }
            }
    }
    private fun validateUpImage(numuni: String){
        if(numuni.isEmpty()){
            showAlertImage()
        }
        else{
            picUpload()

        }
    }
    private fun showAlertFirst(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nuevo Unidad")
        builder.setMessage("Debe añadir una unidad, este mensaje se mostrara una sola vez.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun  checkUnitExist() {
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get()
            .addOnSuccessListener { documento ->
                val numuni = documento.getString("NumerodeUnidad").toString()
                if(numuni.isEmpty()){
                    var x = false
                    db.collection("Unidad").get().addOnSuccessListener { resultado->
                        for(documentos in resultado){
                            if((documentos.id).equals(textNumUni.text.toString())){
                                x = true
                                Toast.makeText(this,"Esta Unidad ya se encuentra Registrada", Toast.LENGTH_LONG).show()
                            }
                        }
                        if(x.equals(false)){
                            sendData()
                        }
                    }
                }
                else if(numuni.equals(textNumUni.text.toString())){
                    sendData()
                }
                else{
                    var x = false
                    db.collection("Unidad").get().addOnSuccessListener { resultado->
                        for(documentos in resultado){
                            if((documentos.id).equals(textNumUni.text.toString())){
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
        builder.setMessage("¿Esta seguro que desea Cambiar el número de su Unidad?")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            deleteUnit(numuni)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun deleteUnit(numuni: String){
        db.collection("Unidad").document(numuni).delete().addOnSuccessListener {
            sendData()
        }.addOnFailureListener {
            Toast.makeText(this,"Error al Eliminar Unidad", Toast.LENGTH_LONG).show()
        }
    }
    private fun showMenuProp(){
        val viewMenuPIntent = Intent(this, MenuPropActivity::class.java).apply {
        }
        startActivity(viewMenuPIntent)
    }
}