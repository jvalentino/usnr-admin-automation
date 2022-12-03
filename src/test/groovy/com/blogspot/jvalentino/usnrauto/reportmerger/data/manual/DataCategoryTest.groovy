package com.blogspot.jvalentino.usnrauto.reportmerger.data.manual

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;


class DataCategoryTest {

	@Test
	void testGetForText() {
		for (DataCategory value : DataCategory.values()) {
			assertThat(DataCategory.getForText(value.text), is(value))
		}
		
		assertThat(DataCategory.getForText("foo"), nullValue())
	}
	
	@Test
	void testListValues() {
		assertThat(DataCategory.listValues(), 
			is("MEMBERSHIP, ELEARNING, INDIVIDUAL AUGMENTEE, GMT, MANUAL INPUTS, ESAMS"))
	}
	
}
