package com.dxd.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dxd.gmall.bean.UmsMember;
import com.dxd.gmall.bean.UmsMemberReceiveAddress;
import com.dxd.gmall.service.UmsMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Dxd
 * @date 2020/04/23
 */
@Controller
public class UmsMemberController {

    @Reference
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
