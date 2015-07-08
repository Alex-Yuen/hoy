package net.xland.util;

import java.io.OutputStream;
import java.io.PrintStream;

import net.xland.aqq.service.PacketSender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4jPrintStream extends PrintStream {
	private Logger logger = LogManager.getLogger(PacketSender.class
			.getName());
	
	public Log4jPrintStream(OutputStream out) {
		super(out);
	}

	private void log(Object info) {
//		System.out.println(info);
		logger.info(info);
	}

	public void println(boolean x) {
		log(Boolean.valueOf(x));
	}

	public void println(char x) {
		log(Character.valueOf(x));
	}

	public void println(char[] x) {
		log(x == null ? null : new String(x));
	}

	public void println(double x) {
		log(Double.valueOf(x));
	}

	public void println(float x) {
		log(Float.valueOf(x));
	}

	public void println(int x) {
		log(Integer.valueOf(x));
	}

	public void println(long x) {
		log(x);
	}

	public void println(Object x) {
		log(x);
	}

	public void println(String x) {
		log(x);
	}
}
