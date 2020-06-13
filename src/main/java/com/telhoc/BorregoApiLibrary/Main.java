package com.telhoc.BorregoApiLibrary;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

/**
 * Hello world!
 *
 */
public class Main {

	public static void main(String[] args) {

		Log.printLogs = false;

		BorregoApi borrego = new BorregoApi();
		BorregoCli cli = new BorregoCli(borrego);

		// Read command from cmd line
		String cmd = "";
		String prompt = "borrego=# ";
		LineReader reader = LineReaderBuilder.builder().build();

		System.out.println("Welcome to Borrego! ");

		while (true) {
			System.out.println("");
			cmd = "";
			try {
				cmd = reader.readLine(prompt);
				cmd = cmd.replace(";", "");
				if (cmd.equals("\\q") || cmd.equals("q") || cmd.equals("\\Q") || cmd.equals("Q")) {
					System.exit(0);
					break;
				}
				if (cmd.equals("") || cmd.equals("\r") || cmd.equals(" ")) {
					continue;
				}
				String cmdOutput = cli.processCommand(cmd);
				if (cmdOutput != null) {
					System.out.println(cmdOutput);
				}

			} catch (UserInterruptException e) {
			} catch (EndOfFileException e) {
				return;
			}

		}

	}


}
