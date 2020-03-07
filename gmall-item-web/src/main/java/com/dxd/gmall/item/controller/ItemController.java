package com.dxd.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dxd.gmall.bean.PmsProductSaleAttr;
import com.dxd.gmall.bean.PmsSkuInfo;
import com.dxd.gmall.bean.PmsSkuSaleAttrValue;
import com.dxd.gmall.service.PmsProductService;
import com.dxd.gmall.service.PmsSkuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品详情页Controller
 */
@Controller
@CrossOrigin
public class ItemController {

    @Reference
    PmsSkuService pmsSkuServiceImpl;

    @Reference
    PmsProductService pmsProductServiceImpl;
    /**
     * 根据skuId从数据库中查询出商品信息
     * @param skuId
     * @return
     */
    @RequestMapping("/{skuId}.html")
    public String getSkuInfo(@PathVariable("skuId") String skuId, ModelMap modelMap){
        PmsSkuInfo pmsSkuInfo = this.pmsSkuServiceImpl.getSkuInfo(skuId);
        modelMap.addAttribute("skuInfo",pmsSkuInfo);
        //商品销售属性和销售属性值信息
        List<PmsProductSaleAttr>  spuSaleAttrList = this.pmsProductServiceImpl.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
        modelMap.addAttribute("spuSaleAttrListCheckBySku",spuSaleAttrList);

        //spu下所有skuId与其属性之间的关联关系(销售属性值组成key,skuId为value的键值对)
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList =  this.pmsSkuServiceImpl.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());
        Map<String,String> skuSaleAttrValuesMap = new HashMap<>();
        String saleAttrValueIds = "";
        for (int i=0;i<pmsSkuSaleAttrValueList.size();i++) {
            PmsSkuSaleAttrValue skuSaleAttrValue = pmsSkuSaleAttrValueList.get(i);
            //拼接某个skuId下的销售属性值id
            saleAttrValueIds = saleAttrValueIds + skuSaleAttrValue.getSaleAttrValueId()+"|";
            if(((i+1)==pmsSkuSaleAttrValueList.size())||!skuSaleAttrValue.getSkuId().equals(pmsSkuSaleAttrValueList.get(i+1).getSkuId())){
                //将skuId下的所有属性值id作为key,skuId作为value存入map集合中
                int idx = saleAttrValueIds.lastIndexOf("|");
                saleAttrValueIds = saleAttrValueIds.substring(0, idx);
                skuSaleAttrValuesMap.put(saleAttrValueIds,skuSaleAttrValue.getSkuId());
                //字符串初始为空
                saleAttrValueIds="";
            }
        }

        String skuSaleAttrValueJsonStr = JSON.toJSONString(skuSaleAttrValuesMap);
        modelMap.addAttribute("skuSaleAttrValueJsonStr",skuSaleAttrValueJsonStr);

        return "item";
    }
}
