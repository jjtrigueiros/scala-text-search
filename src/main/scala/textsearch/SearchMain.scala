package textsearch

import java.io.File
import scala.util.Try

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

  case class Index() // TODO: Implement this

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
  def index(directory: File): Index = ???

  def iterate(indexedFiles: Index): Unit = {
    print(s"search> ")
    val searchString = readLine()
    // TODO: Make it print the ranking of each file and its corresponding score
    iterate(indexedFiles)
  }
}