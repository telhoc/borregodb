package com.telhoc.BorregoApiLibrary;

public class BorregoCli {

	private BorregoApi mBorregoApi;

	public BorregoCli(BorregoApi borregoApi) {
		mBorregoApi = borregoApi;
	}

	private boolean checkValidCmd(String cmd) {

		String lowerCmd = cmd.toLowerCase();
		if (lowerCmd.startsWith("select") || lowerCmd.startsWith("insert") || lowerCmd.startsWith("update")
				|| lowerCmd.startsWith("delete") || lowerCmd.contains("show tables") 
				|| lowerCmd.startsWith("index ")) {
			return true;
		}

		return false;
	}

	public String processCommand(String cmd) {

		String cmdOutput = null;

		boolean validCmd = checkValidCmd(cmd);
		if (!validCmd)
			return null;

		cmdOutput = mBorregoApi.querySql(cmd);

		return cmdOutput;

	}
}
