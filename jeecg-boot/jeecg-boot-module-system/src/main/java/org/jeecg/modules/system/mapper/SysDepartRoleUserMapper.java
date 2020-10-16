package org.jeecg.modules.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysDepartRoleUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;

/**
 * @Description: 部门角色人员信息
 * @Author: jeecg-boot
 * @Date:   2020-02-13
 * @Version: V1.0
 */
public interface SysDepartRoleUserMapper extends BaseMapper<SysDepartRoleUser> {

    List<SysDepartRoleUser> listByUserIds(List<String> userIds);

    List<String> getByUserId(String userId);
}
