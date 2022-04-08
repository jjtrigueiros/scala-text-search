package textsearch

import java.io.File
import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

object SearchMain extends App {
  Program
    .readDirectory(args)
    .fold(
      println,
      dir => Program.iterate(Program.index(dir))
    )
}

object Program {
  import scala.io.StdIn.readLine

  case class Index(filesAndContents: Map[File, Try[String]])
  // Class representing files indexed in memory.
  // A map of (file -> contents) value pairs.

  sealed trait ReadDirectoryError

  case object MissingPathArg extends ReadDirectoryError
  case class NotDirectory(error: String) extends ReadDirectoryError
  case class DirectoryNotFound(t: Throwable) extends ReadDirectoryError

  def readDirectory(args: Array[String]): Either[ReadDirectoryError, File] = {
    for {
      path <- args.headOption.toRight(MissingPathArg)
      directory <- Try(new java.io.File(path)).fold(
        throwable => Left(DirectoryNotFound(throwable)),
        file =>
          if (file.isDirectory) Right(file)
          else Left(NotDirectory(s"Path [$path] is not a directory"))
      )
    } yield directory
  }

  // TODO: Index all files in the directory
  def index(directory: File): Index = {
    def readFile(file: File): Try[String] = {
      Using(Source.fromFile(file)){
        open_file => open_file.getLines().reduceLeft(_+_)
      }
    }

    val files = directory.listFiles.filter { f => f.isFile && f.getName.endsWith(".txt") }.toList

    val files_and_contents = (files zip files.map(f => readFile(f))).toMap

    // TODO: Alert the user if a text file could not be opened
    // TODO: Try validating below with fold (check signature)
    // val valid_files_and_contents =
    //   files_and_contents.map { case (file: File, Success(content)) => (file, content) }

    Index(files_and_contents)
  }

  @tailrec
  def iterate(indexedFiles: Index): Unit = {
    print(s"search> ")
    val searchString = readLine()
    // TODO: Make it print the ranking of each file and its corresponding score
    iterate(indexedFiles)
  }
}