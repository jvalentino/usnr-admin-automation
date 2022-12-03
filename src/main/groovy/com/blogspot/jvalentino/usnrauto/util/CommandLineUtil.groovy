package com.blogspot.jvalentino.usnrauto.util

import java.awt.Desktop;

import com.blogspot.jvalentino.usnrauto.commons.commandline.CommandLineListener;
import com.blogspot.jvalentino.usnrauto.commons.commandline.CommandLineReader;

class CommandLineUtil {

	static void open(File file) throws Exception {
		
		/*String[] cmd = null
		
		if (getOsType() == OsType.WINDOWS) {
			cmd = ["cmd", "/c", "start", "\"" + file.getAbsolutePath() + "\"" ]
		} else {
			cmd = ["open", file.getAbsolutePath() ]
		}
		
		println cmd.toString()
		//Runtime.getRuntime().exec(cmd);*/
		
		Desktop.getDesktop().open(file)
		
	}
	
	static String executeBinary(File binary, String text, CommandLineListener ui=null) {
		if (CommandLineUtil.getOsType() == OsType.WINDOWS) {
			String command = binary.getName() + " " + text
			return CommandLineUtil.execute(command, binary.getParentFile().getAbsolutePath(), ui)
		} else {
			String command = binary.getAbsolutePath() + " " + text
			return CommandLineUtil.execute(command, null, ui)
		}
	}
	
	static String execute(String text, String dir=null, CommandLineListener ui=null) {
		String result = "";
		if (ui != null)
			ui.append("> " + text + "\n");

		String shell = "";
		String shellSwitch = "";

		if (CommandLineUtil.isWindows()) {
			shell = "cmd";
			shellSwitch = "/c";
		} else {
			shell = "/bin/sh";
			shellSwitch = "-c";
		}

		Process p;
		try {
			String[] cmd = [ shell, shellSwitch, text ];
			String[] envp = [];
			
			if (envp.length == 0) {
				envp = null;
			}
			
			println cmd.toString()
			
			if (dir == null) {			
				p = Runtime.getRuntime().exec(cmd, envp);
			} else {
				p = Runtime.getRuntime().exec(cmd, envp, new File(dir));
			}
			
			p.waitFor();
			
			CommandLineReader input = new CommandLineReader(p.getInputStream(), ui);
			CommandLineReader error = new CommandLineReader(p.getErrorStream(), ui);
			
			input.start();
			error.start();
			
			input.join();
			error.join();
			
			result += input.getResult();
			result += error.getResult();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
		
	}
	
	static OsType getOsType() {
		String osName = System.getProperty("os.name");
		String osVersion = System.getProperty("os.version");
		
		if (osName.startsWith("Mac")) {
			return OsType.MAC
			
		} else {
			return OsType.WINDOWS
		}
	}
	
	static boolean isWindows() {
		if (getOsType() == OsType.WINDOWS) {
			return true
		} else {
			return false
		}
	}
}
