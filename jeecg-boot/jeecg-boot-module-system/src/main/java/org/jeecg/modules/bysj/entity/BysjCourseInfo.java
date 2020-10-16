package org.jeecg.modules.bysj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: bysj_course_info
 * @Author: jeecg-boot
 * @Date:   2020-04-27
 * @Version: V1.0
 */
@Data
@TableName("bysj_course_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bysj_course_info对象", description="bysj_course_info")
public class BysjCourseInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;
	/**课程名称*/
	@Excel(name = "课程名称", width = 15)
    @ApiModelProperty(value = "课程名称")
    private String courseName;
	/**课程类型*/
	@Excel(name = "课程类型", width = 15)
    @ApiModelProperty(value = "课程类型")
    @Dict(dicCode = "course_type")
    private Integer courseType;
	/**授课教师id*/
	@Excel(name = "授课教师id", width = 15)
    @ApiModelProperty(value = "授课教师id")
    private String courseTeacherid;
    /**授课教师姓名*/
    @Excel(name = "授课教师姓名", width = 15)
    @TableField(exist = false)
    @JsonInclude
    private String courseTeacherName;
	/**学分*/
	@Excel(name = "学分", width = 15)
    @ApiModelProperty(value = "学分")
    private String courseScore;
    /**授课教师姓名*/
    @Excel(name = "成绩", width = 15)
    @TableField(exist = false)
    @JsonInclude
    private Integer score;
	/**所属院校*/
	@Excel(name = "所属院校", width = 15)
    @ApiModelProperty(value = "所属院校")
    private String departId;
	/**开课时间*/
	@Excel(name = "开课时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开课时间")
    private Date startTime;
	/**结课时间*/
	@Excel(name = "结课时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结课时间")
    private Date endTime;
	/**课程状态*/
	@Excel(name = "课程状态", width = 15)
    @ApiModelProperty(value = "课程状态")
    @Dict(dicCode = "course_status")
    private Integer status;
	/**创建人登录名称*/
	@Excel(name = "创建人登录名称", width = 15)
    @ApiModelProperty(value = "创建人登录名称")
    private String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人登录名称*/
	@Excel(name = "更新人登录名称", width = 15)
    @ApiModelProperty(value = "更新人登录名称")
    private String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
}
