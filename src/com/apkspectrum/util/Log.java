package com.apkspectrum.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class Log {
	static private Logger logger = getLogger(Log.class.getName());
	static private SimpleDateFormat dateFormat
								= new SimpleDateFormat("MM-dd hh:mm:ss.SSS");
	static private StreamHandler consoleHandler;
	static private ByteArrayOutputStream logOutputStream;
	static private boolean enableConsoleHandler = true;

	static public enum Level {
		ALL		(java.util.logging.Level.ALL,		' '),
		VERBOSE	(java.util.logging.Level.FINEST,	'V'),
		DEBUG	(java.util.logging.Level.FINE,		'D'),
		INFO	(java.util.logging.Level.INFO,		'I'),
		WARN	(java.util.logging.Level.WARNING,	'W'),
		ERROR	(java.util.logging.Level.SEVERE,	'E');

		private java.util.logging.Level loggerLevel;
		private char acronym;

		private Level(java.util.logging.Level level, char acronym) {
			this.loggerLevel = level;
			this.acronym = acronym;
		}

		public char getAcronym() {
			return this.acronym;
		}

		public java.util.logging.Level getLoggerLevel() {
			return this.loggerLevel;
		}

		static public char getAcronym(java.util.logging.Level level) {
			for (Level l : Level.values()) {
				if (l.loggerLevel == level)
					return l.getAcronym();
			}
			return ' ';
		}
	}

	static public void e(Object msg) {
		e(Objects.toString(msg));
	}

	static public void e(String msg) {
		e(getCaller(), msg);
	}

	static public void e(String tag, Object msg) {
		e(tag, Objects.toString(msg));
	}

	static public void e(String tag, String msg) {
		logger.severe(makeLogMessage(tag, msg));
	}

	static public void w(Object msg) {
		w(Objects.toString(msg));
	}

	static public void w(String msg) {
		w(getCaller(), msg);
	}

	static public void w(String tag, Object msg) {
		w(tag, Objects.toString(msg));
	}

	static public void w(String tag, String msg) {
		logger.warning(makeLogMessage(tag, msg));
	}

	static public void i(Object msg) {
		i(Objects.toString(msg));
	}

	static public void i(String msg) {
		i(getCaller(), msg);
	}

	static public void i(String tag, Object msg) {
		i(tag, Objects.toString(msg));
	}

	static public void i(String tag, String msg) {
		logger.info(makeLogMessage(tag, msg));
	}

	static public void d(Object msg) {
		d(Objects.toString(msg));
	}

	static public void d(String msg) {
		d(getCaller(), msg);
	}

	static public void d(String tag, Object msg) {
		d(tag, Objects.toString(msg));
	}

	static public void d(String tag, String msg) {
		logger.fine(makeLogMessage(tag, msg));
	}

	static public void v(Object msg) {
		v(Objects.toString(msg));
	}

	static public void v(String msg) {
		v(getCaller(), msg);
	}

	static public void v(String tag, Object msg) {
		v(tag, Objects.toString(msg));
	}

	static public void v(String tag, String msg) {
		logger.finest(makeLogMessage(tag, msg));
	}

	static public String makeLogMessage(String tag, String msg) {
		return tag + " : " + msg;
	}

	static public void setLevel(Level level) {
		logger.setLevel(level.getLoggerLevel());
	}

	static public void enableConsoleLog(boolean enable) {
		enableConsoleHandler = enable;
	}

	static public Logger getLogger() {
		return logger;
	}

	static public String getLog() {
		//streamHandler.flush();
		return logOutputStream.toString();
	}

	static public void saveLogFile(String name) {
		//streamHandler.flush();
		try {
			logOutputStream.writeTo(new FileOutputStream(name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static private Logger getLogger(String name) {
		logger = Logger.getLogger(name);
		logger.setLevel(Level.ALL.getLoggerLevel());
		logger.setUseParentHandlers(false);

		Formatter ft = new LogFormatter();

		consoleHandler = new ConsoleHandlerStd();
		consoleHandler.setFormatter(ft);
		consoleHandler.setLevel(Level.ALL.getLoggerLevel());
		logger.addHandler(consoleHandler);

		logOutputStream = new ByteArrayOutputStream();
		System.setOut(new LogPrintStream(System.out, logOutputStream));
		System.setErr(new LogPrintStream(System.err, logOutputStream));

		return logger;
	}

	static private String getCaller() {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		return caller.getClassName().replaceAll(".*\\.([^$]*).*", "$1")
				+ "(" + caller.getLineNumber() + ")";
	}

	static private class LogFormatter extends Formatter {
		@Override
		public String format(LogRecord rec) {
			String head = String.format("%s %03d %c ",
					dateFormat.format(new Date(rec.getMillis())),
					rec.getThreadID(),
					Level.getAcronym(rec.getLevel()));
			String msg = rec.getMessage();
			if (msg.contains("\n")) {
				String tag = String.format("%" + msg.indexOf(":") + "s", "");
				msg = msg.replaceAll("\n", "\n" + head + tag + ": ");
			}
			return head + msg;
		}
	}

	@SuppressWarnings("unused")
	static private class ConsoleHandlerToStdout extends ConsoleHandler {
		public ConsoleHandlerToStdout() {
			super();
			setOutputStream(System.out);
		}
	}

	static private class LogPrintStream extends PrintStream {
		PrintStream bufferPrintStream;
		public LogPrintStream(OutputStream arg0, OutputStream arg1) {
			super(arg0);
			bufferPrintStream = new PrintStream(arg1);
		}

		@Override
		public void print(String arg0) {
			if(enableConsoleHandler) super.print(arg0);
			bufferPrintStream.println(arg0);
		}

		@Override
		public void println(String arg0) {
			if(enableConsoleHandler) super.println(arg0);
			else bufferPrintStream.println(arg0);
		}
	}

	static private class ConsoleHandlerStd extends StreamHandler {
		public void publish(LogRecord record) {
			if (record.getLevel().intValue()
					< java.util.logging.Level.WARNING.intValue()) {
				System.out.println(getFormatter().format(record));
			} else {
				System.err.println(getFormatter().format(record));
			}
		}
	}
}
