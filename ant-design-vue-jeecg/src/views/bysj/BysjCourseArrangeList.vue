<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="10" :sm="10">
            <a-form-item label="选择班级">
              <a-input-search
                placeholder="点击右侧按钮选择班级"
                v-model="checkedClassNameString"
                disabled
                @search="onSearchClass">
                <a-button slot="enterButton" icon="search">选择</a-button>
              </a-input-search>
            </a-form-item>
          </a-col>

          <a-col :md="10" :sm="10">
            <a-form-item label="选择教室" >
              <a-input-search
                placeholder="点击右侧按钮选择授课教室"
                v-model="checkedClassroomNameString"
                disabled
                @search="onSearchClassroom">
                <a-button slot="enterButton" icon="search">选择</a-button>
              </a-input-search>
            </a-form-item>
          </a-col>
          
          
        </a-row>
        <a-row :gutter="24">
          <a-col :md="10" :sm="10">
            <a-form-item label="选择课程" >
              <a-input-search
                placeholder="点击右侧按钮选择课程"
                v-model="checkedCourseNameString"
                disabled
                @search="onSearchCourse">
                <a-button slot="enterButton" icon="search">选择</a-button>
              </a-input-search>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="handleSearchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="handleSearchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!-- 测试时使用，正常新增应该在课程表中排课 -->
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

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

        <span slot="timetableCodeTmp" slot-scope="text,record">
          {{showTimeTableCode(record)}}
        </span>

        <span slot="action" slot-scope="text, record">
          <span v-if="record.courseStatus == 1">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-dropdown>
              <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
              <a-menu slot="overlay">
                <a-menu-item>
                  <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                    <a>删除</a>
                  </a-popconfirm>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </span>
          <span v-else>
            <a @click="handleBeforDetail(record)" >详情</a>
          </span>
        </span>

      </a-table>
    </div> 

    <!-- 选择查询条件model -->
    <classSelect-modal ref="classSelectModal" @onSelectRes="handleClassSelectRes"  ></classSelect-modal>
    <classroomSelect-modal ref="classroomSelectModal" @onSelectRes="handleClassroomSelectRes"></classroomSelect-modal>
    <courseSelect-modal ref="courseSelectModal" @onSelectRes="handleCourseSelectRes"></courseSelect-modal>
    <!-- 打开新建排课额model -->
    <bysjLinkage-modal ref="modalForm" @ok="modalFormOk"></bysjLinkage-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import BysjLinkageModal from '@/views/bysj/modules/BysjLinkageModal'
  import ClassSelectModal from '@/views/bysj/components/ClassSelectModal'
  import ClassroomSelectModal from '@/views/bysj/components/ClassroomSelectModal'
  import CourseSelectModal from '@/views/bysj/components/CourseSelectModal'



  export default {
    name: "BysjCourseArrangeList",
    mixins:[JeecgListMixin],
    components: {
      BysjLinkageModal,
      ClassSelectModal,
      ClassroomSelectModal,
      CourseSelectModal
    },
    data () {
      return {
        description: '排课管理管理页面',
        checkedClassroomNameString: "",
        checkedClassNameString: "",
        checkedCourseNameString: "",
        // 表头
        columns: [
          {
            title:'班级',
            align:"center",
            dataIndex: 'className'
          },
          {
            title:'课程名称',
            align:"center",
            dataIndex: 'courseName'
          },
          {
            title:'课程状态',
            align:"center",
            dataIndex: 'status_dictText'
          },
          {
            title:'上课教室',
            align:"center",
            dataIndex: 'classroomName'
          },
          {
            title:'开课时间',
            align:"center",
            dataIndex: 'startTime'
          },
          {
            title:'结课时间',
            align:"center",
            dataIndex: 'endTime'
          },
          {
            title:'课表时间',
            align:"center",
            dataIndex: 'timetableStartCode',
            scopedSlots: { customRender: 'timetableCodeTmp' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/bysj/bysjCourseArrange/list",
          delete: "/bysj/bysjCourseArrange/delete",
          deleteBatch: "/bysj/bysjCourseArrange/deleteBatch",
        },
        dictOptions:{},
      }
    },
    computed: {
    },
    methods: {
      initDictConfig(){
      },
      showTimeTableCode(record){
        let fArray = record.timetableStartCode.split("-");
        let sArray = record.timetableEndCode.split("-");
        return "周"+fArray[0]+"第"+fArray[1]+"-"+sArray[1]+"节课";
      },
      //在点击详情打开编辑框之前使其不可编辑
      handleBeforDetail(record){
        this.$refs.modalForm.isEditable = false;
        this.handleDetail(record);
      },
      onSearchClassroom(){
        //打开教室选择框
        this.$refs.classroomSelectModal.visible = true;
      },
      onSearchCourse(){
        //打开课程选择框
        this.$refs.courseSelectModal.visible = true;
      },
      onSearchClass(){
        this.$refs.classSelectModal.add();
      },
      handleClassSelectRes(value, node){
        this.checkedClassNameString = node.selectedNodes[0].data.props.title;
        this.queryParam.classId = value[0];
      },
      handleClassroomSelectRes(value, selected){
        this.checkedClassroomNameString = value.classroomName;
        this.queryParam.classroomId = value.id;
      },
      handleCourseSelectRes(value, selected){
        this.checkedCourseNameString = value.courseName;
        this.queryParam.courseId = value.id;
      },
      handleSearchQuery(){
        console.log(this.queryParam);
        this.searchQuery();
      },
      handleSearchReset(){
        this.checkedClassNameString = "";
        this.checkedClassroomNameString = "";
        this.checkedCourseNameString = "";
        this.$refs.classroomSelectModal.clearSelect();
        this.$refs.courseSelectModal.clearSelect();
        this.queryParam = [];
        this.loadData(1);
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>