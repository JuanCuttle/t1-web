package controllers

import play.api.mvc.{AbstractController, ControllerComponents,Action}
import play.api.data.Form
import play.api.data.Forms.{tuple, text, number}

import javax.inject.{Singleton,Inject}

import models.dados.{Agenda, Licitacao, Produto}
import models.CRUD

@Singleton
class Application @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    val crud = new CRUD
    val agenda = crud.pesquiseTodos

    Ok(views.html.index(agenda))
  }

  def adicionar = Action { implicit request =>
    Ok(views.html.adiciona())
  }

  def pesquisarPorId = Action { implicit request =>
    Ok(views.html.pesquisaPorId())
  }

  def adicione = Action { implicit request =>
  	//val form = Form(tuple("nome" -> text, "area" -> number, "numero" -> number, "idade" -> number))
	val form = Form(tuple("id" -> number, "nome" -> text))
  	//val (nome, area, numero, idade) = form.bindFromRequest.get
	val (id, nome) = form.bindFromRequest.get

    	val crud = new CRUD

  	//crud.adicione(Contato(nome, Telefone(area, numero), idade))

	crud.adicione(Licitacao(id, nome))

  	Redirect(routes.Application.index)
  }

  def pesquisePorId = Action { implicit request =>
    //val form = Form("area" -> number)
    val form = Form(tuple("idLicitacao" -> number, "id" -> number, "nome" -> text, "quantidade" -> number))
    val (idLicitacao, id, nome, quantidade) = form.bindFromRequest.get
    val crud = new CRUD

    val produto = new Produto(id, nome, quantidade)

    crud.adicioneProduto(idLicitacao, Produto(id, nome, quantidade))

    //crud = new CRUD(agenda)

    val itensLicitacao = crud.pesquisePorId(idLicitacao)

    //Thread.sleep(2000)

    Ok(views.html.pesquisaPorId(Some(idLicitacao), Some(itensLicitacao)))
    //Ok(views.html.pesquisaPorId(Some(idLicitacao), Some(itensLicitacao)))
  }
}
