package ds13.logger

trait LoggerSupported {

  val logger: Logger

  val nameForLog: Option[String] = None

  def debug(text: String) = logger.debug(nameForLog.fold("")(_ + ": ") + text)

  def info(text: String) = logger.info(nameForLog.fold("")(_ + ": ") + text)

  def warn(text: String) = logger.warn(nameForLog.fold("")(_ + ": ") + text)

  def err(text: String) = logger.err(nameForLog.fold("")(_ + ": ") + text)

}

trait Logger {

  def info(text: String) = log(text, Logger.SEVERITY_INFO)

  def warn(text: String) = log(text, Logger.SEVERITY_WARN)

  def debug(text: String) = log(text, Logger.SEVERITY_DEBUG)

  def err(text: String) = log(text, Logger.SEVERITY_ERR)

  def log(text: String, severity: Int)

}

object Logger {

  val SEVERITY_INFO = 0

  val SEVERITY_WARN = SEVERITY_INFO + 1

  val SEVERITY_DEBUG = SEVERITY_WARN + 1

  val SEVERITY_ERR = SEVERITY_DEBUG + 1

}

class DummyLogger extends Logger {

  override def log(text: String, severity: Int) {}

}

object DummyLogger {

  def apply(): DummyLogger = new DummyLogger()

}

class PrintAllLogger extends Logger {

  override def log(text: String, severity: Int) =
    println(text)

}

object PrintAllLogger {

  def apply(): PrintAllLogger = new PrintAllLogger()

}

