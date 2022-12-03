package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.awt.Color;
import java.awt.Dimension;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.dozer.DozerBeanMapper;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.blogspot.jvalentino.usnrauto.main.AppState;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.MergedReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.individual.IndividualSummary;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.mas.MasCode;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.summary.SummaryReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.individual.CommandWarningPage;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.individual.CoverPage;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.individual.MemberPage;
import com.blogspot.jvalentino.usnrauto.reportmerger.service.individual.SummaryPage;
import com.blogspot.jvalentino.usnrauto.util.PdfUtil;

import static com.blogspot.jvalentino.usnrauto.util.FormatUtil.*;
import static com.blogspot.jvalentino.usnrauto.util.PdfUtil.*;

/**
 * <p>Service uses for taking the merged report of all data, and turning it into 
 * action items for individual members. That data is exported as a PDF, that uses
 * one page per member.</p>
 * 
 * @author jvalentino2
 *
 */
class IndividualReportService {	
	
	private AppState appState = AppState.getInstance()
    
    private DozerBeanMapper dozerBeanMapper = new DozerBeanMapper()
    
    /**
     * Takes a merged report and uses it to generate a list of actions for individual members
     * 
     * @param service
     * @param report
     * @return
     */
    List<IndividualSummary> generateIndividualActionItems(MasCodeService service, SummaryReport report) {
        List<IndividualSummary> list = new ArrayList<IndividualSummary>()
        
        for (MergedMember member : report.getMembers()) {
            
            IndividualSummary item = dozerBeanMapper.map(member, IndividualSummary.class)
            
            item.seteLearningCoursesToDo(PdfUtil.getCoursesToBeDone(
                report.geteLearningCourseNames(), member.geteLearningCourseCompletions()))
            
            item.setIaCoursesToDo(PdfUtil.getCoursesToBeDone(
                report.getIaCourseNames(), member.getIaCourseCompletions()))
            
            item.setGmtCoursesToDo(PdfUtil.getCoursesToBeDone(
                report.getGmtCourseNames(), member.getGmtCourseCompletions()))
            
            if (item.getMasCodeA() != null) {
                MasCode code = service.lookup(item.getMasCodeA())
                item.getMasCodes().add(code)
            }
            
            if (item.getMasCodeM() != null) {
                MasCode code = service.lookup(item.getMasCodeM())
                item.getMasCodes().add(code)
            }
            
            if (item.getMasCodeT() != null) {
                MasCode code = service.lookup(item.getMasCodeT())
                item.getMasCodes().add(code)
            }
            
            // separate out GMT category one and two
            for (String gmt : item.getGmtCoursesToDo()) {
                int index = this.getIndexOfGmtCourse(gmt, report.getGmtCourseNames())
                String category = report.getGmtCourseCategories()[index]
                
                if (category.equals("CATEGORY ONE")) {
                     item.getCategoryOneGMTsToDo().add(gmt)   
                } else {
                    item.getCategoryTwoGMTsToDo().add(gmt)
                }
            }
                  
            list.add(item)
            
        }
        
        return list
    }
    
    /**
     * Returns the position using a 0 based index of the item in the given list
     * 
     * @param name
     * @param list
     * @return
     */
    private int getIndexOfGmtCourse(String name, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (name.equals(list[i])) {
                return i
            }
        }
        return -1
    }
    
    void generateMemberActionPdf(File file,List<IndividualSummary> list, 
        SummaryReport summary, String idcRseReportText=null) throws Exception {
        
        PDDocument document = new PDDocument();

        // generate a cover page
        document.addPage(CoverPage.draw(document, summary))
		
		// generate a summary page for senior leadership
        document.addPage(SummaryPage.draw(document, summary))
		
		// output various command level warnings
		document.addPage(CommandWarningPage.draw(document, summary))
		
		// generate the idc rse report inputs
		if (idcRseReportText != null) {
			document.addPage(PdfUtil.writeTextToPage(document, "UNCLASSIFIED", "IDC RSE Report Inputs", idcRseReportText))
		}
		
        for (IndividualSummary member : list) {
            PDPage page = MemberPage.draw(document, member, summary)
            document.addPage( page )
        }

        // Save the newly created document
        document.save(file);

        // finally make sure that the document is properly
        // closed.
        document.close();

    }	
	
    
}
