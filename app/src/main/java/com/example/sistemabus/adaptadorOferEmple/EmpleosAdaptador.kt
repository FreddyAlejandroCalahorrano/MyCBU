package com.example.sistemabus.adaptadorOferEmple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemabus.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmpleosAdaptador(private val empList: ArrayList<Empleos>,
                       private val empClickListener: onClickListener
                        ) : RecyclerView.Adapter<EmpleosAdaptador.MyViewHolder>()
                        {

        interface onClickListener {
            fun onDataclick(email: String?, emailP : String?)
        }

        override fun onCreateViewHolder(

            parent: ViewGroup,
            viewType: Int
        ): EmpleosAdaptador.MyViewHolder {

            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.viewofempleo, parent, false)

            return MyViewHolder(itemView)

        }

        override fun onBindViewHolder(holder: EmpleosAdaptador.MyViewHolder, position: Int) {
            val emp: Empleos = empList[position]
            holder.title.text = emp.Titulo
            holder.salario.text = emp.Salario
            holder.hours.text = emp.Horas
            holder.descrip.text = emp.Descripcion
            val user = Firebase.auth.currentUser
            val email = user?.email
            holder.itemView.setOnClickListener {  }
            holder.buton.setOnClickListener { empClickListener.onDataclick(email, emp.emailP) }
        }

        override fun getItemCount(): Int {
            return empList.size
        }

        public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.txtTituloEmp)
            val salario: TextView = itemView.findViewById(R.id.txtSalarioC)
            val hours: TextView = itemView.findViewById(R.id.txtNumH)
            val descrip: TextView = itemView.findViewById(R.id.txtDescripC)
            val buton : Button = itemView.findViewById(R.id.btnPost)

        }
    }