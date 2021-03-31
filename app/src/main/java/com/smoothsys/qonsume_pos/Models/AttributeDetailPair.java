package com.smoothsys.qonsume_pos.Models;

import com.smoothsys.qonsume_pos.Models.ItemClasses.Attribute;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Detail;

import java.util.List;

public class AttributeDetailPair {

    public Attribute attribute;
    public List<Detail> detailList;

    public AttributeDetailPair(Attribute attribute, List<Detail> detail) {
        this.attribute = attribute;
        this.detailList = detail;
    }
}
