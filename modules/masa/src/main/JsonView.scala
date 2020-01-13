package oyun.masa

import play.api.libs.json._
import scala.concurrent.ExecutionContext

import oyun.game.{ Masa, Pov, Player => GamePlayer }
import oyun.user.{ User, UserRepo }
import actorApi.SocketStatus

final class JsonView(
  userRepo: UserRepo,
  getSocketStatus: Masa => Fu[SocketStatus]
)(implicit ec: ExecutionContext) {

  import JsonView._

  def playerJson(pov: Pov): Fu[JsObject] = 
    getSocketStatus(pov.masa) zip
      (pov.masa.seats.map{ _ ??
        { p => userRepo.byId(p.userId) }
      }.sequenceFu) map {
        case socket ~ playerUsers =>
          import pov._
          Json.obj(
            "nbSeats" -> masa.nbSeats.nb,
            "stakes" -> masa.stakes.stakesString,
            "seats" -> (masa.seats zip playerUsers).map{
              case Some(p) ~ Some(u) => playerView(p, u)
              case _ => JsNull
            },
            "player" -> {
              Json.obj(
                "version" -> socket.version.value
              )
            },
            "url" -> Json.obj(
              "socket" -> s"/play/$masaId",
              "round" -> s"/$masaId"
            )
          ).add("me" -> (for {
            _ <- pov.side
            p <- pov.player
          } yield Json.obj(
            "status" -> p.status,
            "side" -> p.side
          )))
      }

  def playerView(player: GamePlayer, user: User): JsObject = Json.obj(
    "name" -> user.username,
    "img" -> user.avatar.link
  )

}

object JsonView {

  def playerJson(
    lightUserApi: oyun.user.LightUserApi
  )(player: GamePlayer)(implicit ec: ExecutionContext): Fu[JsObject] = 
    for {
      light <- lightUserApi.async(player.userId)
    } yield Json
      .obj("id" -> player.id)
      .add("light" -> light)

  implicit val stakesWrites: OWrites[Masa.Stakes] = OWrites { stakes =>
    Json.obj(
      "stakes" -> stakes.stakesString,
      "buyIn" -> stakes.buyIn
    )
  }
  
}
