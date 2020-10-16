<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="courseSearchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="课程名称">
              <j-input placeholder="输入课程名称模糊查询" v-model="queryParam.courseName"></j-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="课程类型">
              <j-dict-select-tag placeholder="请选择课程类型" v-model="queryParam.courseType"
                dictCode="course_type"
              />
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="课程状态">
              <j-dict-select-tag placeholder="请选择课程状态" v-model="queryParam.status"
                dictCode="course_status"
                allowClear=true
              />
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="courseSearchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>

      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
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
        :rowSelection="{fixed:true,selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        
        @change="handleTableChange">


        <span slot="action" slot-scope="text, record" >
          <span>
            <a @click="handleBeforDetail(record)" >详情</a>
            <a-divider type="vertical" v-if="isTeacher  && (record.status == 3 || record.status == 5)" />
            <a-dropdown v-if="isTeacher && (record.status == 3 || record.status == 5)">
              <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
              <a-menu slot="overlay">
                <a-menu-item>
                  <a @click="HandleMarkCourse(record)" >成绩</a>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </span>
        </span>

      </a-table>
    </div>

    <bysjCourseInfo-modal ref="modalForm" @ok="modalFormOk"></bysjCourseInfo-modal>
    <bysjMark-modal ref="bysjMarkModal" 
                    :course="markCourse" 
                    v-if="loadMarkModal" 
                    @submit="handleSubmitMarkModal"
                    ></bysjMark-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction } from '@/api/manage'
  import JInput from '@/components/jeecg/JInput'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import BysjCourseInfoModal from './modules/BysjCourseInfoModal'
  import BysjMarkModal from './modules/BysjMarkModal'

  export default {
    name: "BysjMyCourseList",
    mixins:[JeecgListMixin],
    components: {
      JInput,
      JDictSelectTag,
      BysjCourseInfoModal,
      BysjMarkModal
    },
    data () {
      return {
        description: 'bysj_course_info管理页面',
        selectTeacherName: "",
        hideDefaultSelections: true,
        isTeacher: false,
        loadMarkModal: false,
        dictOptions:{},
        markCourse:{},
        // 表头
        columns: [
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
            title:'设立课程院系',
            align:"center",
            dataIndex: 'departId'
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
            title:'课程状态',
            align:"center",
            dataIndex: 'status_dictText'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/bysj/bysjCourseInfo/myCourseList",
        },
      }
    },
    computed: {
    },
    created(){
      this.verifyTeacherRoleByToken();
    },
    methods: {
      courseSearchQuery(){
        this.markCourse = {};
        this.searchQuery();
      },
      //在点击详情打开编辑框之前使其不可编辑
      handleBeforDetail(record){
        this.$refs.modalForm.isEditable = false;
        this.handleDetail(record);
      },
      //在打开添加前设置初始值
      verifyTeacherRoleByToken(){
        //通过token获取登录用户角色Id
        getAction('sys/user/verifyTeacherRoleByToken', { token: this.tokenHeader['X-Access-Token'] }).then(res => {
          if (res.success) {
            this.isTeacher = res.result;
          }
        });
      },
  //评分相关
      HandleMarkCourse(record){
        this.markCourse = record;
        this.loadMarkModal = true;
        this.$nextTick(() => {
          this.$refs.bysjMarkModal.visible = true;
        })
      },
      handleSubmitMarkModal(){
        this.markCourse = {};
        this.loadData();
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>