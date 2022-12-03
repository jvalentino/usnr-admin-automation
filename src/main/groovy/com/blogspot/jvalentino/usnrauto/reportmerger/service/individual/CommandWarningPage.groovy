package com.blogspot.jvalentino.usnrauto.reportmerger.service.individual

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.util.PdfUtil;
import static com.blogspot.jvalentino.usnrauto.util.FormatUtil.*;
import static com.blogspot.jvalentino.usnrauto.util.PdfUtil.*;

class CommandWarningPage {

	static PDPage draw(PDDocument document, SummaryReport summary) {
		PDPage page = new PDPage()
	
		PDPageContentStream cos = new PDPageContentStream(document, page);

		int y = PdfUtil.getTopOfPagePosition(page)
		
		PdfUtil.drawCenteredText(PII_WARNINGS,
				WEIGHT_NORMAL, FONT_NORMAL, page, cos, y)
		y -= GAP
		
		PdfUtil.drawText("COMMAND WARNINGS",
			WEIGHT_BOLD, FONT_LARGE, page, cos, MARGIN_LEFT, y)
		
		y -= GAP
		PdfUtil.drawText("This section contains member issues that need to be handled.",
			WEIGHT_NORMAL, FONT_NORMAL, page, cos, MARGIN_LEFT, y)
		
		y -= GAP_MICRO
		
		String[][] warningContent = PdfUtil.generateTrainingTableData(summary.commandWarnings, 2)
		y = PdfUtil.drawTable(page, cos, y.floatValue(), MARGIN_LEFT.floatValue(), warningContent)
		
		PdfUtil.drawCenteredText(summary.pdfFooter,
			WEIGHT_NORMAL, FONT_SMALL, page, cos, FOOTER_POSITION)
		
		cos.close()
		
		return page
	}
}
