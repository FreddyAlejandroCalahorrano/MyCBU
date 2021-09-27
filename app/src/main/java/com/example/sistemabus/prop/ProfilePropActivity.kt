package com.example.sistemabus.prop


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.sistemabus.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile_prop.*
import com.google.firebase.ktx.Firebase
import jp.wasabeef.glide.transformations.BlurTransformation

class ProfilePropActivity : AppCompatActivity() {


    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_prop)
        setup()

    }

    private fun setup(){
        title = "Perfil Propietario"
        loadData()
        btnEditar.setOnClickListener{
            finish()
            showEditProp()
        }
        btnRegresar.setOnClickListener{
            finish()
            showMenuProp()
        }
    }
    private fun loadData(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get().addOnSuccessListener { documento ->
            val url = documento.getString("Url")
            txtNombre.setText(documento.getString("Nombre") + " " + documento.getString("Apellido")).toString()
            txtCi.setText(documento.getString("CI")).toString()
            txtEdad.setText(documento.getString("Edad")).toString()
            txtNumUni.setText(documento.getString("NumerodeUnidad")).toString()
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
    private fun showEditProp(){
        val EditProfPropIntent = Intent(this, EditProfilePropActivity::class.java).apply{
        }
        startActivity(EditProfPropIntent)
    }
    private fun showMenuProp(){
        val shoMenuPIntent = Intent(this, MenuPropActivity::class.java).apply{
        }
        startActivity(shoMenuPIntent)
    }
}

