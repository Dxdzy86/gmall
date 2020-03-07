package com.dxd.gmall.manage.mapper;

import com.dxd.gmall.bean.PmsSkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsSkuSaleAttrValueMapper extends Mapper<PmsSkuSaleAttrValue> {
    List<PmsSkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(String productId);
}
