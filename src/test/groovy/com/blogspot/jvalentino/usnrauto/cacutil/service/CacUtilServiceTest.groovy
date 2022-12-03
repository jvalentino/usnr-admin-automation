package com.blogspot.jvalentino.usnrauto.cacutil.service

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.util.CommandLineUtil;
import com.blogspot.jvalentino.usnrauto.util.OsType;

class CacUtilServiceTest {

	private CacUtilService service
	
	@Before
	void setup() {
		service = new CacUtilService()
	}
	
	@Test
	void testSearchForCacKeyLibrary_Preference() {
		
		OsType type = CommandLineUtil.getOsType()
		
		File pref = new File("build/foobar.foo")
		
		File result = service.searchForCacKeyLibrary(pref, type)
		
		assertThat(result.getName(), is("foobar.foo"))
	}
	
	@Test
	void testSearchForCacKeyLibrary_NoPreference() {
		
		OsType type = CommandLineUtil.getOsType()
				
		File result = service.searchForCacKeyLibrary(null, type)
		
		if (result == null)  {
			System.err.println("No cac key library")
		} else if (type == OsType.MAC) {
			assertThat(result.getName(), is("libcackey.dylib"))
		} else {
			assertThat(result.getName(), is("libcackey.dll"))
		}
	}
}
