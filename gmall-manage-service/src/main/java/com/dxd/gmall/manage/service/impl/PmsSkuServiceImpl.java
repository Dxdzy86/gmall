package com.dxd.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dxd.gmall.bean.PmsSkuAttrValue;
import com.dxd.gmall.bean.PmsSkuImage;
import com.dxd.gmall.bean.PmsSkuInfo;
import com.dxd.gmall.bean.PmsSkuSaleAttrValue;
import com.dxd.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.dxd.gmall.manage.mapper.PmsSkuImageMapper;
import com.dxd.gmall.manage.mapper.PmsSkuInfoMapper;
import com.dxd.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.dxd.gmall.service.PmsSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

//sku业务层实现类
@Service
public class PmsSkuServiceImpl implements PmsSkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    /**
     * 保存属于某个商品spuId下的sku信息
     * @param pmsSkuInfo
     */
    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        try {
            pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());//该sku商品属于哪一个spu商品下的
            //如果前台没有选择默认图片，在后台进行设置
            if (StringUtils.isEmpty(pmsSkuInfo.getSkuDefaultImg())) {
                pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
            }

            //将sku信息入库
            this.pmsSkuInfoMapper.insertSelective(pmsSkuInfo);

            //将sku与平台属性的关联入库
            for (PmsSkuAttrValue attrValue : pmsSkuInfo.getSkuAttrValueList()) {
                attrValue.setSkuId(pmsSkuInfo.getId());
                this.pmsSkuAttrValueMapper.insertSelective(attrValue);
            }
            //将sku与平台销售属性的关联入库
            for (PmsSkuSaleAttrValue saleAttrValue : pmsSkuInfo.getSkuSaleAttrValueList()) {
                saleAttrValue.setSkuId(pmsSkuInfo.getId());
                this.pmsSkuSaleAttrValueMapper.insertSelective(saleAttrValue);
            }
            //将sku的图片入库
            for (PmsSkuImage image : pmsSkuInfo.getSkuImageList()) {
                image.setSkuId(pmsSkuInfo.getId());
                this.pmsSkuImageMapper.insertSelective(image);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据skuId从数据库中得到商品详情
     *
     * @param skuId
     * @return
     */
    @Override
    public PmsSkuInfo getSkuInfo(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = this.pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        if (skuInfo != null) {
            //查询当前商品的图片集合
            PmsSkuImage skuImage = new PmsSkuImage();
            skuImage.setSkuId(skuId);
            List<PmsSkuImage> skuImages = this.pmsSkuImageMapper.select(skuImage);
            skuInfo.setSkuImageList(skuImages);
            //查询当前商品的销售属性和销售属性值
            PmsSkuSaleAttrValue skuSaleAttrValue = new PmsSkuSaleAttrValue();
            skuSaleAttrValue.setSkuId(skuId);
            List<PmsSkuSaleAttrValue> skuSaleAttrValues = this.pmsSkuSaleAttrValueMapper.select(skuSaleAttrValue);
            skuInfo.setSkuSaleAttrValueList(skuSaleAttrValues);
        }

        return skuInfo;
    }

    //根据productId查询出下面所有skuId与其销售属性值id的关联关系
    @Override
    public List<PmsSkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = this.pmsSkuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuSaleAttrValueList;
    }
}
