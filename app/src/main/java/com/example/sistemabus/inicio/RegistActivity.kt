package com.example.sistemabus.inicio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sistemabus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_regist.*


class RegistActivity : AppCompatActivity() {
    var tipoCond = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regist)
        spinnerWiew()
        setup()
    }

    private fun spinnerWiew() {

        val items = listOf("Conductor", "Propietario")
        val adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        autoCompleteText.setAdapter(adaptador)

    }

    private fun setup(){
        title = "Registro"

        registrarButton.setOnClickListener {
            if(emailTextView.text!!.isEmpty()){
                Toast.makeText(this, "Ingrese una Correo Electronico", Toast.LENGTH_LONG).show()
            }
            else if(passView.text!!.isEmpty()){
                Toast.makeText(this, "Ingrese una Contraseña", Toast.LENGTH_LONG).show()
            }
            else if(passView2.text!!.isEmpty()){
                Toast.makeText(this, "Repita la contraseña.", Toast.LENGTH_SHORT).show()
            }
            else if(passView.text.toString() != passView2.text.toString()){
                Toast.makeText(this, "Las Contraseñas no coinciden.", Toast.LENGTH_LONG).show()
            }
            else if(autoCompleteText.text!!.isEmpty()){
                Toast.makeText(this,"Seleccione un Tipo de Usuario", Toast.LENGTH_SHORT).show()
            }
            else{
                registro()
            }

        }
        cancelButton.setOnClickListener {
            finish()
          showAuth()
        }
    }
    private fun registro(){
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(emailTextView.text.toString(),
                    passView.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        /////Envio de email de verificacion//////////
                        validateEmail()
                        tipoCond = autoCompleteText.text.toString()
                        if(tipoCond.equals("Conductor")){
                            createCod()
                            showAlertNewEmail()
                        }
                        else{
                            createProp()
                            showAlertNewEmail()
                        }
                    }
                    else{
                        showAlert()
                    }
                }
    }
    private fun showAuth(){
        val AuthIntent = Intent(this, AuthActivity::class.java).apply{
        }
        startActivity(AuthIntent)

    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error Usuario ya Registrado")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlertNewEmail(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exito!!")
        builder.setMessage("Se ha enviado un correo de verificacion al correo electrónico ingresado.")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            finish()
            showAuth()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun createCod(){
        FirebaseFirestore.getInstance()
            .collection(tipoCond).document(emailTextView.text.toString())
            .set(
                hashMapOf(
                    "Nombre" to textNombre.text.toString(),
                    "Apellido" to textApellido.text.toString(),
                    "CI" to textNumeroCedu.text.toString(),
                    "Edad" to "",
                    "Tipo de Licencia" to "",
                    "Teléfono" to "",
                    "email" to emailTextView.text.toString(),
                    "Descripcion" to "",
                    "Url" to "",
                    "numC" to "0",
                    "puntaje" to "0",
                    "ContComent" to "0",
                    "Contratado" to "No",
                    "CodeImg" to ""
                ))
    }
    private fun createProp(){
        FirebaseFirestore.getInstance()
            .collection(tipoCond).document(emailTextView.text.toString())
            .set(
                hashMapOf(
                    "Nombre" to textNombre.text.toString(),
                    "Apellido" to textApellido.text.toString(),
                    "CI" to textNumeroCedu.text.toString(),
                    "Edad" to "",
                    "NumerodeUnidad" to "",
                    "Teléfono" to "",
                    "email" to emailTextView.text.toString(),
                    "Descripcion" to "",
                    "Url" to "",
                    "CodeImg" to ""
                ))
    }
    private fun validateEmail(){
        FirebaseAuth.getInstance().setLanguageCode("es")
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
    }

}