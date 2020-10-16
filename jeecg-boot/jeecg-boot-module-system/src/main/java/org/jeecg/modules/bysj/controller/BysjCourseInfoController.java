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
import org.jeecg.modules.bysj.entity.BysjCourseArrangeVO;
import org.jeecg.modules.bysj.entity.BysjCourseInfo;
import org.jeecg.modules.bysj.service.IBysjCourseArrangeService;
import org.jeecg.modules.bysj.service.IBysjCourseInfoService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserDepartService;
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
 * @Description: bysj_course_info
 * @Author: jeecg-boot
 * @Date:   2020-04-27
 * @Version: V1.0
 */
@Api(tags="bysj_course_info")
@RestController
@RequestMapping("/bysj/bysjCourseInfo")
@Slf4j
public class BysjCourseInfoController extends JeecgController<BysjCourseInfo, IBysjCourseInfoService> {
	@Autowired
	private IBysjCourseInfoService bysjCourseInfoService;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private ISysUserRoleService sysUserRoleService;
	@Autowired
	private ISysUserDepartService sysUserDepartService;
	@Autowired
	private IBysjCourseArrangeService bysjCourseArrangeService;




	/**
	 * 分页列表查询
	 *
	 * @param bysjCourseInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bysj_course_info-分页列表查询")
	@ApiOperation(value="bysj_course_info-分页列表查询", notes="bysj_course_info-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BysjCourseInfo bysjCourseInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//设置查询范围为当前登录用户的子部门
		//获取当前用户id
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		//查询当前登录用户子部门id
		List<SysDepart> sysDeparts = sysDepartService.queryUserDeparts(user.getId());
		String departIds = sysDeparts.stream().map(SysDepart::getId).collect(Collectors.joining(","));
		List<String> subDepIdList = sysDepartService.getMySubDepIdsByDepId(departIds);
		String subDepIds = subDepIdList.stream().collect(Collectors.joining(","));
		//设置查询条件
		bysjCourseInfo.setDepartId(subDepIds);

		QueryWrapper<BysjCourseInfo> queryWrapper = QueryGenerator.initQueryWrapper(bysjCourseInfo, req.getParameterMap());
		Page<BysjCourseInfo> page = new Page<BysjCourseInfo>(pageNo, pageSize);
		IPage<BysjCourseInfo> pageList = bysjCourseInfoService.page(page, queryWrapper);
		pageList.getRecords().forEach(item -> {
			//设置教师姓名
			String teacherName = sysUserService.getById(item.getCourseTeacherid()).getRealname();
			item.setCourseTeacherName(teacherName);

			//设置所属院系名
			String departName = sysDepartService.getById(item.getDepartId()).getDepartName();
			item.setDepartId(departName);
		});
		return Result.ok(pageList);
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
	 @GetMapping(value = "/myCourseList")
	 public Result<?> queryMyCoursePageList(BysjCourseInfo bysjCourseInfo,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									HttpServletRequest req) {
		 Page<BysjCourseInfo> page = new Page<BysjCourseInfo>(pageNo, pageSize);
		 List<BysjCourseArrangeVO> courseArrangeList = new ArrayList<>();

		 //判断当前登录用户角色
		 LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 QueryWrapper<SysUserRole> surQueryWrapper = new QueryWrapper<>();
		 surQueryWrapper.eq("user_id",user.getId());
		 List<SysUserRole> surList = sysUserRoleService.list(surQueryWrapper);
		 List<String> collect = surList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

		 //如果是学生
		 if(collect.indexOf(BysjConst.SUR_STUDENT_CODE) != -1){
			 //通过学生id查询所属班级，再在排课表中查询所有该班级的课程并返回
			 List<String> courseIds = bysjCourseInfoService.queryCourseIdsByStudentId(user.getId());
			 QueryWrapper<BysjCourseInfo> bciQueryWrapper = QueryGenerator.initQueryWrapper(bysjCourseInfo, req.getParameterMap());
			 bciQueryWrapper.in("id",courseIds);
			 IPage<BysjCourseInfo> pageList = bysjCourseInfoService.page(page, bciQueryWrapper);
			 pageList.getRecords().forEach(item -> {
				 //设置教师姓名
				 String teacherName = sysUserService.getById(item.getCourseTeacherid()).getRealname();
				 item.setCourseTeacherName(teacherName);

				 //设置所属院系名
				 String departName = sysDepartService.getById(item.getDepartId()).getDepartName();
				 item.setDepartId(departName);
			 });

			 return Result.ok(pageList);
		 }
		 //如果是老师
		 else if(collect.indexOf(BysjConst.SUR_MASTERT_CODE) != -1 || collect.indexOf(BysjConst.SUR_TEACHER_CODE) != -1){
			 //TODO 通过教师id查询教授课程。（注意排序）querywrapper可查询。
			 bysjCourseInfo.setCourseTeacherid(user.getId());
			 QueryWrapper<BysjCourseInfo> bciQueryWrapper = QueryGenerator.initQueryWrapper(bysjCourseInfo, req.getParameterMap());
			 IPage<BysjCourseInfo> pageList = bysjCourseInfoService.page(page,bciQueryWrapper);
			 pageList.getRecords().forEach(item -> {
				 //设置教师姓名
				 String teacherName = sysUserService.getById(item.getCourseTeacherid()).getRealname();
				 item.setCourseTeacherName(teacherName);

				 //设置所属院系名
				 String departName = sysDepartService.getById(item.getDepartId()).getDepartName();
				 item.setDepartId(departName);
			 });

			 return Result.ok(pageList);

		 }
		 return Result.ok(null);
	 }

	@AutoLog(value = "bysj_course_info-分页列表查询")
	@ApiOperation(value="bysj_course_info-分页列表查询", notes="bysj_course_info-分页列表查询")
	@GetMapping(value = "/unArrangeList")
	public Result<?> queryPageunArrangeList(BysjCourseInfo bysjCourseInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//设置查询范围为当前登录用户的子部门
		//获取当前用户id
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		//查询当前登录用户子部门id
		List<SysDepart> sysDeparts = sysDepartService.queryUserDeparts(user.getId());
		String departIds = sysDeparts.stream().map(SysDepart::getId).collect(Collectors.joining(","));
		List<String> subDepIdList = sysDepartService.getMySubDepIdsByDepId(departIds);
		String subDepIds = subDepIdList.stream().collect(Collectors.joining(","));
		//设置查询条件
		bysjCourseInfo.setDepartId(subDepIds);
		bysjCourseInfo.setStatus(0);
		QueryWrapper<BysjCourseInfo> queryWrapper = QueryGenerator.initQueryWrapper(bysjCourseInfo, req.getParameterMap());
		Page<BysjCourseInfo> page = new Page<BysjCourseInfo>(pageNo, pageSize);
		IPage<BysjCourseInfo> pageList = bysjCourseInfoService.page(page, queryWrapper);
		pageList.getRecords().forEach(item -> {
			//设置教师姓名
			String teacherName = sysUserService.getById(item.getCourseTeacherid()).getRealname();
			item.setCourseTeacherName(teacherName);

			//设置所属院系名
			String departName = sysDepartService.getById(item.getDepartId()).getDepartName();
			item.setDepartId(departName);
		});
		return Result.ok(pageList);
	}
	/**
	 *   添加
	 *
	 * @param bysjCourseInfo
	 * @return
	 */
	@AutoLog(value = "bysj_course_info-添加")
	@ApiOperation(value="bysj_course_info-添加", notes="bysj_course_info-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BysjCourseInfo bysjCourseInfo) {
		bysjCourseInfoService.save(bysjCourseInfo);
		return Result.ok("添加成功！");
	}

	 /**
	  *  提交成绩
	  *
	  * @return
	  */
	 @AutoLog(value = "bysj_course_info-提交成绩")
	 @ApiOperation(value="bysj_course_info-提交成绩", notes="bysj_course_info-提交成绩")
	 @PutMapping(value = "/submitCourse")
	 public Result<?> submitCourse(@RequestBody BysjCourseInfo bysjCourseInfo) {
//		 UpdateWrapper<BysjCourseInfo> updateWrapper = new UpdateWrapper<>();
//		 updateWrapper.set("status",5).eq("id",id);
		 bysjCourseInfo.setStatus(5);
		 boolean result = bysjCourseInfoService.updateById(bysjCourseInfo);
		 if (result){
			 return Result.ok("提交成功!");
		 }
		 return Result.error("提交失败!");
	 }
	
	/**
	 *  编辑
	 *
	 * @param bysjCourseInfo
	 * @return
	 */
	@AutoLog(value = "bysj_course_info-编辑")
	@ApiOperation(value="bysj_course_info-编辑", notes="bysj_course_info-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BysjCourseInfo bysjCourseInfo) {
		bysjCourseInfoService.updateById(bysjCourseInfo);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bysj_course_info-通过id删除")
	@ApiOperation(value="bysj_course_info-通过id删除", notes="bysj_course_info-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bysjCourseInfoService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bysj_course_info-批量删除")
	@ApiOperation(value="bysj_course_info-批量删除", notes="bysj_course_info-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bysjCourseInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bysj_course_info-通过id查询")
	@ApiOperation(value="bysj_course_info-通过id查询", notes="bysj_course_info-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BysjCourseInfo bysjCourseInfo = bysjCourseInfoService.getById(id);
		if(bysjCourseInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(bysjCourseInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bysjCourseInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BysjCourseInfo bysjCourseInfo) {
        return super.exportXls(request, bysjCourseInfo, BysjCourseInfo.class, "bysj_course_info");
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
        return super.importExcel(request, response, BysjCourseInfo.class);
    }

}
