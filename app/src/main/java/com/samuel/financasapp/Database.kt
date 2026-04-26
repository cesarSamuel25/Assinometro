package com.samuel.financasapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_FINANCAS)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    companion object{
        private const val DATABASE_NAME = "FinancasDB"
        private const val DATABASE_VERSION = 1

        private const val CREATE_TABLE_FINANCAS = """
            CREATE TABLE ${Constantes.DatabaseConstants.TABLE_NAME}(
            ${Constantes.DatabaseConstants.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Constantes.DatabaseConstants.DESC} TEXT NOT NULL,
            ${Constantes.DatabaseConstants.VALOR} REAL NOT NULL,
            ${Constantes.DatabaseConstants.CATEGORIA} TEXT NOT NULL,
            ${Constantes.DatabaseConstants.VENCIMENTO} INTEGER NOT NULL,
            ${Constantes.DatabaseConstants.ATIVO} INTEGER NOT NULL
            );
        """
    }

    fun obterResumoFinanceiro(): Map<String, Double> {
        val resumo = mutableMapOf<String, Double>()
        val db = this.readableDatabase

        val cursorTotal = db.rawQuery(
            "SELECT SUM(${Constantes.DatabaseConstants.VALOR}) FROM ${Constantes.DatabaseConstants.TABLE_NAME} " +
                    "WHERE ${Constantes.DatabaseConstants.ATIVO} = 1", null
        )
        if (cursorTotal.moveToFirst()) {
            resumo["TOTAL"] = cursorTotal.getDouble(0)
        }
        cursorTotal.close()

        val cursorCategorias = db.rawQuery(
            "SELECT ${Constantes.DatabaseConstants.CATEGORIA}, SUM(${Constantes.DatabaseConstants.VALOR}) " +
                    "FROM ${Constantes.DatabaseConstants.TABLE_NAME} " +
                    "WHERE ${Constantes.DatabaseConstants.ATIVO} = 1 " +
                    "GROUP BY ${Constantes.DatabaseConstants.CATEGORIA}", null
        )

        while (cursorCategorias.moveToNext()) {
            val categoria = cursorCategorias.getString(0)
            val soma = cursorCategorias.getDouble(1)
            resumo[categoria] = soma
        }
        cursorCategorias.close()

        return resumo
    }
}