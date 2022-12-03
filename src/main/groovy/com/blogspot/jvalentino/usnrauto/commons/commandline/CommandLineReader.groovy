package com.blogspot.jvalentino.usnrauto.commons.commandline

class CommandLineReader extends Thread {
	
	private InputStream is;
	private CommandLineListener ui;
	private String result = "";
	
	public CommandLineReader(InputStream is, CommandLineListener ui) {
		this.is = is;
		this.ui = ui;
	}
	
	public void run() {
		try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (ui != null)
				ui.append(line + "\n");
			result += line + "\n";
		}
		
		} catch (Exception e) {
			ui.append(e.getMessage());
		}
	}
	
	public String getResult() {
		return result;
	}

}
