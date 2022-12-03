package com.blogspot.jvalentino.usnrauto.reportmerger.data.mas

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("codes")
class MasCodes {
    @XStreamImplicit
    List<MasCode> code
}
