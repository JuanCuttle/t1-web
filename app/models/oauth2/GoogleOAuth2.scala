package models.oauth2

import play.api.libs.ws.WSClient
import play.api.libs.json.Json
import play.api.libs.json.JsObject

import scala.concurrent.{ExecutionContext, Future}


class GoogleOAuth2 (ws: WSClient) (implicit ec: ExecutionContext) extends OAuth2 {

  // definido em:
  // https://developers.google.com/
  // https://console.developers.google.com/project
  //

  override val clientID = "369245496841-s8bdoicrc2tcf869qr9m632hsv3hp1mn.apps.googleusercontent.com"
  override val clientSecret = "oP-y6r6RBYNs_FAb7PWs67kE"
  override val callbackURL = "http://web.juan.cuttle.vms.ufsc.br:9000/callback"
  override val loginURL = s"https://accounts.google.com/o/oauth2/auth?client_id=$clientID&response_type=code&scope=openid email&redirect_uri=$callbackURL"


  def usuario(code: String): Future[Option[Usuario]] = {
    for (
      optAccessToken <- obtenhaAccessToken(code);
      optDados <- obtenhaDadosDoUsuario(optAccessToken);
      optUsuario <- obtenhaUsuario(optDados)
    ) yield optUsuario
  }

  private def obtenhaUsuario(optDados: Option[JsObject]) = {
    optDados match {
      case None => Future.successful(None)
      case Some(dados) => {

        val nome = (dados \ "nome").as[String]
        val at = (dados \ "at").as[String]
        val pic = (dados \ "pic").as[String]
        val email = (dados \ "email").as[String]
        val chave = email   // poderia ser uma informação codificada
        Future.successful(Some(Usuario(chave, at, nome, pic, email)))
      }
    }
}

  private def obtenhaDadosDoUsuario(optAccessToken: Option[String]): Future[Option[JsObject]] = {
    optAccessToken match {
      case None => Future.successful(None)
      case Some(accessToken) => {
        val url = s"https://www.googleapis.com/oauth2/v2/userinfo?access_token=$accessToken"
        ws.url(url).get.map(resposta => {
          val respJson = resposta.json
          Some(Json.obj("at" -> accessToken, "nome" -> (respJson \ "name").get, "pic" -> (respJson \ "picture").get, "email" -> (respJson \ "email").get))
        })

      }
    }
  }

  private def obtenhaAccessToken(code: String): Future[Option[String]] = {
    val params = Map("code" -> Seq(code),
      "client_id" -> Seq(clientID),
      "client_secret" -> Seq(clientSecret),
      "redirect_uri" -> Seq(callbackURL),
      "grant_type" -> Seq("authorization_code"))

    ws.url("https://accounts.google.com/o/oauth2/token").post(params).map(resposta => (resposta.json \ "access_token").asOpt[String])
  }
}
