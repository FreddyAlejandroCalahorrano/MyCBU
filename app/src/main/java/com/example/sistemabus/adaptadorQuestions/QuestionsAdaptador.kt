package com.example.sistemabus.adaptadorQuestions


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistemabus.R

class QuestionsAdaptador (private val questList: ArrayList<Questions>,
                          private val questClickListener: onClickListener
                            ) : RecyclerView.Adapter<QuestionsAdaptador.MyViewHolder>() {

    interface onClickListener {
        fun onDataclick(numQuest: String?)
    }
    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int
    ): QuestionsAdaptador.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.viewquestions,parent,false)

        return MyViewHolder(itemView)

    }
    override fun onBindViewHolder(holder: QuestionsAdaptador.MyViewHolder, position: Int) {
        val questions : Questions = questList[position]
        holder.imageW.visibility = View.VISIBLE
        holder.quest1.setBackgroundColor(Color.TRANSPARENT)
        holder.quest2.setBackgroundColor(Color.TRANSPARENT)
        holder.quest3.setBackgroundColor(Color.TRANSPARENT)
        holder.numQuest.text = "Pregunta. " + (position + 1).toString()
        holder.question.text = questions.question
        holder.quest1.text = questions.opt1
        holder.quest2.text = questions.opt2
        holder.quest3.text = questions.opt3
            if(questions.Url == "") {
                holder.imageW.visibility = View.GONE
                if (questions.answer == questions.opt1) {
                    holder.quest1.setBackgroundColor(Color.GREEN)
                }
                else if (questions.answer == questions.opt2) {
                    holder.quest2.setBackgroundColor(Color.GREEN)
                }
                else if (questions.answer == questions.opt3) {
                    holder.quest3.setBackgroundColor(Color.GREEN)
                }
            }
            else{
                if (questions.answer == questions.opt1) {
                    holder.quest1.setBackgroundColor(Color.GREEN)
                }
                else if (questions.answer == questions.opt2) {
                    holder.quest2.setBackgroundColor(Color.GREEN)
                }
                else if (questions.answer == questions.opt3) {
                    holder.quest3.setBackgroundColor(Color.GREEN)
                }
                Glide.with(holder.itemView)
                    .load(questions.Url)
                    .placeholder(R.drawable.ic_profile_unload)
                    .fallback(R.drawable.ic_profile_error)
                    .fitCenter()
                    .into(holder.imageW)
            }
        holder.itemView.setOnClickListener { questClickListener.onDataclick(questions.numQuest )}
    }

    override fun getItemCount(): Int {
        return questList.size
    }
    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val numQuest : TextView = itemView.findViewById(R.id.txtNumQ)
        val question : TextView = itemView.findViewById(R.id.txtQuestion)
        val quest1 : TextView = itemView.findViewById(R.id.txtQuest1)
        val quest2 : TextView = itemView.findViewById(R.id.txtQuest2)
        val quest3 : TextView = itemView.findViewById(R.id.txtQuest3)
        val imageW : ImageView = itemView.findViewById(R.id.imageViewQuest)

    }

}