package com.dxd.gmall.service;

import com.dxd.gmall.bean.UmsMember;
import com.dxd.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsMemberService {
    /**
     * 获取所有用户信息
     * @return List<UmsMember>
     */
    List<UmsMember> getAllMembers();

    /**
     * 根据用户Id获取其所有的收货地址
     * @param memberId 用户id
     * @return List<UmsMemberReceiveAddress>
     */
    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
