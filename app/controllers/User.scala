package controllers

import play.api.mvc._

import oyun.api.{ Context }
import oyun.app._
import oyun.user.{ User => UserModel }
import views._

final class User(
  env: Env
) extends OyunController(env) {

  def show(username: String) = OpenBody { implicit ctx =>
    EnabledUser(username) { u =>
      negotiate(
        html = renderShow(u),
        api = _ => ???
      )
    }
  }

  private def renderShow(u: UserModel, status: Results.Status = Results.Ok)(implicit ctx: Context) =
    funit map { _ =>
      status {
        html.user.show.page.activity(u)
      }
    }


  private def EnabledUser(username: String)(f: UserModel => Fu[Result])(implicit ctx: Context): Fu[Result] =
    env.user.repo named username flatMap {
      case None => notFound
      case Some(u) => f(u)
    }
  


  def list = Open { implicit ctx =>
    funit flatMap { _ =>
      negotiate(
        html =
          funit inject Ok(html.user.list()),
        api = _ => ???
      )
    }
  }
}
