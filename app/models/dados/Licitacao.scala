package models.dados

//case class Contato(nome: String, telefone: Telefone, idade: Int)

case class Licitacao(id: Int, nome: String, itens: Map[Int, Produto] = Map()) {
	def numItens = itens.size
}
