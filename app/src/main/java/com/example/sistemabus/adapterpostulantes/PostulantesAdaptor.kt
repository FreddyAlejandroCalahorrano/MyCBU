package com.example.sistemabus.adapterpostulantes


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemabus.R
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class PostulantesAdaptor(private val postList: ArrayList<Postulantes>,
                         private val postClickListener: onClickListener
                         ) : RecyclerView.Adapter<PostulantesAdaptor.MyViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    interface onClickListener{
        fun onDataclick(email: String?)
    }
    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int
    ): PostulantesAdaptor.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.viewpostulantes,parent,false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: PostulantesAdaptor.MyViewHolder, position: Int) {
        val postulantes : Postulantes = postList[position]
        db.collection("Conductor").document(postulantes.email.toString()).get().addOnSuccessListener { documento ->
            val url = documento.getString("Url")
            holder.firstname.text = documento.getString("Nombre").toString()
            holder.lastname.text = documento.getString("Apellido").toString()
            holder.score.text = documento.getString("puntaje").toString()
            holder.descrip.text = documento.getString("Descripcion").toString()
            Glide.with(holder.itemView)
                .load(url)
                .placeholder(R.drawable.ic_profile_unload)
                .fallback(R.drawable.ic_profile_error)
                .fitCenter()
                .circleCrop()
                .into(holder.imageW)
            holder.itemView.setOnClickListener { postClickListener.onDataclick(postulantes.email) }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val firstname : TextView = itemView.findViewById(R.id.textNamePost)
        val lastname : TextView = itemView.findViewById(R.id.textApellidoPost)
        val score : TextView = itemView.findViewById(R.id.textPuntaje)
        val descrip : TextView = itemView.findViewById(R.id.textComentario)
        val imageW : ImageView = itemView.findViewById(R.id.imageViewPost)

    }

}