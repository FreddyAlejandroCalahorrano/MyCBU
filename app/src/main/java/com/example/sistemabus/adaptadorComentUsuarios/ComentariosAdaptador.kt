package com.example.sistemabus.adaptadorComentUsuarios


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemabus.R
import java.util.ArrayList

class ComentariosAdaptador(private val comentList: ArrayList<Comentarios>,
                           private val comentClickListener: onClickListener
                         ) : RecyclerView.Adapter<ComentariosAdaptador.MyViewHolder>() {

    interface onClickListener {
        fun onDataclick(ID: String?)
    }

    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int
    ): ComentariosAdaptador.MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.viewcomentarios, parent, false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ComentariosAdaptador.MyViewHolder, position: Int) {
        val comentarios: Comentarios = comentList[position]
        holder.firstname.text = comentarios.Nombre.toString()
        holder.score.text = comentarios.Puntaje.toString()
        holder.coment.text = comentarios.Comentario.toString()
        holder.itemView.setOnClickListener { comentClickListener.onDataclick(comentarios.ID) }
    }

    override fun getItemCount(): Int {
        return comentList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstname: TextView = itemView.findViewById(R.id.textNamePost)
        val score: TextView = itemView.findViewById(R.id.textPuntaje)
        val coment: TextView = itemView.findViewById(R.id.textComentario)

    }
}

