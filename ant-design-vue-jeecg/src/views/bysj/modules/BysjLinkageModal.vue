<template>
  <a-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-row :gutter="24" type="flex" justify="start">
          <a-col :span="24">
            <a-form-item label="选择班级" :labelCol="{  xs: { span: 12 }, sm: { span: 3 }, }" :wrapperCol="wrapperCol">
              <a-input-search
                placeholder="点击右侧按钮选择班级"
                v-decorator="[ 'className', validatorRules.className]"
                disabled
                @search="onSearchClass">
                <a-button slot="enterButton" icon="search" :disabled="!isAdd || !isEditable">选择</a-button>
              </a-input-search>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24" type="flex" justify="start" >
          <a-col :span="24">
            <a-form-item label="所选课程名" :labelCol="{  xs: { span: 12 }, sm: { span: 3 }, }" :wrapperCol="wrapperCol">
              <a-input
                placeholder="点击左下侧表格选择课程" 
                v-decorator="[ 'courseName', validatorRules.courseName]"
                disabled>
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24" type="flex" justify="start" v-show="isCouseTimeSelectVisible">
          <a-col :span="24">
            <a-form-item label="选择上课时间" :labelCol="{  xs: { span: 12 }, sm: { span: 3 }, }" :wrapperCol="wrapperCol">
              <a-input-search
                placeholder="点击右侧按钮选择上课时间"
                v-decorator="[ 'scheduleTimeStr', validatorRules.scheduleTimeStr]"
                disabled
                @search="onSearchSchedule">
                <a-button slot="enterButton" icon="search" :disabled="!isEditable || isScheduleSelecteble">选择</a-button>
              </a-input-search>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24" type="flex" justify="start">
          <a-col :span="24">
            <a-form-item label="所选教室" :labelCol="{  xs: { span: 12 }, sm: { span: 3 }, }" :wrapperCol="wrapperCol">
              <a-input
                placeholder="点击右下侧表格选择教室" 
                v-decorator="[ 'classroomName', validatorRules.classroomName]"
                disabled>
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider />

        <!-- 联动框 -->
        <a-row :gutter="10" v-if="isEditable">
          <a-col :md="16" :sm="24" style="margin-bottom: 20px">
            <a-card :bordered="false">
              <!-- 查询区域 -->
              <div class="table-page-search-wrapper">
                <a-form layout="inline" @keyup.enter.native="courSearchQuery">
                  <a-row :gutter="24">

                    <a-col :md="8" :sm="8">
                      <a-form-item label="课程名称">
                        <j-input placeholder="输入课程名称模糊查询" v-model="queryParam.courseName"></j-input>
                      </a-form-item>
                    </a-col>

                    <a-col :md="8" :sm="8">
                      <a-form-item label="课程类型">
                        <j-dict-select-tag placeholder="请选择课程类型" v-model="queryParam.courseType"
                          dictCode="course_type"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :md="6" :sm="8">
                      <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                        <a-button type="primary" @click="courSearchQuery" icon="search">查询</a-button>
                        <a-button type="primary" @click="courseRearchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                      </span>
                    </a-col>
                  </a-row>
                  
                </a-form>
              </div>
              <div>
                <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
                  <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ courSelectedRowKeys.length }}</a>项
                  <a style="margin-left: 24px" @click="onCourseClearSelected">清空</a>
                </div>

                <a-table
                  ref="table"
                  size="middle"
                  bordered
                  rowKey="id"
                  :columns="columns"
                  :dataSource="dataSource"
                  :pagination="ipagination"
                  :loading="loading"
                  :rowSelection="{fixed:true,selectedRowKeys: courSelectedRowKeys,onChange: onSelectChange,onSelect:onCourSelect,onSelectAll:onCourSelectAll}"
                  @change="handleTableChange">

                </a-table>
              </div>
            </a-card>
          </a-col>

          <a-col :md="8" :sm="24" style="margin-bottom: 20px">
            <a-card :bordered="false">
              <div>
                <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
                  <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ classroomSelectedRowKeys.length }}</a>项
                  <a style="margin-left: 24px" @click="onClassroomClearSelected">清空</a>
                </div>

                <a-table
                  ref="table"
                  size="middle"
                  bordered
                  rowKey="id"
                  :columns="classroomColumns"
                  :dataSource="classroomDataSource"
                  :pagination="classroomIpagination"
                  :loading="classroomLoading"
                  :rowSelection="{fixed:true,selectedRowKeys: classroomSelectedRowKeys,onChange: onSelectChange,onSelect:onClassroomSelect,onSelectAll:onClassroomSelectAll}"
                  @change="handleTableChange">

                </a-table>
              </div>
            </a-card>
          </a-col>
          
        </a-row>
       

      </a-form>
    </a-spin>

    <template slot="footer">
      <a-button key="back" @click="handleCancel"> 返回 </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleOk" v-show="isEditable"> 提交 </a-button>
    </template>

    <myClass-window ref="myClassWindow" @ok="handleClassSelectRes"/>
    <scheduleSelect-modal ref="scheduleSelectModal" :courseList="seletedScheduleList" @ok="handleScheduleSelectRes" />

  </a-modal>
</template>

<script>

  import { httpAction,getAction } from '@/api/manage'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { validateDuplicateValue } from '@/utils/util'
  import { filterObj } from '@/utils/util'
  import pick from 'lodash.pick'
  
  import JDate from '@/components/jeecg/JDate'  
  import JInput from '@/components/jeecg/JInput'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'

  import MyClassWindow from '@/views/bysj/components/MyClassWindow'
  import ScheduleSelectModal from '@/views/bysj/components/ScheduleSelectModal'


  export default {
    name: "BysjLinkageModal",
    mixins:[JeecgListMixin],
    components: { 
      JDate,
      JInput,
      JDictSelectTag,
      MyClassWindow,
      ScheduleSelectModal
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:"100%",
        span: 12,
        visible: false,
        model: {},
        labelCol: {  xs: { span: 12 }, sm: { span: 6 }, },
        wrapperCol: { xs: { span: 24 }, sm: { span: 18 }, },
        confirmLoading: false,
        isAdd:false,
        isEditable:true,
        //选择班级相关参数
        userId: "",
        selectedDepartKeys:[], //保存用户选择院系id
        checkedDepartKeys:[],
        checkedDepartNames:[], // 保存院系的名称 =>title
        checkedDepartNameString:"", // 保存院系的名称 =>title
        //选择课程相关参数
        selectedRowKeys: [],
        courSelectedRowKeys: [],
        loading: false,
        dataSource:[],
        queryParam:{},
        filters: {},
        isorter: {
          column: 'createTime',
          order: 'desc'
        },
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        columns:[
          {
            title: "选择课程",
            children:[
              {
                title:'课程名称',
                align:"center",
                dataIndex: 'courseName',
                customRender:function (text) {
                  return !text?"":(text.length>5?text.substr(0,10):text)
                }
              },
              {
                title:'课程类型',
                align:"center",
                dataIndex: 'courseType_dictText',
              },
              {
                title:'授课教师',
                align:"center",
                dataIndex: 'courseTeacherName'
              },
              {
                title:'学分',
                align:"center",
                dataIndex: 'courseScore'
              },
              {
                title:'所属院系',
                align:"center",
                dataIndex: 'departId',
                customRender:function (text) {
                  return !text?"":(text.length>5?text.substr(0,10):text)
                }
              },
              {
                title:'开课时间',
                align:"center",
                dataIndex: 'startTime',
                customRender:function (text) {
                  return !text?"":(text.length>10?text.substr(0,10):text)
                }
              },
              {
                title:'结课时间',
                align:"center",
                dataIndex: 'endTime',
                customRender:function (text) {
                  return !text?"":(text.length>10?text.substr(0,10):text)
                }
              }
            ]
          }
        ],
        //教室相关参数
        classroomSelectedRowKeys: [],
        classroomLoading: false,
        classroomDataSource:[],
        classroomQueryParam:{},
        classroomFilters: {},
        classroomIsorter: {
          column: 'createTime',
          order: 'desc'
        },
        classroomIpagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        classroomColumns: [
          {
            title: "选择教室",
            children: [
              {
                title:'教室名称',
                align:"center",
                dataIndex: 'classroomName'
              },
              {
                title:'可容纳人数',
                align:"center",
                dataIndex: 'holdNumber'
              }
            ]
          }
        ],
        //课表选择相关参数
        scheduleTimeStr:"",
        isCouseTimeSelectVisible:false,
        seletedScheduleList:[],
        isScheduleSelecteble:false,
        firstNode:"",
        secondeNode:"",
        validatorRules: {
          className: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          scheduleTimeStr: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          classroomName: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          courseName: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]}
        },
        url: {
          add: "/bysj/bysjCourseArrange/add",
          edit: "/bysj/bysjCourseArrange/edit",
          scheduleSelected:"/bysj/bysjCourseArrange/scheduleSelected",
          confirmIsSelecteble:"/bysj/bysjCourseArrange/confirmIsSelecteble",

          //课程相关url
          list: "/bysj/bysjCourseInfo/unArrangeList",

          //教室相关url
          classroomList: "/bysj/bysjClassroomInfo/arrangeList",

        }
      }
    },
    created () {
    },
    watch:{
      checkedDepartNameString: function(val){
        //只要选择的班级名称发生变化则清除课表和教室信息
        if(this.isCouseTimeSelectVisible)this.clearScheduleAndClassroom();
        //判断班级和课程是否已选
        if(val != "" && this.courSelectedRowKeys.length > 0){
          this.$nextTick(() => {this.isCouseTimeSelectVisible = true;})
          //已选则查询不可选择的课程列表并显示课表选择组件
          getAction(this.url.scheduleSelected,{departIds:this.model.classId
                                              ,courseId:this.model.courseId
                                              ,timetableStartCode:this.model.timetableStartCode
                                              ,timetableEndCode:this.model.timetableEndCode}).then((res)=>{
            //TODO
            this.seletedScheduleList = res.result;
          })
        }else{
          //没选则不显示课表选择组件
          this.$nextTick(() => {this.isCouseTimeSelectVisible = false;})
        }
      },
      courSelectedRowKeys: function(val){
        //只要选择的班级名称发生变化则清除课表和教室信息
        if(this.isCouseTimeSelectVisible)this.clearScheduleAndClassroom();
        //判断班级和课程是否已选
        if(val.length > 0 && this.checkedDepartNameString != ""){
          this.$nextTick(() => {this.isCouseTimeSelectVisible = true;})
          //已选则查询不可选择的课程列表并显示课表选择组件
          getAction(this.url.scheduleSelected,{departIds:this.model.classId
                                              ,courseId:this.model.courseId
                                              ,timetableStartCode:this.model.timetableStartCode
                                              ,timetableEndCode:this.model.timetableEndtCode}).then((res)=>{
            this.seletedScheduleList = res.result;
          })
        }else{
          //没选则不显示课表选择组件
          this.$nextTick(() => {this.isCouseTimeSelectVisible = false;})
        }
      }
    },
    methods: {
  //List页面相关代码
      add () {
        this.isAdd = true;
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        if(JSON.stringify(record) != "{}"){
          //给班级赋值
          this.checkedDepartNameString = record.className;
          this.$nextTick(() => {this.form.setFieldsValue({'className':record.className});})
          //给课程赋值
          this.courSelectedRowKeys[0] = record.courseId;
          this.$nextTick(() => {this.form.setFieldsValue({'courseName':record.courseName});})
          //给课表赋值
          let fArray = record.timetableStartCode.split("-");
          let sArray = record.timetableEndCode.split("-");
          this.scheduleTimeStr = "周"+fArray[0]+" "+"第"+fArray[1]+"节课——第"+sArray[1]+"节课";
          this.model.scheduleTimeStr = this.scheduleTimeStr;
          this.$nextTick(() => {
            this.form.setFieldsValue({'scheduleTimeStr':this.scheduleTimeStr});
          })
          //发送请求查看当前课表的时间可不可以修改
          getAction(this.url.confirmIsSelecteble,{id:record.id}).then((res) => {
            if (res.success) {
              console.log(res);
              this.isScheduleSelecteble = !res.result;
            }
          })
          //给教室选择框赋值
          this.classroomSelectedRowKeys[0] = record.classroomId; //加载教室
          this.$nextTick(() => {this.form.setFieldsValue({'classroomName':record.classroomName});})
          this.searchClassroomable();
          this.checkedDepartKeys[0] = record.classId;
          this.firstNode = record.timetableStartCode;
          this.secondeNode = record.timetableEndCode;
        }
      },
      close () {
        this.$emit('close');
        //班级选择清除
        this.selectedDepartKeys=[];
        this.checkedDepartKeys=[];
        this.checkedDepartNames=[];
        this.checkedDepartNameString = "";
        //课程选择清除
        this.onCourseClearSelected();
        //教室选择清除
        this.onClassroomClearSelected();
        //课表选择清除
        this.onScheduleClearSelected();

        this.isAdd = false;
        this.isCouseTimeSelectVisible = false;
        this.isEditable = true;
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let formData = {};
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
              method = 'put';
              formData.id = this.model.id;
            }
            formData.classId = this.model.classId;
            formData.courseId = this.model.courseId;
            formData.classroomId = this.model.classroomId;
            formData.timetableStartCode = this.model.timetableStartCode;
            formData.timetableEndCode = this.model.timetableEndCode;
            console.log(formData);
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            })
            
            // this.model.classId.split(',').forEach(item => {
            //   formData.classId = item;
            //   console.log(formData);
            //   // httpAction(httpurl,formData,method).then((res)=>{
            //   //   if(res.success){
            //   //     that.$message.success(res.message);
            //   //     that.$emit('ok');
            //   //   }else{
            //   //     that.$message.warning(res.message);
            //   //   }
            //   // })
            // })
            
            //TODO 暂时不提交
            // httpAction(httpurl,formData,method).then((res)=>{
            //   if(res.success){
            //     that.$message.success(res.message);
            //     that.$emit('ok');
            //   }else{
            //     that.$message.warning(res.message);
            //   }
            // }).finally(() => {
            //   that.confirmLoading = false;
            //   that.close();
            // })
            that.confirmLoading = false;
            that.close();
          }
         
        })
      },
      handleCancel () {
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'className','courseName','classroomName','timetableStartCode','timetableEndCode'))
      },
      //加载教室
      searchClassroomable(){
        //验证model中的四个选项是否已经选择，如果已经选择，则带上条件查询可用的classroom信息
        if(!!this.model.classId &&
           !!this.model.timetableStartCode &&
           !!this.model.timetableEndCode &&
           !!this.model.courseId){
             this.loadClassroomData(1)
           }
        //否则不做处理
      },
      clearScheduleAndClassroom(){
        //清除课表选择
        this.onScheduleClearSelected();
        //清除calssroom选择
        this.onClassroomClearSelected();
      },
  //查询班级所用方法
      onSearchClass(){
        this.$refs.myClassWindow.add(this.checkedDepartKeys,this.userId);
      },
      handleClassSelectRes(departData){
        this.checkedDepartNames = [];
        this.selectedDepartKeys = [];
        this.checkedDepartNameString = '';
        this.userId = departData.userId;
        this.departIds=[];
        this.resultDepartOptions=[];
        var depart=[];
        //获取所有班级的id
        for (let i = 0; i < departData.departIdList.length; i++) {
          if(this.$refs.myClassWindow.leafList.indexOf(departData.departIdList[i].key) != -1){
            this.selectedDepartKeys.push(departData.departIdList[i].key);
            this.checkedDepartNames.push(departData.departIdList[i].title);
            this.checkedDepartNameString = this.checkedDepartNames.join(",");
            //新增院系选择，如果上面院系选择后不为空直接付给负责院系
            depart.push({
                key:departData.departIdList[i].key,
                title:departData.departIdList[i].title
            })
            this.departIds.push(departData.departIdList[i].key)
          }
        }
        this.resultDepartOptions=depart;
        this.checkedDepartKeys = this.selectedDepartKeys  //更新当前的选择keys
        //将选择的班级名称填入输入框并将id添加到model
        this.$nextTick(() => {
          this.form.setFieldsValue({'className':this.checkedDepartNameString});
        })
        this.model.classId = this.departIds.join(",");
      },
  //查询教室所用方法
      loadClassroomData(arg) {
        if (!this.url.classroomList) {
          this.$message.error('请设置url.classroomList!')
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.classroomIpagination.current = 1
        }
        let params = this.getClassroomQueryParams()//查询条件
        //添加请求参数
        params.classId = this.model.classId;
        params.courseId = this.model.courseId;
        params.timetableStartCode = this.model.timetableStartCode;
        params.timetableEndCode = this.model.timetableEndCode;
        this.classroomLoading = true
        console.log(params);
        getAction(this.url.classroomList, params).then((res) => {
          if (res.success) {
            console.log(res.result);
            this.classroomDataSource = res.result.records
            this.classroomIpagination.total = res.result.total
          }
          this.classroomLoading = false
        })

      },
      getClassroomQueryParams(){
        //获取查询条件
        let sqp = {}
        if (this.superQueryParams2) {
          sqp['superQueryParams'] = encodeURI(this.superQueryParams2)
        }
        var param = Object.assign(sqp, this.classroomQueryParam, this.classroomIsorter, this.classroomFilters)
        param.field = this.getClassroomQueryField()
        param.pageNo = this.classroomIpagination.current
        param.pageSize = this.classroomIpagination.pageSize
        return filterObj(param)
      },
      getClassroomQueryField() {
        //字段权限控制
        var str = 'id,'
        this.classroomColumns.forEach(function(value) {
          str += ',' + value.dataIndex
        })
        return str
      },
      classroomRearchReset(){
        delete this.classroomQueryParam.holdNumber;
        delete this.classroomQueryParam.classroomName;
        this.loadClassroomData(1)
      },
      classroomSearchQuery(){
        this.loadClassroomData(1)
      },
      onClassroomSelect(record){
        if(!!record){
          this.classroomSelectedRowKeys = [];
          this.model.classroomId = record.id;
          this.classroomSelectedRowKeys[0] = record.id;
          this.form.setFieldsValue({'classroomName':record.classroomName})
        }else{
          this.onClassroomClearSelected();
        }
      },
      onClassroomSelectAll(){
        this.onClassroomClearSelected();
      },
      onClassroomClearSelected(){
        this.classroomDataSource = [];
        this.classroomSelectedRowKeys = [];
        delete this.model.classroomId;
        this.form.setFieldsValue({'classroomName':""})
        this.searchClassroomable();
      },
  //课程选择相关方法
      courseRearchReset(){
        delete this.queryParam.courseName;
        delete this.queryParam.courseType;
        this.loadData(1)
      },
      courSearchQuery(){
        this.loadData(1)
      },
      onCourSelect(record){
        if(!!record){
          this.courSelectedRowKeys = [];
          this.model.courseId = record.id;
          this.courSelectedRowKeys[0] = record.id;
          
          this.form.setFieldsValue({'courseName':record.courseName})
        }else{
          this.onCourseClearSelected();
        }
      },
      onCourSelectAll(){
        this.onCourseClearSelected();
      },
      onCourseClearSelected(){
        this.courSelectedRowKeys = [];
        this.isScheduleSelecteble = false;
        delete this.model.courseId;
       //将选择的班级名称填入输入框并将id添加到model
        this.$nextTick(() => {
          this.form.setFieldsValue({'courseName':""})
        })},
  //课表时间选择相关方法
      onSearchSchedule(){
        this.$refs.scheduleSelectModal.visible = true;
        this.$nextTick(() => {
            this.$refs.scheduleSelectModal.initSchedule(this.firstNode,this.secondeNode);
        })
      },
      handleScheduleSelectRes(firstNode,secondeNode){
        if(!!firstNode && !!secondeNode){
          let fArray = firstNode.split('-');
          let sArray = secondeNode.split('-');
          this.firstNode = firstNode;
          this.secondeNode = secondeNode;
          this.scheduleTimeStr = "周"+fArray[0]+" "+"第"+fArray[1]+"节课——第"+sArray[1]+"节课";
          this.$nextTick(() => {
            this.form.setFieldsValue({'scheduleTimeStr':this.scheduleTimeStr});
          })
          this.model.timetableStartCode = firstNode;
          this.model.timetableEndCode = secondeNode;
          this.searchClassroomable();
        }else if(!!firstNode){
          let fArray = firstNode.split('-');
          this.firstNode = firstNode;
          this.secondeNode = firstNode;
          this.scheduleTimeStr = "周"+fArray[0]+" "+"第"+fArray[1]+"节课";
          this.$nextTick(() => {
            this.form.setFieldsValue({'scheduleTimeStr':this.scheduleTimeStr});
          })
          this.model.timetableStartCode = firstNode;
          this.model.timetableEndCode = firstNode;
          this.searchClassroomable();
        }
        else{
          this.onClassroomClearSelected();
          this.onScheduleClearSelected();
        }
      },
      onScheduleClearSelected(){
        this.firstNode = "";
        this.secondeNode = "";
        this.seletedScheduleList = [];
        this.scheduleTimeStr = "";
        if(this.isCouseTimeSelectVisible){
          this.$refs.scheduleSelectModal.clearSelected();
          this.$nextTick(() => {
              this.form.setFieldsValue({'scheduleTimeStr':this.scheduleTimeStr});
          })
        }
        delete this.model.timetableStartCode;
        delete this.model.timetableEndCode;
      },
      clearCurrentSelect(list,firstNode,secondeNode,courseId){
        let i = -1;
        list.forEach((item,index) => {
          if(item.courseId == courseId && item.timetableStartCode == firstNode && item.timetableStartCode == secondeNode){
            i = index;
          }
        })
        if(i != -1) list.splice(i,1);
        return list;
      }
     
    }
  }
</script>