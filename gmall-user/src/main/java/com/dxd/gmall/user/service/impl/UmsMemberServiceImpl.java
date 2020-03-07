package com.dxd.gmall.user.service.impl;

import com.dxd.gmall.bean.UmsMember;
import com.dxd.gmall.bean.UmsMemberReceiveAddress;
import com.dxd.gmall.service.UmsMemberService;
import com.dxd.gmall.user.mapper.UmsMemberMapper;
import com.dxd.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private UmsMemberMapper umsMemberMapper;
    @Autowired
    private UmsMemberReceiveAddressMapper receiveAddressMapper;

    @Override
    public List<UmsMember> getAllMembers() {
        List<UmsMember> umsMembers = this.umsMemberMapper.selectAllMembers();
        return umsMembers;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        UmsMemberReceiveAddress receiveAddress = new UmsMemberReceiveAddress();
        receiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> addresses = this.receiveAddressMapper.select(receiveAddress);
        return addresses;
    }
}
