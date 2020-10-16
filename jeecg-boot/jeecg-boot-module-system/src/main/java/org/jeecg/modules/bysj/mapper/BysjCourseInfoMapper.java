package org.jeecg.modules.bysj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.bysj.entity.BysjCourseInfo;

import java.util.List;

/**
 * @Description: bysj_course_info
 * @Author: jeecg-boot
 * @Date:   2020-04-27
 * @Version: V1.0
 */
public interface BysjCourseInfoMapper extends BaseMapper<BysjCourseInfo> {

    List<String> queryCourseIdsByStudentId(@Param("studentId") String studentId);
}
