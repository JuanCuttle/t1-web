package models

import models.persistencia.BD
import models.dados.Licitacao
import models.dados.Produto

class CRUD {
  val agenda = BD.leia

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

def adicioneProduto(idLicitacao: Int, produto: Produto) = {
	
	val agendaAdicionada = agenda.adicioneProduto(idLicitacao, produto)
	
	BD.salve(agendaAdicionada)
	
	agendaAdicionada

  }


}
