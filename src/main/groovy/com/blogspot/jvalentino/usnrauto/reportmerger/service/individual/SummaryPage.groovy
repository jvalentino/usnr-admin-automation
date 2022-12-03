package com.blogspot.jvalentino.usnrauto.reportmerger.service.individual

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.blogspot.jvalentino.usnrauto.main.AppState;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.ServiceBus;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;
import com.blogspot.jvalentino.usnrauto.util.PdfUtil;

import static com.blogspot.jvalentino.usnrauto.util.FormatUtil.*;
import static com.blogspot.jvalentino.usnrauto.util.PdfUtil.*;

class SummaryPage {

	static PDPage draw(PDDocument document, SummaryReport summary) {
		PDPage page = new PDPage()
		
		PDFont font = WEIGHT_BOLD
		int column1 = MARGIN_LEFT + 10
		int column2 = MARGIN_LEFT + 100 + 40
		int column3 = MARGIN_LEFT + 225 + 10
		int column4 = MARGIN_LEFT + 325 + 40
		
		int column2_5 = MARGIN_LEFT + 180
		int column4_5 = column4 + 40

		PDPageContentStream cos = new PDPageContentStream(document, page);

		int y = PdfUtil.getTopOfPagePosition(page)
		
		PdfUtil.drawCenteredText("UNCLASSIFIED",
				WEIGHT_NORMAL, FONT_NORMAL, page, cos, y)
		y -= GAP
		
		PdfUtil.drawText("INDIVIDUAL MEMBER READINESS REPORT SUMMARY - "
			+ new Date().format("MMM dd yyyy").toUpperCase(),
			WEIGHT_BOLD, FONT_LARGE, page, cos, MARGIN_LEFT, y)
		y -= GAP
		
		y = drawMembershipSummary(summary, page, cos, y, column1, column2, column3, column4)
		y -= GAP
		
		y = drawOrdersSummary(summary, page, cos, y, column1)
		
		y = drawRankDistribution(summary, page, cos, y, column1, column2, column3, column4)
		y -= GAP_MICRO
		
		int eLearningY = y
		y = drawELearningSummary(summary, page, cos, y, column1, column2_5, column3, column4_5)
		y -= GAP_MICRO
		int eLearningEndingY = y
		
		y  = eLearningY
		int gmtEndingY = drawGmtSummary(summary, page, cos, y, column1, column2_5, column3, column4_5)
		
		drawEsamsSummary(summary, page, cos, gmtEndingY - GAP, column1, column2_5, column3, column4_5)
				
		
		y = eLearningEndingY
		
				
		y = drawManualInputsSummary(summary, page, cos, y, column1, column2_5, column3, column4_5)
		
		// draw an image of the history
		if (summary.manualFile != null) {
			int height = y - FOOTER_POSITION - 5
			JFreeChart chart = ServiceBus.getInstance().getHistoryService().createHistoryChart(
				summary.history, summary.nrowsFile != null, summary.esamsFile != null)
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsJPEG(out, chart, 500, height);//write to outstream
			//create a new inputstream
			InputStream is = new ByteArrayInputStream(out.toByteArray());
			PDXObjectImage ximage = new PDJpeg(document, is);
			
			cos.drawXObject(ximage, MARGIN_LEFT, y-height, 500.0f, height.floatValue());
		
		}
		
		PdfUtil.drawCenteredText(summary.pdfFooter,
			WEIGHT_NORMAL, FONT_SMALL, page, cos, FOOTER_POSITION)
		
		cos.close()
	
		return page
	}
	
	static int drawMembershipSummary(SummaryReport summary, PDPage page, PDPageContentStream cos, int y,
		int column1, int column2, int column3, int column4) {
		
		PdfUtil.drawText("MEMBERSHIP SUMMARY",
			WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		y -= GAP
		
		PdfUtil.drawText(
			"Current PRD:",
			WEIGHT_BOLD, FONT_SMALL, page, cos, column1, y,
			getStopLightColor(summary.getPrdPercentage()))
		
		PdfUtil.drawText(
			fractionToPercentage(summary.getPrdPercentage()),
			WEIGHT_NORMAL, FONT_SMALL, page, cos, column2, y,
			getStopLightColor(summary.getPrdPercentage()))
		
		PdfUtil.drawText(
			"Fully Medically Ready:",
			WEIGHT_BOLD, FONT_SMALL, page, cos, column3, y,
			getStopLightColor(summary.getImrPercentage()))
		
		PdfUtil.drawText(
			fractionToPercentage(summary.getImrPercentage()),
			WEIGHT_NORMAL, FONT_SMALL, page, cos, column4, y,
			getStopLightColor(summary.getImrPercentage()))
		
		if (summary.nrowsFile != null) {
			y -= GAP
			
			PdfUtil.drawText(
				"12 or more days orders in FY:",
				WEIGHT_BOLD, FONT_SMALL, page, cos, column1, y,
				getStopLightColor(summary.getAtPercentage()))
			
			PdfUtil.drawText(
				fractionToPercentage(summary.getAtPercentage()),
				WEIGHT_NORMAL, FONT_SMALL, page, cos, column2, y,
				getStopLightColor(summary.getAtPercentage()))
		}
		
		return y
	}
		
	static int drawOrdersSummary(SummaryReport summary, PDPage page, PDPageContentStream cos, int y, int column1) {
		if (summary.nrowsFile == null) {
			return y
		}
		
		PdfUtil.drawText("NROWS ORDER SUMMARY",
			WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		y -= GAP
		
		int position = column1
		summary.orderGroup.each { key, value ->
			
			PdfUtil.drawText(
				key + ":",
				WEIGHT_BOLD, FONT_SMALL, page, cos, position, y)
			
			PdfUtil.drawText(
				value + " days",
				WEIGHT_NORMAL, FONT_SMALL, page, cos, position + 24, y)
			
			position += 90
		}
		
		y -= GAP
		
		return y
		
	}
	
	static int drawRankDistributions(SummaryReport summary, PDPage page, PDPageContentStream cos, int y, boolean officer) {
		Map<String, Integer> map = summary.rankGroup
		
		int i = 0
		int j = 0
		
		// enlisted
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			
			boolean isOfficer = summary.officerRanks.contains(key)
			
			if ( (isOfficer && officer) || (!isOfficer && !officer) ) {
				
				int x = MARGIN_LEFT + (j * 50)
				
				PdfUtil.drawText(
					key,
					WEIGHT_BOLD, FONT_SMALL, page, cos, x, y)
				PdfUtil.drawText(
					value.toString(),
					WEIGHT_NORMAL, FONT_SMALL, page, cos, x + 30, y)
				
				
				j = (j + 1) % 10
				
				if (j == 0) {
					i++
					y -= GAP_SMALL
				}
			}
			
		}
		
		y -= GAP_SMALL
		
		return y
	}
		
	static int drawRankDistribution(SummaryReport summary, PDPage page, PDPageContentStream cos, int y,
		int column1, int column2, int column3, int column4) {
		
		PdfUtil.drawText("MEMBERSHIP DISTRIBUTION",
			WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_SMALL
				
		y = drawRankDistributions(summary, page, cos, y, false)
		y = drawRankDistributions(summary, page, cos, y, true)		
		
		return y
	}
		
	static int drawELearningSummary(SummaryReport summary, PDPage page, PDPageContentStream cos, int y,
		int column1, int column2, int column3, int column4) {
		
		PdfUtil.drawText("eLearning",
			WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		y -= GAP_SMALL
		
		// Combine category II GMTs with eLearning
		List<String> names = new ArrayList<String>()
		List<Double> values = new ArrayList<Double>()
		
		for (int i = 0; i < summary.geteLearningCourseNames().length; i++) {
			String name = summary.geteLearningCourseNames()[i]
			Double percentage = summary.geteLearningCourseCompletionPercentages()[i]
			names.add(name)
			values.add(percentage)
		}
		
		for (int i = 0; i < summary.gmtCourseCategories.length; i++) {
			String category = summary.gmtCourseCategories[i]
			String name = summary.gmtCourseNames[i]
			Double value = summary.gmtCourseCompletionPercentages[i]
			
			//if (category.equalsIgnoreCase("CATEGORY TWO")) {
				//names.add(name)
				//values.add(value)
			//}
		}
		
		for (int i = 0; i < names.size(); i++) {
			PdfUtil.drawText(
				names.get(i),
				WEIGHT_BOLD, FONT_SMALL, page, cos, column1, y,
				getStopLightColor(values.get(i)))
			PdfUtil.drawText(
				fractionToPercentage(values.get(i)),
				WEIGHT_NORMAL, FONT_SMALL, page, cos, column2, y,
				getStopLightColor(values.get(i)))
			y -= GAP_SMALL
		}
		
		return y
	}
	
	static int drawGmtSummary(SummaryReport summary, PDPage page, PDPageContentStream cos, int y,
		int column1, int column2, int column3, int column4) {
		
		// Handle category I GMT's
		PdfUtil.drawText("GMTs",
			WEIGHT_BOLD, FONT_NORMAL, page, cos, column3, y)
		y -= GAP_SMALL
		
		for (int i = 0; i < summary.gmtCourseCategories.length; i++) {
			String category = summary.gmtCourseCategories[i]
			String name = summary.gmtCourseNames[i]
			Double value = summary.gmtCourseCompletionPercentages[i]
			
			//if (category.equalsIgnoreCase("CATEGORY ONE")) {
				PdfUtil.drawText(
					name,
					WEIGHT_BOLD, FONT_SMALL, page, cos, column3 + 10, y,
					getStopLightColor(value))
				PdfUtil.drawText(
					fractionToPercentage(value),
					WEIGHT_NORMAL, FONT_SMALL, page, cos, column4 + 10, y,
					getStopLightColor(value))
				
				y -= GAP_SMALL
			//}
			
		}
				
		return y
	}
		
	static int drawEsamsSummary(SummaryReport summary, PDPage page, PDPageContentStream cos, int y,
		int column1, int column2, int column3, int column4) {
		
		if (summary.esamsFile == null) {
			return y
		}
		
		PdfUtil.drawText("ESAMS COURSES TO BE COMPLETED",
			WEIGHT_BOLD, FONT_NORMAL, page, cos, column3, y)
		y -= GAP_SMALL
		
		for (int i = 0; i < summary.esamsCourseNames.length; i++) {
			String name = FormatUtil.removeTextInParenthesis(summary.esamsCourseNames[i], 45)
			Double value = summary.esamsCourseCompletionPercentages[i]
			
			PdfUtil.drawText(
				name,
				WEIGHT_BOLD, FONT_SMALL, page, cos, column3 + 10, y,
				getStopLightColor(value))
			PdfUtil.drawText(
				fractionToPercentage(value),
				WEIGHT_NORMAL, FONT_SMALL, page, cos, column4+ 10, y,
				getStopLightColor(value))
			
			y -= GAP_SMALL
			
		}
				
		return y
	}
		
	static int drawManualInputsSummary(SummaryReport summary, PDPage page, PDPageContentStream cos, int y,
		int column1, int column2, int column3, int column4) {
		
		// Handle Manual inputs
		String lastPrimary = ""
		for (int i = 0; i < summary.primaryColumnHeaders.size(); i++) {
			String primary = summary.primaryColumnHeaders.get(i)
			String secondary = summary.secondaryColumnHeaders.get(i)
			Double value = summary.manualPercentages[i]
			
			if (lastPrimary.equals(primary)) {
				// this is just another row
			} else {
				y -= GAP_MICRO
				PdfUtil.drawText(primary,
					WEIGHT_BOLD, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
				y -= GAP_SMALL
			}
			
			PdfUtil.drawText(
				secondary,
				WEIGHT_BOLD, FONT_SMALL, page, cos, column1, y,
				getStopLightColor(value))
			
			PdfUtil.drawText(
				fractionToPercentage(value),
				WEIGHT_NORMAL, FONT_SMALL, page, cos, column2, y,
				getStopLightColor(value))
			
			y -= GAP_SMALL
			
			lastPrimary = primary
		}
		
		return y
	}
}
