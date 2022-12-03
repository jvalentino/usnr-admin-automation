package com.blogspot.jvalentino.usnrauto.util

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.joda.time.DateTime;
import org.junit.Test;

class FormatUtilTest {

	@Test
	void testFractionToPercentage() {
		assertThat(FormatUtil.fractionToPercentage(0.999), is("99.9%"))
	}
	
	@Test
	void testFormatTwoStringToDate() {
		Date one = FormatUtil.formatTwoStringToDate("02-Jan-2014")
		assertThat(FormatUtil.dateToFormatTwoString(one), is("02-Jan-2014"))
		
		assertThat(FormatUtil.formatTwoStringToDate("?"), nullValue())
		
		assertThat(FormatUtil.formatTwoStringToDate(null), nullValue())
	}
	
	@Test
	void testDaysBetweenDates() {
		Date one = FormatUtil.formatTwoStringToDate("01-Jan-2014")
		Date two = FormatUtil.formatTwoStringToDate("01-Jan-2013")
		assertThat(FormatUtil.daysBetweenDates(one, two), is(-365))
		
		one = FormatUtil.formatTwoStringToDate("01-Jan-2014")
		two = FormatUtil.formatTwoStringToDate("01-Jan-2012")
		assertThat(FormatUtil.daysBetweenDates(one, two), is(-731))
		
		one = FormatUtil.formatTwoStringToDate("01-Dec-2014")
		two = FormatUtil.formatTwoStringToDate("01-Jan-2012")
		assertThat(FormatUtil.daysBetweenDates(one, two), is(-1065))
		
		one = FormatUtil.formatTwoStringToDate("01-Jan-2014")
		two = FormatUtil.formatTwoStringToDate("01-Jan-2009")
		assertThat(FormatUtil.daysBetweenDates(one, two), is(-1826))
		
		one = FormatUtil.formatTwoStringToDate("01-Jan-2014")
		two = FormatUtil.formatTwoStringToDate("01-Jan-2008")
		assertThat(FormatUtil.daysBetweenDates(one, two), is(-2192))
	}
	
	@Test
	void testIsValidPhone() {
		assertThat(FormatUtil.isValidPhone("111-222-3333"), is(true))
		assertThat(FormatUtil.isValidPhone("111-222-333"), is(false))
		assertThat(FormatUtil.isValidPhone("111222333"), is(false))
	}
	
	@Test
	void testPercentageToDouble() {
		assertThat(FormatUtil.percentageToDouble(".98"), is(0.98D))
		assertThat(FormatUtil.percentageToDouble("0.9876"), is(0.9876D))
		assertThat(FormatUtil.percentageToDouble("98%"), is(0.98D))
		assertThat(FormatUtil.percentageToDouble("98.76%"), is(0.9876D))
	}
	
	@Test
	void testGetIndexesForElements() {
		String[] elements = ["c", "d"]
		String[] list = ["a", "b", "c", "d", "e"]
		int[] results = FormatUtil.getIndexesForElements(elements, list)
		assertThat(results.toString(), is("[2, 3]"))
	}
	
	@Test
	void testRemoveFromArray() {
		String[] list = ["a", "b", "c", "d", "e"]
		int[] indexes = [1, 3]
		String[] result = FormatUtil.removeFromArray(list, indexes)
		assertThat(result.toString(), is("[a, c, e]"))
	}
	
	@Test
	void testArrayToCommaSeparatedString() {
		String[] one = ["a", "b", "c"]
		assertThat(FormatUtil.arrayToCommaSeparatedString(one), is("a, b, c"))
		
		int[] two = [1, 2, 3]
		assertThat(FormatUtil.arrayToCommaSeparatedString(two), is("1, 2, 3"))
	}
	
	@Test
	void testGetFriendlyDifference() {
		int year = 2010
		int monthOfYear = 1
		int dayOfMonth = 1
		int hourOfDay = 10
		int minuteOfHour = 0
		int secondOfMinute = 0
		
		DateTime start = new DateTime(
			year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute)
		
		DateTime end = new DateTime(
			year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour + 2, secondOfMinute + 2)
		
		assertThat(FormatUtil.getFriendlyDifference(start, end), is("2 minutes, 2 seconds"))
		
		end = new DateTime(
			year, monthOfYear, dayOfMonth, hourOfDay + 2, minuteOfHour + 2, secondOfMinute + 2)
		
		assertThat(FormatUtil.getFriendlyDifference(start, end), is("2 hours, 2 minutes, 2 seconds"))
		
		
		
	}
	
	@Test
	void testFormatMMDDYYYY() {
		String input = "07/03/1995"
		Date result = FormatUtil.formatMMDDYYYY(input)
		String output = FormatUtil.dateToFormatTwoString(result)
		assertThat(output, is("03-Jul-1995"))
	}
	
	@Test
	void testRemoveTextInParenthesis() {
		assertThat(FormatUtil.removeTextInParenthesis("alpha (bravo charlie) (zulu) delta"), is("alpha delta"))
	}
}
