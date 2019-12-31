package controllers

import io.prismic.Document
import play.api.mvc._

import oyun.api.Context
import oyun.app._
import oyun.blog.BlogApi

final class Blog(
  env: Env,
  prismicC: Prismic
)(implicit ws: play.api.libs.ws.WSClient)
extends OyunController(env) {

  import prismicC._

  private def blogApi = env.blog.api

  def show(id: String, slug: String, ref: Option[String]) = WithPrismic { implicit ctx => implicit prismic =>
    blogApi.one(prismic, id) flatMap { maybeDocument =>
      checkSlug(maybeDocument, slug) {
        case Left(newSlug) => MovedPermanently(routes.Blog.show(id, newSlug, ref).url)
        case Right(doc) => Ok(views.html.blog.show(doc))
      }
    } recoverWith {
      case e: RuntimeException if e.getMessage contains "Not Found" => notFound
    }
  }

  private def WithPrismic(f: Context => BlogApi.Context => Fu[Result]): Action[Unit] = Open { ctx =>
    blogApi context ctx.req flatMap { prismic =>
      f(ctx)(prismic)
    }
  }

  // -- Helper: Check if the slug is valid and redirect to the most recent version id needed
  private def checkSlug(document: Option[Document], slug: String)(
      callback: Either[String, Document] => Result
  )(implicit ctx: oyun.api.Context) =
    document.collect {
      case document if document.slug == slug         => fuccess(callback(Right(document)))
      case document if document.slugs.contains(slug) => fuccess(callback(Left(document.slug)))
    } getOrElse notFound
  
}
