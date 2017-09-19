package models

import models.persistencia.BD
import models.dados.Licitacao
import models.dados.Produto

class CRUD {
  var agenda = BD.leia

  def pesquiseTodos = agenda

  def pesquisePorId(id: Int) = {
  	agenda.pesquisePorId(id)
  }

//  def adicione(contato: Contato) = {
def adicione(licitacao: Licitacao) = {

  	val agendaAdicionada = agenda.adicione(licitacao)

  	//if (agenda.numContatos != agendaAdicionada.numContatos)
  	
	BD.salve(agendaAdicionada)

  	agendaAdicionada
  }

def remova(id: Int) = {
	val agendaRemovida = agenda.remova(id)
	BD.salve(agendaRemovida)
	agendaRemovida
}

def adicioneProduto(idLicitacao: Int, produto: Produto) = {
	
	var agendaAdicionada = agenda.adicioneProduto(idLicitacao, produto)

	//println(s"novaLic2: ${agendaAdicionada.licitacoes(idLicitacao)}")	
	BD.salve(agendaAdicionada)

	//this.pesquisePorId(idLicitacao)
	
	this.agenda = agendaAdicionada

  }

def removaProduto(idL: Int, idP: Int) = {
	var agendaRemovida = agenda.removaProduto(idL, idP)

	BD.salve(agendaRemovida)

	this.agenda = agendaRemovida
  }
}
