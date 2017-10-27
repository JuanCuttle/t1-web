package controllers

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import play.api.libs.ws.WSClient

import play.api.mvc.{AbstractController, ControllerComponents,Action}
import play.api.data.Form
import play.api.data.Forms.{tuple, text, number}

import javax.inject.{Singleton,Inject}

import scala.concurrent.{Future, ExecutionContext}

import models.dados.{Agenda, Licitacao, LicitacaoBasica, Produto, ProdutoCompleto}
import models.CRUD

import models.Resposta
import models.oauth2.Usuario
import models.oauth2.GoogleOAuth2
import models.Pesquisador

@Singleton
class Application @Inject() (ws: WSClient, cc: ControllerComponents) (implicit ec: ExecutionContext) extends AbstractController(cc) {

  var usuarios = Map[String, Usuario]()
  val googleOAuth2 = new GoogleOAuth2(ws)
  val pesquisador = new Pesquisador(ws)

  def index = Action {
    val crud = new CRUD
    val agenda = crud.pesquiseTodos

    Ok(views.html.index(agenda, googleOAuth2.loginURL)).withNewSession
  }

  def callback = Action.async { implicit request =>
    val optCode = request.getQueryString("code")
    optCode match {
      case None => Future.successful(Redirect(routes.Application.index).withNewSession)
      case Some(code) => {
        googleOAuth2.usuario(code).map(optUsuario => {
          optUsuario match {
            case None => Redirect(routes.Application.index).withNewSession
            case Some(usuario) => {
                usuarios += usuario.chave -> usuario
		val crud = new CRUD
		val agenda = crud.pesquiseTodos
                Ok(views.html.index1(agenda)).withSession("chave" -> usuario.chave)
            }
          }
        })
      }
    }
  }

  def processe = Action.async { request =>
    obtenhaEValideChave(request) match {
      case None => Future.successful(Redirect(routes.Application.index).withNewSession)
      case Some(chave) => {
        val origem = request.body.asFormUrlEncoded.get("origem").head
        val destino = request.body.asFormUrlEncoded.get("destino").head
        (origem.length, destino.length) match {
          case (a,b) if (a > 0 && b > 0) => pesquisador.pesquise(origem, destino).map(resposta => Ok(views.html.form(usuarios(chave), Some(resposta))))
          case _ => Future.successful(Ok(views.html.form(usuarios(chave), None)))
        }
      }
    }
  }

  def sair = Action.async { request =>
    obtenhaEValideChave(request) match {
      case None => Future.successful(Redirect(routes.Application.index).withNewSession)
      case Some(chave) => {
        val usuario = usuarios(chave)
        val at = usuario.at
        val url = s"https://accounts.google.com/o/oauth2/revoke?token=$at"
        usuarios -= chave
        ws.url(url).get.map (_ => Redirect(routes.Application.index).withNewSession)
      }
    }
  }

  private def obtenhaEValideChave (request: play.api.mvc.Request[_]) = {
    val optChave = request.session.get("chave")

    if (optChave.isEmpty || !usuarios.contains(optChave.get)) None else optChave
  }

  def adicionar = Action { implicit request =>
    Ok(views.html.adiciona())
  }

  def remover = Action { implicit request =>
    Ok(views.html.remova())
  }

  def adicionarProduto = Action.async { implicit request =>
    val futProdutos =  buscarProdutosValidos 
    //val produtos = buscarProdutosValidos
    futProdutos.map { produtos => 
	Ok(views.html.adicionaProduto(produtos))
    }
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

  def adicioneProduto = Action.async { implicit request =>
    //val form = Form("area" -> number)

    val form = Form(tuple("idLicitacao" -> number, "id" -> number, "nome" -> text, "quantidade" -> number))
    val (idLicitacao, id, nome, quantidade) = form.bindFromRequest.get
    var crud = new CRUD

    //println(s"vai adicionar produto $id na licitacao  $idLicitacao")
    crud.adicioneProduto(idLicitacao, Produto(id, nome, quantidade))
    //println("adicionou")

    val licitacao = crud.pesquisePorId(idLicitacao)

    //println(s"pesquisou licitacao : $licitacao")

    //println("vai montar pagina")

    val futProdutos =  buscarProdutosValidos
    futProdutos.map { produtos =>
	    Ok(views.html.adicionaProduto(produtos, Some(idLicitacao), Some(licitacao)))
    }

    //println("montou")
    //pagina
  }

  def removaProduto = Action.async { implicit request =>
    val form = Form(tuple("idLicitacao" -> number, "idProduto" -> number))
    val (idLicitacao, idProduto) = form.bindFromRequest.get
    var crud = new CRUD

    crud.removaProduto(idLicitacao, idProduto)

    val licitacao = crud.pesquisePorId(idLicitacao)

    val futProdutos =  buscarProdutosValidos
    futProdutos.map { produtos => 
    	Ok(views.html.adicionaProduto(produtos, Some(idLicitacao), Some(licitacao)))
    }

  }

  implicit val produtoReads = Json.reads[ProdutoCompleto]

  implicit val licitacaoBasicaReads: Reads[LicitacaoBasica] = (
	(JsPath \ "id").read[Int] and 
	(JsPath \ "nome").read[String]
	)(LicitacaoBasica.apply _)

  implicit val licitacaoBasicaWrites: Writes[LicitacaoBasica] = (
	(JsPath \ "id").write[Int] and 
	(JsPath \ "nome").write[String]
	)(unlift(LicitacaoBasica.unapply))

  implicit val licBasicaWrites = Json.writes[LicitacaoBasica]

  //implicit val licitacoesWrites = Json.writes[Licitacao]

  implicit val produtoWrites = new Writes[Produto] {
	def writes(produto: Produto) = Json.obj(
		"id" -> produto.id,
		"nome" -> produto.nome,
		"numero" -> produto.numero
	)
  }

  implicit val licitacaoWrites = new Writes[Licitacao] {
	def writes(licitacao: Licitacao) = Json.obj(
		"id" -> licitacao.id,
		"nome" -> licitacao.nome,
		"itens" -> licitacao.itens
	)
  }

  def APILeilaoBasico = Action { implicit request =>
	val crud = new CRUD

	//var jsonLeilao: JsValue = Json.toJson("")

	var jsonLeilao = Json.toJson(crud.agenda.licitacoes.values)
	
	//crud.agenda.licitacoes.map { case (id, licitacao) =>
		//val licitacao = crud.agenda.licitacoes(id)
		//val leilaoResumido = new LicitacaoBasica(licitacao.id, licitacao.nome)
		//jsonLeilao = Json.toJson(leilaoResumido)
	println(s"json: {$jsonLeilao}")

	//}
	
	Ok(jsonLeilao)
  }

  private def buscarProdutosValidos = {
	val servico = ws.url(definaEndereco)
	val futureResposta = servico.get

	var mapaProdutos: Map[Int, ProdutoCompleto] = Map()

	futureResposta.map { resposta => 
		val jsonProdutos = (resposta.json)
		//println(jsonProdutos)
		val produtos = jsonProdutos.asOpt[List[ProdutoCompleto]]
		val listaProdutos = produtos.get

		//var mapaProdutos: Map[Int, ProdutoCompleto] = Map()

		for(produto <- listaProdutos) {
			mapaProdutos += (produto.id -> produto)
		}
		
		println(mapaProdutos)
		mapaProdutos
	}

	//println(mapaProdutos)
	//mapaProdutos
  }

  private def definaEndereco = {
	"http://produtos.g.schiar.vms.ufsc.br:3000/produtos.json"
  }
}
