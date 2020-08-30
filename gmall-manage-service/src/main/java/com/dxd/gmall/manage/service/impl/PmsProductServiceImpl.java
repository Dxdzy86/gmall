package com.dxd.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dxd.gmall.bean.PmsProductImage;
import com.dxd.gmall.bean.PmsProductInfo;
import com.dxd.gmall.bean.PmsProductSaleAttr;
import com.dxd.gmall.bean.PmsProductSaleAttrValue;
import com.dxd.gmall.cons.RedisConst;
import com.dxd.gmall.manage.mapper.PmsProductImageMapper;
import com.dxd.gmall.manage.mapper.PmsProductInfoMapper;
import com.dxd.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.dxd.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.dxd.gmall.service.PmsProductService;
import com.dxd.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品的service实现类
 *
 * @author Dxd
 * @date 2020/04/08
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
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 根据三级分类id查找商品信息
     *
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

    /**
     * 保存商品信息
     *
     * @param pmsProductInfo
     */
    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        try {
            this.pmsProductInfoMapper.insert(pmsProductInfo);
            //将商品的每张图片绑定上商品得productId，并插入到数据库中
            for (PmsProductImage image : pmsProductInfo.getSpuImageList()) {
                image.setProductId(pmsProductInfo.getId());
                this.pmsProductImageMapper.insert(image);
            }
            //将商品中的每个销售属性绑定上商品的productId
            List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductInfo.getSpuSaleAttrList();
            for (PmsProductSaleAttr attr : pmsProductSaleAttrs) {
                attr.setProductId(pmsProductInfo.getId());
                //将每一个销售属性对应的销售属性值绑定上商品的productId
                for (PmsProductSaleAttrValue attrValue : attr.getSpuSaleAttrValueList()) {
                    attrValue.setProductId(pmsProductInfo.getId());
                    //将销售属性值插入到对应的数据库表中(pms_product_sale_attr_value)
                    this.pmsProductSaleAttrValueMapper.insert(attrValue);
                }
                //将销售属性插入到对应的数据库表中(pms_product_sale_attr)
                this.pmsProductSaleAttrMapper.insert(attr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据商品id得到相应的商品图片
     *
     * @param spuId
     * @return
     */
    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = this.pmsProductImageMapper.select(pmsProductImage);
        return pmsProductImages;
    }

    /**
     * 根据商品id得到销售属性列表
     *
     * @param productId
     * @return
     */
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String productId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(productId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = this.pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        for (PmsProductSaleAttr attr : pmsProductSaleAttrs) {
            //根据商品id和销售属性id查询属性值是属于哪一个销售属性
            PmsProductSaleAttrValue value = new PmsProductSaleAttrValue();
            value.setProductId(attr.getProductId());
            value.setSaleAttrId(attr.getSaleAttrId());
            List<PmsProductSaleAttrValue> attrValues = this.pmsProductSaleAttrValueMapper.select(value);
            attr.setSpuSaleAttrValueList(attrValues);
        }
        return pmsProductSaleAttrs;
    }

    /**
     * 根据商品id得到销售属性列表
     *
     * @param productId
     * @param skuId
     * @return
     */
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId) {
        Jedis jedis = redisUtil.getJedis();
        String productSaleAttrKey = RedisConst.spu_prefix + productId + RedisConst.spu_suffix;
        String productSaleAttrJson = jedis.get(productSaleAttrKey);
        String empty = "empty";
        List<PmsProductSaleAttr> pmsProductSaleAttrs = null;
        if (productSaleAttrJson == null || productSaleAttrJson.length() == 0) {
            //缓存没有命中，需要从数据库查询出数据，并放入缓存中；这里要注意缓存击穿或者缓存雪崩的问题
            //使用分布式锁解决数据库瞬时压力过大问题
            String lockKey = RedisConst.spu_prefix + productId + RedisConst.lock_suffix;
            String lockedResult = jedis.set(lockKey, "OK", "NX", "PX", RedisConst.lock_expire_px);
            String expectedResult = "OK";
            if (expectedResult.equals(lockedResult)) {
                //进入到该代码块，说明已经当前线程已经获得到分布式锁，其他线程自旋等待获取锁
                pmsProductSaleAttrs = this.pmsProductSaleAttrMapper.spuSaleAttrListCheckBySku(productId, skuId);
                //这里需要对数据库拿出来的数据进行判断，看是否为null值；其实是解决缓存穿透问题
                if (pmsProductSaleAttrs == null) {
                    jedis.setex(productSaleAttrKey, RedisConst.exp_sec, "empty");
                    return null;
                }
                //数据库的数据不为空
                productSaleAttrJson = JSON.toJSONString(pmsProductSaleAttrs);
                jedis.setex(productSaleAttrKey, RedisConst.exp_sec, productSaleAttrJson);
                jedis.close();
                return pmsProductSaleAttrs;
            } else {
                //该代码块是对未获得分布式锁的线程做自旋操作
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return spuSaleAttrListCheckBySku(productId, skuId);
            }
        } else if (empty.equals(productSaleAttrJson)) {
            //如果是从redis缓存中拿到的是"empty"字符串，说明发生了缓存穿透问题
            jedis.close();
            return null;
        } else {
            //缓存命中，将数据返回给前台
            pmsProductSaleAttrs = JSON.parseObject(productSaleAttrJson, new ArrayList<PmsProductSaleAttr>().getClass());
            jedis.close();
            return pmsProductSaleAttrs;
        }
    }
}
