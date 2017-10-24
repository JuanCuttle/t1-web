package models

case class Resposta (origem: String, destino: String, optDistancia: Option[Long], optTempo: Option[Long]) {
  def optDistanciaKm = optDistancia.map(d => {
    val dKm = if (d % 1000 == 0) d / 1000 else (d / 1000) + 1
    s"$dKm Km"
  })
  
  def optTempoHoras = optTempo.map(tempoSegs => {
    val tempoMins = tempoSegs / 60
    if (tempoMins < 60)
      s"$tempoMins minutos"
      else {
        val tempoHrs = tempoMins / 60
        val restoMins = tempoMins % 60
        if (restoMins == 0) s"$tempoHrs horas" else s"$tempoHrs horas e $restoMins minutos"
      }
  })
}