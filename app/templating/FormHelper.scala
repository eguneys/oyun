package oyun.app
package templating

import play.api.data._

import oyun.api.Context
import oyun.app.ui.ScalatagsTemplate._
import oyun.i18n.I18nDb

trait FormHelper { self: I18nHelper =>

  val postForm = form(method := "post")
  val submitButton = button(tpe := "submit")

  object form3 {

    private val idPrefix = "form3"

    def id(field: Field): String = s"$idPrefix-${field.id}"

    private def groupLabel(field: Field) = label(cls := "form-label", `for` := id(field))
    private val helper = small(cls := "form-help")


    private def errors(errs: Seq[FormError])(implicit ctx: Context): Frag =
      errs map error
    private def errors(field: Field)(implicit ctx: Context): Frag =
      errors(field.errors)
    private def error(err: FormError)(implicit ctx: Context): Frag =
      p(cls := "error")(transKey(err.message, I18nDb.Site, err.args))


    private def validationModifiers(field: Field): Seq[Modifier] = field.constraints collect {

      case ("constraint.minLength", Seq(m: Int)) => minlength := m
      case ("constraint.maxLength", Seq(m: Int)) => maxlength := m
      case ("constraint.min", Seq(m: Int)) => min := m
      case ("constraint.max", Seq(m: Int)) => max := m
    }

    val split = div(cls := "form-split")

    def group(
      field: Field,
      labelContent: Frag,
      klass: String = "",
      half: Boolean = false,
      help: Option[Frag] = None
    )(content: Field => Frag)(implicit ctx: Context): Frag =
      div(
        cls := List(
          "form-group" -> true,
          "is-invalid" -> field.hasErrors,
          "form-half" -> half,
          klass -> klass.nonEmpty
        )
      )(
        groupLabel(field)(labelContent),
        content(field),
        errors(field),
        help map { helper(_) }
      )

    def input(field: Field, typ: String = "", klass: String = ""): BaseTagType =
      st.input(
        st.id := id(field),
        name := field.name,
        value := field.value,
        tpe := typ.nonEmpty.option(typ),
        cls := List("form-control" -> true, klass -> klass.nonEmpty)
      )(validationModifiers(field))


    def checkbox(
      field: Field,
      labelContent: Frag,
      half: Boolean = false,
      help: Option[Frag] = None,
      disabled: Boolean = false
    ): Frag =
      div(
        cls := List(
          "form-check form-group" -> true,
          "form-half" -> half
        )
      )(
        div(
          span(cls := "form-check-input")(
            st.input(
              st.id := id(field),
              name := field.name,
              value := field.value,
              tpe := "checkbox",
              cls := "form-control cmn-toggle",
              field.value.has("true") option checked,
              disabled option st.disabled
          ),
            label(`for` := id(field))
          ),
          groupLabel(field)(labelContent)
        ),
        help map { helper(_) }
      )

    def submit(
      content: Frag,
      icon: Option[String] = Some("E"),
      nameValue: Option[(String, String)] = None,
      klass: String = "",
      confirm: Option[String] = None
    ): Tag =
      submitButton(
        dataIcon := icon,
        name := nameValue.map(_._1),
        value := nameValue.map(_._2),
        cls := List(
          "submit button" -> true,
          "text" -> icon.isDefined,
          "confirm" -> confirm.nonEmpty,
          klass -> klass.nonEmpty
        ),
        title := confirm
      )(content)


    def password(field: Field, content: Frag)(implicit ctx: Context): Frag =
      group(field, content)(input(_, typ = "password")(required))


    def globalError(form: Form[_])(implicit ctx: Context): Option[Frag] =
      form.globalError map { err =>
        div(cls := "form-group is-invalid")(error(err))
      }

  }

}
