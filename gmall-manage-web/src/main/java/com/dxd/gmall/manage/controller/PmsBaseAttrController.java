package com.dxd.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dxd.gmall.bean.PmsBaseAttrInfo;
import com.dxd.gmall.bean.PmsBaseAttrValue;
import com.dxd.gmall.bean.PmsBaseSaleAttr;
import com.dxd.gmall.manage.util.PmsUploadUtil;
import com.dxd.gmall.service.PmsBaseAttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class PmsBaseAttrController {

    @Reference
    PmsBaseAttrService pmsBaseAttrService;
    
    /**
     * 根据三级分类id查询平台属性
     * @param catalog3Id
     * @return
     */
    @RequestMapping(value = "attrInfoList",method = RequestMethod.GET)
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = this.pmsBaseAttrService.getAttrInfoList(catalog3Id);
        return pmsBaseAttrInfoList;
    }

    /**
     * 保存平台属性信息
     * @param pmsBaseAttrInfo
     * @return
     */
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        this.pmsBaseAttrService.saveAttrInfo(pmsBaseAttrInfo);
        return "success";
    }

    //根据属性id得到对应的平台属性值列表
    @RequestMapping(value="getAttrValueList",method = RequestMethod.POST)
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> pmsBaseAttrValueList = this.pmsBaseAttrService.getAttrValueList(attrId);
        return pmsBaseAttrValueList;
    }

    //获取商品所有销售属性(颜色、尺寸、版本、容量)
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> pmsBaseSaleAttrList = this.pmsBaseAttrService.baseSaleAttrList();
        return pmsBaseSaleAttrList;
    }

    //图片上传
    @RequestMapping(value = "fileUpload",method = RequestMethod.POST)
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){
        String imgUrl = PmsUploadUtil.imageUpload(multipartFile);
        return imgUrl;
    }
}
