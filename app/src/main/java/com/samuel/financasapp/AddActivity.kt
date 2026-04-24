package com.samuel.financasapp

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.databinding.ActivityAddBinding
import android.text.Editable
import android.text.TextWatcher
import java.text.NumberFormat
import java.util.Locale

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarDropdownCategorias()

        configurarMascaraMoeda()
    }

    private fun configurarDropdownCategorias() {
        val categorias = arrayOf("Streaming", "Saúde", "Aplicativos", "Outros")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categorias
        )

        binding.autoCompleteCategoria.setAdapter(adapter)
    }

    private fun configurarMascaraMoeda() {
        val editText = binding.editValorServico

        editText.addTextChangedListener(object : TextWatcher {
            private var atual = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != atual) {
                    editText.removeTextChangedListener(this)

                    val limpo = s.toString().replace(Regex("[^\\d]"), "")

                    if (limpo.isNotEmpty()) {
                        val valorDigitado = limpo.toDouble() / 100
                        val formatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valorDigitado)

                        atual = formatado
                        editText.setText(formatado)
                        editText.setSelection(formatado.length) // Mantém o cursor no final
                    }

                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    private fun obterValorLimpo(): Double {
        val texto = binding.editValorServico.text.toString()
        // Remove "R$", espaços e troca a vírgula por ponto
        val limpo = texto.replace(Regex("[R$\\s.]"), "").replace(",", ".")
        return limpo.toDoubleOrNull() ?: 0.0
    }
}