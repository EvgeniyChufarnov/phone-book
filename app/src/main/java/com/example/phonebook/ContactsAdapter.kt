package com.example.phonebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.databinding.ItemContactBinding

class ContactsAdapter :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private var dataSet = emptyList<ContactEntity>()

    fun setData(data: List<ContactEntity>) {
        dataSet = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(viewGroup.context).inflate(R.layout.item_contact, viewGroup, false)
    ) {
        private var binding = ItemContactBinding.bind(itemView)

        fun bind(contact: ContactEntity) {
            binding.nameTextView.text = contact.name
            binding.phoneTextView.text = contact.phoneNumber
        }
    }
}