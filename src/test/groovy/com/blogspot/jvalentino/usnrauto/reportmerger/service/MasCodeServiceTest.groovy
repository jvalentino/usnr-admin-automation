package com.blogspot.jvalentino.usnrauto.reportmerger.service

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.jvalentino.usnrauto.reportmerger.data.mas.MasCode;

class MasCodeServiceTest {

    private ServiceBus bus = ServiceBus.getInstance()
    private MasCodeService service
    
    @Before
    void setup() {
        service = bus.getMasCodeService()
    }
    
    @Test
    public void testLoadMasCodes() throws Exception {
        
        MasCode code = service.lookup("AS1")
        assertThat(code.getImpact(), is(true))
        assertThat(code.getText(), is("Administrative spare 1."))
        
        code = service.lookup("SAD")
        assertThat(code.getName(), is("SAD"))
        
        code = service.lookup("WTF")
        assertThat(code.getName(), is("WTF"))
        assertThat(code.getImpact(), is(false))
        assertThat(code.getText(), is("Unknown: We don't know what this MAS code means"))
    }
}
