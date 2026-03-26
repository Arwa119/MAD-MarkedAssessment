package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gridviewactivity)

        recyclerView = findViewById(R.id.gridRecyclerView)

        adapter = GridAdapter(MainActivity.contactList) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            intent.putExtra("contact", contact)
            startActivityForResult(intent, 100)
        }

        // 2 columns grid
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.updateList(MainActivity.contactList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val updated = data?.getParcelableExtra<Contact>("updatedContact")
            updated?.let { updatedContact ->
                val index = MainActivity.contactList.indexOfFirst { it.id == updatedContact.id }
                if (index != -1) {
                    MainActivity.contactList[index] = updatedContact
                }
                adapter.updateList(MainActivity.contactList)
            }
        }
    }
}