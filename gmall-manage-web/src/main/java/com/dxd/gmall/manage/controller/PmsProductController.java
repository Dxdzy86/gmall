package com.dxd.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dxd.gmall.bean.PmsProductImage;
import com.dxd.gmall.bean.PmsProductInfo;
import com.dxd.gmall.bean.PmsProductSaleAttr;
import com.dxd.gmall.service.PmsProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//商品属性SPU管理的controller
@Controller
@CrossOrigin
public class PmsProductController {

    @Reference
    PmsProductService pmsProductService;

    /**
     * 根据三级分类id得到所有商品spu的信息列表
     * @param catalog3Id
     * @return
     */
    @RequestMapping("/spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id){
        List<PmsProductInfo> pmsProductInfoList = this.pmsProductService.getPmsProductInfoList(catalog3Id);
        return pmsProductInfoList;
    }

    //保存新增的商品(spu)信息
    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        this.pmsProductService.saveSpuInfo(pmsProductInfo);
        return "success";
    }

    //根据商品id得到相应的商品图片
    @RequestMapping(value="/spuImageList",method= RequestMethod.GET)
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){
        List<PmsProductImage> pmsProductImages = this.pmsProductService.spuImageList(spuId);
        return pmsProductImages;
    }

    //根据商品id得到销售属性列表
    @RequestMapping(value="/spuSaleAttrList",method = RequestMethod.GET)
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrs = this.pmsProductService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrs;
    }
}
