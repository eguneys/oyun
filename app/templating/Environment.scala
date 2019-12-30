package oyun.app
package templating


object Environment
    extends oyun.Oyunisms
    with AssetHelper 
    with I18nHelper
    with UserHelper
    with FormHelper
    with StringHelper {


  private var envVar: Option[Env] = None
  def setEnv(e: Env) = { envVar = Some(e) }
  def destroy() = { envVar = None }
  def env: Env = envVar.get


  def isProd = env.isProd
  

}
