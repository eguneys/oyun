package controllers

import play.api.http._
import play.api.libs.json.{ JsArray, JsObject, JsString, Json, Writes }
import play.api.mvc._
import scalatags.Text.Frag

import oyun.api.{ BodyContext, Context, HeaderContext, PageData }
import oyun.app._
import oyun.common.{ ApiVersion }
import oyun.user.{ UserContext, User => UserModel }

abstract private[controllers] class OyunController(val env: Env)
    extends BaseController
    with RequestGetter {

  def controllerComponents = env.controllerComponents
  implicit def executionContext = env.executionContext

  implicit final protected class OyunPimpedResult(result: Result) {
    def fuccess = scala.concurrent.Future successful result
  }

  implicit protected def OyunFragToResult(frag: Frag): Result = Ok(frag)

  protected val keyPages = new KeyPages(env)
  protected val renderNotFound = keyPages.notFound _

  implicit def ctxLang(implicit ctx: Context) = ctx.lang
  implicit def ctxReq(implicit ctx: Context) = ctx.req

  protected def Open(f: Context => Fu[Result]): Action[Unit] =
    Open(parse.empty)(f)

  protected def Open[A](parser: BodyParser[A])(f: Context => Fu[Result]): Action[A] =
    Action.async(parser)(handleOpen(f, _))


  protected def OpenBody(f: BodyContext[_] => Fu[Result]): Action[AnyContent] =
    OpenBody(parse.anyContent)(f)

  protected def OpenBody[A](parser: BodyParser[A])(f: BodyContext[A] => Fu[Result]): Action[A] =
    Action.async(parser) { req =>
      reqToCtx(req) flatMap f
    }

  private def handleOpen(f: Context => Fu[Result], req: RequestHeader): Fu[Result] =
    reqToCtx(req) flatMap f


  protected def OptionOk[A, B: Writeable: ContentTypeOf](
    fua: Fu[Option[A]])(op: A => B)(implicit ctx: Context): Fu[Result] =
    OptionFuOk(fua) { a =>
      fuccess(op(a))
    }

  protected def OptionFuOk[A, B: Writeable: ContentTypeOf](
    fua: Fu[Option[A]]
  )(op: A => Fu[B])(implicit ctx: Context) =
    fua flatMap { _.fold(notFound(ctx))(a => op(a) map { Ok(_) }) }


  def notFound(implicit ctx: Context): Fu[Result] = negotiate(
    html = fuccess(renderNotFound(ctx)),
    api = _ => notFoundJson("Resource not found")
  )

  def notFoundJson(msg: String = "Not found"): Fu[Result] = fuccess {
    NotFound(jsonError(msg))
  }

  def jsonError[A: Writes](err: A): JsObject = Json.obj("error" -> err)

  protected def negotiate(html: => Fu[Result], api: ApiVersion => Fu[Result]
  )(implicit req: RequestHeader): Fu[Result] =
    oyun.api.Mobile.Api
      .requestVersion(req)
      .fold(html) { v =>
        api(v) dmap (_ as JSON)
      }
      .dmap(_.withHeaders("Vary" -> "Accept"))


  protected def reqToCtx(req: RequestHeader): Fu[HeaderContext] = restoreUser(req) flatMap {
    case (d) =>
      val ctx = UserContext(req, d, oyun.i18n.I18nLangPicker(req, d))//d.map(_.user))
      pageDataBuilder(ctx) dmap { Context(ctx, _) }
  }

  protected def reqToCtx[A](req: Request[A]): Fu[BodyContext[A]] =
    restoreUser(req) flatMap {
      case d =>
        val ctx = UserContext(req, d, oyun.i18n.I18nLangPicker(req, d))
        pageDataBuilder(ctx) dmap { Context(ctx, _) }
    }


  private def pageDataBuilder(ctx: UserContext): Fu[PageData] = {
    // val isPage = HTTPRequest isSynchronousHttp ctx.req
    ctx.me.fold(fuccess(PageData.anon(ctx.req))) { me =>
      fuccess(PageData.anon(ctx.req))
    }
  }


  type RestoredUser = (Option[UserModel])
  private def restoreUser(req: RequestHeader): Fu[RestoredUser] =
    env.security.api restoreUser req

}
