 
package com.telhoc.BorregoApiLibrary;

import java.sql.Timestamp;

public class Log {
	
	public static boolean printLogs = true;
	public static boolean printErrors = true;

	private Log() {
	}

	public static void printf(String format, Object... args) {
		if (printLogs) {
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			System.out.printf(ts.toString() + " : " + format, args);
		}
	}

	public static void println(String stringOut) {
		if (printLogs) {
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			System.out.println(ts.toString() + " : " + stringOut);
		}
	}
	
	public static void printe(String stringOut) {
		if (printErrors) {
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			System.err.println(ts.toString() + " : " + stringOut);
		}
	}
	
	public static void print(String stringOut) {
		if (printLogs) {
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			System.out.print(ts.toString() + " : " + stringOut);
		}
	}

	public static void v(String msg) {
		println(msg);
	}

	public static void d(String msg) {
		println(msg);
	}

	public static void e(String msg) {
		println(msg);
	}
	
}
