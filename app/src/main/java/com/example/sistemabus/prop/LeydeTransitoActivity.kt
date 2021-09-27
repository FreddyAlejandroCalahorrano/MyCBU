package com.example.sistemabus.prop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.example.sistemabus.R
import com.example.sistemabus.cond.MenuCondActivity
import kotlinx.android.synthetic.main.activity_leyde_transito.*

class LeydeTransitoActivity : AppCompatActivity() {
    val BASE_URL = "https://www.obraspublicas.gob.ec/wp-content/uploads/downloads/2015/03/Decreto-Ejecutivo-No.-1196-de-11-06-2012-REGLAMENTO-A-LA-LEY-DE-TRANSPORTE-TERRESTRE-TRANSITO-Y-SEGURIDAD-VIA.pdf"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leyde_transito)
        setup()
        btnRegMP.setOnClickListener {
            finish()
            viewMenu()
        }
    }
    private fun setup(){
        title = "Ley de Tránsito"

        //WebView///
        webViewP.webChromeClient = object : WebChromeClient(){

        }
        webViewP.webViewClient = object : WebViewClient(){

        }
        val settings = webViewP.settings
        settings.javaScriptEnabled = true
        webViewP.loadUrl("http://docs.google.com/gview?embedded=true&url=" + BASE_URL)
    }
    private fun viewMenu(){
        val viewMenuPIntent = Intent(this, MenuPropActivity::class.java).apply {
        }
        startActivity(viewMenuPIntent)
    }
}