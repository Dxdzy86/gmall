package com.dxd.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dxd.gmall.bean.PmsBaseAttrInfo;
import com.dxd.gmall.bean.PmsBaseAttrValue;
import com.dxd.gmall.bean.PmsBaseSaleAttr;
import com.dxd.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.dxd.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.dxd.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.dxd.gmall.service.PmsBaseAttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Dxd
 * @date 2020/04/19
 */
@Service
public class PmsBaseAttrServiceImpl implements PmsBaseAttrService {

    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;
    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    /**
     * 根据三级分类id查询平台属性
     * @param catalog3Id 三级分类Id
     * @return
     */
    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = this.pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        //将每一个平台属性对应的平台属性值添加到对应的平台属性中
        PmsBaseAttrValue value = value = new PmsBaseAttrValue();
        for(PmsBaseAttrInfo attr:pmsBaseAttrInfoList){
            value.setAttrId(attr.getId());
            List<PmsBaseAttrValue> values = this.pmsBaseAttrValueMapper.select(value);
            attr.setAttrValueList(values);
        }
        return pmsBaseAttrInfoList;
    }

    /**
     * 保存平台属性和属性值
     * @param pmsBaseAttrInfo 平台属性bean对象
     */
    @Override
    public void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        try {
            if (pmsBaseAttrInfo != null) {
                //先根据平台属性的id去数据库中查找是否含有该属性
                PmsBaseAttrInfo attrInfo = this.pmsBaseAttrInfoMapper.selectByPrimaryKey(pmsBaseAttrInfo);
                //如果有，则说明这是一条更新操作
                if (attrInfo != null) {
                    /**
                     * updateByPrimaryKeySelective 接收的参数为对应于数据库的实体类对象，利用字段的自动匹配进行更新表的操作，
                     * 如果传入obj对象中的某个属性值为null，则不进行数据库对应字段的更新
                     */
                    this.pmsBaseAttrInfoMapper.updateByPrimaryKeySelective(pmsBaseAttrInfo);

                    PmsBaseAttrValue attrValue = new PmsBaseAttrValue();
                    attrValue.setAttrId(attrInfo.getId());
                    List<PmsBaseAttrValue> attrValues = this.pmsBaseAttrValueMapper.select(attrValue);
                    if (attrValues != null) {
                        List<String> ids = new ArrayList<>(pmsBaseAttrInfo.getAttrValueList().size());
                        for(PmsBaseAttrValue value:pmsBaseAttrInfo.getAttrValueList()){
                            if(value.getId()!=null){
                                ids.add(value.getId());
                            }
                        }
                        for (PmsBaseAttrValue value : attrValues) {
                            //对于同一个平台属性，如果前台传入的平台属性值ids不包括数据库中该平台属性对应的每个平台属性值id，
                            //就删除数据库中多余的平台属性值
                            if(!ids.contains(value.getId())) {
                                this.pmsBaseAttrValueMapper.delete(value);
                            }
                        }
                    }
                    attrValues = pmsBaseAttrInfo.getAttrValueList();
                    if(attrValues!=null){
                        for (PmsBaseAttrValue value:attrValues){
                            if(value.getAttrId()==null){
                                value.setAttrId(attrInfo.getId());
                                this.pmsBaseAttrValueMapper.insert(value);
                            }else{
                                this.pmsBaseAttrValueMapper.updateByPrimaryKeySelective(value);
                            }
                        }
                    }
                } else {
                    //如果没有，说明这是一条插入操作
                    //将属性插入到数据库中
                    this.pmsBaseAttrInfoMapper.insert(pmsBaseAttrInfo);
                    String attrId = pmsBaseAttrInfo.getId();
                    //根据属性得到属性值，将属性值插入到属性值的表中
                    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                    if (attrValueList != null) {
                        for (PmsBaseAttrValue value : attrValueList) {
                            value.setAttrId(attrId);
                            this.pmsBaseAttrValueMapper.insert(value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        List<PmsBaseAttrValue> pmsBaseAttrValueList = null;
        try{
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(attrId);
            pmsBaseAttrValueList = this.pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
        }catch (Exception e){
            e.printStackTrace();
        }
        return pmsBaseAttrValueList;
    }

    /**
     * 获取商品销售属性列表
     * @return List<PmsBaseSaleAttr>
     */
    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrList = this.pmsBaseSaleAttrMapper.selectAll();
        return pmsBaseSaleAttrList;
    }


    /**
     * @param attrValueIdList 平台销售属性值的Set集合
     * @return List<PmsBaseAttrInfo>
     */
    @Override
    public List<PmsBaseAttrInfo> getAttrValueIdList(Set<String> attrValueIdList) {
        String attrValueIds = StringUtils.join(attrValueIdList.iterator(), ",");
        List<PmsBaseAttrInfo> attrInfoList = this.pmsBaseAttrInfoMapper.selectBaseAttrInfoByIds(attrValueIds);
        return attrInfoList;
    }

}
