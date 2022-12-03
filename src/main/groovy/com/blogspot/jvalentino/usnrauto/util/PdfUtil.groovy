package com.blogspot.jvalentino.usnrauto.util

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

class PdfUtil {

	static final int MARGIN_LEFT = 70
	static final int MARGIN_TOP = 35
	
	static final PDFont WEIGHT_BOLD = PDType1Font.HELVETICA_BOLD
	static final PDFont WEIGHT_NORMAL = PDType1Font.HELVETICA
	static final PDFont WEIGHT_NORMAL_OBLIQUE = PDType1Font.HELVETICA_OBLIQUE
	
	static final int FONT_LARGE = 11
	static final int FONT_NORMAL = 9
	static final int FONT_SMALL = 7
	static final int FONT_MICRO = 6
	
	static final int GAP = 12
	static final int GAP_MICRO = 4
	static final int GAP_SMALL = 8
	static final int GAP_LARGE = 16
	
	static final int FOOTER_POSITION = 30
	
	static String PII_WARNINGS = "FOR OFFICIAL USE ONLY - PRIVACY SENSITIVE"
	
	
	/**
	 * Utility for turning a list into an array of arrays to represent table
	 * data in two columns
	 *
	 * @param coursesToBeDone
	 * @return
	 */
	static String[][] generateTrainingTableData(List<String> coursesToBeDone, int columns=3) {
		
		int items = coursesToBeDone.size()
		int rows = Math.ceil(items / columns)
		String[][] content = new String[rows][columns]
		
		int index = 0
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (index < items) {
					content[i][j] = coursesToBeDone.get(index)
				} else {
					content[i][j] = ""
				}
				index++
			}
		}
		
		return content
	}
	
	/**
	 * Utility for drawing a table in a PDF, since you are left to manually
	 * handle this in PDFBox
	 *
	 * @param page
	 * @param contentStream
	 * @param y
	 * @param margin
	 * @param content
	 * @return
	 * @throws IOException
	 */
	static int drawTable(PDPage page, PDPageContentStream contentStream,
			float y, float margin,
			String[][] content, Color[][] colorContent=null, int fontSize=FONT_SMALL) throws IOException {
			
		final int rows = content.length;
		final int cols = content[0].length;
		final float rowHeight = 12f;
		final float tableWidth = page.findMediaBox().getWidth() - margin - margin;
		final float tableHeight = rowHeight * rows;
		final float colWidth = tableWidth/(float)cols;
		final float cellMargin=5.0f;

		//draw the rows
		float nexty = y ;
		for (int i = 0; i <= rows; i++) {
			contentStream.drawLine(margin, nexty, (float) (margin+tableWidth), nexty);
			nexty-= rowHeight;
		}

		//draw the columns
		float nextx = margin;
		for (int i = 0; i <= cols; i++) {
			contentStream.drawLine(nextx, y, nextx, (float) (y-tableHeight) );
			nextx += colWidth;
		}

		//now add the text
		contentStream.setFont( WEIGHT_NORMAL , fontSize );

		float textx = margin+cellMargin;
		float texty = y-9;
		for(int i = 0; i < content.length; i++){
			for(int j = 0 ; j < content[i].length; j++){
				
				if (colorContent != null) {
					contentStream.setNonStrokingColor(colorContent[i][j])
				}
				
				String text = content[i][j];
				contentStream.beginText();
				contentStream.moveTextPositionByAmount(textx,texty);
				contentStream.drawString(text);
				contentStream.endText();
				textx += colWidth;
				
				if (colorContent != null) {
					contentStream.setNonStrokingColor(Color.BLACK)
				}
			}
			texty-=rowHeight;
			textx = margin+cellMargin;
		}
		
		return texty
	}
			
	
	/**
	 * Utility for drawing text at the given y position, but centered in the document
	 *
	 * @param text
	 * @param font
	 * @param fontSize
	 * @param page
	 * @param cos
	 * @param y
	 */
	static void drawCenteredText(String text, PDFont font, int fontSize, PDPage page,
			PDPageContentStream cos, int y) {

		int x = this.findXPositionForCenteringText(text, fontSize, font, page)
		this.drawText(text, font, fontSize, page, cos, x, y)
	}
	
	/**
	 * Utility for drawing text in a PDF, since it requires 6 lines of code
	 *
	 * @param text
	 * @param font
	 * @param fontSize
	 * @param page
	 * @param contentStream
	 * @param x
	 * @param y
	 */
	static void drawText(String text, PDFont font, int fontSize, PDPage page, PDPageContentStream contentStream,
			int x, int y, Color color=Color.BLACK) {

		contentStream.setNonStrokingColor(color)
		contentStream.beginText()
		contentStream.setFont( font, fontSize )
		contentStream.moveTextPositionByAmount( x, y )
		contentStream.drawString( text )
		contentStream.endText()
		contentStream.setNonStrokingColor(Color.BLACK)
	}

	/**
	 * Utility for drawing a line in a PDF that spans the space in between left and right margins
	 * @param page
	 * @param cos
	 * @param y
	 */
	static void drawLine(PDPage page, PDPageContentStream cos, y) {
		cos.setNonStrokingColor(Color.BLACK);
		cos.setLineWidth(1);
		cos.addLine(MARGIN_LEFT, y, getRightMostPositionOnPage(page), y);
		cos.closeAndStroke();
	}
	
	/**
	 * Utility for finding the x position in order to center text based on font size and document size
	 *
	 * @param text
	 * @param fontSize
	 * @param font
	 * @param page
	 * @return
	 */
	static int findXPositionForCenteringText(String text, int fontSize, PDFont font, PDPage page) {
		float titleWidth = font.getStringWidth(text) / 1000 * fontSize
		int result = (int) (page.getMediaBox().getWidth() - titleWidth) / 2
		return result
	}
	
	/**
	 * Returns the position at the top of the page relative to the top margin
	 *
	 * @param page
	 * @return
	 */
	static int getTopOfPagePosition(PDPage page) {
		return (int) (page.getMediaBox().getHeight() -  MARGIN_TOP)
	}
	
	
	/**
	 * Returns the right most position to be used for placing text, according to the left margin
	 *
	 * @param page
	 * @return
	 */
	static int getRightMostPositionOnPage(PDPage page) {
		return (int) (page.getMediaBox().getWidth() -  MARGIN_LEFT)
	}
	
	static PDPage writeTextToPage(PDDocument document, String header, String title, String text) {
		PDPage page = new PDPage()
		
		PDPageContentStream cos = new PDPageContentStream(document, page);

		int y = PdfUtil.getTopOfPagePosition(page)
		
		PdfUtil.drawCenteredText(header,
			WEIGHT_NORMAL, FONT_NORMAL, page, cos, y)
	
		y -= GAP
		
		PdfUtil.drawCenteredText(title,
			WEIGHT_BOLD, FONT_LARGE, page, cos, y)
		
		y -= GAP
		
		String[] lines = text.split("\n")
		
		for (String line : lines) {
			
			line = line.replaceAll("\t", "        ")
			
			PdfUtil.drawText(
				line,
				WEIGHT_NORMAL, FONT_MICRO, page, cos, MARGIN_LEFT, y)
			
			y -= 7
		}
		
		cos.close()
		
		return page
		
	}
	
	static Color getStopLightColor(Double number) {
		if (number >= 0.85) {
			return Color.BLACK
		} else if (number >= 0.70) {
			return new Color(255, 140, 0)
		} else {
			return Color.RED
		}
	}
	
	/**
	 * Utility for generating a list of coures that need to be done based on the
	 * give names and completion states
	 *
	 * @param names
	 * @param states
	 * @return
	 */
	static List<String> getCoursesToBeDone(String[] names, boolean[] states) {
		List<String> coursesToBeDone = new ArrayList<String>()
		
		if (states == null)
			return []
		
		for (int i = 0; i < names.length; i++) {
			String name = names[i]
			boolean state = states[i]
			
			if (!state) {
				coursesToBeDone.add(name)
			}
		}
		
		return coursesToBeDone
		
	}
}
