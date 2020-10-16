package org.jeecg.modules.bysj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.bysj.entity.BysjClassroomInfo;
import org.jeecg.modules.bysj.entity.BysjCourseArrange;
import org.jeecg.modules.bysj.service.IBysjClassroomInfoService;
import org.jeecg.modules.system.service.ISysDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: bysj_classroom_info
 * @Author: jeecg-boot
 * @Date:   2020-04-30
 * @Version: V1.0
 */
@Api(tags="bysj_classroom_info")
@RestController
@RequestMapping("/bysj/bysjClassroomInfo")
@Slf4j
public class BysjClassroomInfoController extends JeecgController<BysjClassroomInfo, IBysjClassroomInfoService> {
	@Autowired
	private IBysjClassroomInfoService bysjClassroomInfoService;
	@Autowired
	private ISysDepartService sysDepartService;
	/**
	 * 分页列表查询
	 *
	 * @param bysjClassroomInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bysj_classroom_info-分页列表查询")
	@ApiOperation(value="bysj_classroom_info-分页列表查询", notes="bysj_classroom_info-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BysjClassroomInfo bysjClassroomInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<BysjClassroomInfo> page = new Page<BysjClassroomInfo>(pageNo, pageSize);
		//设置可容纳人数大于所选择的人数
		QueryWrapper<BysjClassroomInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.ge((bysjClassroomInfo.getHoldNumber()!=null),"hold_number",bysjClassroomInfo.getHoldNumber());
        queryWrapper.like(StringUtils.isNotBlank(bysjClassroomInfo.getClassroomName()),"classroom_name",bysjClassroomInfo.getClassroomName());
		IPage<BysjClassroomInfo> pageList = bysjClassroomInfoService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	 /**
	  * 分页列表查询
	  *
	  * @param bysjCourseArrange
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "bysj_classroom_info-排课分页列表查询")
	 @ApiOperation(value="bysj_classroom_info-排课分页列表查询", notes="bysj_classroom_info-排课分页列表查询")
	 @GetMapping(value = "/arrangeList")
	 public Result<?> queryArrangePageList(BysjCourseArrange bysjCourseArrange,
										   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
										   HttpServletRequest req) {
		 Page<BysjClassroomInfo> page = new Page<BysjClassroomInfo>(pageNo, pageSize);
		 //TODO查询所选班级人数，传入arrangePage作为查询条件
		 Integer num = sysDepartService.queryUserNumByIds(bysjCourseArrange.getClassId());
		 //设置可容纳人数大于所选择的人数
		 IPage<BysjClassroomInfo> pageList = bysjClassroomInfoService.arrangePage(page, bysjCourseArrange,num);
		 return Result.ok(pageList);
	 }
	
	/**
	 *   添加
	 *
	 * @param bysjClassroomInfo
	 * @return
	 */
	@AutoLog(value = "bysj_classroom_info-添加")
	@ApiOperation(value="bysj_classroom_info-添加", notes="bysj_classroom_info-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BysjClassroomInfo bysjClassroomInfo) {
		bysjClassroomInfoService.save(bysjClassroomInfo);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bysjClassroomInfo
	 * @return
	 */
	@AutoLog(value = "bysj_classroom_info-编辑")
	@ApiOperation(value="bysj_classroom_info-编辑", notes="bysj_classroom_info-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BysjClassroomInfo bysjClassroomInfo) {
		bysjClassroomInfoService.updateById(bysjClassroomInfo);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bysj_classroom_info-通过id删除")
	@ApiOperation(value="bysj_classroom_info-通过id删除", notes="bysj_classroom_info-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bysjClassroomInfoService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bysj_classroom_info-批量删除")
	@ApiOperation(value="bysj_classroom_info-批量删除", notes="bysj_classroom_info-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bysjClassroomInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bysj_classroom_info-通过id查询")
	@ApiOperation(value="bysj_classroom_info-通过id查询", notes="bysj_classroom_info-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BysjClassroomInfo bysjClassroomInfo = bysjClassroomInfoService.getById(id);
		if(bysjClassroomInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(bysjClassroomInfo);
	}

	 /**
	  * 查询classroomName是否重复
	  *
	  * @param classroomName
	  * @return
	  */
	 @AutoLog(value = "bysj_classroom_info-通过id查询")
	 @ApiOperation(value="bysj_classroom_info-通过id查询", notes="bysj_classroom_info-通过id查询")
	 @GetMapping(value = "/validateClassroomName")
	 public Result<?> validateClassroomName(@RequestParam(name="classroomName",required=true) String classroomName) {
		 Boolean isExist = bysjClassroomInfoService.isClassroomNameExist(classroomName);
		 if(!isExist){
			 return Result.ok();
		 }
		 return Result.error("classroomName exists");
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param bysjClassroomInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BysjClassroomInfo bysjClassroomInfo) {
        return super.exportXls(request, bysjClassroomInfo, BysjClassroomInfo.class, "bysj_classroom_info");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BysjClassroomInfo.class);
    }

}
