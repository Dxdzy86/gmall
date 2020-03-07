package com.dxd.gmall.service;

import com.dxd.gmall.bean.UmsMember;
import com.dxd.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsMemberService {
    List<UmsMember> getAllMembers();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
