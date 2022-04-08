package textsearch

import munit.FunSuite

import java.io.File
import scala.io.{BufferedSource, Source}
import scala.util.{Try, Using}


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

    val obtained = Program.readDirectory(test_args).fold(println, dir => Program.index(dir))
    obtained match{
      case obtained: Program.Index =>
        assert(obtained.filesAndContents.sizeIs == 28)
      case _ =>
        fail("Obtained something other than a Program.Index")
    }
  }

  // test("Ignores an open file"){
  //   val filepath: String = getClass.getResource("/test_folder/203.txt").getPath
  //   Using(Source.fromFile(filepath)){
  //     open_file =>
  //       val path: String = getClass.getResource("/test_folder").getPath
  //       val test_args: Array[String] = Array(path)
  //       val obtained = Program.readDirectory(test_args).fold(println, dir => Program.index(dir))
  //       obtained match{
  //         case obtained: Program.Index =>
  //           assert(obtained.filesAndContents.sizeIs == 9999) // not working
  //         case _ =>
  //           assert(false)
  //       }
  //   }
  // }


}