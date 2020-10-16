package org.jeecg.modules.bysj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.bysj.entity.BysjCourseArrange;
import org.jeecg.modules.bysj.entity.BysjCourseArrangeVO;

import java.util.List;

/**
 * @Description: bysj_course_arrange
 * @Author: jeecg-boot
 * @Date:   2020-05-01
 * @Version: V1.0
 */
public interface IBysjCourseArrangeService extends IService<BysjCourseArrange> {

    /**
     * 根据传入的部门id,课程id查询已选择的课表
     * @param courseId
     * @param
     * @return
     */
    List<BysjCourseArrangeVO> queryScheduleSelected(List<String> departIdList, String courseId);

    /**
     * 查询排课表
     * @param page
     * @param bysjCourseArrange
     * @return
     */
    IPage<BysjCourseArrangeVO> pageList(Page<BysjCourseArrangeVO> page, BysjCourseArrange bysjCourseArrange,List<String> subDepIdList);

    boolean confirmIsSelecteble(String id);

    List<BysjCourseArrangeVO> queryScheduleByClass(List<String> classIds, String date);

    List<BysjCourseArrangeVO> queryScheduleByCourse(List<String> courseIds, String date);
}
