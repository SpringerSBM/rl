package rl
package tests

import org.specs2.Specification
import rl.UrlCodingUtils._
import collection.immutable.BitSet

class UrlCodingSpec extends Specification {
  def is =

    "Encoding a URI should" ^
      "not change any of the allowed chars" ! {
        val encoded = urlEncode("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890!$&'()*+,;=:/?#[]@-._~")
        encoded must_== "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890!$&'()*+,;=:/?#[]@-._~"
      } ^
      "uppercase encodings already in a string" ! {
        ensureUppercasedEncodings("hello%3fworld") must_== "hello%3Fworld"
      } ^
      "percent encode spaces" ! {
        urlEncode("hello world") must_== "hello%20world"
      } ^
      "encode a letter with an accent as 2 values" ! {
        urlEncode("é") must_== "%C3%A9"
      } ^ p ^
    "Decoding a URI should" ^
      "not change any of the allowed chars" ! {
        val decoded = urlDecode("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890!$&'()*,;=:/?#[]@-._~")
        decoded must_== "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890!$&'()*,;=:/?#[]@-._~"
      } ^
      "leave Fußgängerübergänge as is" ! {
        urlDecode("Fußgängerübergänge") must_== "Fußgängerübergänge"
      } ^
      "not overflow on all utf-8 chars" ! {
        urlDecode("äéèüああああああああ") must_== "äéèüああああああああ"
      } ^
      "decode a pct encoded string" ! {
        urlDecode("hello%20world") must_== "hello world"
      } ^
      "decode a badly pct encoded string" ! {
        urlDecode("hello%20world%xx") must_== "hello world%xx"
      } ^
      "decode value consisting of 2 values to 1 char" ! {
        urlDecode("%C3%A9") must_== "é"
      } ^
      "skip the chars in toSkip when decoding" ^
        "skips '%2F' when decoding" ! { urlDecode("%2F", toSkip = "/?#") must_== "%2F" } ^
        "skips '%23' when decoding" ! { urlDecode("%23", toSkip = "/?#") must_== "%23" } ^
        "skips '%3F' when decoding" ! { urlDecode("%3F", toSkip = "/?#") must_== "%3F" } ^
        "still encodes others" ! { urlDecode("br%C3%BCcke", toSkip = "/?#") must_== "brücke"} ^
        "handles mixed" ! { urlDecode("/ac%2Fdc/br%C3%BCcke%2342%3Fcheck", toSkip = "/?#") must_== "/ac%2Fdc/brücke%2342%3Fcheck"
      } ^ p ^
    "Checking if a string is UrlEncoded should" ^
      "recognize a correctly url-encoded string as urlencoded" ! {
        isUrlEncoded("/foo?%22bar%2Ccar") must beTrue
      } ^
      "recognize an incorrectly url-encoded string as not urlencoded" ! {
        isUrlEncoded("/foo?%22bar%xx") must beFalse
      } ^ p ^
    "The plusIsSpace flag specifies how to treat pluses" ^
      "it treats + as allowed when the plusIsSpace flag is either not supplied or supplied as false" ! {
        urlDecode("+") must_== "+"
        urlDecode("+", plusIsSpace = false) must_== "+"
      } ^
      "it decodes + as space when the plusIsSpace flag is true" ! {
        urlDecode("+", plusIsSpace = false) must_== "+"
      } ^ end
}
