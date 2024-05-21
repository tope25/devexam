package exam.dev.examdev

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import exam.dev.examdev.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var mobileNumberEditText: EditText
    private lateinit var selectedDateTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var genderSpinner: Spinner
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fullNameEditText = binding.fullNameText
        emailEditText = binding.emailText
        mobileNumberEditText = binding.mobileNumberText

        selectedDateTextView = binding.dateOfBirth
        val selectDateButton: Button = binding.selectDateButton
        ageTextView = binding.age
        genderSpinner = binding.genderSpinner
        submitButton = binding.submitButton

        val genders = resources.getStringArray(R.array.gender_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = adapter
        binding.genderSpinner.setSelection(0, false)

        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val selectedGender = parent.getItemAtPosition(position) as String
                    handleSelectedGender(selectedGender)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        submitButton.setOnClickListener {
            submitForm()
        }

        fullNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val fullName = s.toString()
                val containsInvalidCharacters = fullName.any { !isCharacterValid(it) }

                if (containsInvalidCharacters) {
                    val filteredText = fullName.filter { isCharacterValid(it) }
                    fullNameEditText.setText(filteredText)
                    fullNameEditText.setSelection(filteredText.length)
                }
            }
        })

        emailEditText.addTextChangedListener { editable ->
            val email = editable.toString()
            if (isValidEmail(email)) {
                emailEditText.error = null
            } else {
                emailEditText.error = "Invalid email format"
            }
        }

        mobileNumberEditText.addTextChangedListener() { editable ->
            val mobileNumber = editable.toString()
            if (isValidMobileNumber(mobileNumber)) {
                mobileNumberEditText.error = null
            } else {
                mobileNumberEditText.error = "Invalid mobile number format"
            }
        }
    }

    private fun isCharacterValid(char: Char): Boolean {
        return char.isLetter() || char == ',' || char == '.' || char == ' '
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun isValidMobileNumber(mobileNumber: String): Boolean {
        val mobNumberRegex = Regex("^((\\+?\\d{11})|(\\+?63\\d{10}))$")
        return mobNumberRegex.matches(mobileNumber)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = formatDate(year, month, day)
                selectedDateTextView.text = selectedDate
                updateAge(year, month, day)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun updateAge(year: Int, month: Int, day: Int) {
        val currentDate = Calendar.getInstance()
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, day)

        var age = currentDate.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR)
        if (currentDate.get(Calendar.DAY_OF_YEAR) < selectedDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        ageTextView.text = "Age: $age"
        if (age < 18) {
            ageTextView.error = "Age must be greater than or equal to 18"
        } else {
            ageTextView.error = null
        }
    }
    private fun handleSelectedGender(selectedGender: String) {
        println("Selected gender: $selectedGender")
    }

    private fun submitForm() {
        val userData = UserData(
            fullNameEditText.text.toString(),
            emailEditText.text.toString(),
            mobileNumberEditText.text.toString(),
            selectedDateTextView.text.toString(),
            genderSpinner.selectedItem.toString()
        )

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.submitUserData(userData).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Submission successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Submission failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Void>, e: Throwable) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Submission failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}