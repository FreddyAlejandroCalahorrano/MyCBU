package com.example.sistemabus.adaptadorQuestions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.example.sistemabus.admin.ViewCuestionsActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_cuest.*
import java.util.HashMap

class EditCuestActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    var ands = ""
    var imgUrl = ""
    var codeImg = ""
    var quest = ""
    var x = 0
    private val GALLERY_INTENT = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_cuest)

        radioButtonA.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId.equals(R.id.radioButtonA1)){
                ands=textQuestA1.text.toString()
            }
            else if(checkedId.equals(R.id.radioButtonA2)){
                ands = textQuestA2.text.toString()
            }
            else if(checkedId.equals(R.id.radioButtonA3)){
                ands = textQuestA3.text.toString()
            }
        }
        setup()

    }
    private fun setup () {
        title = "Opciones de Preguntas"
        txtLyautNumQuest.visibility = View.GONE
        validateQ()
        btnSaveQuestEA.setOnClickListener {
            if((intent.getStringExtra("numQuest")).toString() == "null"){
                if(txtNumQuestionEA.text.toString() == ""){
                    Toast.makeText(this, "Debe ingresar un Número de pregunta", Toast.LENGTH_SHORT).show()
                }
                else{
                    quest = txtNumQuestionEA.text.toString()
                    validateQuest()
                }
            }
            else{
                sendData()
            }
        }
        btnImageQuest.setOnClickListener {
            if((intent.getStringExtra("numQuest")).toString() == "null"){
                if(txtNumQuestionEA.text.toString() == ""){
                    Toast.makeText(this, "Debe ingresar un Número de pregunta", Toast.LENGTH_SHORT).show()
                }
                else{
                    quest = txtNumQuestionEA.text.toString()
                    validateIma()
                }
            }
            else{
                picUpload()
            }
        }
        btnImageDelet.setOnClickListener {
            showDeletImage()
        }
        btnCancelEA.setOnClickListener {
            finish()
            showQuest()
        }
    }
    private fun validateQ(){
        if((intent.getStringExtra("numQuest")).toString() == "null"){
            txtLyautNumQuest.visibility = View.VISIBLE
        }
        else{
            quest = (intent.getStringExtra("numQuest")).toString()
            loadData()
        }
    }
    private fun loadData(){
        ///ABRIR LA BASE DE DATOS Y LLENAR LOS CAMPOS CON LOS DATOS GUARDADOS/////
        db.collection("Quizz").document(quest)
            .get().addOnSuccessListener { documento ->
            val img = documento.get("Url").toString()
                Glide.with(this)
                    .load(img)
                    .placeholder(R.drawable.ic_profile_unload)
                    .fallback(R.drawable.ic_profile_error)
                    .fitCenter()
                    .into(imgQuest)
                //////CARGO DATOS/////////
                txtQuestionEA.setText(documento.getString("question")).toString()
                textQuestA1.setText(documento.getString("opt1")).toString()
                textQuestA2.setText(documento.getString("opt2")).toString()
                textQuestA3.setText(documento.getString("opt3")).toString()
                imgUrl = documento.getString("Url").toString()
                codeImg = documento.getString("CodeImg").toString()
                if(documento.getString("answer") == documento.getString("opt1")){
                    radioButtonA1.isChecked = true
                }
                else if(documento.getString("answer") == documento.getString("opt2")){
                    radioButtonA2.isChecked = true
                }
                else{
                    radioButtonA3.isChecked = true
                }
        }
    }
    private fun picUpload(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_INTENT)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_INTENT) {
            if(resultCode == RESULT_OK){
                ////////////////ALERT DIALOG////////////////
                deletimg()
                val fileUri = data!!.data
                val Storage: StorageReference = FirebaseStorage.getInstance().reference.child("CuestionImages")
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
                            .into(imgQuest)
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
    private fun deletimg(){
        valuesdatabase()
        val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child("CuestionImages/")
        val desertRef : StorageReference = storageRef.child(codeImg)
        desertRef.delete().addOnSuccessListener {
        }.addOnFailureListener {
        }
    }
    private fun deletimgBtn(){
        valuesdatabase()
        val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child("CuestionImages/")
        val desertRef : StorageReference = storageRef.child(codeImg)
        desertRef.delete().addOnSuccessListener {
            FirebaseFirestore.getInstance()
                .collection("Quizz").document(quest)
                .update(
                    hashMapOf(
                        "Url" to "",
                        "CodeImg" to "codImg"
                    ) as Map<String, Any>
                ).addOnSuccessListener { Toast.makeText(this, "La imágen se a borrado con exito.", Toast.LENGTH_SHORT).show() }
        }.addOnFailureListener {
            Toast.makeText(this, "Se produjo un Error al eliminar la Imágen", Toast.LENGTH_SHORT).show()
        }

    }
    private fun sendDataImg(img:String, codImg:String){
        FirebaseFirestore.getInstance()
            .collection("Quizz").document(quest)
            .update(
                hashMapOf(
                    "Url" to img,
                    "CodeImg" to codImg
                ) as Map<String, Any>
            )
        valuesdatabase()
    }
    private fun valuesdatabase(){
        db.collection("Quizz").document(quest)
            .get().addOnSuccessListener { documento ->
            imgUrl = documento.getString("Url").toString()
            codeImg = documento.getString("CodeImg").toString()
        }
    }
    private fun sendData(){
        FirebaseFirestore.getInstance()
            .collection("Quizz").document(quest)
            .update(
                hashMapOf(
                    "question" to txtQuestionEA.text.toString(),
                    "opt1" to textQuestA1.text.toString(),
                    "opt2" to textQuestA2.text.toString(),
                    "opt3" to textQuestA3.text.toString(),
                    "answer" to ands
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
    private fun setdData(){
        FirebaseFirestore.getInstance()
            .collection("Quizz").document(quest)
            .set(
                hashMapOf(
                    "numQuest" to quest,
                    "question" to txtQuestionEA.text.toString(),
                    "opt1" to textQuestA1.text.toString(),
                    "opt2" to textQuestA2.text.toString(),
                    "opt3" to textQuestA3.text.toString(),
                    "answer" to ands
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
    private fun showQuest(){
        finish()
        val showTestIntent = Intent(this, ViewCuestionsActivity::class.java).apply{
        }
        startActivity(showTestIntent)
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exito!!")
        builder.setMessage("Los Datos se han cargado Correctamente")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            showQuest()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun validateQuest(){
        FirebaseFirestore.getInstance().collection("Quizz").document(quest)
            .get().addOnSuccessListener { documento ->
                if (documento.getString("numQuest").toString() == "null") {
                    setdData()
                } else {
                    showAlertQ()
                }
            }

    }
    private fun showAlertQ(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta!!")
        builder.setMessage("La Pregunta que desea ingresar ya existe si continua se sobre escriviran los datos ya existentes.")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            finish()
            sendData()
        }
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun validateIma(){
        FirebaseFirestore.getInstance().collection("Quizz").document(quest)
            .get().addOnSuccessListener { documento ->
                if (documento.getString("numQuest").toString() == "null") {
                    picUpload()
                } else {
                    showAlertImage()
                }
            }
    }
    private fun showAlertImage(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta!!")
        builder.setMessage("La Pregunta que desea ingresar ya existe si continua se sobre escriviran los datos ya existentes.")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            finish()
            picUpload()
        }
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showDeletImage(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta!!")
        builder.setMessage("Esta seguro que desea realizar esta operación?")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            deletimgBtn()
        }
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}