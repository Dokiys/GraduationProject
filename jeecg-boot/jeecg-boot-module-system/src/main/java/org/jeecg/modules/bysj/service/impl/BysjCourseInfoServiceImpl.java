package org.jeecg.modules.bysj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.bysj.entity.BysjCourseInfo;
import org.jeecg.modules.bysj.mapper.BysjCourseInfoMapper;
import org.jeecg.modules.bysj.service.IBysjCourseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: bysj_course_info
 * @Author: jeecg-boot
 * @Date:   2020-04-27
 * @Version: V1.0
 */
@Service
public class BysjCourseInfoServiceImpl extends ServiceImpl<BysjCourseInfoMapper, BysjCourseInfo> implements IBysjCourseInfoService {

    @Autowired
    private BysjCourseInfoMapper bysjCourseInfoMapper;

    @Override
    public List<String> queryCourseIdsByStudentId(String studentId) {
        return bysjCourseInfoMapper.queryCourseIdsByStudentId(studentId);
    }
}
