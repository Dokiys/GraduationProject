package org.jeecg.modules.bysj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @Description: bysj_course_arrange
 * @Author: jeecg-boot
 * @Date:   2020-05-01
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bysj_course_arrange对象", description="bysj_course_arrange")
public class BysjCourseArrangeVO implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;
    /**班级id*/
    @Excel(name = "班级id", width = 15)
    @ApiModelProperty(value = "班级id")
    private String classId;
    /**课程id*/
    @Excel(name = "课程id", width = 15)
    @ApiModelProperty(value = "课程id")
    private String courseId;
    /**教室id*/
    @Excel(name = "教室id", width = 15)
    @ApiModelProperty(value = "教室id")
    private String classroomId;
    /**班级名称*/
    @Excel(name = "班级名称", width = 15)
    @TableField(exist = false)
    @JsonInclude
    private String className;
    /**课程名称*/
    @Excel(name = "课程名称", width = 15)
    @TableField(exist = false)
    @JsonInclude
    private String courseName;
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
    /**教室名称*/
    @Excel(name = "教室名称", width = 15)
    @TableField(exist = false)
    @JsonInclude
    private String classroomName;
	/**课表开始时间*/
	@Excel(name = "课表开始时间", width = 15)
    @ApiModelProperty(value = "课表开始时间")
    private String timetableStartCode;
	/**课表结束时间*/
	@Excel(name = "课表结束时间", width = 15)
    @ApiModelProperty(value = "课表结束时间")
    private String timetableEndCode;
    @Excel(name = "课程状态", width = 15)
    @TableField(exist = false)
    @Dict(dicCode = "course_status")
    private String status;
}
