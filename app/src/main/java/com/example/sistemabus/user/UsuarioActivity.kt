package com.example.sistemabus.user

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.sistemabus.R
import com.example.sistemabus.inicio.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_usuario.*

class UsuarioActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    var nomUsuario = ""
    var descripUsuario = ""
    var ands = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)
        radioGroupU.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId.equals(R.id.radioButtonU1)){
                ands= radioButtonU1.text.toString()
            }
            else if(checkedId.equals(R.id.radioButtonU2)){
                ands = radioButtonU2.text.toString()
            }
            else if(checkedId.equals(R.id.radioButtonU3)){
                ands = radioButtonU3.text.toString()
            }
            else if(checkedId.equals(R.id.radioButtonU4)){
                ands = radioButtonU4.text.toString()
            }
            else if(checkedId.equals(R.id.radioButtonU5)){
                ands = radioButtonU5.text.toString()
            }
        }
        spinnerWiew()
        setup()
        btnEnviarU.setOnClickListener {
            validation()
        }
        btnCncelU.setOnClickListener {
            finish()
            showAuth()
        }
        ptbPanic.setOnClickListener {
            if(textNumeroU.text!!.isEmpty()){
                Toast.makeText(this,"Debe Ingresar una Unidad", Toast.LENGTH_SHORT).show()
            }
            else{
                sendEmail()
            }
        }
    }
    private fun setup(){
        title = "Usuario"
    }
    private fun spinnerWiew() {

        val items = listOf("Positiva", "Negativa")
        val adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        autoCompleteTextU.setAdapter(adaptador)

    }
    private fun validation(){
        var email = "pruebaprop@yopmail.com"
        var contr = "123456"
        if(textNombreU.text!!.isEmpty()){
            nomUsuario = "Usuario Anónimo"
            if(textNumeroU.text!!.isEmpty()){
                Toast.makeText(this,"Debe Ingresar una Unidad", Toast.LENGTH_SHORT).show()
            }
            else if(autoCompleteTextU.text!!.isEmpty()){
                Toast.makeText(this,"Seleccione un Tipo de Calificación", Toast.LENGTH_SHORT).show()
            }
            else if (textComentU.text!!.isEmpty()){
                descripUsuario = "No realizo un Comentario"
                SingInUser(email, contr)
            }
            else{
                descripUsuario = textComentU.text.toString()
                SingInUser(email, contr)
            }
        }
        else if(textNumeroU.text!!.isEmpty()){
            Toast.makeText(this,"Debe Ingresar una Unidad", Toast.LENGTH_SHORT).show()
        }
        else if(autoCompleteTextU.text!!.isEmpty()){
            Toast.makeText(this,"Seleccione un Tipo de Calificacion", Toast.LENGTH_SHORT).show()
        }
        else if (textComentU.text!!.isEmpty()){
            nomUsuario = textNombreU.text.toString()
            descripUsuario = "No realizo un Comentario"
            SingInUser(email, contr)
        }
        else{
            nomUsuario = textNombreU.text.toString()
            descripUsuario = textComentU.text.toString()
            SingInUser(email, contr)
        }

    }
    private fun SingInUser(email :  String, contr : String){

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email,contr).addOnCompleteListener {
                if (it.isSuccessful) {
                    if(FirebaseAuth.getInstance().currentUser?.isEmailVerified == false){
                        Toast.makeText(this,"El correo no ha sido no verificado", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        loadCond()
                    }
                }
                else{
                    Toast.makeText(this, "Se ha producido un Error al enviar el Comentario." , Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun loadCond(){
        db.collection("Unidad").document(textNumeroU.text.toString()).get()
            .addOnSuccessListener { documento ->
                if(documento.getString("Conductor").isNullOrEmpty()){
                    Toast.makeText(this, "Esta Unidad no tiene un conductor asigando.", Toast.LENGTH_SHORT).show()
                }
                else{
                    loadNumCom(documento.getString("Conductor").toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al leer los datos de la Unidad", Toast.LENGTH_SHORT).show()
            }
    }
    private fun loadNumCom(cond : String){
        db.collection("Conductor").document(cond).get().addOnSuccessListener { documento ->
            var contC = documento.getString("ContComent")?.toInt() ?: 0
            contC = contC + 1
            var puntC = (documento.getString("puntaje")?.toFloat() ?: 0) as Float
            calScore(cond, contC, puntC)
        }
    }
    private fun calScore(cond : String, contC : Int, puntC : Float){
        var valor: Float
        var total: Float
        if((autoCompleteTextU.text.toString()).equals("Positiva")){
            if(ands.equals("1")){
                valor = (0.0001).toFloat()
            }
            else if(ands.equals("2")){
                valor = (0.0002).toFloat()
            }
            else if(ands.equals("3")){
                valor = (0.0003).toFloat()
            }
            else if(ands.equals("4")){
                valor = (0.0004).toFloat()
            }
            else{
                valor = (0.0005).toFloat()
            }
            total = puntC + valor
            sentComent(cond, contC, total)
        }
        else{
            if(ands.equals("1")){
                valor = (0.001).toFloat()
            }
            else if(ands.equals("2")){
                valor = (0.002).toFloat()
            }
            else if(ands.equals("3")){
                valor = (0.003).toFloat()
            }
            else if(ands.equals("4")){
                valor = (0.004).toFloat()
            }
            else{
                valor = (0.005).toFloat()
            }
            total = puntC - valor
            sentComent(cond, contC, total)
        }
        
    }
    private fun sentComent(cond : String, contC : Int, puntC : Float){
        db.collection("Conductor").document(cond)
            .collection("Comentarios").document(contC.toString())
            .set(
                hashMapOf(
                    "ID" to contC.toString(),
                    "Nombre" to nomUsuario,
                    "Comentario" to descripUsuario,
                    "Puntaje" to ands,
                    "TipoComent" to autoCompleteTextU.text.toString()
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Comentario enviado con Exito!!", Toast.LENGTH_SHORT).show()
                    sendNumComent(cond, contC, puntC)
                }
                else{
                    Toast.makeText(this,"Error al enviar comentario!!", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun sendNumComent(cond : String, contC : Int, puntC: Float){
        db.collection("Conductor").document(cond)
            .update(
                hashMapOf(
                    "ContComent" to contC.toString(),
                    "puntaje" to puntC.toString()
                ) as Map<String, Any>
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    FirebaseAuth.getInstance().signOut()
                    finish()
                    showAuth()

                } else {
                    Toast.makeText(this, "¡¡Error!!", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun showAuth(){
        val AuthIntent = Intent(this, AuthActivity::class.java).apply{
        }
        startActivity(AuthIntent)
    }
    private fun sendEmail(){
        val TO = arrayOf("aux@yopmail.com") //aquí pon tu correo

        val CC = arrayOf("")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
        emailIntent.putExtra(Intent.EXTRA_CC, CC)
        // Esto podrás modificarlo si quieres, el asunto y el cuerpo del mensaje
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EMERGENCIA")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Se solicita que se envíe de manera urgente una unidad de policia a la unidad " + textNumeroU.text.toString())

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email..."))
            finish()
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this,"No tienes clientes de email instalados.", Toast.LENGTH_SHORT
            ).show()
        }
    }
}