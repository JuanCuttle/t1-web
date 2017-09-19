package models.dados

//case class Contato(nome: String, telefone: Telefone, idade: Int)

case class Licitacao(id: Int, nome: String, itens: Map[Int, Produto] = Map()) {
	def numItens = itens.size

def adicioneProduto(produto: Produto) {
		val novosItens = itens + (produto.id -> produto)
		Licitacao(this.id, this.nome, novosItens)
	}
}
