package com.example.sistemabus.cond

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_test.*
import java.util.Random

class testActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    var contQ = 0
    var contP = 0
    var contPE = 0
    var ands = ""
    var numPreg = "0"
    var list = arrayOfNulls<Int>(20)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId.equals(R.id.radioButtonEA1)){
                ands= radioButtonEA1.text.toString()
            }
            else if(checkedId.equals(R.id.radioButtonEA2)){
                ands = radioButtonEA2.text.toString()
            }
            else if(checkedId.equals(R.id.radioButtonEA3)){
                ands = radioButtonEA3.text.toString()
            }
        }
        setup()
        btnSig.setOnClickListener {
            checkAnswer()
            loadQuestion()
        }
        btnFinTest.setOnClickListener {
            checkAnswer1()
        }
    }
    private fun setup(){
        title =  "Cuestionario"
        layudImg.visibility = View.GONE
        btnFinTest.visibility = View.GONE
        loadQuestion()
    }
    private fun loadQuestion(){
        if (contQ<=20){
            contQ = contQ+1
            radioGroup.clearCheck()
            random()

        }
        if (contQ==20){
            btnSig.visibility = View.GONE
            btnFinTest.visibility = View.VISIBLE
        }
    }
    private fun loadData(numPregunta: String) {
        numPreg=numPregunta
        layudImg.visibility = View.GONE
        textPreguntaid.setText("Pregunta "+ contQ).toString()
        ///ABRIR LA BASE DE DATOS Y LLENAR LOS CAMPOS CON LOS DATOS GUARDADOS/////
        db.collection("Quizz").document(this.numPreg).get().addOnSuccessListener { documento ->
            val img = documento.get("Url").toString()
            if(img == ""){
                //////CARGO DATOS/////////
                textQuestion.setText(documento.getString("question")).toString()
                radioButtonEA1.setText(documento.getString("opt1")).toString()
                radioButtonEA2.setText(documento.getString("opt2")).toString()
                radioButtonEA3.setText(documento.getString("opt3")).toString()
            }
            else{
                layudImg.visibility = View.VISIBLE
                /////////CARGO LA IMAGEN ////////
                Glide.with(this)
                    .load(img)
                    .placeholder(R.drawable.ic_profile_unload)
                    .fallback(R.drawable.ic_profile_error)
                    .fitCenter()
                    .into(imageViewQ)
                //////CARGO DATOS/////////
                textQuestion.setText(documento.getString("question")).toString()
                radioButtonEA1.setText(documento.getString("opt1")).toString()
                radioButtonEA2.setText(documento.getString("opt2")).toString()
                radioButtonEA3.setText(documento.getString("opt3")).toString()
            }
        }
    }
    private fun checkAnswer(){
        db.collection("Quizz").document(numPreg).get().addOnSuccessListener { documento ->
            if(ands.equals(documento.getString("answer").toString())){
                contP=contP+1
            }
            else
            {
                contPE=contPE+1
            }
        }
    }
    private fun checkAnswer1(){
        db.collection("Quizz").document(numPreg).get().addOnSuccessListener { documento ->
            if(ands.equals(documento.getString("answer").toString())){
                contP=contP+1
                showAlert(contP)
            }
            else
            {
                contPE=contPE+1
                showAlert(contP)
            }
        }
    }
    private fun showAlert(valP : Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Test Finalizado")
        builder.setMessage("Su calificacion es " + contP + "/20")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            calculate(valP)
            finish()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun random(){
        var x = 0
        var numero = Random().nextInt(20)+1
        for(i in 0 until 20){
            if(list[i] == numero){
                 x = 1
            }
        }
        if(x == 1){
            random()
        }
        else{
            list[contQ-1] = numero
            loadData(numero.toString())
        }
    }
    private fun calculate(valP : Int){
        val user = Firebase.auth.currentUser
        val email = user?.email
        var numCuest : Int
        var score : Float
        var res : Float
        db.collection("Conductor").document(email.toString()).get().addOnSuccessListener { documento ->
            numCuest = documento.getString("numC")?.toInt() ?: 0
            score = (documento.getString("puntaje")?.toFloat() ?: 0) as Float
            numCuest = numCuest + 1
            score = score + (valP).toFloat()
            res = score/numCuest
            sendScore(res, numCuest)
        }

    }
    private fun sendScore(res : Float, numCuest : Int){
        val user = Firebase.auth.currentUser
        val email = user?.email
        FirebaseFirestore.getInstance()
            .collection("Conductor").document(email.toString())
            .update(
                hashMapOf(
                    "numC" to numCuest.toString(),
                    "puntaje" to res.toString()
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Puntaje actualizado!!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"Error al actualizar los Datos!!", Toast.LENGTH_LONG).show()
                }
            }
    }
}