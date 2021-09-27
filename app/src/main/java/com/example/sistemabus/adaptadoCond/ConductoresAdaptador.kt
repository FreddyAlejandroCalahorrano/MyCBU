package com.example.sistemabus.adaptadoCond


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import java.util.ArrayList


class ConductoresAdaptador(private val condList: ArrayList<Conductores>,
                           private val comentClickListener: onClickListener
                         ) : RecyclerView.Adapter<ConductoresAdaptador.MyViewHolder>() {

    interface onClickListener {
        fun onDataclick(email: String?)
    }

    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int
    ): ConductoresAdaptador.MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.viewconductores, parent, false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ConductoresAdaptador.MyViewHolder, position: Int) {
        val conductores: Conductores = condList[position]
        holder.firstname.text = conductores.Nombre.toString()
        holder.lastname.text = conductores.Apellido.toString()
        holder.ci.text = conductores.CI.toString()
        holder.tef.text = conductores.Teléfono.toString()
        Glide.with(holder.itemView)
            .load(conductores.Url)
            .placeholder(R.drawable.ic_profile_unload)
            .fallback(R.drawable.ic_profile_error)
            .fitCenter()
            .into(holder.imageW)
        holder.itemView.setOnClickListener { comentClickListener.onDataclick(conductores.email) }
    }

    override fun getItemCount(): Int {
        return condList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstname: TextView = itemView.findViewById(R.id.textNameCondA)
        val lastname: TextView = itemView.findViewById(R.id.textApellidoCondA)
        val ci: TextView = itemView.findViewById(R.id.textCI)
        val tef: TextView = itemView.findViewById(R.id.textTelf)
        val imageW : ImageView = itemView.findViewById(R.id.imageViewCond)

    }
}

