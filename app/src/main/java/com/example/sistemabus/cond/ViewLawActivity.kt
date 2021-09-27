package com.example.sistemabus.cond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.example.sistemabus.R
import kotlinx.android.synthetic.main.activity_view_law.*

class ViewLawActivity : AppCompatActivity() {
    val BASE_URL = "https://www.obraspublicas.gob.ec/wp-content/uploads/downloads/2015/03/Decreto-Ejecutivo-No.-1196-de-11-06-2012-REGLAMENTO-A-LA-LEY-DE-TRANSPORTE-TERRESTRE-TRANSITO-Y-SEGURIDAD-VIA.pdf"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_law)
        setup()
        btnRegC.setOnClickListener {
            finish()
            viewMenu()
        }
    }
    private fun setup(){
        title = "Ley de Tránsito"

        //WebView///
        webViewLTC.webChromeClient = object : WebChromeClient(){

        }
        webViewLTC.webViewClient = object : WebViewClient(){

        }
        val settings = webViewLTC.settings
        settings.javaScriptEnabled = true
        webViewLTC.loadUrl("http://docs.google.com/gview?embedded=true&url=" + BASE_URL)
    }
    private fun viewMenu(){
        val viewMenuCIntent = Intent(this, MenuCondActivity::class.java).apply {
        }
        startActivity(viewMenuCIntent)
    }
}