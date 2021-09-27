package com.example.sistemabus.inicio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sistemabus.R
import com.example.sistemabus.admin.adminActivity
import com.example.sistemabus.cond.MenuCondActivity
import com.example.sistemabus.prop.MenuPropActivity
import com.example.sistemabus.user.UsuarioActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_auth.*



class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.ThemeSistemaBus)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        setup()
    }

    private fun setup() {
        title = " Autenticación"

        logInButton.setOnClickListener {
            showRegist()
        }

        sigInButton.setOnClickListener {
            if (emailEditText.text!!.isEmpty()) {
                Toast.makeText(this, "Ingrese un Correo Electronico", Toast.LENGTH_LONG).show()
            }
            else if (passwordEditText.text!!.isEmpty()){
                Toast.makeText(this, "Ingrese una Contraseña", Toast.LENGTH_LONG).show()
            }
            else{
                SingInUser()
            }
        }
        btnUsuario.setOnClickListener {
            showUser()
        }

        recpasswdTxt.setOnClickListener {
            showRecPasswd()
        }
        txtGuiadeU.setOnClickListener {
            showGuiaU()
        }

    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error Usuario o contraseña Incorrectos")
        builder.setPositiveButton("Aceptar" , null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun SingInUser(){

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(emailEditText.text.toString(),
                passwordEditText.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    FirebaseFirestore.getInstance().collection("Admin")
                            .document(emailEditText.text.toString()).get().addOnSuccessListener {
                                if (it.exists()){
                                    finish()
                                    showAdmin()
                                }
                            else{
                                    if(FirebaseAuth.getInstance().currentUser?.isEmailVerified == false){
                                        Toast.makeText(this,"El correo no ha sido no verificado", Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        FirebaseFirestore.getInstance().collection("Conductor")
                                            .document(emailEditText.text.toString()).get().addOnSuccessListener {
                                                if (it.exists()) {
                                                    finish()
                                                    showMenuCond()
                                                }
                                                else{
                                                    FirebaseFirestore.getInstance().collection("Propietario")
                                                        .document(emailEditText.text.toString()).get().addOnSuccessListener {
                                                            if (it.exists()) {
                                                                finish()
                                                                showMenuProp()
                                                            }
                                                        }
                                                }
                                            }
                                    }
                                }
                        }
                }
                else{
                showAlert()
                }
        }

    }

    private fun showMenuCond(){
        finish()
        val MenuCondIntent = Intent(this, MenuCondActivity::class.java).apply{
        }
        startActivity(MenuCondIntent)
    }
    private fun showMenuProp(){
        finish()
        val MenuPropIntent = Intent(this, MenuPropActivity::class.java).apply {
        }
        startActivity(MenuPropIntent)
    }
    private fun showRegist(){
        finish()
        val RegistIntent = Intent(this, RegistActivity::class.java).apply{
        }
        startActivity(RegistIntent)
    }
    private fun showRecPasswd(){
        finish()
        val RecPasswdIntent = Intent(this, RecPasswdActivity::class.java).apply {
        }
        startActivity(RecPasswdIntent)
    }
    private fun showUser(){
        finish()
        val UsuarioIntent = Intent(this, UsuarioActivity::class.java).apply {
        }
        startActivity(UsuarioIntent)
    }
    private fun showAdmin(){
        finish()
        val AdminIntent = Intent(this, adminActivity::class.java).apply {
        }
        startActivity(AdminIntent)
    }
    private fun showGuiaU(){
        finish()
        val GuiaIntent = Intent(this, GiaActivity::class.java).apply {
        }
        startActivity(GuiaIntent)
    }
}

