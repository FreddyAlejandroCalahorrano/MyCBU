package com.example.sistemabus.inicio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sistemabus.R
import kotlinx.android.synthetic.main.activity_gia.*

class GiaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gia)
        setup()
        btnRegAuth.setOnClickListener {
            showAuth ()
        }
    }
    private fun setup(){
        title = "Guía de Usuário"

    }
    private fun showAuth (){
        finish()
        val AuthIntent = Intent(this, AuthActivity::class.java).apply{
        }
        startActivity(AuthIntent)
    }
}