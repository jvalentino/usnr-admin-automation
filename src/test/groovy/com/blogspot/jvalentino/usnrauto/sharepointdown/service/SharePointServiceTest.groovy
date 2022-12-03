package com.blogspot.jvalentino.usnrauto.sharepointdown.service

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

class SharePointServiceTest {
	private SharePointService service
	
	@Before
	void setup() {
		service = new SharePointService()
	}
	
	@Test
	void testIsLocationValid() {
		File file = new File(".")
		
		assertThat(service.isLocationValid("http://foo.bar", file), 
			is("The URL must start with https://private.navyreserve.navy.mil"))
		
		assertThat(service.isLocationValid("https://private.navyreserve.navy.mil/...", file),
			is("You must enter a valid SharePoint location, try the URL to documents on your unit SharePoint"))
		
		assertThat(service.isLocationValid("https://private.navyreserve.navy.mil", file),
			is("You can't enter the root of the Navy SharePoint, try the URL to documents on your unit SharePoint"))
		
		assertThat(service.isLocationValid("https://private.navyreserve.navy.mil/foo/bar", file), nullValue())
	}
	
}
