package com.blogspot.jvalentino.usnrauto.reportmerger.service

import com.blogspot.jvalentino.usnrauto.reportmerger.data.mas.MasCode;
import com.blogspot.jvalentino.usnrauto.reportmerger.data.mas.MasCodes;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

class MasCodeService {

    private static Map<String, MasCode> masCodeMap;
    
    static {
        masCodeMap = loadMasCodes()
    }
    
    private static Map<String, MasCode> loadMasCodes() throws Exception {
        XStream xstream = new XStream(new DomDriver())
        xstream.processAnnotations(MasCodes.class)
        
        InputStream is = MasCodeService.class.getClassLoader().getResourceAsStream("mas.xml")
        
        MasCodes codes = new MasCodes()
        xstream.fromXML(is, codes)
        is.close()
        
        Map<String, MasCode> map = new HashMap<String, MasCode>()
        
        for (MasCode code : codes.code) {
            map.put(code.name, code)
        }
        
        return map
        
    }
	
	List<MasCode> getMasCodes() {
		List<MasCode> list = new ArrayList<MasCode>()
		for (MasCode value : masCodeMap.values()) {
			list.add(value)
		}
		return list
	}
    
    MasCode lookup(String text) {
        MasCode code = masCodeMap.get(text)
        
        if (code != null) {
            return code
        }
        
        code = new MasCode()
        code.setImpact(false)
        code.setName(text)
        code.setText("Unknown: We don't know what this MAS code means")
        
        return code
    }
}
