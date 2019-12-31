package oyun.blog

import io.prismic._
import play.api.mvc.RequestHeader
import play.api.libs.ws.WSClient

final class BlogApi(
  config: BlogConfig
)(implicit ec: scala.concurrent.ExecutionContext, ws: WSClient) {

  private def collection = config.collection

  private val prismicBuilder = new Prismic

  def prismicApi = prismicBuilder.get(config.apiUrl)

  def one(api: Api, ref: Option[String], id: String): Fu[Option[Document]] =
    api
      .forms(collection)
      .query(s"""[[:d = at(document.id, "$id")]]""")
      .ref(ref | api.master.ref)
      .submit() map (_.results.headOption)

  def one(prismic: BlogApi.Context, id: String): Fu[Option[Document]] = one(prismic.api, prismic.ref.some, id)

  def context(req: RequestHeader)
  (implicit linkResolver: (Api, Option[String]) => DocumentLinkResolver): Fu[BlogApi.Context] = {
    prismicApi map { api =>
      val ref = resolveRef(api) {
        req.cookies
          .get(Prismic.previewCookie)
          .map(_.value)
          .orElse(req.queryString get "ref" flatMap (_.headOption) filter (_.nonEmpty))
      }
      BlogApi.Context(api, ref | api.master.ref, linkResolver(api, ref))
    }
  }

  private def resolveRef(api: Api)(ref: Option[String]) =
    ref.map(_.trim).filterNot(_.isEmpty) map { reqRef =>
      api.refs.values.collectFirst {
        case r if r.label == reqRef => r.ref
      } getOrElse reqRef
    }
  
}

object BlogApi {

  case class Context(api: Api, ref: String, linkResolver: DocumentLinkResolver) {
    def maybeRef = Option(ref).filterNot(_ == api.master.ref)
  }
  
}
