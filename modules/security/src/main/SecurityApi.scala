package oyun.security

import play.api.mvc.RequestHeader

import oyun.user.{ User, UserRepo }

final class SecurityApi(
  userRepo: UserRepo,
  store: Store
)(implicit ec: scala.concurrent.ExecutionContext, system: akka.actor.ActorSystem) {

  def restoreUser(req: RequestHeader): Fu[Option[User]] =
    reqSessionId(req) ?? { sessionId =>
      store userId sessionId flatMap {
        _ ?? { d =>
          userRepo byId d
        }
      }
    }

  val sessionIdKey = "sessionId"

  def reqSessionId(req: RequestHeader): Option[String] =
    req.session.get(sessionIdKey) orElse req.headers.get(sessionIdKey)

}
