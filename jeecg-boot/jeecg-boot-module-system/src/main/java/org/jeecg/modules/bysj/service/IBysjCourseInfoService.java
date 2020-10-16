package org.jeecg.modules.bysj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.bysj.entity.BysjCourseInfo;

import java.util.List;

/**
 * @Description: bysj_course_info
 * @Author: jeecg-boot
 * @Date:   2020-04-27
 * @Version: V1.0
 */
public interface IBysjCourseInfoService extends IService<BysjCourseInfo> {

    List<String> queryCourseIdsByStudentId(String id);
}
