package com.blogspot.jvalentino.usnrauto.reportmerger.service

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadMemberEntry;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.ruad.RuadReport;
import com.blogspot.jvalentino.usnrauto.util.FormatUtil;

class RuadService {

	
    /**
     * Parses a RUAD spreadsheet, as it comes from NRRMS. The user must convert
     * the file from XLS to XLSX using Excel. This is because what comes out of NRRMS 
     * is either old or contains errors that make it not usable.
     * 
     * @param input
     * @return
     * @throws Exception
     */
    RuadReport parseRuad(File input) throws Exception {
        
        RuadReport report = new RuadReport()
		report.file = input
        
        List<RuadMemberEntry> members = new ArrayList<RuadMemberEntry>()
        
        if (!input.exists())
            throw new Exception("The file " + input.getAbsolutePath() + " does not exist")
            
        if (!input.getName().toLowerCase().endsWith(".xlsx"))
            throw new Exception("The file extension must end with XLSX and be in Word 2007 or later format.")
        
        FileInputStream file = new FileInputStream(input)

        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(file)

        //Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0)
		
		int memberColumn = this.getColumnThatContainsOfficerRank(sheet)
		
		println "Column for members = " + memberColumn
		
		if (memberColumn == -1) {
			throw new Exception("The column that contains member data could not be determined "+
				"because an officer rank could not be found.")
		}
		
        Iterator<Row> rows=sheet.rowIterator()
        while (rows.hasNext()) {
            Row row = (Row) rows.next()
            
            Iterator cells = row.cellIterator();
            
            while (cells.hasNext()) {
                Cell cell = (Cell) cells.next();
                
                RuadMemberEntry member = processLookingForPerson(sheet, row, cell, memberColumn)
                
				String text = cell.toString().trim()
				
                if (member != null) {
                    members.add(member)
					
					if (FormatUtil.isOfficer(member.rank)) {
						report.totalOfficer++	
					} else {
						report.totalEnlisted++
					}
					
                    break
                } else if (text.startsWith("Total IAP")) {
					report.totalIAP += this.totalStatementToNumber(text)
                } else if (text.startsWith("Total CAI")) {
					report.totalCAI += this.totalStatementToNumber(text)
                } else if (text.startsWith("Total CAO")) {
					report.totalCAO += this.totalStatementToNumber(text)
                }
               
            }
                        
        }
        
        file.close()
            
        report.setMembers(members)
		report.sheet = sheet
        
        return report
    }
	
	public int getColumnThatContainsOfficerRank(XSSFSheet sheet) {
		
		int result = -1
		
		int lastRowNumber = sheet.getLastRowNum()
		
		
		Iterator<Row> rows=sheet.rowIterator()
		while (rows.hasNext()) {
			Row row = (Row) rows.next()
			
			Iterator cells = row.cellIterator()
			
			while (cells.hasNext()) {
				Cell cell = (Cell) cells.next()
				
				String text = cell.toString().trim()
								
				// does this column start with a navy officer rank?
				for (String rank : FormatUtil.officerRanks) {
					if (text.startsWith(rank)) {
						return cell.columnIndex
					}
				}
			}
						
		}
		
		return result
	}
	
	/**
	 * Takes a statement like "Total Foo Monday Bar = 2" and returns 2 
	 * @param text
	 * @return
	 */
	protected int totalStatementToNumber(String text) {
		try {
			return Integer.parseInt(text.split("=")[1].trim())
		} catch (NumberFormatException e) {
			return 0
		}
	}
    
    /**
     * Looks at a cell within a row to see if it contains the name of a member.
     * If the cell is in column index 1 and contains a comma it is a member name.
     * For members, nearby cells are inspected for the needed content and a record
     * is returned.
     * 
     * @param sheet
     * @param row
     * @param cell
     * @return
     */
    RuadMemberEntry processLookingForPerson(XSSFSheet sheet, Row row, Cell cell, int memberColumn) {
						
		//println cell.columnIndex + ": " + cell.toString().trim()
		
        
        String text = cell.toString().trim()
		
		// it can't be blank
		if (text.length() == 0)
			return null
		
		// The person name will always be in the same column
		if (cell.columnIndex != memberColumn)
			return null       
        
        // if probably contains a comma
        if (!text.contains(","))
            return null
         
        // remove any commas
        text = text.replaceAll(",", "")
        String[] split = text.split("\\s+")
        
        RuadMemberEntry member = new RuadMemberEntry()
        
        member.setRank(split[0].toUpperCase())
        member.setLastName(split[1].toUpperCase())
        member.setFirstName(split[2].toUpperCase())
        
        if (split.length == 4) {
            member.setMiddleInitial(split[3])
        } else {
            member.setMiddleInitial(null)
        }
                
        // find the PRD
        Date prd = findPrd(sheet, row.rowNum + 2, 31 + memberColumn)
        member.setPrd(prd)
        
        String ims = findCellText(sheet, row.rowNum + 2, 32 + memberColumn)
        member.setIms(ims)
       
        String masCodeA = findCellText(sheet, row.rowNum + 2, 33 + memberColumn)
        member.setMasCodeA(masCodeA)
        
        String masCodeM = findCellText(sheet, row.rowNum + 2, 34 + memberColumn)
        member.setMasCodeM(masCodeM)
        
        String masCodeT = findCellText(sheet, row.rowNum + 2, 35 + memberColumn)
        member.setMasCodeT(masCodeT)
		
		// find the designator
		String designator = findCellText(sheet, row.rowNum + 2, 20 + memberColumn)
		member.setDesignator(designator)
        
        return member
        
    }
    
    /**
     * Handles pulling a PRD style date out of the specified cell
     * 
     * @param sheet
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    Date findPrd(XSSFSheet sheet, int rowIndex, int columnIndex) {
        
        String text = findCellText(sheet, rowIndex, columnIndex)
        
        // date is in 201410 format, so put it in 20141001 format by adding a day on
        String dateString = text + "01"
        
		try {
			// now format the date
			Date date = Date.parse( 'yyyyMMdd', dateString )
			return date
		} catch (Exception e) {
			// TODO: Log this
			e.printStackTrace()
		}
        
       return new Date()
        
    }
    
    /**
     * Generic method for looking for a text value at the row and column
     * of the given sheet. If the cell is empty null is returned.
     * @param sheet
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    String findCellText(XSSFSheet sheet, int rowIndex, int columnIndex) {
        Row row = sheet.getRow(rowIndex)
        Cell cell = row.getCell(columnIndex)
        
        String text = cell.toString().trim()
        
        if (text.length() == 0) {
            return null
        } else {
            return text
        }
    }
    
   
}
