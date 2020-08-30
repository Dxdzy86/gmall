package com.dxd.gmall.service;

import com.dxd.gmall.bean.PmsSkuInfo;
import com.dxd.gmall.bean.PmsSkuSaleAttrValue;

import java.util.List;

/**
 * @author Dxd
 * @date 2020/04/10
 */
public interface PmsSkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    /**
     * 从redis缓存中根据skuId查询出skuInfo信息
     * @param skuId
     * @return
     */
    PmsSkuInfo getSkuInfo(String skuId);

    /**
     * 根据productId查询出下面所有skuId与其销售属性值id的关联关系
     * @param productId
     * @return
     */
    List<PmsSkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String productId);

    /**
     * 根据skuId从数据库中查询出对应的信息
     * @param skuId
     * @return
     */
    public PmsSkuInfo getSkuInfoFromDb(String skuId);

    /**
     * 返回所有商品的信息
     * @return 返回数据库中所有的sku商品的信息
     */
    List<PmsSkuInfo> getAllSkuInfos();
}
