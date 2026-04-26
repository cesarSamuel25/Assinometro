package com.samuel.financasapp

import android.content.ContentValues
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samuel.financasapp.databinding.ActivityAddBinding
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
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

        setupListiners()
    }

    private fun setupListiners(){
        binding.btnSalvarServico.setOnClickListener { btnSalvar() }
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
        val limpo = texto.replace(Regex("[R$\\s.]"), "").replace(",", ".")
        return limpo.toDoubleOrNull() ?: 0.0
    }

    private fun salvarNoBanco() {
        // 1. Instancia o banco
        val dbHelper = Database(this)
        val db = dbHelper.writableDatabase

        // 2. Coleta os dados da tela
        val descricao = binding.editNomeServico.text.toString()
        val valor = obterValorLimpo()
        val categoria = binding.autoCompleteCategoria.text.toString()
        val vencimento = binding.editVencimento.text.toString().toIntOrNull() ?: 0

        // 3. Organiza os dados em um ContentValues (um "pacote" de dados para o SQLite)
        val valores = ContentValues().apply {
            put(Constantes.DatabaseConstants.DESC, descricao)
            put(Constantes.DatabaseConstants.VALOR, valor)
            put(Constantes.DatabaseConstants.CATEGORIA, categoria)
            put(Constantes.DatabaseConstants.VENCIMENTO, vencimento)
            put(Constantes.DatabaseConstants.ATIVO, 1) // 1 para ativo, 0 para inativo
        }

        // 4. Insere no banco
        val resultado = db.insert(Constantes.DatabaseConstants.TABLE_NAME, null, valores)

        if (resultado != -1L) {
            Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
            finish() // Volta para a tela anterior
        } else {
            Toast.makeText(this, "Erro ao salvar no banco", Toast.LENGTH_SHORT).show()
        }

        db.close()
    }

    private fun validarCampos(): Boolean {
        var todosValidos = true

        binding.apply {
            // Validação do Nome do Serviço
            if (editNomeServico.text.toString().isEmpty()) {
                inputLayoutNome.error = "Digite o nome do serviço"
                todosValidos = false
            } else {
                inputLayoutNome.error = null
            }

            // Validação do Valor
            if (editValorServico.text.toString().isEmpty()) {
                inputLayoutValor.error = "Digite o valor"
                todosValidos = false
            } else {
                inputLayoutValor.error = null
            }

            // Validação da Categoria
            if (autoCompleteCategoria.text.toString().isEmpty()) {
                inputLayoutCategoria.error = "Selecione uma categoria"
                todosValidos = false
            } else {
                inputLayoutCategoria.error = null
            }

            // Validação do Vencimento
            if (editVencimento.text.toString().isEmpty()) {
                inputLayoutVencimento.error = "Digite o dia"
                todosValidos = false
            } else {
                inputLayoutVencimento.error = null
            }
        }

        return todosValidos
    }

    private fun btnSalvar(){
        binding.btnSalvarServico.setOnClickListener {
            if (validarCampos()) {
                salvarNoBanco()
            } else {
                Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            }
        }
    }


}