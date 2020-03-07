package com.dxd.gmall.manage.mapper;

import com.dxd.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {
    //根据商品skuId和商品productId找到该商品选中的和没有被选中的销售属性和销售属性值
    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(@Param("productId") String productId,@Param("skuId") String skuId);
}
