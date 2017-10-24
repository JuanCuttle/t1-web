package models.oauth2

trait OAuth2 {
  val callbackURL: String
  
  val clientID: String
  val clientSecret: String
  
  val loginURL : String  
}