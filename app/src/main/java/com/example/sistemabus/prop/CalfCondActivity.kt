package com.example.sistemabus.prop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sistemabus.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_calf_cond.*

class CalfCondActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    var ands = ""
    var conductor = ""
    var contC = 0
    var puntC : Float = 0.0f
    var nomPropie = ""
    var numuni = ""
    var descripUsuario = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calf_cond)
        spinnerWiew()
        radioGroupCC.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId.equals(R.id.radioButton1CC)){
                ands= radioButton1CC.text.toString()
            }
            else if(checkedId.equals(R.id.radioButton2CC)){
                ands = radioButton2CC.text.toString()
            }
            else if(checkedId.equals(R.id.radioButton3CC)){
                ands = radioButton3CC.text.toString()
            }
            else if(checkedId.equals(R.id.radioButton4CC)){
                ands = radioButton4CC.text.toString()
            }
            else if(checkedId.equals(R.id.radioButton5CC)){
                ands = radioButton5CC.text.toString()
            }
        }
        setup()
    }
    private fun setup(){
        title = "Reportar Conductor"
        loadUni()
        btnEnviarCC.setOnClickListener {
            var x = 0
            validate(x)
        }
        btnCncelCC.setOnClickListener {
            finish()
            showMenuProp()
        }
        DeletCC.setOnClickListener {
            shoAlertFirstDelete()
        }
    }
    private fun spinnerWiew() {

        val items = listOf("Positivo", "Negativo")
        val adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        autoCompleteTextCC.setAdapter(adaptador)

    }
    private fun loadUni(){
        val user = Firebase.auth.currentUser
        val email = user?.email
        db.collection("Propietario").document(email.toString()).get()
            .addOnSuccessListener { documento ->
                numuni = documento.getString("NumerodeUnidad").toString()
                loadCond(numuni)
                nomPropie = documento.getString("Nombre").toString() + " " + documento.getString("Apellido").toString()
            }
    }
    private fun loadCond(numuni : String){
        db.collection("Unidad").document(numuni).get()
            .addOnSuccessListener { documento ->
                loadDataCond(documento.getString("Conductor").toString())
            }
    }
    private fun loadDataCond(cond : String){
        db.collection("Conductor").document(cond).get().addOnSuccessListener { documento ->
            textNomCondCalf.setText(documento.getString("Nombre") + " " + documento.getString("Apellido")).toString()
            conductor = documento.getString("email").toString()
            contC = documento.getString("ContComent")?.toInt() ?: 0
            puntC = (documento.getString("puntaje")?.toFloat() ?: 0) as Float
        }
    }
    private fun validate(x : Int){
        if(autoCompleteTextCC.text.isEmpty()){
            Toast.makeText(this, "Por favor seleccione un tipo de Reporte", Toast.LENGTH_SHORT).show()
        }
        else if (ands.isEmpty()){
            Toast.makeText(this, "Por favor seleccione una puntuación", Toast.LENGTH_SHORT).show()
        }
        else{
         calf(x)
        }

    }
    private fun calf(x : Int){
        var valor: Float
        var total: Float
        if(x == 1){
            ///////////////-Eliminar Conductor-//////////
            if((autoCompleteTextCC.text.toString()).equals("Positiva")){
                if(ands.equals("1")){
                    valor = (1.0).toFloat()
                }
                else if(ands.equals("2")){
                    valor = (2.0).toFloat()
                }
                else if(ands.equals("3")){
                    valor = (3.0).toFloat()
                }
                else if(ands.equals("4")){
                    valor = (4.0).toFloat()
                }
                else{
                    valor = (5.0).toFloat()
                }
                total = puntC + valor
                if(total > 20){
                    total = 20.0f
                    sentPunt(total, x)
                    deleteCond()
                }
                else{
                    sentPunt(total, x)
                    deleteCond()
                }

            }
            else{
                if(ands.equals("1")){
                    valor = (1).toFloat()
                }
                else if(ands.equals("2")){
                    valor = (2).toFloat()
                }
                else if(ands.equals("3")){
                    valor = (3).toFloat()
                }
                else if(ands.equals("4")){
                    valor = (4).toFloat()
                }
                else{
                    valor = (5).toFloat()
                }
                total = puntC - valor
                if(total<0){
                    total=0.0f
                    sentPunt(total, x)
                    deleteCond()
                }
                else{
                    sentPunt(total, x)
                    deleteCond()
                }

            }
        }
        else{
            if((autoCompleteTextCC.text.toString()).equals("Positiva")){
                if(ands.equals("1")){
                    valor = (1.0).toFloat()
                }
                else if(ands.equals("2")){
                    valor = (2.0).toFloat()
                }
                else if(ands.equals("3")){
                    valor = (3.0).toFloat()
                }
                else if(ands.equals("4")){
                    valor = (4.0).toFloat()
                }
                else{
                    valor = (5.0).toFloat()
                }
                total = puntC + valor
                if(total > 20){
                    total = 20.0f
                    sentPunt(total, x)
                }
                else{
                    sentPunt(total, x)
                }

            }
            else{
                if(ands.equals("1")){
                    valor = (1).toFloat()
                }
                else if(ands.equals("2")){
                    valor = (2).toFloat()
                }
                else if(ands.equals("3")){
                    valor = (3).toFloat()
                }
                else if(ands.equals("4")){
                    valor = (4).toFloat()
                }
                else{
                    valor = (5).toFloat()
                }
                total = puntC - valor
                if(total<0){
                    total=0.0f
                    sentPunt(total, x)
                }
                else{
                    sentPunt(total, x)
                }

            }
        }
    }
    private fun showMenuProp(){
        val showMPropIntent = Intent(this, MenuPropActivity::class.java).apply{
        }
        startActivity(showMPropIntent)
    }
    private fun sentPunt(total : Float, x : Int){
        contC += 1
        db.collection("Conductor").document(conductor).update(
            hashMapOf(
                "ContComent" to contC.toString(),
                "puntaje" to total.toString()
            ) as Map<String, Any>
        ).addOnCompleteListener {
            if(it.isSuccessful){
                sentComentC(x)
            }
            else{
                Toast.makeText(this, "Error al enviar comentario", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun sentComentC(x: Int){

        if(textComentCC.text.isNullOrEmpty()){
            descripUsuario = "No envió ningun comentario"
        }
        else{
            descripUsuario = textComentCC.text.toString()
        }
        db.collection("Conductor").document(conductor)
            .collection("Comentarios").document(contC.toString())
            .set(
                hashMapOf(
                    "ID" to contC.toString(),
                    "Nombre" to nomPropie,
                    "Comentario" to descripUsuario,
                    "Puntaje" to ands,
                    "TipoComent" to autoCompleteTextCC.text.toString()
                ) as Map<String, Any>
            ).addOnSuccessListener {
                if(x == 1){

                }
                else{
                    shoAlertComent()
                }
            }
    }
    private fun deleteCond(){
        db.collection("Unidad").document(numuni).update(
            hashMapOf(
                "Conductor" to "",
            ) as Map<String, Any>
        ).addOnCompleteListener {
            ValidateCond()
        }
    }
    private fun shoAlertComent(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Éxito!!")
        builder.setMessage("Reporte enviado con éxito")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            finish()
            showMenuProp()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun shoAlertDelete(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Éxito!!")
        builder.setMessage("Reporte enviado y Conductor removido de su Unidad")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            finish()
            showMenuProp()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun shoAlertFirstDelete(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡¡Alerta!!")
        builder.setMessage("Esta seguro que desea eliminar a su Conductor")
        builder.setPositiveButton("Aceptar")
        { dialog, id ->
            var x = 1
            validate(x)
        }
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun ValidateCond(){
        db.collection("Conductor").document(conductor)
            .update(
                hashMapOf(
                    "Contratado" to "No",
                ) as Map<String, Any>
            ).addOnCompleteListener {
                shoAlertDelete()
            }
    }
}