package textsearch

import munit.FunSuite
import java.io.File
//import scala.io.Source
import scala.util.Try


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
    assert(obtained.isInstanceOf[Program.Index])
  }
}