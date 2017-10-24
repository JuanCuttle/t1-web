package models

import java.net.URLEncoder
import play.api.libs.ws.WSClient
import play.api.libs.json.JsArray

import scala.concurrent.ExecutionContext

class Pesquisador (ws: WSClient) (implicit ec: ExecutionContext) {

  def pesquise(origem: String, destino: String) = {

    val origins = URLEncoder.encode(origem, "UTF-8")
    val destinations = URLEncoder.encode(destino, "UTF-8")
    val url = s"http://maps.googleapis.com/maps/api/distancematrix/json?origins=$origins&destinations=$destinations&sensor=false"

    ws.url(url).get.map { resposta =>
      val respostaJson = resposta.json

      val arrayDestination = (respostaJson \ "destination_addresses").get.asInstanceOf[JsArray]
      val arrayOrigin = (respostaJson \ "origin_addresses").get.asInstanceOf[JsArray]
      val elements = ((respostaJson \ "rows").get.asInstanceOf[JsArray](0) \ "elements").get.asInstanceOf[JsArray](0)
      val semCaminho = !(elements \ "status").as[String].equals("OK")
      if (arrayDestination.value.isEmpty || arrayOrigin.value.isEmpty || semCaminho)
        Resposta(origem, destino, None, None)
      else {
        val destinoGoogle = arrayDestination(0).as[String]
        val origemGoogle = arrayOrigin(0).as[String]
        val distancia = (elements \ "distance" \ "value").as[Long]
        val tempo = (elements \ "duration" \ "value").as[Long]
        Resposta(origemGoogle, destinoGoogle, Some(distancia), Some(tempo))
      }
    }
  }
}
