package com.dxd.gmall.service;

import com.dxd.gmall.bean.PmsBaseAttrInfo;
import com.dxd.gmall.bean.PmsBaseAttrValue;
import com.dxd.gmall.bean.PmsBaseSaleAttr;

import java.util.List;

public interface PmsBaseAttrService {
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();

}
