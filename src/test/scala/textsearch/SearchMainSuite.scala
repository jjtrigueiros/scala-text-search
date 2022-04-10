package textsearch

import munit.FunSuite

import java.io.File
import scala.io.{BufferedSource, Source}
import scala.util.{Success, Try, Using}


class SearchMainSuite extends FunSuite {
  test("Open and read a directory") {

    val path: String = getClass.getResource("/test_folder").getPath
    val test_args: Array[String] = Array(path)

    val obtained = Program.readDirectory(test_args)
    assert(obtained.isRight)
  }

  test("Index all files in the directory"){
    val path: String = getClass.getResource("/test_folder").getPath
    val test_args: Array[String] = Array(path)

    val obtained = Program.readDirectory(test_args)
      .fold(println, dir => Program.index(dir)
        .fold(println, index => index))

    obtained match{
      case index: Program.Index =>
        assert(index.filesAndWords.sizeIs == 28)
      case _ =>
        fail("Failed to generate a Program.Index")
    }
  }

  //test("Ignores an open file"){
  //  val filepath: String = getClass.getResource("/test_folder/203.txt").getPath
  //  Using(Source.fromFile(filepath)){
  //    open_file =>
  //      val path: String = getClass.getResource("/test_folder").getPath
  //      val test_args: Array[String] = Array(path)
  //      val obtained = Program.readDirectory(test_args).fold(println, dir => Program.index(dir))
  //      obtained match{
  //        case obtained: Program.Index =>
  //          assert(obtained.filesAndWords.sizeIs == 9999) // not working
  //        case _ =>
  //          fail("Obtained something other than a Program.Index")
  //      }
  //  }
  //}

  // test match simple word

  test("Parse example words") {
    val obtained = Program.parseWords("Trader's state-of-the-art State states States are Stoats")
    assert(obtained.contains("state"))
    assert(obtained.contains("trader's"))
    assert(obtained.contains("state-of-the-art"))
  }

  // test match word with apostrophe (Trader's Cove should have a match in 203.txt)

  // regex: "[^\W\d](\w|[-']{1,2}(?=\w))*"
  // regex: "[^\W\d]([A-Za-z]|[-'](?=[A-Za-z]))*"
  //    => matches a sequence of letters interrupted by, at most, one ' or -
  //    (trader's state-of-the-art)
  // (y'all'd've is a contrived, yet valid word)
  // TODO: document what a word is in the README

}