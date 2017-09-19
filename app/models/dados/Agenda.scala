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

	def remova(id: Int) = {
		
		if (licitacoes.contains(id)) {
			var novaLic = licitacoes
			novaLic -= id
			Agenda(novaLic)

		} else {
			this
		}
	}

	def adicioneProduto(idLicitacao: Int, produto: Produto) = {
		licitacoes.contains(idLicitacao) match {
			case false => this
			case true => {
				//val novaLic = licitacoes(idLicitacao).adicioneProduto(produto)

				var itens = licitacoes(idLicitacao).itens
				itens += (produto.id -> produto)
				
				//val novaLicitacao = licitacoes(idLicitacao).copy(itens = itens)
				
				var novaLicitacao = new Licitacao(idLicitacao, licitacoes(idLicitacao).nome, itens)
				//println(s"novaLic: ${novaLicitacao}")
				//novaLicitacao.itens += (produto.id -> produto)

				this.remova(idLicitacao)

				var novaLicitacoes = licitacoes + (novaLicitacao.id -> novaLicitacao)
				Agenda(novaLicitacoes)
			}
 		}
	}

	def pesquisePorId(id : Int) = {
		val licitacao = 
			//contatos.filter {case (nome,contato) => contato.telefone.area == area}
			licitacoes.filter {case (id, licitacao) => licitacao.id == id}
			//licitacoes(id)
		Agenda(licitacao)
		//this
		//licitacao
	}
}
