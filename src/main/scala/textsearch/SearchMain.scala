package textsearch

import java.io.File
import scala.annotation.tailrec
import scala.io.{Codec, Source}
import scala.util.{Failure, Success, Try, Using}
import scala.util.matching.Regex

object SearchMain extends App {
  Program
    .readDirectory(args)
    .fold(
      println,
      dir => Program.index(dir)
        .fold(
          println,
          index => Program.iterate(index)
        )
    )
}

object Program {
  import scala.io.StdIn.readLine

  case class Index(filesAndWords: List[(File, Map[String, Int])])
  // Class representing files indexed in memory.
  // A list of (file, map(words -> n_matches)) value pairs.

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

  def parseWords(src: String): Map[String, Int] = { // : Map[String, Int]
    // From a provided string, returns a map of (found word -> number of times word was found)
    // Word defined as a case-insensitive sequence of letters
    // which can be interrupted by one consecutive hyphen or apostrophe
    // Ex.: banana, should've, state-of-the-art
    val wordPattern: Regex = "[a-zA-ZÀ-ÖØ-öø-ÿ]([a-zA-ZÀ-ÖØ-öø-ÿ]|[-'](?=[a-zA-ZÀ-ÖØ-öø-ÿ]))*".r
    wordPattern.findAllIn(src)
      .toList.map(word => word.toLowerCase)
      .groupBy(identity)
      .transform((_, v) => v.size)
  }

  def index(directory: File): Try[Index] = {
    def readFile(file: File): Try[String] = {
      Using(Source.fromFile(file)){
        open_file => open_file.getLines().reduceLeft(_+_)
      }.orElse(
        Using(Source.fromFile(file)(Codec("ISO-8859-1"))){
          open_file => open_file.getLines().reduceLeft(_+_)
        }
      )
    }

    // filter for valid .txt files
    val files = directory.listFiles.filter { f => f.isFile && f.getName.endsWith(".txt") }.toList
    // attempt to open each file, then filter out files that could not be opened
    val listOfTries: List[(File, Try[String])] = files zip files.map(f => readFile(f))

    // separate successful and unsuccessful cases
    val (filesReadSuccessfully, filesCouldNotBeRead) = listOfTries.partition(_._2.isSuccess)

    // print the unsuccessful cases
    filesCouldNotBeRead.foreach {
      case (file, failure) =>
        println(s"Error parsing ${file.getName}: $failure. Maybe the encoding is unsupported?")
    }

    // convert List[(File, Success[String])] to Try[List[(File, String)]]
    val tryFilesAndContents: Try[List[(File, String)]] = {
      Try(
        filesReadSuccessfully.map(fileAndContents => (fileAndContents._1, fileAndContents._2.get))
      )
    }

    // convert Try[List[(File, String)]] to Try[Index] if successfully filtered
    tryFilesAndContents match {
      case Success(contents) => Success(
        Index(contents.map(fileAndContents => (fileAndContents._1, parseWords(fileAndContents._2))))
      )
      case Failure(exception) => Failure(exception)
    }
  }

  @tailrec
  def iterate(indexedFiles: Index): Unit = {
    // For a given map of words and their occurrences,
    // returns the number of occurrences of the provided searchWord up to a maximum of maxHits
    def scoreWord(wordCounts: Map[String, Int], searchWord: String, maxHits: Int): Int ={
      wordCounts.get(searchWord) match {
        case Some(nCount) => maxHits min nCount
        case None => 0
      }
    }

    // For a given indexed file, tallies the score for the list of searchWords
    // Returns a tuple of filename and percentage of words matched.
    def scoreFile(indexedFile: (File, Map[String, Int]), searchWords: Map[String, Int]): (String, Double) ={
      val maxScore: Int = searchWords.values.sum
      val filename = indexedFile._1.getName
      val score: Int = searchWords.foldLeft(0)((acc, next) => acc + scoreWord(indexedFile._2, next._1, next._2))
      val rating: Double = (score.toDouble/maxScore.toDouble)*100
      (filename, rating)
    }

    print(s"search> ")
    val searchString = readLine()
    // parse the list of input keywords
    val searchWords = parseWords(searchString)
    // calculate and order the ranking for each file paired with the appropriate filename
    val ranking: List[(String, Double)] =
      indexedFiles.filesAndWords.map(scoreFile(_, searchWords))
        .sortBy(_._2).reverse
    // print the ranking of each file and its corresponding score
    for((file, rank) <- ranking.take(10)){
      if(rank>0) println("["+rank+" %]: "+file)
    }
    iterate(indexedFiles)
  }
}