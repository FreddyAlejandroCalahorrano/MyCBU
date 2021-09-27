package com.example.sistemabus.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sistemabus.R
import com.example.sistemabus.inicio.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_admin.*

class adminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("Propietarios")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        setup()

    }
    private fun setup(){
        title = "Menú Administrador"
        btnEditTestAdmin.setOnClickListener(){
            finish()
            showTest()
        }
        btnCerrarSecionAdmin.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
            showAuth()
        }
        btnVerConduc.setOnClickListener {
            finish()
            showCond()
        }
        btnVerProp.setOnClickListener {
            finish()
            shoeProp()
        }
        btnUnidadA.setOnClickListener {
            finish()
            showUnit()
        }

    }
    private fun showTest(){
        val showTestIntent = Intent(this, ViewCuestionsActivity::class.java).apply{
        }
        startActivity(showTestIntent)
    }
    private fun showAuth(){
        val AuthIntent = Intent(this, AuthActivity::class.java).apply{
        }
        startActivity(AuthIntent)
    }
    private fun showCond(){
        val CondIntent = Intent(this, CondAdminActivity::class.java).apply{
        }
        startActivity(CondIntent)
    }
    private fun shoeProp(){
        val PropIntent = Intent(this, PropAdminActivity::class.java).apply{
        }
        startActivity(PropIntent)
    }
    private fun showUnit(){
        val UnitIntent = Intent(this, UnidadAdminActivity::class.java).apply{
        }
        startActivity(UnitIntent)
    }

}