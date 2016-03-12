package models

import models.persistencia.BD
import models.dados.Contato

class CRUD {
  val agenda = BD.leia

  def pesquiseTodos = agenda

  def pesquisePorArea(area: Int) = {
  	agenda.pesquisePorArea(area)
  }

  def adicione(contato: Contato) = {
  	val agendaAdicionada = agenda.adicione(contato)

  	if (agenda.numContatos != agendaAdicionada.numContatos)
  		BD.salve(agendaAdicionada)

  	agendaAdicionada
  }
}