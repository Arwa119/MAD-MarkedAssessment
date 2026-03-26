package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private lateinit var etSearch: EditText
    private lateinit var btnSort: Button
    private lateinit var btnGridView: Button
    private lateinit var btnAddContact: Button

    // Shared contact list (static so other activities can update it)
    companion object {
        val contactList: MutableList<Contact> = mutableListOf(
            Contact(1, "Ali Hassan", "0300-1234567"),
            Contact(2, "Sara Ahmed", "0311-9876543"),
            Contact(3, "Bilal Khan", "0321-4561230"),
            Contact(4, "Ayesha Malik", "0333-7654321"),
            Contact(5, "Usman Tariq", "0345-1112223")
        )
    }

    private var filteredList: MutableList<Contact> = mutableListOf()
    private var isAscending = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        etSearch = findViewById(R.id.etSearch)
        btnSort = findViewById(R.id.btnSort)
        btnGridView = findViewById(R.id.btnGridView)
        btnAddContact = findViewById(R.id.btnAddContact)

        filteredList = contactList.toMutableList()

        adapter = ContactAdapter(filteredList) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            intent.putExtra("contact", contact)
            startActivityForResult(intent, 100)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Search
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }
        })

        // Sort button
        btnSort.setOnClickListener {
            showSortDialog()
        }

        // Grid View button
        btnGridView.setOnClickListener {
            val intent = Intent(this, GridViewActivity::class.java)
            startActivity(intent)
        }

        // Add Contact
        btnAddContact.setOnClickListener {
            showAddContactDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        filterContacts(etSearch.text.toString())
    }

    private fun filterContacts(query: String) {
        filteredList = if (query.isEmpty()) {
            contactList.toMutableList()
        } else {
            contactList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        adapter.updateList(filteredList)
    }

    private fun showSortDialog() {
        val options = arrayOf("Ascending (A-Z)", "Descending (Z-A)")
        AlertDialog.Builder(this)
            .setTitle("Sort Contacts")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        isAscending = true
                        contactList.sortBy { it.name }
                    }
                    1 -> {
                        isAscending = false
                        contactList.sortByDescending { it.name }
                    }
                }
                filterContacts(etSearch.text.toString())
            }
            .show()
    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_gridadapter, null)
        val etName = dialogView.findViewById<EditText>(R.id.etDialogName)
        val etPhone = dialogView.findViewById<EditText>(R.id.etDialogPhone)

        AlertDialog.Builder(this)
            .setTitle("Add Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    val newId = (contactList.maxOfOrNull { it.id } ?: 0) + 1
                    contactList.add(Contact(newId, name, phone))
                    filterContacts(etSearch.text.toString())
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val updated = data?.getParcelableExtra<Contact>("updatedContact")
            updated?.let { updatedContact ->
                val index = contactList.indexOfFirst { it.id == updatedContact.id }
                if (index != -1) {
                    contactList[index] = updatedContact
                }
                filterContacts(etSearch.text.toString())
            }
        }
    }
}