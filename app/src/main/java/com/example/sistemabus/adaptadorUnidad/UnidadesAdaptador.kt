package com.example.sistemabus.adaptadorUnidad


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistemabus.R
import java.util.ArrayList


class UnidadesAdaptador(private val unidList: ArrayList<Unidades>,
                        private val comentClickListener: onClickListener
                         ) : RecyclerView.Adapter<UnidadesAdaptador.MyViewHolder>() {

    interface onClickListener {
        fun onDataclick(NumerodeUnidad: String?)
    }

    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int
    ): UnidadesAdaptador.MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.viewunidades, parent, false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: UnidadesAdaptador.MyViewHolder, position: Int) {
        val unidad: Unidades = unidList[position]
        holder.Nunidad.text = unidad.NumerodeUnidad.toString()
        holder.placa.text = unidad.Placa.toString()
        holder.cooperat.text = unidad.Cooperativa.toString()
        Glide.with(holder.itemView)
            .load(unidad.Url)
            .placeholder(R.drawable.ic_profile_unload)
            .fallback(R.drawable.ic_profile_error)
            .fitCenter()
            .into(holder.imageW)
        holder.itemView.setOnClickListener { comentClickListener.onDataclick(unidad.NumerodeUnidad) }
    }

    override fun getItemCount(): Int {
        return unidList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Nunidad: TextView = itemView.findViewById(R.id.textNumUniAD)
        val placa: TextView = itemView.findViewById(R.id.textPlacaAD)
        val cooperat: TextView = itemView.findViewById(R.id.textCoopeA)
        val imageW : ImageView = itemView.findViewById(R.id.imageViewUniAD)

    }
}

