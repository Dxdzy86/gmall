package com.dxd.gmall.service;

import com.dxd.gmall.bean.PmsProductImage;
import com.dxd.gmall.bean.PmsProductInfo;
import com.dxd.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface PmsProductService {
    List<PmsProductInfo> getPmsProductInfoList(String catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrList(String productId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String id);
}
