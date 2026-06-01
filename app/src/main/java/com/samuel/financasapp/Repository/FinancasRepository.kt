package com.samuel.financasapp.Repository

import android.content.Context
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import com.samuel.financasapp.Util.Constantes
import com.samuel.financasapp.Util.ServicoModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class FinancasRepository(context: Context) {
    private val dbHelper = Database(context)
    private val sharedPref = context.getSharedPreferences(Constantes.SharedPreferencesConst.PREFS_NAME, MODE_PRIVATE)

    // --- Métodos do Dashboard (MainActivity) ---
    fun buscarResumo(): Map<String, Double> {
        return dbHelper.obterResumoFinanceiro()
    }

    // --- Métodos de Login (LoginActivity) ---
    fun buscarNomeUsuario(): String? {
        return sharedPref.getString(Constantes.SharedPreferencesConst.KEY_NOME_USUARIO, null)
    }

    fun salvarNomeUsuario(nome: String) {
        sharedPref.edit().putString(Constantes.SharedPreferencesConst.KEY_NOME_USUARIO, nome).apply()
    }

    // --- Métodos de Cadastro (AddActivity) ---
    fun salvarServico(nome: String, valor: Double, categoria: String, vencimento: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put(Constantes.DatabaseConstants.DESC, nome)
            put(Constantes.DatabaseConstants.VALOR, valor)
            put(Constantes.DatabaseConstants.CATEGORIA, categoria)
            put(Constantes.DatabaseConstants.VENCIMENTO, vencimento)
            put(Constantes.DatabaseConstants.ATIVO, 1) // 1 para Ativo
        }

        val resultado = db.insert(Constantes.DatabaseConstants.TABLE_NAME, null, valores)
        return resultado != -1L
    }

    fun buscarTodosServicos(): List<ServicoModel> {
        val lista = mutableListOf<ServicoModel>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${Constantes.DatabaseConstants.TABLE_NAME} WHERE ${Constantes.DatabaseConstants.ATIVO} = 1",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.ID))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.DESC))
                val valor = cursor.getDouble(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.VALOR))
                val cat = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.CATEGORIA))
                val venc = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.VENCIMENTO))
                val ativo = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.ATIVO))

                lista.add(ServicoModel(id, desc, valor, cat, venc, ativo))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    // Busca apenas os serviços onde Ativo = 1
    fun buscarServicosAtivos(): List<ServicoModel> {
        val lista = mutableListOf<ServicoModel>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${Constantes.DatabaseConstants.TABLE_NAME} WHERE ${Constantes.DatabaseConstants.ATIVO} = 1",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.ID))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.DESC))
                val valor = cursor.getDouble(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.VALOR))
                val cat = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.CATEGORIA))
                val venc = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.VENCIMENTO))
                val ativo = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.ATIVO))

                lista.add(ServicoModel(id, desc, valor, cat, venc, ativo))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    // CORRIGIDO: Faz o "Soft Delete" e limpa o ponto do mês abreviado nativo do sistema (ex: "jun." vira "jun")
    fun ocultarServico(id: Int): Boolean {
        val db = dbHelper.writableDatabase

        // Captura a data atual
        val hoje = LocalDate.now()
        val formatador = DateTimeFormatter.ofPattern("dd/MMM", Locale("pt", "BR"))

        // O .replace(".", "") remove o ponto final que o sistema injeta na abreviação do mês
        val dataFormatada = hoje.format(formatador)
            .replace(".", "")
            .replaceFirstChar { it.uppercase() }

        val valores = ContentValues().apply {
            put(Constantes.DatabaseConstants.ATIVO, 0) // Desativa o item
            put(Constantes.DatabaseConstants.DATA_CANCELAMENTO, dataFormatada) // Persiste a data limpa
        }

        val linhasAfetadas = db.update(
            Constantes.DatabaseConstants.TABLE_NAME,
            valores,
            "${Constantes.DatabaseConstants.ID} = ?",
            arrayOf(id.toString())
        )
        return linhasAfetadas > 0
    }

    // --- MÉTODOS DE EDIÇÃO ---

    // Busca um único serviço pelo ID para preencher os inputs no modo de alteração
    fun buscarServicoPorId(id: Int): ServicoModel? {
        val db = dbHelper.readableDatabase
        var servico: ServicoModel? = null

        val cursor = db.rawQuery(
            "SELECT * FROM ${Constantes.DatabaseConstants.TABLE_NAME} WHERE ${Constantes.DatabaseConstants.ID} = ?",
            arrayOf(id.toString())
        )

        if (cursor.moveToFirst()) {
            val desc = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.DESC))
            val valor = cursor.getDouble(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.VALOR))
            val cat = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.CATEGORIA))
            val venc = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.VENCIMENTO))
            val ativo = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.ATIVO))
            val dataCancelamento = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.DATA_CANCELAMENTO))

            servico = ServicoModel(id, desc, valor, cat, venc, ativo, dataCancelamento)
        }
        cursor.close()
        return servico
    }

    // Executa a alteração dos dados na linha correspondente do ID (SQL Update)
    fun atualizarServico(id: Int, nome: String, valor: Double, categoria: String, vencimento: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put(Constantes.DatabaseConstants.DESC, nome)
            put(Constantes.DatabaseConstants.VALOR, valor)
            put(Constantes.DatabaseConstants.CATEGORIA, categoria)
            put(Constantes.DatabaseConstants.VENCIMENTO, vencimento)
        }

        val linhasAfetadas = db.update(
            Constantes.DatabaseConstants.TABLE_NAME,
            valores,
            "${Constantes.DatabaseConstants.ID} = ?",
            arrayOf(id.toString())
        )
        return linhasAfetadas > 0
    }

    // --- MÉTODOS DO HISTÓRICO ---

    // Busca apenas os serviços ocultados (Ativo = 0) para o histórico
    fun buscarServicosCancelados(): List<ServicoModel> {
        val lista = mutableListOf<ServicoModel>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${Constantes.DatabaseConstants.TABLE_NAME} WHERE ${Constantes.DatabaseConstants.ATIVO} = 0",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.ID))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.DESC))
                val valor = cursor.getDouble(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.VALOR))
                val cat = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.CATEGORIA))
                val venc = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.VENCIMENTO))
                val ativo = cursor.getInt(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.ATIVO))
                val dataCancelamento = cursor.getString(cursor.getColumnIndexOrThrow(Constantes.DatabaseConstants.DATA_CANCELAMENTO))

                lista.add(ServicoModel(id, desc, valor, cat, venc, ativo, dataCancelamento))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    // Deleta fisicamente os itens cancelados do banco (Hard Delete)
    fun excluirDefinitivoCancelados(): Boolean {
        val db = dbHelper.writableDatabase
        val linhasAfetadas = db.delete(
            Constantes.DatabaseConstants.TABLE_NAME,
            "${Constantes.DatabaseConstants.ATIVO} = 0",
            null
        )
        return linhasAfetadas > 0
    }
}