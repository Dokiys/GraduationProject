package org.jeecg.modules.bysj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.bysj.common.consts.BysjConst;
import org.jeecg.modules.bysj.entity.BysjCourseInfo;
import org.jeecg.modules.bysj.entity.BysjScoreInfo;
import org.jeecg.modules.bysj.entity.BysjScoreInfoVO;
import org.jeecg.modules.bysj.service.IBysjCourseInfoService;
import org.jeecg.modules.bysj.service.IBysjScoreInfoService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserRoleService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: bysj_score_info
 * @Author: jeecg-boot
 * @Date:   2020-05-11
 * @Version: V1.0
 */
@Api(tags="bysj_score_info")
@RestController
@RequestMapping("/bysj/bysjScoreInfo")
@Slf4j
public class BysjScoreInfoController extends JeecgController<BysjScoreInfo, IBysjScoreInfoService> {
	@Autowired
	private IBysjScoreInfoService bysjScoreInfoService;
	@Autowired
	private IBysjCourseInfoService bysjCourseInfoService;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private ISysUserRoleService sysUserRoleService;

	/**
	 * 分页列表查询
	 *
	 * @param bysjScoreInfoVO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bysj_score_info-分页列表查询")
	@ApiOperation(value="bysj_score_info-分页列表查询", notes="bysj_score_info-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BysjScoreInfoVO bysjScoreInfoVO,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<BysjScoreInfo> page = new Page<BysjScoreInfo>(pageNo, pageSize);
		bysjScoreInfoVO.setStatus(5);
		List<String> classIds = new ArrayList<>();
		//如果没有传入部门为查询条件则默认为当前登录用户的子部门
		if(bysjScoreInfoVO.getClassId() != null && !"".equals(bysjScoreInfoVO.getClassId())){
			classIds = Arrays.asList(bysjScoreInfoVO.getClassId().split(","));
		}
		else{
			//获取登录用户，并获取其角色
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			//查询当前登录用户子部门id
			List<SysDepart> sysDeparts = sysDepartService.queryUserDeparts(user.getId());
			String departIds = sysDeparts.stream().map(SysDepart::getId).collect(Collectors.joining(","));
			classIds = sysDepartService.getMySubDepIdsByDepId(departIds);
		}
		IPage<BysjScoreInfo> pageList = bysjScoreInfoService.pageList(page, bysjScoreInfoVO,classIds);
		return Result.ok(pageList);
	}

	/**
	 * 分页列表查询
	 *
	 * @param bysjScoreInfoVO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bysj_score_info-分页列表查询")
	@ApiOperation(value="bysj_score_info-分页列表查询", notes="bysj_score_info-分页列表查询")
	@GetMapping(value = "/markList")
	public Result<?> queryMarkPageList(BysjScoreInfoVO bysjScoreInfoVO,
									   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									   HttpServletRequest req) {
		Page<BysjScoreInfo> page = new Page<BysjScoreInfo>(pageNo, pageSize);
		IPage<BysjScoreInfoVO> pageList = bysjScoreInfoService.markPage(page,bysjScoreInfoVO);
		return Result.ok(pageList);
	}
	/**
	 * 分页列表查询
	 *
	 * @return
	 */
	@AutoLog(value = "bysj_score_info-分页列表查询")
	@ApiOperation(value="bysj_score_info-分页列表查询", notes="bysj_score_info-分页列表查询")
	@GetMapping(value = "/noScoreCount")
	public Result<?> queryNoMarkCount(@RequestParam(name="courseId",required=true) String courseId) {
		QueryWrapper<BysjScoreInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.isNull("score");
		queryWrapper.eq("course_id",courseId);
		int count = bysjScoreInfoService.count(queryWrapper);
		return Result.ok(count);
	}

	/**
	 * 我的课程分页列表查询
	 *
	 * @param bysjCourseInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bysj_course_info-我的课程分页列表查询")
	@ApiOperation(value="bysj_course_info-我的课程分页列表查询", notes="bysj_course_info-我的课程分页列表查询")
	@GetMapping(value = "/myScoreList")
	public Result<?> queryMyScorePageList(BysjCourseInfo bysjCourseInfo,
										   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
										   HttpServletRequest req) {
		Page<BysjCourseInfo> page = new Page<BysjCourseInfo>(pageNo, pageSize);

		//判断当前登录用户角色
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		QueryWrapper<SysUserRole> surQueryWrapper = new QueryWrapper<>();
		surQueryWrapper.eq("user_id",user.getId());
		List<SysUserRole> surList = sysUserRoleService.list(surQueryWrapper);
		List<String> collect = surList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

		//如果是学生
		if(collect.indexOf(BysjConst.SUR_STUDENT_CODE) != -1){
			//
			// 通过学生id查询所属班级，再在排课表中查询所有该班级的课程并返回
			List<String> courseIds = bysjCourseInfoService.queryCourseIdsByStudentId(user.getId());
			QueryWrapper<BysjCourseInfo> bciQueryWrapper = QueryGenerator.initQueryWrapper(bysjCourseInfo, req.getParameterMap());
			bciQueryWrapper.in("id",courseIds);
			bciQueryWrapper.eq("status",5);
			IPage<BysjCourseInfo> pageList = bysjCourseInfoService.page(page, bciQueryWrapper);
			pageList.getRecords().forEach(item -> {
				//设置教师姓名
				String teacherName = sysUserService.getById(item.getCourseTeacherid()).getRealname();
				item.setCourseTeacherName(teacherName);

				//设置所属院系名
				String departName = sysDepartService.getById(item.getDepartId()).getDepartName();
				item.setDepartId(departName);

				//设置成绩
				QueryWrapper<BysjScoreInfo> bsiQueryWrapper = new QueryWrapper<>();
				bsiQueryWrapper.eq("course_id",item.getId());
				bsiQueryWrapper.eq("student_id",user.getId());
				List<BysjScoreInfo> list = bysjScoreInfoService.list(bsiQueryWrapper);
				if(list.size() == 1) item.setScore(list.get(0).getScore());
			});
			return Result.ok(pageList);
		}
		return Result.ok(page);
	}

	/**
	 *
	 * @param bysjCourseInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bysj_course_info-我的课程分页列表查询")
	@ApiOperation(value="bysj_course_info-我的课程分页列表查询", notes="bysj_course_info-我的课程分页列表查询")
	@GetMapping(value = "/scoreList")
	public Result<?> queryScorePageList(BysjCourseInfo bysjCourseInfo,
										  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
										  HttpServletRequest req) {
		Page<BysjCourseInfo> page = new Page<BysjCourseInfo>(pageNo, pageSize);

		//判断当前登录用户角色
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		QueryWrapper<SysUserRole> surQueryWrapper = new QueryWrapper<>();
		surQueryWrapper.eq("user_id",user.getId());
		List<SysUserRole> surList = sysUserRoleService.list(surQueryWrapper);
		List<String> collect = surList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

		//如果是学生
		if(collect.indexOf(BysjConst.SUR_STUDENT_CODE) != -1){
			//
			// 通过学生id查询所属班级，再在排课表中查询所有该班级的课程并返回
			List<String> courseIds = bysjCourseInfoService.queryCourseIdsByStudentId(user.getId());
			QueryWrapper<BysjCourseInfo> bciQueryWrapper = QueryGenerator.initQueryWrapper(bysjCourseInfo, req.getParameterMap());
			bciQueryWrapper.in("id",courseIds);
			bciQueryWrapper.eq("status",5);
			IPage<BysjCourseInfo> pageList = bysjCourseInfoService.page(page, bciQueryWrapper);
			pageList.getRecords().forEach(item -> {
				//设置教师姓名
				String teacherName = sysUserService.getById(item.getCourseTeacherid()).getRealname();
				item.setCourseTeacherName(teacherName);

				//设置所属院系名
				String departName = sysDepartService.getById(item.getDepartId()).getDepartName();
				item.setDepartId(departName);

				//设置成绩
				QueryWrapper<BysjScoreInfo> bsiQueryWrapper = new QueryWrapper<>();
				bsiQueryWrapper.eq("course_id",item.getId());
				bsiQueryWrapper.eq("student_id",user.getId());
				List<BysjScoreInfo> list = bysjScoreInfoService.list(bsiQueryWrapper);
				if(list.size() == 1) item.setScore(list.get(0).getScore());
			});
			return Result.ok(pageList);
		}
		return Result.ok(page);
	}

	/**
	 *   添加
	 *
	 * @param bysjScoreInfo
	 * @return
	 */
	@AutoLog(value = "bysj_score_info-添加")
	@ApiOperation(value="bysj_score_info-添加", notes="bysj_score_info-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BysjScoreInfo bysjScoreInfo) {
		bysjScoreInfoService.save(bysjScoreInfo);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bysjScoreInfo
	 * @return
	 */
	@AutoLog(value = "bysj_score_info-编辑")
	@ApiOperation(value="bysj_score_info-编辑", notes="bysj_score_info-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BysjScoreInfo bysjScoreInfo) {

		if(bysjScoreInfo.getScore() != null) {
			bysjScoreInfoService.updateById(bysjScoreInfo);
		}else{
			bysjScoreInfoService.emptyScoreById(bysjScoreInfo.getId());
		}
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bysj_score_info-通过id删除")
	@ApiOperation(value="bysj_score_info-通过id删除", notes="bysj_score_info-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bysjScoreInfoService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bysj_score_info-批量删除")
	@ApiOperation(value="bysj_score_info-批量删除", notes="bysj_score_info-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bysjScoreInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bysj_score_info-通过id查询")
	@ApiOperation(value="bysj_score_info-通过id查询", notes="bysj_score_info-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BysjScoreInfo bysjScoreInfo = bysjScoreInfoService.getById(id);
		if(bysjScoreInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(bysjScoreInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bysjScoreInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BysjScoreInfo bysjScoreInfo) {
        return super.exportXls(request, bysjScoreInfo, BysjScoreInfo.class, "bysj_score_info");
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
        return super.importExcel(request, response, BysjScoreInfo.class);
    }

}
