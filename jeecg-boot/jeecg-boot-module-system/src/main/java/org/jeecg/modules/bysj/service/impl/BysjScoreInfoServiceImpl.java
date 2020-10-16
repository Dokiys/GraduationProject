package org.jeecg.modules.bysj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.bysj.entity.BysjScoreInfo;
import org.jeecg.modules.bysj.entity.BysjScoreInfoVO;
import org.jeecg.modules.bysj.mapper.BysjScoreInfoMapper;
import org.jeecg.modules.bysj.service.IBysjScoreInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: bysj_score_info
 * @Author: jeecg-boot
 * @Date: 2020-05-11
 * @Version: V1.0
 */
@Service
public class BysjScoreInfoServiceImpl extends ServiceImpl<BysjScoreInfoMapper, BysjScoreInfo> implements IBysjScoreInfoService {

    @Autowired
    private BysjScoreInfoMapper bysjScoreInfoMapper;

    @Override
    public IPage<BysjScoreInfo> pageList(Page<BysjScoreInfo> page, BysjScoreInfoVO bysjScoreInfoVO, List<String> classIds) {
        return bysjScoreInfoMapper.pageList(page,bysjScoreInfoVO,classIds);
    }

    @Override
    public IPage<BysjScoreInfoVO> markPage(Page<BysjScoreInfo> page, BysjScoreInfoVO bysjScoreInfo) {
        return bysjScoreInfoMapper.markPage(page,bysjScoreInfo);
    }

    @Override
    public void emptyScoreById(String id) {
        bysjScoreInfoMapper.emptyScoreById(id);
    }

}
