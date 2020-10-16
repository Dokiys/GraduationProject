package org.jeecg.modules.bysj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.bysj.entity.BysjClassroomInfo;
import org.jeecg.modules.bysj.entity.BysjCourseArrange;

/**
 * @Description: bysj_classroom_info
 * @Author: jeecg-boot
 * @Date:   2020-04-30
 * @Version: V1.0
 */
public interface BysjClassroomInfoMapper extends BaseMapper<BysjClassroomInfo> {

    boolean isClassroomNameExist(@Param("classroomName") String classroomName);

    IPage<BysjClassroomInfo> arrangePage(Page<BysjClassroomInfo> page, @Param("bysjCourseArrange") BysjCourseArrange bysjCourseArrange,@Param("num") int num);
}
