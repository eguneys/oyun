package oyun.masa
package actorApi

import oyun.game.{ Side }
import oyun.user.User

package masa {

  case class Sit(userId: User.ID, side: Side)
  
}
