package com.dxd.gmall.service;

import com.dxd.gmall.bean.PmsBaseAttrInfo;
import com.dxd.gmall.bean.PmsBaseAttrValue;
import com.dxd.gmall.bean.PmsBaseSaleAttr;

import java.util.List;
import java.util.Set;

/**
 * @author Dxd
 * @date 2020/04/
 */
public interface PmsBaseAttrService {

    /**
     * 根据三级分类Id查询出平台的属性和属性值
     * @param catalog3Id 三级分类Id
     * @return List<PmsBaseAttrInfo>
     */
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    /**
     * 保存平台属性到数据库中
     * @param pmsBaseAttrInfo 平台属性bean对象
     */
    void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    /**
     * 根据平台属性id得到对应的平台属性值集合
     * @param attrId 平台属性id
     * @return List<PmsBaseAttrValue>
     */
    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    /**
     * 查询出所有的销售属性
     * @return List<PmsBaseSaleAttr>
     */
    List<PmsBaseSaleAttr> baseSaleAttrList();

    /**
     * 根据平台属性值的Set集合找到各个属性值对应的属性，封装成PmsBaseAttrInfo对象
     * @param attrValueIdList 平台销售属性值的Set集合
     * @return List<PmsBaseAttrInfo></>
     */
    List<PmsBaseAttrInfo> getAttrValueIdList(Set<String> attrValueIdList);

}
