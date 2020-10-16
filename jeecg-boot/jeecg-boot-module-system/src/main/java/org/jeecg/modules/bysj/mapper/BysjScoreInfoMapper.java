package org.jeecg.modules.bysj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.bysj.entity.BysjScoreInfo;
import org.jeecg.modules.bysj.entity.BysjScoreInfoVO;

import java.util.List;

/**
 * @Description: bysj_score_info
 * @Author: jeecg-boot
 * @Date:   2020-05-11
 * @Version: V1.0
 */
public interface BysjScoreInfoMapper extends BaseMapper<BysjScoreInfo> {

    IPage<BysjScoreInfo> pageList(Page<BysjScoreInfo> page, @Param("bysjScoreInfoVO") BysjScoreInfoVO bysjScoreInfoVO, @Param("classIds")  List<String> classIds);

    IPage<BysjScoreInfoVO> markPage(Page<BysjScoreInfo> page, @Param("bysjScoreInfoVO") BysjScoreInfoVO bysjScoreInfoVO);

    void emptyScoreById( @Param("id") String id);
}
