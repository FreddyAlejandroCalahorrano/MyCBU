package com.example.sistemabus.unidad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import com.example.sistemabus.prop.LeydeTransitoActivity
import com.example.sistemabus.prop.MenuPropActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile_uni.*
import kotlinx.android.synthetic.main.activity_profile_uni.btnEditar
import kotlinx.android.synthetic.main.activity_profile_uni.btnRegresar
import kotlinx.android.synthetic.main.activity_profile_uni.imageProfile
import kotlinx.android.synthetic.main.activity_profile_uni.txtDesc

class ProfileUniActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_uni)
        loadData()
        setUp()

    }
    private fun setUp() {
        title = "Perfil Unidad"
        btnEditar.setOnClickListener {
            finish()
            showEdirUni()
        }
        btnRegresar.setOnClickListener {
            finish()
            showMenuProp()
        }
    }
    private fun loadData() {
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get()
            .addOnSuccessListener { documento ->
                val numuni = documento.getString("NumerodeUnidad").toString()
                if(numuni.isEmpty()) {
                    Toast.makeText(this, "Erro!!!", Toast.LENGTH_SHORT).show()
                }
                else{
                    db.collection("Unidad").document(numuni).get()
                        .addOnSuccessListener { documento ->
                            val url = documento.getString("Url")
                            txtMarca.setText(documento.getString("Marca")).toString()
                            txtCooperativa.setText(documento.getString("Cooperativa")).toString()
                            txtNumUni.setText(documento.getString("NumerodeUnidad")).toString()
                            txtAnio.setText(documento.getString("Anio")).toString()
                            txtPlaca.setText(documento.getString("Placa")).toString()
                            txtDesc.setText(documento.getString("Descripcion"))
                            ///////Cargar Imagen/////////////////
                            Glide.with(this)
                                .load(url)
                                .placeholder(R.drawable.ic_profile_unload)
                                .fallback(R.drawable.ic_profile_error)
                                .into(imageProfile)
                        }
                }
            }
    }
    private fun showMenuProp(){
        val viewMenuPIntent = Intent(this, MenuPropActivity::class.java).apply {
        }
        startActivity(viewMenuPIntent)
    }
    private fun showEdirUni(){
        val EditProfUniIntent = Intent(this, EditProfileUniActivity::class.java).apply {
        }
        startActivity(EditProfUniIntent)
    }
}