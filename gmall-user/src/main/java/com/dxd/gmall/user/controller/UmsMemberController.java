package com.dxd.gmall.user.controller;

import com.dxd.gmall.bean.UmsMember;
import com.dxd.gmall.bean.UmsMemberReceiveAddress;
import com.dxd.gmall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UmsMemberController {

    @Autowired
    public UmsMemberService umsMemberService;

    @RequestMapping("getAllMembers")
    @ResponseBody
    public List<UmsMember> getAllMembers(){
        List<UmsMember> umsMembers = this.umsMemberService.getAllMembers();
        return umsMembers;
    }

    @RequestMapping("getReceiveAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){
        List<UmsMemberReceiveAddress> addresses = this.umsMemberService.getReceiveAddressByMemberId(memberId);
        return addresses;
    }
}
