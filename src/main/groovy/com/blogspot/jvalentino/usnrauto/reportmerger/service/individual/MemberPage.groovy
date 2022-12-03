package com.blogspot.jvalentino.usnrauto.reportmerger.service.individual

import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import com.blogspot.jvalentino.usnrauto.main.AppState;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.mas.MasCode;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.merge.NrowsOrder;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;
import com.blogspot.jvalentino.usnrauto.util.PdfUtil;

import static com.blogspot.jvalentino.usnrauto.util.FormatUtil.*;
import static com.blogspot.jvalentino.usnrauto.util.PdfUtil.*;

class MemberPage {

	/**
	 * Creates a PDF page that summarizes what a member needs to take care of
	 *
	 * @param document
	 * @param member
	 * @return
	 */
	static PDPage draw(PDDocument document, IndividualSummary member,
		SummaryReport report) {
		
		PDPage page = new PDPage();

		// Start a new content stream which will "hold" the to be created content
		PDPageContentStream cos = new PDPageContentStream(document, page);

		int y = PdfUtil.getTopOfPagePosition(page)

		y = drawPageHeader(page, cos, y, member)
		
		y = drawMemberSummary(page, cos, y, member, report)
		
		y = drawElearning(page, cos, y, member)
		
		y = drawCategory2Gmts(page, cos, y, member)
		
		y = drawCategory1Gmts(page, cos, y, member)
		
		y = drawIaCourses(page, cos, y, member)
		
		y = drawEsamsCourses(page, cos, y, member, report)
		
		y = drawMasCodes(page, cos, y, member)
		
		y = drawOrders(page, cos, y, member, report)
		
		y = drawManualInputs(page, cos, y, member, report)
		
		PdfUtil.drawCenteredText(report.pdfFooter,
			WEIGHT_NORMAL, FONT_SMALL, page, cos, FOOTER_POSITION)
		
		// Make sure that the content stream is closed:
		cos.close();

		return page
	}
		
	static int drawPageHeader(PDPage page, PDPageContentStream cos, int y, IndividualSummary member) {
		
		PdfUtil.drawCenteredText(PII_WARNINGS,
			WEIGHT_NORMAL, FONT_NORMAL, page, cos, y)

		y -= GAP
		PdfUtil.drawText("INDIVIDUAL MEMBER READINESS REPORT - "
			+ new Date().format("MMM dd yyyy").toUpperCase(),
			WEIGHT_BOLD, FONT_LARGE, page, cos, MARGIN_LEFT, y)
	
		y -= GAP
		PdfUtil.drawText(member.getRank() + " " + member.getFirstName() + " " + member.getLastName(),
				WEIGHT_NORMAL, FONT_LARGE, page, cos, MARGIN_LEFT, y)
	
		y -= GAP_SMALL
		PdfUtil.drawLine(page, cos, y)
	
		y -= GAP_SMALL
		PdfUtil.drawCenteredText("FOLD TO THIS LINE TO COVER PII",
				WEIGHT_NORMAL, FONT_NORMAL, page, cos, y)
		
		y -= GAP
		
		return y
	}
	
	static int drawElearning(PDPage page, PDPageContentStream cos, int y, IndividualSummary member) {
		
		PdfUtil.drawText("GENERAL E-LEARNING COURSES TO BE COMPLETED",
			WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
	
		y -= GAP_SMALL
		PdfUtil.drawText(
			"Note that some of these courses overlap category I and II GMTs. Do the GMT versions instead.",
			WEIGHT_NORMAL, FONT_SMALL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_MICRO
		if (member.existsInELearning && member.geteLearningCoursesToDo().size() != 0) {
			
			String[][] content = PdfUtil.generateTrainingTableData(member.geteLearningCoursesToDo())
			cos.setNonStrokingColor(Color.RED)
		   y = PdfUtil.drawTable(page, cos, y.floatValue(), MARGIN_LEFT.floatValue(), content)
		   cos.setNonStrokingColor(Color.BLACK)
		   
		} else if (member.existsInELearning && member.geteLearningCoursesToDo().size() == 0) {
			
			y -= GAP_MICRO
			PdfUtil.drawText("Member has completed all available courses",
				WEIGHT_NORMAL_OBLIQUE, FONT_SMALL, page, cos, MARGIN_LEFT, y)
			y -= GAP_SMALL
		
		} else {
		
			y -= GAP_MICRO
			cos.setNonStrokingColor(Color.RED)
			PdfUtil.drawText("Member does not show on unit e-Learning report from FLTMPS",
				WEIGHT_NORMAL_OBLIQUE, FONT_SMALL, page, cos, MARGIN_LEFT, y)
			cos.setNonStrokingColor(Color.BLACK)
			y -= GAP_SMALL
			
		}
		
		return y
	}
	
	static int drawCategory2Gmts(PDPage page, PDPageContentStream cos, int y, IndividualSummary member) {
		// CAT TWO GMTS
		y -= GAP_MICRO
		PdfUtil.drawText("CATEGORY TWO GMT COURSES TO BE COMPLETED ON E-LEARNING",
				WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_SMALL
		PdfUtil.drawText(
			"Category II GMTs have migrated to e-Learning and must now be done online.",
			WEIGHT_NORMAL, FONT_SMALL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_MICRO
		if (member.existsInGMT && member.getCategoryTwoGMTsToDo().size() != 0) {
			
			String[][] content = PdfUtil.generateTrainingTableData(member.getCategoryTwoGMTsToDo())
			cos.setNonStrokingColor(Color.RED)
			y = PdfUtil.drawTable(page, cos, y.floatValue(), MARGIN_LEFT.floatValue(), content)
			cos.setNonStrokingColor(Color.BLACK)
			
		} else if (member.existsInGMT && member.getCategoryTwoGMTsToDo().size() == 0) {
		
			y -= GAP_MICRO
			PdfUtil.drawText("Member has completed all available courses",
				WEIGHT_NORMAL_OBLIQUE, FONT_SMALL, page, cos, MARGIN_LEFT, y)
			y -= GAP_SMALL
		
		} else {
		
			y -= GAP_MICRO
			cos.setNonStrokingColor(Color.RED)
			PdfUtil.drawText("Member does not show on unit GMT report from FLTMPS",
				WEIGHT_NORMAL_OBLIQUE, FONT_SMALL, page, cos, MARGIN_LEFT, y)
			cos.setNonStrokingColor(Color.BLACK)
			y -= GAP_SMALL
			
		}
		
		return y
	}
	
	static int drawCategory1Gmts(PDPage page, PDPageContentStream cos, int y, IndividualSummary member) {
		// CAT ONE GMTS
		y -= GAP_MICRO
		PdfUtil.drawText("CATEGORY ONE GMT COURSES TO BE DONE IN A CLASSROOM",
				WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_SMALL
		PdfUtil.drawText(
			"Category I GMTs have to be taught by an E7 or above.",
			WEIGHT_NORMAL, FONT_SMALL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_MICRO
		if (member.existsInGMT && member.getCategoryOneGMTsToDo().size() != 0) {
			
			String[][] content = PdfUtil.generateTrainingTableData(member.getCategoryOneGMTsToDo())
			cos.setNonStrokingColor(Color.RED)
			y = PdfUtil.drawTable(page, cos, y.floatValue(), MARGIN_LEFT.floatValue(), content)
			cos.setNonStrokingColor(Color.BLACK)
			
		} else if (member.existsInGMT && member.getCategoryOneGMTsToDo().size() == 0) {
			
			y -= GAP_MICRO
			PdfUtil.drawText("Member has completed all available courses",
				WEIGHT_NORMAL_OBLIQUE, FONT_SMALL, page, cos, MARGIN_LEFT, y)
			y -= GAP_SMALL
			
		} else {
		
			y -= GAP_MICRO
			cos.setNonStrokingColor(Color.RED)
			PdfUtil.drawText("Member does not show on unit GMT report from FLTMPS",
				WEIGHT_NORMAL_OBLIQUE, FONT_SMALL, page, cos, MARGIN_LEFT, y)
			cos.setNonStrokingColor(Color.BLACK)
			y -= GAP_SMALL
			
		}
		
		return y
	}
	
	static int drawIaCourses(PDPage page, PDPageContentStream cos, int y, IndividualSummary member) {
		// IA - Only if they have a VOL MAS code
		boolean isVol  = false
		for (MasCode code : member.masCodes) {
			if (code.name.equalsIgnoreCase("vol")) {
				isVol = true
			}
		}
		
		if (isVol) {
			y -= GAP_MICRO
			PdfUtil.drawText("IA COURSES TO BE COMPLETED ON E-LEARNING (MOB VOL ONLY)",
					WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
			
			y -= GAP_SMALL
			PdfUtil.drawText(
				"These are required if you are being mobilized, though some courses will depend on where you are going.",
				WEIGHT_NORMAL, FONT_SMALL, page, cos, MARGIN_LEFT, y)
			
			y -= GAP_MICRO
			if (member.existsInIA && member.getIaCoursesToDo().size() != 0) {
				
				String[][] content = PdfUtil.generateTrainingTableData(member.getIaCoursesToDo())
				y = PdfUtil.drawTable(page, cos, y.floatValue(), MARGIN_LEFT.floatValue(), content)
				
			} else if (member.existsInIA && member.getIaCoursesToDo().size() == 0) {
			
				y -= GAP_MICRO
				PdfUtil.drawText("Member has completed all available courses",
					WEIGHT_NORMAL_OBLIQUE, FONT_SMALL, page, cos, MARGIN_LEFT, y)
				y -= GAP_SMALL
				
			} else {
			
				y -= GAP_MICRO
				cos.setNonStrokingColor(Color.RED)
				PdfUtil.drawText("Member does not show on unit IA report from FLTMPS",
					WEIGHT_NORMAL_OBLIQUE, FONT_SMALL, page, cos, MARGIN_LEFT, y)
				cos.setNonStrokingColor(Color.BLACK)
				y -= GAP_SMALL
			}
		}
		
		return y
	}
	
	static int drawEsamsCourses(PDPage page, PDPageContentStream cos, int y, IndividualSummary member, SummaryReport report) {
		
		if (report.esamsFile == null) {
			return y
		}
		
		y -= GAP_MICRO
		PdfUtil.drawText("ESAMS COURSES TO BE COMPLETED",
				WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_SMALL
		PdfUtil.drawText(
			"Some of these can be done online while others must be taught in a classroom by a supervisor",
			WEIGHT_NORMAL, FONT_SMALL, page, cos, MARGIN_LEFT, y)
		
		if (member.esamsCoursesToDo.size() != 0) {
			y -= GAP_MICRO
			String[][] content = PdfUtil.generateTrainingTableData(
				FormatUtil.removeTextInParenthesis(member.esamsCoursesToDo, 45))
			cos.setNonStrokingColor(Color.RED)
			y = PdfUtil.drawTable(page, cos, y.floatValue(), MARGIN_LEFT.floatValue(), content)
			cos.setNonStrokingColor(Color.BLACK)
			
		} else if (member.esamsCoursesToDo.size() == 0) {
		
			y -= GAP
			PdfUtil.drawText("Member has completed all available courses",
				WEIGHT_NORMAL_OBLIQUE, FONT_SMALL, page, cos, MARGIN_LEFT, y)
			y -= GAP_SMALL
			
		}
		
		return y
	}
	
	static int drawMasCodes(PDPage page, PDPageContentStream cos, int y, IndividualSummary member) {
		// MAS
		y -= GAP_MICRO
		PdfUtil.drawText("MANPOWER AVAILABILITY STATUS",
				WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		
		List<String> masCodes = new ArrayList<String>()
		List<Color> masColors = new ArrayList<Color>()
		for (MasCode code : member.getMasCodes()) {
			masCodes.add(code.toString())
			masColors.add(code.impact ? Color.red : Color.black)
		}
		if (masCodes.size() == 0) {
			masCodes.add("No MAS Codes")
			masColors.add(Color.black)
		}
		
		String[][] masContent = new String[masCodes.size()][1]
		Color[][] masColorContent = new Color[masCodes.size()][1]
		for (int i = 0; i < masCodes.size(); i++) {
			masContent[i][0] = masCodes.get(i)
			masColorContent[i][0] = masColors.get(i)
		}
		
		y -= GAP_MICRO
		y = PdfUtil.drawTable(page, cos, y.floatValue(), MARGIN_LEFT.floatValue(), masContent, masColorContent)
		y -= GAP_MICRO
		
		return y
	}
	
	static int drawMemberSummary(PDPage page, PDPageContentStream cos, int y, IndividualSummary member, SummaryReport report) {
		// MEDICAL
		
		PdfUtil.drawText("SUMMARY",
				WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		
		int column1 = MARGIN_LEFT + 10
		int column2 = MARGIN_LEFT + 100 + 40
		int column3 = MARGIN_LEFT + 225 + 10
		int column4 = MARGIN_LEFT + 325 + 40
		
		y -= GAP
		
		boolean expiredPrd = !member.getPrd().after(new Date())
		
		PdfUtil.drawText(
			"Current PRD:",
			WEIGHT_BOLD, FONT_SMALL, page, cos, column1, y,
			expiredPrd ? Color.red : Color.black)
		
		PdfUtil.drawText(
			FormatUtil.dateToFormatTwoString(member.getPrd()),
			WEIGHT_NORMAL, FONT_SMALL, page, cos, column2, y,
			expiredPrd ? Color.red : Color.black)
		
		boolean goodMedical = member.getImrStatus().equalsIgnoreCase("fully medically ready")
		
		PdfUtil.drawText(
			"Medical Status:",
			WEIGHT_BOLD, FONT_SMALL, page, cos, column3, y,
			goodMedical ? Color.black : Color.red)
		
		PdfUtil.drawText(
			member.getImrStatus(),
			WEIGHT_NORMAL, FONT_SMALL, page, cos, column4, y,
			 goodMedical ? Color.black : Color.red)
		

		y -= GAP
		
		return y
	}
	
	static int drawOrders(PDPage page, PDPageContentStream cos, int y, IndividualSummary member, SummaryReport report) {
		
		if (report.nrowsFile == null) {
			return y
		}
		
		PdfUtil.drawText("ORDERS",
			WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		
		// only draw the box if the member has at least one order
		if (member.orders.size() > 0) {
		
			String[][] content = new String[1 + member.orders.size()][6]
			content[0][0] = "Tracking #"
			content[0][1] = "Start Date"
			content[0][2] = "End Date"
			content[0][3] = "Days"
			content[0][4] = "Type"
			content[0][5] = "Status"
			
			int contentIndex = 1
			for (NrowsOrder order : member.orders) {
				content[contentIndex][0] = order.trackingNumber
				content[contentIndex][1] = FormatUtil.dateToFormatTwoString(order.startDate)
				content[contentIndex][2] = FormatUtil.dateToFormatTwoString(order.endDate)
				content[contentIndex][3] = order.days + ""
				content[contentIndex][4] = order.dutyType
				content[contentIndex][5] = order.status
				contentIndex++
			}
			
			y -= GAP_MICRO
			y = PdfUtil.drawTable(page, cos, y.floatValue(), MARGIN_LEFT.floatValue(), content)
		
		} else {
			y -= GAP_MICRO
		}
		
		// warn if the member doesn't have enough days of orders
		if (!member.hasEnoughDaysOfOrdersForCurrentFY) {
			String message = "You only have " + member.daysOfOrdersInCurrentFY + " days of orders in the current fiscal year"
					
			y -= GAP_MICRO
			PdfUtil.drawText(
				message,
				WEIGHT_BOLD, FONT_SMALL, page, cos, MARGIN_LEFT, y,
				Color.red)
			
			y -= GAP_MICRO
		} else {
			y -= GAP_MICRO
		}
		
		return y
	}
	
	static int drawManualInputs(PDPage page, PDPageContentStream cos, int y, IndividualSummary member, SummaryReport report) {
		// MANUAL INPUTS
		y -= GAP_SMALL
		PdfUtil.drawText("MANUAL INPUTS (" + member.getRank() + " " + member.getFirstName() + " " + member.getLastName() + ")",
				WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_SMALL
		PdfUtil.drawText(
			"The following data is manually maintained by the unit. If any of this info changes or is missing notify your chain immediately.",
			WEIGHT_NORMAL, FONT_SMALL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_SMALL
		PdfUtil.drawText(
			"Items highlighted in red negatively impact your readiness status. The causes can be missing data, incompletions, or other problems.",
			WEIGHT_NORMAL, FONT_SMALL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_MICRO
		if (member.existsInManualInputs) {
			
			int column1 = MARGIN_LEFT + 10
			int column2 = MARGIN_LEFT + 100 + 10
			int column3 = MARGIN_LEFT + 225 + 10
			int column4 = MARGIN_LEFT + 325 + 10
			
			String lastPrimary = null
			boolean leftOrRight = true
			
			Color manualColor = Color.BLACK
			
			for (int i = 0; i < member.values.length; i++) {
				String primary = report.primaryColumnHeaders.get(i)
				String secondary = report.secondaryColumnHeaders.get(i)
				String value = member.values[i]
				boolean valid = member.valueValid[i]
				
				// if this is in the same group
				if (primary.equals(lastPrimary)) {
					// do nothing with the primary label
					
					// increment the column
					leftOrRight = !leftOrRight
					
					if (leftOrRight) {
						y -= GAP_SMALL
					}
					
				} else {
					
					y -= GAP_SMALL
				
					//output the label
					PdfUtil.drawText(primary,
						WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y, manualColor)
				
					y -= GAP_SMALL
					
					// set the column back to left
					leftOrRight = true
				}
				
				int firstPosition = column1
				int secondPosition = column2
				
				if (!leftOrRight) {
					firstPosition = column3
					secondPosition = column4
				}
				
				// output the name value pair
				
				if (!valid) {
					manualColor = Color.RED
				}
				PdfUtil.drawText(
					secondary,
					WEIGHT_BOLD, FONT_SMALL, page, cos, firstPosition, y, manualColor)
				
				PdfUtil.drawText(
					FormatUtil.trimWithDotDotDot(value, 35),
					WEIGHT_NORMAL, FONT_SMALL, page, cos, secondPosition, y, manualColor)
				
				manualColor = Color.BLACK
				
				lastPrimary = primary
				
				// only move to the next row if we were on the right
				if (!leftOrRight) {
					//y -= 10
				}
			}
			
			
			
			
		} else {
			PdfUtil.drawText(
				"Member does not exist in manual inputs spreadsheet",
				WEIGHT_NORMAL, FONT_SMALL, page, cos, MARGIN_LEFT, y - 5)
		}
		
		return y
	}
	
}
