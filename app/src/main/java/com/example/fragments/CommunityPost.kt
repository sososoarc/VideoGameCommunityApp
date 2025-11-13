package com.example.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.identity.util.UUID
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CommunityPost : AppCompatActivity() {

    private lateinit var etContent: TextInputEditText
    private lateinit var btnPublicar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communitypost)

        etContent = findViewById(R.id.etContent)
        btnPublicar = findViewById(R.id.btnPublicar)

        btnPublicar.isEnabled = false

        etContent.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btnPublicar.isEnabled = !s.isNullOrBlank()
                }
                override fun afterTextChanged(s: android.text.Editable?) {}
            }
        )

        btnPublicar.setOnClickListener { publicar() }
    }

    private fun publicar() {
        val content = etContent.text?.toString()?.trim().orEmpty()

        if (content.isEmpty()) {
            etContent.error = "Escribe tu publicación"
            return
        }

        btnPublicar.isEnabled = false
        val postDao = DatabaseProvider.getDatabase(this).postDao() // ✅ con p minúscula

        lifecycleScope.launch(Dispatchers.IO) {
            postDao.insert(
                Post(
                    id = UUID.randomUUID().toString(),
                    name = "Usuario",
                    handle = "@usuario",
                    time = "",
                    content = content,
                    avatarRes = R.drawable.ic_launcher_foreground
                )
            )

            launch(Dispatchers.Main) {
                hideKeyboard()
                Toast.makeText(this@CommunityPost, "Publicado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    @SuppressLint("ServiceCast")
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.windowToken?.let { imm.hideSoftInputFromWindow(it, 0) }
    }
}
