package models.dados

case class Agenda(licitacoes: Map[Int, Licitacao] = Map()) {

	def numLicitacoes = licitacoes.size
	
	def adicione(licitacao: Licitacao) = {
		if (licitacoes.contains(licitacao.id))
			this
		else {
			Agenda(licitacoes + (licitacao.id -> licitacao))
		}
	}

	def adicioneProduto(idLicitacao: Int, produto: Produto) = {
		licitacoes.contains(idLicitacao) match {
			case false => this
			case true => {
				var itens = licitacoes(idLicitacao).itens
				itens += (produto.id -> produto)
				val novaLicitacao = licitacoes(idLicitacao).copy(itens = itens)
				val novaLicitacoes = licitacoes + (novaLicitacao.id -> novaLicitacao)
				Agenda(novaLicitacoes)
				Agenda(novaLicitacoes)
				//Agenda(licitacoes + (novaLicitacao.id -> novaLicitacao))
			 }
 		}
	}

	def pesquisePorId(id : Int) = {
		val itensDaLicitacao = 
			//contatos.filter {case (nome,contato) => contato.telefone.area == area}
			licitacoes.filter {case (id, licitacao) => licitacao.id == id}
		Agenda(itensDaLicitacao)
	}
}
