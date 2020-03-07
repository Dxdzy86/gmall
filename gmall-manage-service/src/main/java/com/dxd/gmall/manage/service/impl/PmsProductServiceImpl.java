package com.dxd.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dxd.gmall.bean.PmsProductImage;
import com.dxd.gmall.bean.PmsProductInfo;
import com.dxd.gmall.bean.PmsProductSaleAttr;
import com.dxd.gmall.bean.PmsProductSaleAttrValue;
import com.dxd.gmall.manage.mapper.PmsProductImageMapper;
import com.dxd.gmall.manage.mapper.PmsProductInfoMapper;
import com.dxd.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.dxd.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.dxd.gmall.service.PmsProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 商品的service实现类
 */
@Service
public class PmsProductServiceImpl implements PmsProductService {

    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;
    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

    /**
     * 根据三级分类id查找商品信息
     * @param catalog3Id
     * @return
     */
    @Override
    public List<PmsProductInfo> getPmsProductInfoList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = this.pmsProductInfoMapper.select(pmsProductInfo);
        return pmsProductInfos;
    }

    //保存商品信息
    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        try{
            this.pmsProductInfoMapper.insert(pmsProductInfo);
            //将商品的每张图片绑定上商品得productId，并插入到数据库中
            for(PmsProductImage image:pmsProductInfo.getSpuImageList()){
                image.setProductId(pmsProductInfo.getId());
                this.pmsProductImageMapper.insert(image);
            }
            //将商品中的每个销售属性绑定上商品的productId
            List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductInfo.getSpuSaleAttrList();
            for(PmsProductSaleAttr attr:pmsProductSaleAttrs){
                attr.setProductId(pmsProductInfo.getId());
                //将每一个销售属性对应的销售属性值绑定上商品的productId
                for(PmsProductSaleAttrValue attrValue:attr.getSpuSaleAttrValueList()){
                    attrValue.setProductId(pmsProductInfo.getId());
                    //将销售属性值插入到对应的数据库表中(pms_product_sale_attr_value)
                    this.pmsProductSaleAttrValueMapper.insert(attrValue);
                }
                //将销售属性插入到对应的数据库表中(pms_product_sale_attr)
                this.pmsProductSaleAttrMapper.insert(attr);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //根据商品id得到相应的商品图片
    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = this.pmsProductImageMapper.select(pmsProductImage);
        return pmsProductImages;
    }

    //根据商品id得到销售属性列表
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String productId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(productId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = this.pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        for(PmsProductSaleAttr attr:pmsProductSaleAttrs){
            //根据商品id和销售属性id查询属性值是属于哪一个销售属性
            PmsProductSaleAttrValue value = new PmsProductSaleAttrValue();
            value.setProductId(attr.getProductId());
            value.setSaleAttrId(attr.getSaleAttrId());
            List<PmsProductSaleAttrValue> attrValues = this.pmsProductSaleAttrValueMapper.select(value);
            attr.setSpuSaleAttrValueList(attrValues);
        }
        return pmsProductSaleAttrs;
    }

    //根据商品id得到销售属性列表
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrs = this.pmsProductSaleAttrMapper.spuSaleAttrListCheckBySku(productId,skuId);
        return pmsProductSaleAttrs;
    }
}
