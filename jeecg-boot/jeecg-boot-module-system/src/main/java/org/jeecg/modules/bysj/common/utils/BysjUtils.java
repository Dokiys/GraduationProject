package org.jeecg.modules.bysj.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.SysUserDepart;
import org.jeecg.modules.system.service.ISysUserDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class BysjUtils {

    @Autowired
    private static ISysUserDepartService sysUserDepartService;

    private static BysjUtils bysjUtils;

    @PostConstruct
    public void init() {
        bysjUtils = this;
        bysjUtils.sysUserDepartService = this.sysUserDepartService;
    }

    public static String getLoginUserDepId(){
        //获取登录用户，并获取其角色
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //查询所在部门
        QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        List<SysUserDepart> list = sysUserDepartService.list(queryWrapper);
        return list.get(0).getDepId();
    }
}
