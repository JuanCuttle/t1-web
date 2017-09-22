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

  def remover = Action { implicit request =>
    Ok(views.html.remova())
  }

  def pesquisarPorId = Action { implicit request =>
    Ok(views.html.pesquisaPorId())
  }

  def removerProduto = Action { implicit request =>
    Ok(views.html.removaProduto())
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

  def remova = Action {implicit request =>
	val form = Form("id" -> number)
	val id = form.bindFromRequest.get
	val crud = new CRUD
	crud.remova(id)

	Redirect(routes.Application.index)
  }

  def pesquisePorId = Action { implicit request =>
    //val form = Form("area" -> number)

    val form = Form(tuple("idLicitacao" -> number, "id" -> number, "nome" -> text, "quantidade" -> number))
    val (idLicitacao, id, nome, quantidade) = form.bindFromRequest.get
    var crud = new CRUD

    println(s"vai adicionar produto $id na licitacao  $idLicitacao")
    crud.adicioneProduto(idLicitacao, Produto(id, nome, quantidade))
    println("adicionou")

    val licitacao = crud.pesquisePorId(idLicitacao)

    println(s"pesquisou licitacao : $licitacao")

    println("vai montar pagina")
    val pagina = Ok(views.html.pesquisaPorId(Some(idLicitacao), Some(licitacao)))
    println("montou")
    pagina
  }

  def removaProduto = Action { implicit request =>
    val form = Form(tuple("idLicitacao" -> number, "idProduto" -> number))
    val (idLicitacao, idProduto) = form.bindFromRequest.get
    var crud = new CRUD

    crud.removaProduto(idLicitacao, idProduto)

    val licitacao = crud.pesquisePorId(idLicitacao)
    Ok(views.html.pesquisaPorId(Some(idLicitacao), Some(licitacao)))

  }
}
