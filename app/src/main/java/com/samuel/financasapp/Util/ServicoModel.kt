package com.samuel.financasapp.Util

data class ServicoModel(
    val id: Int,
    val descricao: String,
    val valor: Double,
    val categoria: String,
    val vencimento: Int,
    val ativo: Int,
    val dataCancelamento: String? = null // Novo campo adicionado
)