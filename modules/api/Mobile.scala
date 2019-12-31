package oyun.api

import play.api.mvc.RequestHeader

import oyun.common.{ ApiVersion, HTTPRequest }

object Mobile {

  object Api {
    def requestVersion(req: RequestHeader): Option[ApiVersion] =
      HTTPRequest apiVersion req
  }

}

