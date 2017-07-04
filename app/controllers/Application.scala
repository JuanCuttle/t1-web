package controllers

import play.api.mvc.{AbstractController, ControllerComponents,Action}
import play.api.data.Form
import play.api.data.Forms.{tuple, text, number}

import javax.inject.{Singleton,Inject}

import models.dados.{Agenda, Contato, Telefone}
import models.CRUD

@Singleton
class Application @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    val crud = new CRUD
    val agenda = crud.pesquiseTodos

    Ok(views.html.index(agenda))
  }

  def adicionar = Action {
    Ok(views.html.adiciona())
  }

  def pesquisarPorArea = Action {
    Ok(views.html.pesquisaPorArea())
  }

  def adicione = Action { implicit request =>
  	val form = Form(tuple("nome" -> text, "area" -> number, "numero" -> number))
  	val (nome, area, numero) = form.bindFromRequest.get
    val crud = new CRUD

  	crud.adicione(Contato(nome, Telefone(area, numero)))

  	Redirect(routes.Application.index)
  }

  def pesquisePorArea = Action { implicit request =>
    val form = Form("area" -> number)
    val area = form.bindFromRequest.get
    val crud = new CRUD
    val agendaPorArea = crud.pesquisePorArea(area)

    Ok(views.html.pesquisaPorArea(Some(area), Some(agendaPorArea)))
  }
}
