package models.persistencia

import models.dados.Agenda

object BD {
	var agenda = Agenda()

	def salve(agenda: Agenda) {this.agenda = agenda}

	def leia = agenda
}