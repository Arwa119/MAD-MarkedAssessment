package com.example.myapplication
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class EventRegistrationActivity : AppCompatActivity() {

    // ---- Views ----
    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var spinnerEventType: Spinner
    private lateinit var btnPickDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var rgGender: RadioGroup
    private lateinit var ivProfileImage: ImageView
    private lateinit var btnUploadImage: Button
    private lateinit var cbTerms: CheckBox
    private lateinit var btnSubmit: Button

    // ---- Data ----
    private var selectedDate: String = ""
    private var selectedImageUri: Uri? = null

    // ---- Gallery Launcher ----
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            ivProfileImage.setImageURI(it)
            Toast.makeText(this, "Image selected successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_registration)

        supportActionBar?.title = "Event Registration"

        initViews()
        setupSpinner()
        setupDatePicker()
        setupImagePicker()
        setupSubmitButton()
    }

    private fun initViews() {
        etFullName      = findViewById(R.id.etFullName)
        etPhone         = findViewById(R.id.etPhone)
        etEmail         = findViewById(R.id.etEmail)
        spinnerEventType = findViewById(R.id.spinnerEventType)
        btnPickDate     = findViewById(R.id.btnPickDate)
        tvSelectedDate  = findViewById(R.id.tvSelectedDate)
        rgGender        = findViewById(R.id.rgGender)
        ivProfileImage  = findViewById(R.id.ivProfileImage)
        btnUploadImage  = findViewById(R.id.btnUploadImage)
        cbTerms         = findViewById(R.id.cbTerms)
        btnSubmit       = findViewById(R.id.btnSubmit)
    }

    private fun setupSpinner() {
        val eventTypes = listOf(
            "-- Select Event Type --",
            "Seminar",
            "Workshop",
            "Conference",
            "Webinar",
            "Cultural Event"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            eventTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEventType.adapter = adapter
    }

    private fun setupDatePicker() {
        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year  = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day   = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    tvSelectedDate.text    = "Selected: $selectedDate"
                    tvSelectedDate.setTextColor(
                        resources.getColor(android.R.color.holo_green_dark, theme)
                    )
                },
                year, month, day
            )
            // Sirf future dates allow karo
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.setTitle("Select Event Date")
            dpd.show()
        }
    }

    private fun setupImagePicker() {
        btnUploadImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun setupSubmitButton() {
        btnSubmit.setOnClickListener {
            if (validateForm()) {
                showConfirmationDialog()
            }
        }
    }

    private fun validateForm(): Boolean {

        val fullName = etFullName.text.toString().trim()
        val phone    = etPhone.text.toString().trim()
        val email    = etEmail.text.toString().trim()

        // 1. Full Name
        if (fullName.isEmpty()) {
            etFullName.error = "Full name is required"
            etFullName.requestFocus()
            return false
        }
        if (fullName.length < 3) {
            etFullName.error = "Name must be at least 3 characters"
            etFullName.requestFocus()
            return false
        }
        if (!fullName.matches(Regex("^[a-zA-Z ]+$"))) {
            etFullName.error = "Name should contain only letters"
            etFullName.requestFocus()
            return false
        }

        // 2. Phone Number
        if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
            etPhone.requestFocus()
            return false
        }
        if (phone.length < 10 || phone.length > 13) {
            etPhone.error = "Enter a valid phone number (10-13 digits)"
            etPhone.requestFocus()
            return false
        }

        // 3. Email
        if (email.isEmpty()) {
            etEmail.error = "Email address is required"
            etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email address"
            etEmail.requestFocus()
            return false
        }

        // 4. Event Type Spinner
        if (spinnerEventType.selectedItemPosition == 0) {
            Toast.makeText(this, "⚠️ Please select an Event Type", Toast.LENGTH_SHORT).show()
            return false
        }

        // 5. Event Date
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "⚠️ Please select an Event Date", Toast.LENGTH_SHORT).show()
            return false
        }

        // 6. Gender
        if (rgGender.checkedRadioButtonId == -1) {
            Toast.makeText(this, "⚠️ Please select your Gender", Toast.LENGTH_SHORT).show()
            return false
        }

        // 7. Terms & Conditions
        if (!cbTerms.isChecked) {
            Toast.makeText(
                this,
                " Please accept the Terms and Conditions",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true // Sab kuch theek hai!
    }

    private fun showConfirmationDialog() {
        val fullName   = etFullName.text.toString().trim()
        val phone      = etPhone.text.toString().trim()
        val email      = etEmail.text.toString().trim()
        val eventType  = spinnerEventType.selectedItem.toString()
        val genderView = findViewById<RadioButton>(rgGender.checkedRadioButtonId)
        val gender     = genderView.text.toString()

        val message = """
            Please confirm your registration:
            
            Name:  $fullName
            Phone: $phone
            Email: $email
            Event: $eventType
            Date:  $selectedDate
            Gender: $gender
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Confirm Registration")
            .setMessage(message)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton("✅ Yes, Submit") { _, _ ->
                navigateToConfirmation(fullName, phone, email, eventType, gender)
            }
            .setNegativeButton("❌ Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Registration cancelled", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun navigateToConfirmation(
        fullName: String,
        phone: String,
        email: String,
        eventType: String,
        gender: String
    ) {
        val intent = Intent(this, ConfirmationActivity::class.java).apply {
            putExtra("fullName",   fullName)
            putExtra("phone",      phone)
            putExtra("email",      email)
            putExtra("eventType",  eventType)
            putExtra("eventDate",  selectedDate)
            putExtra("gender",     gender)
            putExtra("imageUri",   selectedImageUri?.toString() ?: "")
        }
        startActivity(intent)
        finish()
    }
}