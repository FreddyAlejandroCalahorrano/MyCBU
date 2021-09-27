package com.example.sistemabus.inicio


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sistemabus.R
import kotlinx.android.synthetic.main.activity_rec_passwd.*
import com.google.firebase.auth.FirebaseAuth

class RecPasswdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rec_passwd)

        setup()

    }
    private fun setup(){
        title = "Recuperar Contraseña"

        BtnRecPasswd.setOnClickListener {
            if(!emailText.text.isEmpty()){
                RecPasswd()
            }
            else{
                Toast.makeText(this,"Ingrese una Correo Electronico",Toast.LENGTH_SHORT).show()
            }
        }

        BtnCancelar.setOnClickListener {
            finish()
            val AuthIntent = Intent(this, AuthActivity::class.java).apply{
            }
            startActivity(AuthIntent)
        }
    }

    private fun RecPasswd(){
            FirebaseAuth.getInstance().setLanguageCode("es")
            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(emailText.text.toString()).addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this,"Se ha enviado un Correo para restablecer su contraseña.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this,"Usuario no Registrado", Toast.LENGTH_SHORT).show()
                    }
                }
    }

}