package com.dxd.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dxd.gmall.bean.PmsSkuAttrValue;
import com.dxd.gmall.bean.PmsSkuImage;
import com.dxd.gmall.bean.PmsSkuInfo;
import com.dxd.gmall.bean.PmsSkuSaleAttrValue;
import com.dxd.gmall.cons.RedisConst;
import com.dxd.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.dxd.gmall.manage.mapper.PmsSkuImageMapper;
import com.dxd.gmall.manage.mapper.PmsSkuInfoMapper;
import com.dxd.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.dxd.gmall.service.PmsSkuService;
import com.dxd.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

/**
 * @author Dxd
 * @date 2020/04/11
 */

@Service
public class PmsSkuServiceImpl implements PmsSkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 保存属于某个商品spuId下的sku信息
     *
     * @param pmsSkuInfo
     */
    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        try {
            //该sku商品属于哪一个spu商品下的
            pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
            //如果前台没有选择默认图片，在后台进行设置
            if (StringUtils.isEmpty(pmsSkuInfo.getSkuDefaultImg())) {
                pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
            }

            //将sku信息入库
            this.pmsSkuInfoMapper.insertSelective(pmsSkuInfo);

            //将sku与平台属性的关联入库
            for (PmsSkuAttrValue attrValue : pmsSkuInfo.getSkuAttrValueList()) {
                attrValue.setSkuId(pmsSkuInfo.getId());
                this.pmsSkuAttrValueMapper.insertSelective(attrValue);
            }
            //将sku与平台销售属性的关联入库
            for (PmsSkuSaleAttrValue saleAttrValue : pmsSkuInfo.getSkuSaleAttrValueList()) {
                saleAttrValue.setSkuId(pmsSkuInfo.getId());
                this.pmsSkuSaleAttrValueMapper.insertSelective(saleAttrValue);
            }
            //将sku的图片入库
            for (PmsSkuImage image : pmsSkuInfo.getSkuImageList()) {
                image.setSkuId(pmsSkuInfo.getId());
                this.pmsSkuImageMapper.insertSelective(image);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据skuId从数据库中得到商品详情
     *
     * @param skuId
     * @return
     */
    @Override
    public PmsSkuInfo getSkuInfoFromDb(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = this.pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        if (skuInfo != null) {
            //查询当前商品的图片集合
            PmsSkuImage skuImage = new PmsSkuImage();
            skuImage.setSkuId(skuId);
            List<PmsSkuImage> skuImages = this.pmsSkuImageMapper.select(skuImage);
            skuInfo.setSkuImageList(skuImages);
            //查询当前商品的销售属性和销售属性值
            PmsSkuSaleAttrValue skuSaleAttrValue = new PmsSkuSaleAttrValue();
            skuSaleAttrValue.setSkuId(skuId);
            List<PmsSkuSaleAttrValue> skuSaleAttrValues = this.pmsSkuSaleAttrValueMapper.select(skuSaleAttrValue);
            skuInfo.setSkuSaleAttrValueList(skuSaleAttrValues);
        }
        return skuInfo;
    }

    /**
     * 从redis服务器缓存中查询出skuInfo信息
     *
     * @param skuId
     * @return
     */
    @Override
    public PmsSkuInfo getSkuInfo(String skuId) {
        //从redis池中得到封装后的jedis对象
        Jedis jedis = redisUtil.getJedis();
        //设置key的命名规范
        //eg:  sku:114:info
        String key = RedisConst.sku_prefix + skuId + RedisConst.sku_suffix;
        //从缓存中根据key找到对应的value
        String skuInfoJson = jedis.get(key);
        PmsSkuInfo pmsSkuInfo = null;
        if (skuInfoJson != null) {
            System.out.println(Thread.currentThread().getName() + "命中缓存......");
            pmsSkuInfo = JSON.parseObject(skuInfoJson, PmsSkuInfo.class);
            jedis.close();
            return pmsSkuInfo;
        } else {
            System.out.println(Thread.currentThread().getName() + "缓存没有命中......");
            //redis缓存没有命中，需要从数据库中查询数据；但是为了防止发生缓存击穿现象，
            //需要采用分布式锁方式解决瞬时数据库服务压力大的问题
            String lockKey = RedisConst.sku_prefix + skuId + RedisConst.lock_suffix;
            /**
             * 分布式锁设置了失效时间，在这个失效时间之内可以访问数据库。但是在多线程环境下，当获得分布式锁的线程访问数据库时由于
             * 网络或者其他原因导致拿到数据较慢，超出了失效时间，分布式锁失效。该分布式锁刚好被另一个线程获得，但是恰恰这时，上一个
             * 获得锁的线程拿到了数据库返回的数据，进行下面的删除分布式锁的操作，这时删除的就是当前线程的锁。该问题如何解决？
             */
            String token = UUID.randomUUID().toString();
            String lockResult = jedis.set(lockKey, token, "NX", "PX", RedisConst.lock_expire_px);
            //获得分布式锁
            if ("OK".equals(lockResult)) {
                System.out.println(Thread.currentThread().getName() + "获得分布式锁......");
                pmsSkuInfo = this.getSkuInfoFromDb(skuId);
                //判断从数据库中查询出的数据是否为null或者空,若为null或者空直接返回null
                if (pmsSkuInfo == null || "".equals(pmsSkuInfo)) {
                    //为了防止缓存穿透，将key的值设为null或者为空
                    jedis.setex(key, RedisConst.exp_sec, "empty");
                    jedis.close();
                    return null;
                }
                //数据库查询出的数据不为空，直接放入redis，并设置过期时间
                jedis.setex(key, RedisConst.exp_sec, JSON.toJSONString(pmsSkuInfo));
                //将mysql中的数据存入redis缓存后，删除分布式锁
                /**
                 * 从redis缓存中根据lockKey取出值与token对比，看锁是否被同一个线程获得
                 * 如果对比结果为真，说明没有其他线程在锁过期的时候获得锁，此时可以删除锁
                 */
                String lockKeyValue = jedis.get(lockKey);
                if (!StringUtils.isEmpty(lockKeyValue) && lockKeyValue.equals(token)) {
                    jedis.del(lockKey);
                }
            } else {
                //没有获得分布式锁，开始自旋
                System.out.println(Thread.currentThread().getName() + "开始自旋......");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                jedis.close();
                return getSkuInfo(skuId);
            }
        }
        jedis.close();
        return pmsSkuInfo;
    }

    /**
     * 根据productId查询出下面所有skuId与其销售属性值id的关联关系
     * @param productId
     * @return
     */
    @Override
    public List<PmsSkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = this.pmsSkuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuSaleAttrValueList;
    }

    /**
     * @return List<PmsSkuInfo>
     */
    @Override
    public List<PmsSkuInfo> getAllSkuInfos() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for(PmsSkuInfo skuInfo:pmsSkuInfos){
            PmsSkuAttrValue attrValue = new PmsSkuAttrValue();
            attrValue.setSkuId(skuInfo.getId());
            List<PmsSkuAttrValue> skuAttrValues = pmsSkuAttrValueMapper.select(attrValue);
            skuInfo.setSkuAttrValueList(skuAttrValues);
        }
        return pmsSkuInfos;
    }

}
