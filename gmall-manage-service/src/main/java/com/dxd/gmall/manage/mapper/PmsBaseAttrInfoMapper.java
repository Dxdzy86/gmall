package com.dxd.gmall.manage.mapper;

import com.dxd.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Dxd
 * @date 2020/04/16
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
    /**
     * 根据不重复的平台属性值得到对应的平台属性，封装成bean对象
     * 注意：这个方法的参数如果没有@Param注解，会报“通过getter方法得不到attrValueIds的错误”，搞不懂？？？
     * @param attrValueIds 所有平台属性值拼成的字符串
     * @return
     */
    List<PmsBaseAttrInfo> selectBaseAttrInfoByIds(@Param("attrValueIds") String attrValueIds);
}
