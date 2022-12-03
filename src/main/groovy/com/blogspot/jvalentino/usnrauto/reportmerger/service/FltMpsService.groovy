package com.blogspot.jvalentino.usnrauto.reportmerger.service

import java.io.File;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.CommonFltMpsProperties;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsELearningReport;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMember;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps.FltMpsGMTMemberReport;

import org.dozer.DozerBeanMapper;

import static com.blogspot.jvalentino.usnrauto.util.FormatUtil.*;

class FltMpsService {

    private DozerBeanMapper dozerBeanMapper = new DozerBeanMapper()
    
    /**
     * Parses the GMT spreadsheet that comes from FLTMPS. The only difference from
     * the other FLTMPS spreadsheets is that course names contain categories.
     * 
     * @param input
     * @return
     * @throws Exception
     */
    FltMpsGMTMemberReport parseGMT(File input) throws Exception {
        
        FltMpsGMTMemberReport report = new FltMpsGMTMemberReport()
		report.file = input
                        
        List<FltMpsGMTMember> list = new ArrayList<FltMpsGMTMember>()
        
        CommonFltMpsProperties props = this.determineCommonAttributesAboutFltMpsReport(
            input, "Group")
        
        // the courses are prefixed with category designations like CATEGORY ONE, pull them off
        List<String> categories = this.getCategoriesAndModifyCourseNames(props)
        
        // get a list of members and their completion status on different courses
        
        List<FltMpsELearningMember> members = this.getELearningMemberData(
            props.sheet, props.groupRowNumber, props.headerRowSpan, props.courses, props.columnThatStartsMemberData)
        
        // we are going to map the base class to the parent class using dozer property mapping
        for (FltMpsELearningMember member : members) {
            FltMpsGMTMember convert = dozerBeanMapper.map(member, FltMpsGMTMember.class)
            convert.setCourseCategories(categories as String[])
            list.add(convert)
        }
        
        report.setMembers(list)
        report.setCourseNames(props.getCourses()  as String[])
        report.setCourseCategories(categories as String[])
        
        return report
    }
    
    /**
     * The course names for GMTs on FLTMPS are prefixed with categories that we have to extract
     * such as CATEGORY ONE
     * @param props
     * @return
     */
    private List<String> getCategoriesAndModifyCourseNames(CommonFltMpsProperties props) {
        // the courses are prefixed with category designations like CATEGORY ONE, pull them off
        List<String> courses = new ArrayList<String>()
        List<String> categories = new ArrayList<String>()
        
        for (String course : props.courses) {
            String[] split = course.split("[\\s\u00A0]+")
			
			if (split.length == 1) {
				continue
			}
			
            String category= split[0] + " " + split[1]
            String newCourse = ""
            
            for (int i = 2; i < split.length; i++) {
                newCourse += split[i] + " "
            }
            
            newCourse = newCourse.trim()
            
            courses.add(newCourse)
            categories.add(category)
            
        }
        props.courses = courses
        
        return categories
    }
    
    /**
     * Parses an e-Learning spreadsheet from FLTMPS for member completion data
     * 
     * @param input
     * @return
     * @throws Exception
     */
    FltMpsELearningReport parseELearning(File input) throws Exception {
        
        FltMpsELearningReport report = new FltMpsELearningReport()
		report.file = input
                
        CommonFltMpsProperties props = this.determineCommonAttributesAboutFltMpsReport(
            input, "Group")
        
        // get a list of members and their completion status on different courses
        
        List<FltMpsELearningMember> members = this.getELearningMemberData(
            props.sheet, props.groupRowNumber, props.headerRowSpan, props.courses,
            props.columnThatStartsMemberData)
        
        props.file.close()
        
        report.setMembers(members)
        report.setCourseNames(props.getCourses()  as String[])
        
        return report
    }
    
    FltMpsELearningReport parseIA(File input) throws Exception {
        
        FltMpsELearningReport report = new FltMpsELearningReport();
		report.file = input
                
        CommonFltMpsProperties props = this.determineCommonAttributesAboutFltMpsReport(
            input, "Desig", -1)
        
        // get a list of members and their completion status on different courses
        
        List<FltMpsELearningMember> members = this.getELearningMemberData(
            props.sheet, props.groupRowNumber, props.headerRowSpan, props.courses,
            props.columnThatStartsMemberData)
        
        props.file.close()
        
        report.setMembers(members)
        report.setCourseNames(props.getCourses() as String[])
        
        return report
    }
    
    /**
     * All of the spreadsheets out of FLTMPS are very similar in style and structure. For this reason
     * the logic for getting course names and figuring out where the member data is located has been abstracted.
     * 
     * @param input
     * @return
     * @throws Exception
     */
    private CommonFltMpsProperties determineCommonAttributesAboutFltMpsReport(File input,
         String headerRowText, int memberDataColumnOffset=0) throws Exception  {
        CommonFltMpsProperties props = new CommonFltMpsProperties()
        
        
        if (!input.getName().toLowerCase().endsWith(".xlsx"))
            throw new Exception("The file extension must end with XLSX and be in Word 2007 or later format.")
    
        FileInputStream file = new FileInputStream(input)
        props.file = file
    
        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(file)
    
        //Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0)
        props.sheet = sheet
        
        // find the row that contains the group header data
        Integer[] groupCell = this.findCellThatStartsGroupHeader(sheet, headerRowText)
        
        if (groupCell == null) {
            String message = "Unable to locate the row with the first column value of \""+headerRowText+"\"."
            message += " We need this information to identify the names of courses."
            throw new Exception(message)
        }
        
        int groupRowNumber = groupCell[0]
        props.groupRowNumber = groupRowNumber
        props.columnThatStartsMemberData = groupCell[1] + 1 + memberDataColumnOffset
        
        props.columnInHeaderThatSpans = groupCell[1] + 2
                
        // find how many rows the "Group" row spans
        int headerRowSpan = findNumberOfRowsThatGroupHeaderSpans(sheet, groupRowNumber, props.columnInHeaderThatSpans)
        
        // in the event we can't find it, try again one more column to the right (IA report)
        if (headerRowSpan == -1) {
            headerRowSpan = findNumberOfRowsThatGroupHeaderSpans(sheet, groupRowNumber, props.columnInHeaderThatSpans + 1)
        }
        
        if (headerRowSpan == -1) {
            String message = "Unable to determine how many rows that \""+headerRowText+"\" row actually spans."
            message += " We need this information in order to determine the different course names that span rows."
            throw new Exception(message)
        }
        
        props.headerRowSpan = headerRowSpan
        
        // Determine the names of courses
        List<String> courses = this.findCourseNames(sheet, groupRowNumber, headerRowSpan, props.columnThatStartsMemberData)
        props.courses = courses
        
        if (courses.size() == 0) {
            String message = "Unable to determine the names of any courses starting on row number"
            message += groupRowNumber + ", which spans a total of " + headerRowSpan + " rows."
            throw new Exception(message)
        }
        
        return props
    }
    
    /**
     * Looks through the entire sheet to find the row number
     * of a row where the first column contains the word "Group". This indicates
     * that this is the beginning of where the data is located.
     * 
     * @param sheet
     * @return
     */
    private Integer[] findCellThatStartsGroupHeader(XSSFSheet sheet, String headerTextInFirstColumn) {
        Integer[] index = null
        
        Iterator<Row> rows=sheet.rowIterator()
        while (rows.hasNext()) {
            Row row = (Row) rows.next()
            
            Iterator cells = row.cellIterator();
            
            while (cells.hasNext()) {
                Cell cell = (Cell) cells.next();
                                
                if (cellToString(cell).equals(headerTextInFirstColumn)) {
                    return [row.rowNum, cell.columnIndex] as Integer[]
                } else {
                    break // stop looking at cells, go to the next row
                }
                
            }
            
        }
        
        return index
    }
    
    /**
     * Due to how the spreadsheet was setup, the "Group" header row can span multiple rows.
     * For this reason we need to know how may rows the "Group" header row spans. In
     * Excel land this has to be done by looking at all of the merged regions within the document
     * and finding a region that corresponds to the "Group" header row's first column.
     *  
     * @param sheet
     * @param groupRowNumber
     * @return
     */
    private int findNumberOfRowsThatGroupHeaderSpans(XSSFSheet sheet, int groupRowNumber, int column) {
        int regions = sheet.getNumMergedRegions()
        int span = -1
        for (int i = 0; i < regions; i++) {
            CellRangeAddress address = sheet.getMergedRegion(i)
            if (address.getFirstRow() == groupRowNumber && address.getFirstColumn() == column) {
                return address.getNumberOfCells()
            }
        }
        
        return span
    }
    
    /**
     * Using the knowledge of the row that contains the course names, and the number of rows
     * that the row itself can span, determines the full names of all of those courses.
     * 
     * @param sheet
     * @param groupRowNumber
     * @param rowSpan
     * @return
     */
    private List<String> findCourseNames(XSSFSheet sheet, int groupRowNumber, 
        int rowSpan, int columnThatStartsMemberData) {
        
        List<String> courses = new ArrayList<String>()
        
        Row row = sheet.getRow(groupRowNumber)
        
        Iterator cells = row.cellIterator();
        
        // for each cell on the group header row...
        while (cells.hasNext()) {
            Cell cell = (Cell) cells.next();
            
            // ignore the Group, Rank/Rate, and Name columns
            if (cell.columnIndex < 2 + columnThatStartsMemberData ) {
                continue
            }
            
            String courseName = ""
            
            // look at all the cells that this row spans for the full course name...
            for (int i = 0; i < rowSpan; i++) {
                Row currentRow = sheet.getRow(groupRowNumber + i)
                Cell currentCell = currentRow.getCell(cell.columnIndex)
                String courseText = cellToString(currentCell)
                
                if (courseText != null && courseText.length() != 0) {
                   courseName += courseText + " "
                   
                }
            }
            
            courses.add(courseName.trim())
        }
        
        return courses
    }
    
    /**
     * Looks after the rows containing course name information for member completion
     * data for e-Learning
     * 
     * @param sheet
     * @param groupRowNumber
     * @param groupRowSpan
     * @param courseNames
     * @return
     */
    private List<FltMpsELearningMember> getELearningMemberData(XSSFSheet sheet, int groupRowNumber, 
        int groupRowSpan, List<String> courseNames, int columnThatStartsMemberData) {
        
        List<FltMpsELearningMember> list = new ArrayList<FltMpsELearningMember>()
        
        Iterator<Row> rows=sheet.rowIterator()
        while (rows.hasNext()) {
            Row row = (Row) rows.next()
            
            /*
            // the member data starts at the row after the group headers, then one after
            if (row.getRowNum() < groupRowNumber + groupRowSpan + 1) {
                continue
            }
            
            // ignore the title rows that say "RESERVE ENL" or "RESERVE OFF" when
            // the column uses these things as a header
            if (row.getCell(0).toString().contains("RESERVE ENL") 
                || row.getCell(0).toString().contains("RESERVE OFF") ) {
                continue
            }*/
                                       
            FltMpsELearningMember member = this.processRowForMember(row, courseNames, columnThatStartsMemberData)
            
            if (member != null) {
                list.add(member)
            }
                        
        }
        
        return list
    }
        
    /**
     * Turns a row into an e-Learning member record, marking course completion states.
     * @param row
     * @param courseNames
     * @return
     */
    private FltMpsELearningMember processRowForMember(Row row, List<String> courseNames, int columnThatStartsMemberData) {
        
        // if there is no rank, then there is no more data
        if (row.getCell(columnThatStartsMemberData).toString().trim().length() == 0) {
            return null
        }
                              
        Iterator cells = row.cellIterator()
        
        FltMpsELearningMember member = new FltMpsELearningMember()
        
        // initialize all course statuses as false
        member.courseCompletions = new boolean[courseNames.size()]
        member.courseNames = new String[courseNames.size()]
        for (int i = 0; i < courseNames.size(); i++) {
            member.courseCompletions[i] = false
            member.courseNames[i] = courseNames.get(i)
        }
        
        while (cells.hasNext()) {
            Cell cell = (Cell) cells.next()
            String cellText = cellToString(cell)
            
            println cellText
            
            switch (cell.getColumnIndex()) {
                case columnThatStartsMemberData: // rank/rate
                    member.setRank(cellText.toUpperCase())
                break
                case columnThatStartsMemberData + 1: // name
                    try {
                        String[] firstLast = this.parseFltMpsName(cellText.toUpperCase())
                        member.setFirstName(firstLast[0])
                        member.setLastName(firstLast[1])
                    } catch (e) {
                        return null
                    }
                    
                    break
                default: // everything else is a training status column 
                    
                    // if is possible for this column to be before meber data starts,
                    // so if the column that contains member data has not been reached 
                    // ignore it
                    if (cell.getColumnIndex() >= columnThatStartsMemberData) {
                        if (cellText.equalsIgnoreCase("C")) {
                            // position is current - rank column - name column - where ever data starts
                            member.courseCompletions[cell.getColumnIndex() - 2 - columnThatStartsMemberData] = true
                        }
                    }
                break
            }
            
        }
        
        if (member.firstName == null || member.lastName == null) {
            return null
        }
        
        return member
    }
    
    /**
     * The name is in an unreliable format like LAST FIRST MIDDLE SUFFIX,
     * where MIDDLE and SUFFIX are optional. This makes it impossible to reliably determine
     * anything beyond first and last name. Returns an array in the format of
     * [0] = first name
     * [1] = last name
     * 
     * @param cellText
     * @return
     */
    private String[] parseFltMpsName(String cellText) {
        
        String[] firstLast = new String[2]
        
        String[] split = cellText.split("\\s+")
        
        firstLast[0] =  split[1]
        firstLast[1] =  split[0]
        
        return firstLast
    }
}
