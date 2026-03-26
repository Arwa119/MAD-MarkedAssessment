package com.example.myapplication

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GridAdapter(
    private var contacts: MutableList<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgGridProfile)
        val tvName: TextView = itemView.findViewById(R.id.tvGridName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_contactadapter, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvName.text = contact.name

        if (contact.profilePicUri != null) {
            holder.imgProfile.setImageURI(Uri.parse(contact.profilePicUri))
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_default_profile)
        }

        holder.itemView.setOnClickListener { onItemClick(contact) }
    }

    override fun getItemCount(): Int = contacts.size

    fun updateList(newList: MutableList<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }
}