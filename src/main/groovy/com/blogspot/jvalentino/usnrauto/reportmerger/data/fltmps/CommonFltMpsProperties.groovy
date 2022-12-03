package com.blogspot.jvalentino.usnrauto.reportmerger.data.fltmps

import org.apache.poi.xssf.usermodel.XSSFSheet;

class CommonFltMpsProperties {

    // row that contains the group header data
    int groupRowNumber
    // how many rows the "Group" row spans
    int headerRowSpan
    // names of courses
    List<String> courses
    
    FileInputStream file
    XSSFSheet sheet
    
    int columnThatStartsMemberData
    int columnInHeaderThatSpans
    
}
