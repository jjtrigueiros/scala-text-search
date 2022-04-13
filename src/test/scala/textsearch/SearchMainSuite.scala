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
        assert(index.filesAndWords.sizeIs == 27)
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
    val obtained = Program.parseWords("Trader's state-of-the-art State states States are démodée")
    assert(obtained.contains("state"))
    assert(obtained.contains("trader's"))
    assert(obtained.contains("state-of-the-art"))
    assert(obtained.contains("démodée"))
    assert(obtained("states") == 2)
  }

  // TODO: test match special word in a specific file (Trader's Cove should have a match in 203.txt)

  // TODO: test match multiples of a certain word in a file

  // TODO: as complete a test as possible (integration)
}