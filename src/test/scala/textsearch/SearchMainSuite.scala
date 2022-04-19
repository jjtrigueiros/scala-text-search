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

  test("Parse example words") {
    val obtained = Program.parseWords("Trader's state-of-the-art State states States are démodée")
    assert(obtained.contains("state"))
    assert(obtained.contains("trader's"))
    assert(obtained.contains("state-of-the-art"))
    assert(obtained.contains("démodée"))
    assert(obtained("states") == 2)
  }

  // TODO: test match special word in a specific file (Trader's Cove should have a match in 203.txt)
  //test("Match specific words") {
  //
  //}

  // TODO: test match multiples of a certain word in a file

  // TODO: scoring tests
}