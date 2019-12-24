package controllers

import play.api.mvc._
import scalatags.Text.Frag

import oyun.api.{ Context, HeaderContext, PageData }
import oyun.app._
import oyun.common.{ ApiVersion }
import oyun.user.{ UserContext, User => UserModel }

abstract private[controllers] class OyunController(val env: Env)
    extends BaseController {

  def controllerComponents = env.controllerComponents
  implicit def executionContext = env.executionContext
  implicit def ctxReq(implicit ctx: Context) = ctx.req

  implicit protected def OyunFragToResult(frag: Frag): Result = Ok(frag)

  protected val keyPages = new KeyPages(env)


  protected def Open(f: Context => Fu[Result]): Action[Unit] =
    Open(parse.empty)(f)

  protected def Open[A](parser: BodyParser[A])(f: Context => Fu[Result]): Action[A] =
    Action.async(parser)(handleOpen(f, _))


  private def handleOpen(f: Context => Fu[Result], req: RequestHeader): Fu[Result] =
    reqToCtx(req) flatMap f


  protected def negotiate(html: => Fu[Result], api: ApiVersion => Fu[Result]
  )(implicit req: RequestHeader): Fu[Result] =
    html.dmap(_.withHeaders("Vary" -> "Accept"))


  protected def reqToCtx(req: RequestHeader): Fu[HeaderContext] = restoreUser(req) flatMap {
    case (d) =>
      val ctx = UserContext(req, d, oyun.i18n.I18nLangPicker(req, d))//d.map(_.user))
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
