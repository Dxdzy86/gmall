package com.dxd.gmall.service;

import com.dxd.gmall.bean.PmsSkuInfo;
import com.dxd.gmall.bean.PmsSkuSaleAttrValue;

import java.util.List;

public interface PmsSkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuInfo(String skuId);

    //根据productId查询出下面所有skuId与其销售属性值id的关联关系
    List<PmsSkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String productId);
}
