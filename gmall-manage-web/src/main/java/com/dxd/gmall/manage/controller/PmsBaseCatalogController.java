package com.dxd.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dxd.gmall.bean.PmsBaseCatalog1;
import com.dxd.gmall.bean.PmsBaseCatalog2;
import com.dxd.gmall.bean.PmsBaseCatalog3;
import com.dxd.gmall.service.PmsBaseCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/*
* 一级分类Controller
* */
@Controller
@CrossOrigin
public class PmsBaseCatalogController {
    @Reference
    PmsBaseCatalogService pmsBaseCatalogService;

    /**
     * 一级分类查询
     * @return
     */
    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1(){
        List<PmsBaseCatalog1> catalog1List = this.pmsBaseCatalogService.getCatalog1();
        return catalog1List;
    }
    //二级分类查询
    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){
        List<PmsBaseCatalog2> catalog2List = this.pmsBaseCatalogService.getCatalog2(catalog1Id);
        return catalog2List;
    }

    //三级分类查询
    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){
        List<PmsBaseCatalog3> pmsBaseCatalog3List = this.pmsBaseCatalogService.getCatalog3(catalog2Id);
        return pmsBaseCatalog3List;
    }
}
