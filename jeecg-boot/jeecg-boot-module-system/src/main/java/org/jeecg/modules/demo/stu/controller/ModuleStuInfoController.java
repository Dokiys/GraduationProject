package org.jeecg.modules.demo.stu.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.stu.entity.ModuleStuInfo;
import org.jeecg.modules.demo.stu.service.IModuleStuInfoService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: module_stu_info
 * @Author: jeecg-boot
 * @Date:   2020-04-22
 * @Version: V1.0
 */
@Api(tags="module_stu_info")
@RestController
@RequestMapping("/stu/moduleStuInfo")
@Slf4j
public class ModuleStuInfoController extends JeecgController<ModuleStuInfo, IModuleStuInfoService> {
	@Autowired
	private IModuleStuInfoService moduleStuInfoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param moduleStuInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "module_stu_info-分页列表查询")
	@ApiOperation(value="module_stu_info-分页列表查询", notes="module_stu_info-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ModuleStuInfo moduleStuInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ModuleStuInfo> queryWrapper = QueryGenerator.initQueryWrapper(moduleStuInfo, req.getParameterMap());
		Page<ModuleStuInfo> page = new Page<ModuleStuInfo>(pageNo, pageSize);
		IPage<ModuleStuInfo> pageList = moduleStuInfoService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param moduleStuInfo
	 * @return
	 */
	@AutoLog(value = "module_stu_info-添加")
	@ApiOperation(value="module_stu_info-添加", notes="module_stu_info-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ModuleStuInfo moduleStuInfo) {
		moduleStuInfoService.save(moduleStuInfo);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param moduleStuInfo
	 * @return
	 */
	@AutoLog(value = "module_stu_info-编辑")
	@ApiOperation(value="module_stu_info-编辑", notes="module_stu_info-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ModuleStuInfo moduleStuInfo) {
		moduleStuInfoService.updateById(moduleStuInfo);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "module_stu_info-通过id删除")
	@ApiOperation(value="module_stu_info-通过id删除", notes="module_stu_info-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		moduleStuInfoService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "module_stu_info-批量删除")
	@ApiOperation(value="module_stu_info-批量删除", notes="module_stu_info-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.moduleStuInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "module_stu_info-通过id查询")
	@ApiOperation(value="module_stu_info-通过id查询", notes="module_stu_info-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ModuleStuInfo moduleStuInfo = moduleStuInfoService.getById(id);
		if(moduleStuInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(moduleStuInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param moduleStuInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ModuleStuInfo moduleStuInfo) {
        return super.exportXls(request, moduleStuInfo, ModuleStuInfo.class, "module_stu_info");
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
        return super.importExcel(request, response, ModuleStuInfo.class);
    }

}
