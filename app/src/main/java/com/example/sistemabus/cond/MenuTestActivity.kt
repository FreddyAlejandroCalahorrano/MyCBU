package com.example.sistemabus.cond

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sistemabus.R
import com.example.sistemabus.admin.ViewCuestionsActivity
import kotlinx.android.synthetic.main.activity_menu_test.*


class MenuTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_test)
        setup()
        btnViewResp.setOnClickListener {
            showCuest()
        }
        btnRealTest.setOnClickListener {
            showTest()
        }
        btnRegresarMT.setOnClickListener {
            finish()
            showMenu()
        }
    }
    private fun setup(){
        title = "Menú Test"
    }
    private fun showMenu(){
        val MenuCondIntent = Intent(this, MenuCondActivity::class.java).apply{
        }
        startActivity(MenuCondIntent)
    }
    private fun showCuest(){
        val CuestIntent = Intent(this, ViewCuestionsActivity::class.java).apply{
        }
        startActivity(CuestIntent)
    }
    private fun showTest(){
        val testIntent = Intent(this, testActivity::class.java).apply{
        }
        startActivity(testIntent)
    }
}