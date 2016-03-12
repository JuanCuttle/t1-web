package models.dados

case class Agenda(contatos: Map[String, Contato] = Map()) {

	def numContatos = contatos.size
	
	def adicione(contato: Contato) = {
		if (contatos.contains(contato.nome))
			this
		else {
			Agenda(contatos + (contato.nome -> contato))
		}
	}

	def pesquisePorArea(area : Int) = {
		val contatosDaArea = 
			contatos.filter {case (nome,contato) => contato.telefone.area == area}
		Agenda(contatosDaArea)
	}
}