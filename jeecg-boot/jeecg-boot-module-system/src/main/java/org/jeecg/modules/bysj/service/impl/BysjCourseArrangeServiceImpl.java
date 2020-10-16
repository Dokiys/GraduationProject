package org.jeecg.modules.bysj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.bysj.entity.BysjCourseArrange;
import org.jeecg.modules.bysj.entity.BysjCourseArrangeVO;
import org.jeecg.modules.bysj.mapper.BysjCourseArrangeMapper;
import org.jeecg.modules.bysj.service.IBysjCourseArrangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: bysj_course_arrange
 * @Author: jeecg-boot
 * @Date:   2020-05-01
 * @Version: V1.0
 */
@Service
public class BysjCourseArrangeServiceImpl extends ServiceImpl<BysjCourseArrangeMapper, BysjCourseArrange> implements IBysjCourseArrangeService {

    @Autowired
    BysjCourseArrangeMapper bysjCourseArrangeMapper;

    @Override
    public List<BysjCourseArrangeVO> queryScheduleSelected(List<String> departIds,String courseId) {
        return bysjCourseArrangeMapper.queryScheduleSelected(departIds,courseId);
    }

    @Override
    public IPage<BysjCourseArrangeVO> pageList(Page<BysjCourseArrangeVO> page, BysjCourseArrange bysjCourseArrange,List<String> subDepIdList) {
        return bysjCourseArrangeMapper.pageList(page,bysjCourseArrange,subDepIdList);
    }

    @Override
    public boolean confirmIsSelecteble(String id) {
        List<BysjCourseArrangeVO> list = bysjCourseArrangeMapper.querySameCourseArrangeById(id);
        return list.size() <= 1;
    }

    @Override
    public List<BysjCourseArrangeVO> queryScheduleByClass(List<String> classIds, String date) {
        return bysjCourseArrangeMapper.queryScheduleByClass(classIds,date);
    }

    @Override
    public List<BysjCourseArrangeVO> queryScheduleByCourse(List<String> courseIds, String date) {
        return bysjCourseArrangeMapper.queryScheduleByCourse(courseIds,date);
    }
}
