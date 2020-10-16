package org.jeecg.modules.bysj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.bysj.entity.BysjClassroomInfo;
import org.jeecg.modules.bysj.entity.BysjCourseArrange;

/**
 * @Description: bysj_classroom_info
 * @Author: jeecg-boot
 * @Date:   2020-04-30
 * @Version: V1.0
 */
public interface IBysjClassroomInfoService extends IService<BysjClassroomInfo> {

    /**
     * 验证教室名称是否存在
     * @param classroomName
     * @return
     */
    boolean isClassroomNameExist(String classroomName);

    /**
     * 查询未被排的教室
     * @param page
     * @param bysjCourseArrange
     * @return
     */
    IPage<BysjClassroomInfo> arrangePage(Page<BysjClassroomInfo> page, BysjCourseArrange bysjCourseArrange,Integer num);
}
