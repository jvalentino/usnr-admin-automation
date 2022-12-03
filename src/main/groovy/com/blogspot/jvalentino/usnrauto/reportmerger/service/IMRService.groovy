package com.blogspot.jvalentino.usnrauto.reportmerger.service

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrRecord;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.imr.ImrReport;

class IMRService {

    ImrReport parse(File file) {
        ImrReport report = new ImrReport()
		report.file = file
        
        List<ImrRecord> list = new ArrayList<ImrRecord>()
        
        InputStream inputStream = new FileInputStream(file)
        Reader reader = new InputStreamReader(new BOMInputStream(inputStream), "UTF-8")
        CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader())
        try {
            for (CSVRecord record : parser) {
                
                String firstName = record.get("FirstName").toUpperCase()
                String lastName = record.get("LastName").toUpperCase()
                String status = record.get("Imr Status")
                
                ImrRecord item = new ImrRecord()
                item.setFirstName(firstName)
                item.setLastName(lastName)
                item.setStatus(status)
                
                list.add(item)
            }
        } finally {
            parser.close()
            reader.close()
        }
        
        report.setMembers(list)
        
        return report
    }
    
}
