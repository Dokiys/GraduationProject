<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="10" :sm="8">
            <a-form-item label="学生账号">
              <a-input placeholder="输入学生账号查询" v-model="queryParam.studentId"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="8" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="handleSearchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="handleSearchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>

        <!-- 选择教师 -->
        <a-row :gutter="24" v-if="toggleSearchStatus">
          <a-col :md="16" :sm="8">
            <a-form-item label="授课教师" >
              <a-input-search
                placeholder="点击右侧按钮选择授课教师"
                v-model="selectTeacherName"
                disabled
                @search="onTeacherSearch">
                <a-button slot="enterButton" icon="search">选择</a-button>
              </a-input-search>
            </a-form-item>
          </a-col>
          
        </a-row>
        <!-- 选择班级 -->
        <a-row :gutter="24" v-if="toggleSearchStatus">
          <a-col :span="16">
            <a-form-item label="选择班级" >
              <a-input-search
                placeholder="点击右侧按钮选择班级"
                v-model="selectClassName"
                disabled
                @search="onClassSearch">
                <a-button slot="enterButton" icon="search">选择</a-button>
              </a-input-search>
            </a-form-item>
          </a-col>
        </a-row>
        <!-- 选择课程 -->
        <a-row :gutter="24" v-if="toggleSearchStatus">
          <a-col :md="16" :sm="10">
            <a-form-item label="选择课程" >
              <a-input-search
                placeholder="点击右侧按钮选择课程"
                v-model="selectCourseName"
                disabled
                @search="onCourseSearch">
                <a-button slot="enterButton" icon="search">选择</a-button>
              </a-input-search>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- table区域-begin -->
    <div>
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
          <myEditableCell-modal :text="text" 
                                :isIconShow="true" 
                                :isConfirmModal="true" 
                                @change="onCellChange(record,arguments)" />
        </span>

      </a-table>
    </div>

    <bysjScoreInfo-modal ref="modalForm" @ok="modalFormOk"></bysjScoreInfo-modal>
    <!-- 选择查询条件model -->
    <myClass-window ref="myClassWindow" @ok="handleClassSelectRes"/>
    <Select-User-Modal ref="selectUserModal" 
                       urlList="/sys/user/listOnlyTeacher" 
                       @selected="onTeacherSelected" 
                       @onSelectAll="onTeacherClearSelect"/>
    <courseSelect-modal ref="courseSelectModal" @onSelectRes="handleCourseSelectRes"></courseSelect-modal>

  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { httpAction } from '@/api/manage'
  import JInput from '@/components/jeecg/JInput'
  import BysjScoreInfoModal from './modules/BysjScoreInfoModal'
  import MyEditableCellModal from '@/views/bysj/components/MyEditableCellModal'

  import SelectUserModal from '@views/system/modules/SelectUserModal'
  import MyClassWindow from '@/views/bysj/components/MyClassWindow'
  import CourseSelectModal from '@/views/bysj/components/CourseSelectModal'


  export default {
    name: "BysjScoreInfoList",
    mixins:[JeecgListMixin],
    components: {
      BysjScoreInfoModal,
      MyEditableCellModal,
      JInput,
      SelectUserModal,
      MyClassWindow,
      CourseSelectModal
    },
    data () {
      return {
        description: 'bysj_score_info管理页面',
        userId: "",
        selectedDepartKeys:[], //保存用户选择院系id
        checkedDepartKeys:[],
        checkedDepartNames:[], 
        selectClassName:"", // 保存选择的班级名称
        selectCourseName: "", // 保存选择的课程名
        selectTeacherName: "", // 保存选择的教师姓名
        // 表头
        columns: [
          {
            title:'学号',
            align:"center",
            dataIndex: 'studentId'
          },
          {
            title:'学生姓名',
            align:"center",
            dataIndex: 'studentName'
          },
          {
            title:'班级',
            align:"center",
            dataIndex: 'className'
          },
          {
            title:'课程',
            align:"center",
            dataIndex: 'courseName'
          },
          {
            title:'授课教师',
            align:"center",
            dataIndex: 'courseTeacherName'
          },
          {
            title:'结课时间',
            align:"center",
            dataIndex: 'endTime'
          },
          {
            title:'成绩',
            align:"center",
            dataIndex: 'score',
            scopedSlots: { customRender: 'score' }
          }
        ],
        url: {
          list: "/bysj/bysjScoreInfo/list",
          edit: "/bysj/bysjScoreInfo/edit",
        },
        dictOptions:{},
      }
    },
    computed: {
    },
    methods: {
  //Table相关方法
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
            this.$message.success(res.message);
          }else{
            this.$message.warning(res.message);
          }
        })
      },
  //查询相关方法
      handleSearchQuery(arg){
        console.log(this.queryParam);
        this.loadData(1);
      },
      handleSearchReset(){
        this.onTeacherClearSelect();
        this.onClassClearSelected();
        this.onCourseClearSelected();
        this.queryParam = {};
        this.handleSearchQuery(1);
      },
    //教师相关方法
      onTeacherSearch(){
        this.$refs.selectUserModal.visible = true;
      },
      onTeacherClearSelect(){
        this.selectTeacherName = ""
        delete this.queryParam.courseTeacherid;
        this.$refs.selectUserModal.selectedRowKeys = [];
      },
      //设置教师单选
      onTeacherSelected(record){
        if(!!record){
          console.log(this.$refs.selectUserModal.selectedRowKeys);
          this.selectTeacherName = record.realname;
          this.queryParam.courseTeacherid = record.id;
          this.$refs.selectUserModal.selectedRowKeys = [];
          this.$refs.selectUserModal.selectedRowKeys[0] = record.id;
        }else{
          this.selectTeacherName = ""
          delete this.queryParam.courseTeacherid;
          this.$refs.selectUserModal.selectedRowKeys = [];
        }
      },
    //课程相关方法
      onCourseSearch(){
        this.$refs.courseSelectModal.visible = true;
      },
      handleCourseSelectRes(value, selected){
        this.selectCourseName = value.courseName;
        this.queryParam.courseId = value.id;
      },
      onCourseClearSelected(){
        this.selectCourseName = "";
        delete this.queryParam.courseId;
        this.$refs.courseSelectModal.clearSelect();
      },
    //班级相关方法
      onClassSearch(){
        this.$refs.myClassWindow.add(this.checkedDepartKeys,this.userId);
      },
      handleClassSelectRes(departData){
        this.onClassClearSelected();
        this.userId = departData.userId;
        this.departIds=[];
        let depart=[];
        //获取所有班级的id
        for (let i = 0; i < departData.departIdList.length; i++) {
          if(this.$refs.myClassWindow.leafList.indexOf(departData.departIdList[i].key) != -1){
            this.selectedDepartKeys.push(departData.departIdList[i].key);
            this.checkedDepartNames.push(departData.departIdList[i].title);
            this.selectClassName = this.checkedDepartNames.join(",");
            //新增院系选择，如果上面院系选择后不为空直接付给负责院系
            depart.push({
                key:departData.departIdList[i].key,
                title:departData.departIdList[i].title
            })
            this.departIds.push(departData.departIdList[i].key)
          }
        }
        this.checkedDepartKeys = this.selectedDepartKeys  //更新当前的选择keys
        
        this.queryParam.classId = this.departIds.join(",");
      },
      onClassClearSelected(){
        this.selectedDepartKeys=[];
        this.checkedDepartKeys=[];
        this.checkedDepartNames=[];
        this.selectClassName = "";
        delete this.queryParam.classId;
      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>