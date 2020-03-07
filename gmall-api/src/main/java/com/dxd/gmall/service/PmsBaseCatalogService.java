package com.dxd.gmall.service;

import com.dxd.gmall.bean.PmsBaseCatalog1;
import com.dxd.gmall.bean.PmsBaseCatalog2;
import com.dxd.gmall.bean.PmsBaseCatalog3;

import java.util.List;

public interface PmsBaseCatalogService{
    //一级分类查询
    List<PmsBaseCatalog1> getCatalog1();
    //二级分类查询
    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);
    //三级分类查询
    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
