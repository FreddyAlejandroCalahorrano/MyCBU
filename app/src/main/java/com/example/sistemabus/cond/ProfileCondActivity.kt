package com.example.sistemabus.cond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile_cond.*


class ProfileCondActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_cond)
        setup()
        btnEditarC.setOnClickListener{
            finish()
            shoeEditCond()
        }
        btnRegresarC.setOnClickListener{
            showMenuCond()
            finish()
        }
    }
    private fun setup(){
        title = "Perfil Conductor"
        loadData()
    }

    private fun loadData(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Conductor").document(email.toString()).get().addOnSuccessListener { documento ->
            val url = documento.getString("Url")
            txtNombre.setText(documento.getString("Nombre") + " " + documento.getString("Apellido")).toString()
            txtCi.setText(documento.getString("CI")).toString()
            txtEdad.setText(documento.getString("Edad")).toString()
            txtTipLicen.setText(documento.getString("Tipo de Licencia")).toString()
            txtTelf.setText(documento.getString("Teléfono")).toString()
            txtMail.setText(documento.getString("email")).toString()
            txtDesc.setText(documento.getString("Descripcion"))
            ///////Cargar Imagen/////////////////
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_profile_unload)
                .fallback(R.drawable.ic_profile_error)
                .circleCrop()
                .into(imageProfile)
        }
    }
    private fun showMenuCond(){
        val MenuCondIntent = Intent(this, MenuCondActivity::class.java).apply{
        }
        startActivity(MenuCondIntent)
    }
    private fun shoeEditCond(){
        val EditProfCondIntent = Intent(this, EditProfileCondActivity::class.java).apply{
        }
        startActivity(EditProfCondIntent)
    }
}