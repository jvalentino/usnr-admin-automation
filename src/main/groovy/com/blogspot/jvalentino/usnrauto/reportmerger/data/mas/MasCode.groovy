package com.blogspot.jvalentino.usnrauto.reportmerger.data.mas

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


@XStreamAlias("code")
class MasCode {

    @XStreamAsAttribute
    String name
    @XStreamAsAttribute
    String text
    @XStreamAsAttribute
    Boolean impact
 
    String toString() {
        return name + ": " + text
    }   
}
