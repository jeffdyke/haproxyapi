package haproxyapi.syntax

object string {
  implicit class StringOps(val s: String) extends AnyVal {

    def orEmpty: String = Option(s).getOrElse("")
    /**
      * Takes an underscore separated identifier name and returns a camel cased one
      *
      * Example:
      *    underscoreToCamel("this_is_a_1_test") == "thisIsA1Test"
      */
    def underscoreToCamelCase: String = {
      val underscorePrefix = s.startsWith("_")

      val s1 = "_([a-z\\d])".r.replaceAllIn(s.stripPrefix("_"), {m =>
        m.group(1).toUpperCase()
      })

      if (s1.isEmpty) {
        s
      }
      else if(underscorePrefix) {
        s"_$s1"
      } else {
        s1
      }
    }
  }
}
