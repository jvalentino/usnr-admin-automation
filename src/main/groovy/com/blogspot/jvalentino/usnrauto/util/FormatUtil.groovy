package com.blogspot.jvalentino.usnrauto.util

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

class FormatUtil {
	
	//07/06/2015
	static String DATE_FORMAT_MM_DD_YYYY = "MM/dd/yyyy"
	
	static def officerRanks = ["CWO2", "CWO3", "CWO4", "CWO5",
		"ENS", "LTJG", "LT", "LCDR", "CDR", "CAPT", "RADM", "VADM", "ADM"]
	
	/**
	 * Converts double to a string percentage, so 0.98 becomes 98%
	 * @param value
	 * @return
	 */
	static String fractionToPercentage(Double value) {
		DecimalFormat df = new DecimalFormat()
		
		df.setMaximumFractionDigits(2)
		
		df.setMinimumFractionDigits(0)
		
		df.setGroupingUsed(false)
		return df.format(  value * 100) + "%"
	}
	
	/**
	 * Takes a string in two formats, and converts it to a double.
	 * Format 1: 0.99 becomes 0.99D
	 * Format 2: 99% becomes 0.99D
	 * @param value
	 * @return
	 */
	static Double percentageToDouble(String value) {
		Double result = null
		
		if (value.contains("%")) {
			result = Double.parseDouble(value.replace("%", "")) * 0.01
		} else {
			result = Double.parseDouble(value)
		}
		
		return result
	}
	
	static Date toDateInAllPossibleFormats(String value) {
		Date found = formatMMDDYYYY(value)
		
		if (found != null) {
			return found
		}
		
		found = formatTwoStringToDate(value)
		
		if (found != null) {
			return found
		}
		
		return null
	}
	
	static Date formatMMDDYYYY(String value) {
		try {
			return Date.parse(DATE_FORMAT_MM_DD_YYYY, value)
		} catch (Exception e) {
			return null
		}
	}
	
	static String toMMDDYYYY(Date date) {
		if (date == null) {
			return null
		}
		return date.format(DATE_FORMAT_MM_DD_YYYY)
	}
	
	
	static Date formatTwoStringToDate(String value) {
		try {
			return Date.parse("dd-MMM-yyyy", value)
		} catch (Exception e) {
			return null
		}
	}
	
	static String dateToFormatTwoString(Date date) {
		return date.format("dd-MMM-yyyy")
		//return date.parse("dd-MMM-yyyy")
	}
	
	static int daysBetweenDates(Date two, Date one) {
		use(groovy.time.TimeCategory) {
			def duration = one - two
			int days = duration.days
						
			return days
		}
	}
	
	static isValidPhone(String phone) {
		Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}")
		Matcher matcher = pattern.matcher(phone)
		return matcher.matches()
	}
	
	static boolean stringToBoolean(String value) {
		if (value.equalsIgnoreCase("true")) {
			return true
		} else {
			return false
		}
	}
	
	static String[] listToArray(List<String> values) {
		String[] results = new String[values.size()]
		for (int i = 0; i < values.size(); i++) {
			results[i] = values.get(i)
		}
		return results
	}
	
	static double[] doubleUnwrap(Double[] values) {
		double[] results = new double[values.length]
		for (int i = 0; i < values.length; i++) {
			results[i] = values[i].doubleValue()
		}
		return results
	}
	
	static int[] intUnwrap(Integer[] values) {
		int[] results = new int[values.length]
		for (int i = 0; i < values.length; i++) {
			results[i] = values[i].intValue()
		}
		return results
	}
	
	/**
	 * Returns a list of the indexes for any of the given element in the given list.
	 * For example if given [a,b] with [a,b,c,d] then the result is [0,1]
	 * @param elements
	 * @param list
	 * @return
	 */
	static int[] getIndexesForElements(String[] elements, String[] list) {
		List<Integer> result = new ArrayList<Integer>()
		for (String element : elements) {
			int index = ArrayUtils.indexOf(list, element)
			
			if (index != -1) {
				result.add(index)
			}
		}
		return FormatUtil.intUnwrap(result as Integer[])
	}
	
	/**
	 * Removes the given indexes from the given list.
	 * For example for list [a,b,c] if given [0,2] then the result is [b]
	 * @param list
	 * @param indexes
	 * @return
	 */
	static <T> T[] removeFromArray(T[] list, int[] indexes) {
		List<T> results = new ArrayList<T>()
				
		for (int i = 0; i < list.length; i++) {
			
			// if the index is NOT in the list of indexes
			int index = ArrayUtils.indexOf(indexes, i)
						
			if (index == -1) {
				results.add(list[i])
			}
		}
		
		return results as T[]
	}
	
	/**
	 * Utility for taking an array of anything and turning it to a comma separated string
	 * @param list
	 * @return
	 */
	@SafeVarargs
	static <T> String arrayToCommaSeparatedString(final T[] list) {
		String result = ""
		for (int i = 0; i < list.length; i++) {
			if (i != list.length - 1) {
				result += list[i] + ", "
			} else {
				result += list[i]
			}
		}
		return result
	}
	
	@SafeVarargs
	static <T> String arrayToSlashSeparatedString(final T[] list) {
		String result = ""
		for (int i = 0; i < list.length; i++) {
			if (i != list.length - 1) {
				result += list[i] + "/"
			} else {
				result += list[i]
			}
		}
		return result
	}
	
	static String cellToString(Cell cell) {
		String cellValue = cell.toString().trim()
		/*byte[] bytes = cellValue.getBytes()
		def validBytes = []
		for (byte b : bytes) {
			if (b >= 0) {
				validBytes.add(b)
			} else {
				validBytes.add(32)
			}
		}
		return new String(validBytes as byte[])*/
		return cellValue
	}
	
	static boolean isOfficer(String rank) {
		if (officerRanks.contains(rank)) {
			return true
		} else {
			return false
		}
	}
	
	static String getFriendlyDifference(DateTime start, DateTime end) {
		Period period = new Period(start, end);
		
		PeriodFormatter formatter = new PeriodFormatterBuilder()
			.appendYears().appendSuffix(" years, ")
			.appendMonths().appendSuffix(" months, ")
			.appendWeeks().appendSuffix(" weeks, ")
			.appendDays().appendSuffix(" days, ")
			.appendHours().appendSuffix(" hours, ")
			.appendMinutes().appendSuffix(" minutes, ")
			.appendSeconds().appendSuffix(" seconds")
			.printZeroNever()
			.toFormatter();
		
		String elapsed = formatter.print(period)
		
		return elapsed
	}
	
	static String removeTextInParenthesis(String text, Integer maxLength=null) {
		String result = ""
		
		boolean inParenthsis = false
		
		for (int i = 0; i < text.length(); i++) {
			String current = text.charAt(i).toString()
			
			if (current.equals("(")) {
				inParenthsis = true
			} else if (current.equals(")")) {
				inParenthsis = false
			} else if (!inParenthsis) {
				result += current
			}
		}
		
		result = result.replaceAll("\\s+", " ")
		
		return trimWithDotDotDot(result, maxLength)
	}
	
	static String trimWithDotDotDot(String result, Integer maxLength=null) {
		if (maxLength != null) {
			if (result.length() >= maxLength) {
				result = result.substring(0, maxLength) + "..."
			}
		}
		return result
	}
	
	static List<String> removeTextInParenthesis(List<String> list, Integer maxLength=null) {
		List<String> results = new ArrayList<String>()
		
		for (String text : list) {
			String result = removeTextInParenthesis(text, maxLength)
			results.add(result)
		}
		
		return results
	}
	
}
