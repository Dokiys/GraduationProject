package org.jeecg.modules.bysj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.bysj.entity.BysjCourseArrange;
import org.jeecg.modules.bysj.entity.BysjCourseArrangeVO;

import java.util.List;

/**
 * @Description: bysj_course_arrange
 * @Author: jeecg-boot
 * @Date:   2020-05-01
 * @Version: V1.0
 */
public interface BysjCourseArrangeMapper extends BaseMapper<BysjCourseArrange> {

    List<BysjCourseArrangeVO> queryScheduleSelected(@Param("departIds")List<String> departIds, @Param("courseId")String courseId);

    IPage<BysjCourseArrangeVO> pageList(Page<BysjCourseArrangeVO> page,@Param("bysjCourseArrange") BysjCourseArrange bysjCourseArrange,@Param("depIds") List<String> depIds);

    List<BysjCourseArrangeVO> querySameCourseArrangeById(@Param("id")String id);

    List<BysjCourseArrangeVO> queryScheduleByClass(@Param("classIds")List<String> classIds,@Param("date") String date);

    List<BysjCourseArrangeVO> queryScheduleByCourse(@Param("courseIds")List<String> courseIds,@Param("date") String date);
}
