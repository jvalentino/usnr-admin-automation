package com.blogspot.jvalentino.usnrauto.commons

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.util.CommandLineUtil;
import com.blogspot.jvalentino.usnrauto.util.OsType;


class ExcelServiceTest {

	ExcelService service
	
	@Before
	void setup() {
		service = new ExcelService()
	}
	
	@Test
	void testSearchForLibreOfficeBinary_Preference() {
		
		OsType type = CommandLineUtil.getOsType()
		
		File pref = new File("build/foobar.foo")
		
		File result = service.searchForLibreOfficeBinary(pref, type)
		
		if (result != null) {
			assertThat(result.getName(), is("foobar.foo"))
		}
	}
	
	@Test
	void testSearchForLibreOfficeBinary_NoPreference() {
		
		OsType type = CommandLineUtil.getOsType()
				
		File result = service.searchForLibreOfficeBinary(null, type)
		
		if (result == null) {
			// This will happen on a build server
			System.err.println("File could not be found ${result}")
		} else if (type == OsType.MAC) {
			assertThat(result.getName(), is("soffice"))
		} else {
			assertThat(result.getName(), is("soffice.exe"))
		}
	}
	
	@Test
	void testExecuteLibreOfficeCommand() {
		File file = service.searchForLibreOfficeBinary(null, CommandLineUtil.getOsType())
		
		if (file == null) {
			// This will happen on a build server
			System.err.println("File could not be found ${file}")
		} else {
		
			String result = service.executeLibreOfficeCommand(file, "--headless --help")
			assertThat(result, containsString("Usage: soffice [options] [documents...]"))
		}
	}
}
