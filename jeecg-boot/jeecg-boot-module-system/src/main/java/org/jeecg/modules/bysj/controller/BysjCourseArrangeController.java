package org.jeecg.modules.bysj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.bysj.common.consts.BysjConst;
import org.jeecg.modules.bysj.entity.BysjCourseArrange;
import org.jeecg.modules.bysj.entity.BysjCourseArrangeVO;
import org.jeecg.modules.bysj.entity.BysjCourseInfo;
import org.jeecg.modules.bysj.service.IBysjClassroomInfoService;
import org.jeecg.modules.bysj.service.IBysjCourseArrangeService;
import org.jeecg.modules.bysj.service.IBysjCourseInfoService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUserDepart;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserDepartService;
import org.jeecg.modules.system.service.ISysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: bysj_course_arrange
 * @Author: jeecg-boot
 * @Date:   2020-05-01
 * @Version: V1.0
 */
@Api(tags="bysj_course_arrange")
@RestController
@RequestMapping("/bysj/bysjCourseArrange")
@Slf4j
public class BysjCourseArrangeController extends JeecgController<BysjCourseArrange, IBysjCourseArrangeService> {
	@Autowired
	private IBysjCourseArrangeService bysjCourseArrangeService;
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private IBysjClassroomInfoService bysjClassroomInfoService;
	@Autowired
	private IBysjCourseInfoService bysjCourseInfoService;
	@Autowired
	private ISysUserRoleService sysUserRoleService;
	@Autowired
	private ISysUserDepartService sysUserDepartService;
	/**
	 * 分页列表查询
	 *
	 * @param bysjCourseArrange
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bysj_course_arrange-分页列表查询")
	@ApiOperation(value="bysj_course_arrange-分页列表查询", notes="bysj_course_arrange-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BysjCourseArrange bysjCourseArrange,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<BysjCourseArrangeVO> page = new Page<BysjCourseArrangeVO>(pageNo, pageSize);
		List<String> subDepIdList = new ArrayList<>();
		if(StringUtils.isEmpty(bysjCourseArrange.getClassId())){
			//获取当前用户id
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			//查询当前登录用户子部门id
			List<SysDepart> sysDeparts = sysDepartService.queryUserDeparts(user.getId());
			String departIds = sysDeparts.stream().map(SysDepart::getId).collect(Collectors.joining(","));
			subDepIdList = sysDepartService.getMySubDepIdsByDepId(departIds);
		}else {
			//查询选择部门的子部门id
			subDepIdList = sysDepartService.getMySubDepIdsByDepId(bysjCourseArrange.getClassId());
		}
		IPage<BysjCourseArrangeVO> pageList = bysjCourseArrangeService.pageList(page, bysjCourseArrange,subDepIdList);
		return Result.ok(pageList);
	}

	/**
	 * 已选择课表查询
	 *
	 * @param date
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bysj_course_arrange-已选择课表查询")
	@ApiOperation(value="bysj_course_arrange-已选择课表查询", notes="bysj_course_arrange-已选择课表查询")
	@GetMapping(value = "/schedule")
	public Result<?> querySchedule( @RequestParam(name="date",defaultValue="") String date,
											HttpServletRequest req) throws ParseException {
		List<BysjCourseArrangeVO> courseArrangeList = new ArrayList<>();
		//判断当前登录用户角色
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		QueryWrapper<SysUserRole> surQueryWrapper = new QueryWrapper<>();
		surQueryWrapper.eq("user_id",user.getId());
		List<SysUserRole> surList = sysUserRoleService.list(surQueryWrapper);
		List<String> collect = surList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
		//如果是学生
		if(collect.indexOf(BysjConst.SUR_STUDENT_CODE) != -1){
			//则查询所属班级，班级id到排课表中查询结果
			QueryWrapper<SysUserDepart> sudQueryWrapper = new QueryWrapper<>();
			sudQueryWrapper.eq("user_id",user.getId());
			List<SysUserDepart> sudList = sysUserDepartService.list(sudQueryWrapper);
			List<String> classIds = sudList.stream().map(SysUserDepart::getDepId).collect(Collectors.toList());
			courseArrangeList = bysjCourseArrangeService.queryScheduleByClass(classIds,date);
		}
		//如果是老师
		if(collect.indexOf(BysjConst.SUR_MASTERT_CODE) != -1 || collect.indexOf(BysjConst.SUR_TEACHER_CODE) != -1){
			//则在课程表中查询课程id，然后再排课表中查询结果
			QueryWrapper<BysjCourseInfo> bciQueryWrapper = new QueryWrapper<>();
			bciQueryWrapper.eq("course_teacherId",user.getId());
			List<BysjCourseInfo> bciList = bysjCourseInfoService.list(bciQueryWrapper);
			List<String> courseIds = bciList.stream().map(BysjCourseInfo::getId).collect(Collectors.toList());
			courseArrangeList = bysjCourseArrangeService.queryScheduleByCourse(courseIds,date);
			bysjCourseInfoService.list();
		}
		return Result.ok(courseArrangeList);
	}

	/**
	 *   查询当前选择的课程是否可以更改课表
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bysj_course_arrange-查询当前选择的课程是否可以更改课表")
	@ApiOperation(value="bysj_course_arrange-查询当前选择的课程是否可以更改课表", notes="bysj_course_arrange-查询当前选择的课程是否可以更改课表")
	@GetMapping(value = "/confirmIsSelecteble")
	public Result<?> confirmIsSelecteble(@RequestParam(name="id",required=true) String id) {
		Boolean result = bysjCourseArrangeService.confirmIsSelecteble(id);
		return Result.ok(result);
	}

	/**
	 * 已选择课表查询
	 *
	 * @param departIds
	 * @param req
	 * @return
	 */
	@AutoLog(value = "bysj_course_arrange-班级已选择课表查询")
	@ApiOperation(value="bysj_course_arrange-班级已选择课表查询", notes="bysj_course_arrange-班级已选择课表查询")
	@GetMapping(value = "/scheduleSelected")
	public Result<?> queryScheduleSelected( @RequestParam(name="departIds") String departIds,
                                            @RequestParam(name="courseId") String courseId,
								            @RequestParam(name="timetableStartCode",defaultValue="") String timetableStartCode,
                                            @RequestParam(name="timetableEndCode",defaultValue="") String timetableEndCode,
								            HttpServletRequest req) {
		List<String> departIdList = Arrays.asList(departIds.split(","));
		List<BysjCourseArrangeVO> courseArrangeList = bysjCourseArrangeService.queryScheduleSelected(departIdList,courseId);
        if(!"".equals(timetableStartCode) && !"".equals(timetableEndCode)){
			courseArrangeList.removeIf(item ->
					(item.getCourseId().equals(courseId) &&
					item.getTimetableStartCode().equals(timetableStartCode) &&
					item.getTimetableEndCode().equals(timetableEndCode))
			);
        }
        return Result.ok(courseArrangeList);
	}

	/**
	 *   添加
	 *
	 * @param bysjCourseArrange
	 * @return
	 */
	@AutoLog(value = "bysj_course_arrange-添加")
	@ApiOperation(value="bysj_course_arrange-添加", notes="bysj_course_arrange-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BysjCourseArrange bysjCourseArrange) {
		String[] split = bysjCourseArrange.getClassId().split(",");
		BysjCourseArrange item = bysjCourseArrange;
		for (String classId:split ) {
			item.setId("");
			item.setClassId(classId);
			bysjCourseArrangeService.save(item);
		}
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param bysjCourseArrange
	 * @return
	 */
	@AutoLog(value = "bysj_course_arrange-编辑")
	@ApiOperation(value="bysj_course_arrange-编辑", notes="bysj_course_arrange-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BysjCourseArrange bysjCourseArrange) {
		bysjCourseArrangeService.updateById(bysjCourseArrange);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bysj_course_arrange-通过id删除")
	@ApiOperation(value="bysj_course_arrange-通过id删除", notes="bysj_course_arrange-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bysjCourseArrangeService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bysj_course_arrange-批量删除")
	@ApiOperation(value="bysj_course_arrange-批量删除", notes="bysj_course_arrange-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bysjCourseArrangeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bysj_course_arrange-通过id查询")
	@ApiOperation(value="bysj_course_arrange-通过id查询", notes="bysj_course_arrange-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BysjCourseArrange bysjCourseArrange = bysjCourseArrangeService.getById(id);
		if(bysjCourseArrange==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(bysjCourseArrange);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param bysjCourseArrange
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BysjCourseArrange bysjCourseArrange) {
        return super.exportXls(request, bysjCourseArrange, BysjCourseArrange.class, "bysj_course_arrange");
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
        return super.importExcel(request, response, BysjCourseArrange.class);
    }

}
