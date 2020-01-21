package oyun.masa

import actorApi.masa.{ HumanPlay }
import poker.{ Side, Move, Status }
import poker.format.Uci
import oyun.game.{ Pov, Masa, Game, Progress }
import oyun.user.User

final private class Player(
  finisher: Finisher
)(implicit ec: scala.concurrent.ExecutionContext) {

  sealed private trait MoveResult
  private case class MoveApplied(progress: Progress, move: Move) extends MoveResult

  private[masa] def human(play: HumanPlay, masaDuct: MasaDuct)(
    pov: Pov
  )(implicit proxy: MasaProxy): Fu[Events] = play match {
    case HumanPlay(_, uci) =>
      pov match {
        case Pov(masa, Some(side)) if masa playableBy side =>
          masa.game match {
            case Some(game) => 
              applyUci(masa, game, uci)
                .prefixFailuresWith(s"$pov ")
                .fold(errs => fufail(ClientError(errs.shows)), fuccess)
                .flatMap {
                  case MoveApplied(progress, move) =>
                    proxy.save(progress) >>
                    postHumanPlay(masaDuct, pov, progress, move)
                }
            case _ => fufail(ClientError(s"$pov no game found"))
          }
        case Pov(masa, Some(side)) if !masa.turnOf(side) => 
          fufail(ClientError(s"$pov not your turn"))
        case Pov(masa, _) if masa.noGame =>
          fufail(ClientError(s"$pov no game playing"))
        case Pov(masa, _) if masa.finished =>
          fufail(ClientError(s"$pov game is finished"))
        case Pov(masa, None) => fufail(ClientError(s"$pov non player"))
        case _ => fufail(ClientError(s"$pov move refused for some reason"))
      }
  }

  private def postHumanPlay(
    masa: MasaDuct,
    pov: Pov,
    progress: Progress,
    move: Move)(implicit proxy: MasaProxy): Fu[Events] = {
    progress.masa.game ?? { game => 
      if (game.finished) moveFinish(progress.masa, game) dmap { progress.events ::: _ }
      else {
        fuccess(progress.events)
      }
    }
  }

  private def applyUci(masa: Masa, game: Game, uci: Uci): Valid[MoveResult] =
    (uci match {
      case Uci.Move(act) =>
        game.poker(act) map {
          case (npg, move) => npg -> move
        }
    }).map {
      case (newPokerGame, move) =>
        MoveApplied(
          masa.update(newPokerGame, move),
          move
        )
    }

  private def moveFinish(masa: Masa, game: Game)(implicit proxy: MasaProxy): Fu[Events] =
    game.status match {
      case Status.OneWin => finisher.other(masa, _.OneWin)
      case Status.Showdown => finisher.other(masa, _.Showdown)
      case _ => fuccess(Nil)
    }

}
