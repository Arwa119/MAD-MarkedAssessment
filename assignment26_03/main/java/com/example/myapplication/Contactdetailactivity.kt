package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var btnChangePic: Button
    private lateinit var btnBack: Button

    private lateinit var contact: Contact
    private val PICK_IMAGE_REQUEST = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactdetailactivity)

        imgProfile = findViewById(R.id.detailImgProfile)
        tvName = findViewById(R.id.detailTvName)
        tvPhone = findViewById(R.id.detailTvPhone)
        btnChangePic = findViewById(R.id.btnChangePic)
        btnBack = findViewById(R.id.btnBack)

        contact = intent.getParcelableExtra("contact")!!

        tvName.text = contact.name
        tvPhone.text = contact.phone

        if (contact.profilePicUri != null) {
            imgProfile.setImageURI(Uri.parse(contact.profilePicUri))
        } else {
            imgProfile.setImageResource(R.drawable.ic_default_profile)
        }

        btnChangePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnBack.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("updatedContact", contact)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                // Take persistent permission
                contentResolver.takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                contact = contact.copy(profilePicUri = imageUri.toString())
                imgProfile.setImageURI(imageUri)
            }
        }
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("updatedContact", contact)
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }
}