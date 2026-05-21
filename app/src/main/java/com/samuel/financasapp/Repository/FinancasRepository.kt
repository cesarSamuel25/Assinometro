package com.samuel.financasapp.Repository

import android.content.Context
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import com.samuel.financasapp.Util.Constantes
import com.samuel.financasapp.Util.ServicoModel

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

    // Adicione estes métodos dentro da sua classe FinancasRepository

    // 1. Busca apenas os serviços onde Ativo = 1
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

    // 2. Faz o "Soft Delete" mudando o status para 0
    fun ocultarServico(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = android.content.ContentValues().apply {
            put(Constantes.DatabaseConstants.ATIVO, 0) // Desativa o item
        }

        val linhasAfetadas = db.update(
            Constantes.DatabaseConstants.TABLE_NAME,
            valores,
            "${Constantes.DatabaseConstants.ID} = ?",
            arrayOf(id.toString())
        )
        return linhasAfetadas > 0
    }


}