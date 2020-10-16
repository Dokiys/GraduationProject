package org.jeecg.modules.bysj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.bysj.entity.BysjScoreInfo;
import org.jeecg.modules.bysj.entity.BysjScoreInfoVO;

import java.util.List;

/**
 * @Description: bysj_score_info
 * @Author: jeecg-boot
 * @Date:   2020-05-11
 * @Version: V1.0
 */
public interface IBysjScoreInfoService extends IService<BysjScoreInfo> {

    IPage<BysjScoreInfo> pageList(Page<BysjScoreInfo> page, BysjScoreInfoVO bysjScoreInfoVO,List<String> classIds);

    IPage<BysjScoreInfoVO> markPage(Page<BysjScoreInfo> page, BysjScoreInfoVO bysjScoreInfo);

    void emptyScoreById(String id);
}
