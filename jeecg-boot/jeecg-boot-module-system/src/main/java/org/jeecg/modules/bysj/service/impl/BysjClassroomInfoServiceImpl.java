package org.jeecg.modules.bysj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.bysj.entity.BysjClassroomInfo;
import org.jeecg.modules.bysj.entity.BysjCourseArrange;
import org.jeecg.modules.bysj.mapper.BysjClassroomInfoMapper;
import org.jeecg.modules.bysj.service.IBysjClassroomInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: bysj_classroom_info
 * @Author: jeecg-boot
 * @Date:   2020-04-30
 * @Version: V1.0
 */
@Service
public class BysjClassroomInfoServiceImpl extends ServiceImpl<BysjClassroomInfoMapper, BysjClassroomInfo> implements IBysjClassroomInfoService {

    @Autowired
    BysjClassroomInfoMapper bysjClassroomInfoMapper;

    @Override
    public boolean isClassroomNameExist(String classroomName) {
        return bysjClassroomInfoMapper.isClassroomNameExist(classroomName);
    }

    @Override
    public IPage<BysjClassroomInfo> arrangePage(Page<BysjClassroomInfo> page, BysjCourseArrange bysjCourseArrange,Integer num) {
        return bysjClassroomInfoMapper.arrangePage(page,bysjCourseArrange, num);
    }
}
