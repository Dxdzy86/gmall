package com.dxd.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dxd.gmall.bean.PmsSkuInfo;
import com.dxd.gmall.service.PmsSkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//Sku控制层
@Controller
@CrossOrigin
public class PmsSkuController {

    @Reference
    private PmsSkuService pmsSkuService;

    //保存sku信息
    @RequestMapping(value="/saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        this.pmsSkuService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }
}
