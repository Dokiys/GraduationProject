<template>
  <a-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    cancelText="关闭">


    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="10" :sm="8">
            <a-form-item label="学生账号">
              <a-input placeholder="输入学生账号查询" v-model="queryParam.studentId"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>
        
      </a-form>
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
        
        @change="handleTableChange">

      <span slot="score" slot-scope="text,record">
        <myEditableCell-modal :text="text" :isIconShow="record.status == 3" @change="onCellChange(record,arguments)" />
      </span>

      </a-table>

    <template slot="footer">
      <div>
        <a-button key="back" @click="handleCancel">返回</a-button>
        <a-popconfirm title="提交后不可更改，确定提交吗?" @confirm="handleSubmit" v-if="isSubmitable && course.status == 3">
          <a-button key="submit" type="primary" :loading="submitLoading">
            提交
          </a-button>
        </a-popconfirm>
      </div>
    </template>
  </a-modal>
</template>

<script>

  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import MyEditableCellModal from '@/views/bysj/components/MyEditableCellModal'

  export default {
    name: "BysjMarkModal",
    components: { 
      MyEditableCellModal
    },
    props:{
      course: {
        type: Object,
        default: null,
        required: true,
      }
    },
    data () {
      return {
        title:"评分",
        width:800,
        visible: false,
        model: {},
        confirmLoading: false,
        submitLoading: false,
        isSubmitable: false,
        span: 11,
        labelCol: {
          xs: { span: 8 },
          sm: { span: 8 },
        },
        wrapperCol: {
          xs: { span: 16 },
          sm: { span: 16 },
        },
    //table参数
        /* 筛选参数 */
        filters: {},  
        /* 查询条件-请不要在queryParam中声明非字符串值的属性 */
        queryParam: {},
        /* 数据源 */
        dataSource:[],
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        /* table加载状态 */
        loading:false,
        // 表头
        columns: [
          {
            title:'姓名',
            align:"center",
            dataIndex: 'studentName'
          },
          {
            title:'课程名称',
            align:"center",
            dataIndex: 'courseName'
          },
          {
            title:'课程类型',
            align:"center",
            dataIndex: 'courseType_dictText'
          },
          {
            title:'学分',
            align:"center",
            dataIndex: 'courseScore'
          },
          {
            title:'所属院系',
            align:"center",
            dataIndex: 'departName'
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
          },
          {
            title: '成绩',
            dataIndex: 'score',
            align:"center",
            scopedSlots: { customRender: 'score' }
          },
        ],
        url: {
          list: "/bysj/bysjScoreInfo/markList",
          edit: "/bysj/bysjScoreInfo/edit",
          noScoreCount: "/bysj/bysjScoreInfo/noScoreCount",
          submitCourse: "/bysj/bysjCourseInfo/submitCourse"
        }
      }
    },
    created () {
      this.loadData();
      this.verifyIsSubmitable();
    },
    methods: {
  //成绩列表相关方法
      loadData(arg) {
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件
        params.courseId = this.course.id;
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      getQueryParams() {
        //获取查询条件
        let sqp = {}
        var param = Object.assign(sqp, this.queryParam, this.isorter,this.filters);
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return param;
      },
      getQueryField() {
        //TODO 字段权限控制
        var str = "id,";
        this.columns.forEach(function (value) {
          str += "," + value.dataIndex;
        });
        return str;
      },
      handleTableChange(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
        }
        this.ipagination = pagination;
        this.loadData();
      },
      searchQuery() {
        this.loadData(1);
      },
      searchReset() {
        this.queryParam = {}
        this.loadData(1);
      },
  //成绩编辑相关方法
      close () {
        this.visible = false;
      },
      onCellChange() {
        //发送保存成绩请求
        let params = {};
        let httpurl = '';
        let method = 'put';
        let record = arguments[0];

        params.id = record.id;
        params.score = arguments[1][0];
        params.studentId = record.studentId;
        params.courseId = record.courseId;
        
        this.submitLoading = true;
        httpAction(this.url.edit,params,"put").then((res)=>{
          if(res.success){
            this.verifyIsSubmitable();
          }else{
            this.$message.warning(res.message);
          }
        }).finally(() => {
          this.$nextTick(() =>{
            this.submitLoading = false;
          })
        })
      },
      verifyIsSubmitable(){
        getAction(this.url.noScoreCount, {courseId:this.course.id}).then((res) => {
          if (res.success && res.result == 0) {
            this.isSubmitable = true;
          }else{
            this.isSubmitable = false;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
        })
      },
      handleSubmit () {
        console.log(this.course);
        //发送请求修改课程状态
        httpAction(this.url.submitCourse,{id:this.course.id},"put").then((res)=>{
          if(res.success){
            this.$emit('submit');
            this.loadData();
            this.$message.success(res.message);
          }else{
            this.$message.warning(res.message);
          }
        }).finally(() => {
          this.close();
        })
      },
      handleCancel () {
        this.close();
      },
    }
  }
</script>