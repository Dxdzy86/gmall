package com.dxd.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dxd.gmall.bean.PmsBaseCatalog1;
import com.dxd.gmall.bean.PmsBaseCatalog2;
import com.dxd.gmall.bean.PmsBaseCatalog3;
import com.dxd.gmall.manage.mapper.PmsBaseCatalog1Mapper;
import com.dxd.gmall.manage.mapper.PmsBaseCatalog2Mapper;
import com.dxd.gmall.manage.mapper.PmsBaseCatalog3Mapper;
import com.dxd.gmall.service.PmsBaseCatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 商品管理系统PMS基本分类的service实现类
 */
@Service
public class PmsBaseCatalogServiceImpl implements PmsBaseCatalogService {

    @Autowired
    PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    //一级分类查询
    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1List = this.pmsBaseCatalog1Mapper.selectAll();
        return pmsBaseCatalog1List;
    }

    //二级分类查询
    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        List<PmsBaseCatalog2> pmsBaseCatalog2List = this.pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);
        return pmsBaseCatalog2List;
    }
    //三级分类查询
    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);
        List<PmsBaseCatalog3> pmsBaseCatalog3List = this.pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);
        return pmsBaseCatalog3List;
    }
}
