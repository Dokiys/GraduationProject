<template>
  <div>
    <a-modal
      centered
      :title="title"
      :width="1000"
      :visible="visible"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭">


      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline"  @keyup.enter.native="searchQuery">
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
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                <a-button type="primary" @click="mySearchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                <a @click="handleToggleSearch" style="margin-left: 8px">
                  {{ toggleSearchStatus ? '收起' : '展开' }}
                  <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
                </a>
              </span>
            </a-col>

          </a-row>

          <a-row :gutter="24" v-if="toggleSearchStatus">
            <a-col :md="8" :sm="8">
              <a-form-item label="授课教师" >
                <a-input-search
                  placeholder="点击右侧按钮选择授课教师"
                  v-model="selectTeacherName"
                  disabled
                  @search="onSearchTeacher">
                  <a-button slot="enterButton" icon="search">选择</a-button>
                </a-input-search>
              </a-form-item>
            </a-col>
            
          </a-row>
        </a-form>
      </div>
      <!-- table区域-begin -->
      <div>
        <a-table
          size="small"
          bordered
          rowKey="id"
          :columns="columns1"
          :dataSource="dataSource1"
          :pagination="ipagination"
          :loading="loading"
          :scroll="{ y: 240 }"
          :rowSelection="{selectedRowKeys: selectedRowKeys,onSelectAll:onSelectAll,onSelect:onSelect,onChange: onSelectChange}"
          @change="handleTableChange">

        </a-table>
      </div>
      <!-- table区域-end -->

    <Select-User-Modal ref="selectUserModal" urlList="/sys/user/listOnlyTeacher" @selected="selectUser" @onSelectAll="onSelectUserAll"></Select-User-Modal>

    </a-modal>
  </div>
</template>

<script>
  import {filterObj} from '@/utils/util'
  import {getAction} from '@/api/manage'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  import JInput from '@/components/jeecg/JInput'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import SelectUserModal from '@views/system/modules/SelectUserModal'

  export default {
    name: "CourseSelectModal",
    mixins:[JeecgListMixin],
    components: {
      JInput,
      JDictSelectTag,
      SelectUserModal
    },
    props:{
      queryCourseStatus:{
        required: false,
        type: Number,
        default: null
      },
      urlList:{
        required: false,
        type: String,
        default: ""
      }
    },
    data() {
      return {
        title: "选择课程",
        names: [],
        visible: false,
        placement: 'right',
        description: '',
        // 查询条件
        queryParam: {},
        //选择教师名字显示
        selectTeacherName: "",
        // 表头
        columns1: [
          {
            title: '课程名称',
            align: "center",
            width: 100,
            dataIndex: 'courseName'
          },
          {
            title: '课程类型',
            align: "center",
            width: 100,
            dataIndex: 'courseType_dictText'
          },
          {
            title: '课程状态',
            align: "center",
            width: 100,
            dataIndex: 'status_dictText'
          },
          {
            title: '授课教师',
            align: "center",
            width: 100,
            dataIndex: 'courseTeacherName'
          },
          {
            title: '学分',
            align: "center",
            width: 100,
            dataIndex: 'courseScore'
          },
        ],
        //数据集
        dataSource1: [],
        dataSource2: [],
        // 分页参数
        ipagination: {
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
        isorter: {
          column: 'createTime',
          order: 'desc',
        },
        loading: false,
        selectedRowKeys: [],
        selectedRows: [],
        url: {
          list: "/bysj/bysjCourseInfo/list",
        }
      }
    },
    created() {
      this.loadData();
    },
    methods: {
      searchQuery() {
        //设置请求课程状态必须为未排课
        if(this.queryCourseStatus!=null) this.queryParam.status = this.queryCourseStatus;
        this.loadData(1);
      },
      searchReset() {
        this.queryParam = {};
        this.loadData(1);
      },
      handleCancel() {
        this.$emit("selectCancel");
        this.queryParam = {};
        this.selectTeacherName = "";
        this.visible = false;
      },
      handleOk() {
        this.dataSource2 = this.selectedRowKeys;
        this.$emit("selectOK");
        this.visible = false;
      },
      add() {
        this.visible = true;
      },
      loadData(arg) {
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件
        //如果传入了指定的请求路径则查询指定的请求路径否则默认查询
        if(!!this.urlList){
          getAction(this.urlList, params).then((res) => {
            if (res.success) {
              this.dataSource1 = res.result.records;
              this.ipagination.total = res.result.total;
            }
          })
        }else{
          getAction(this.url.list, params).then((res) => {
            if (res.success) {
              this.dataSource1 = res.result.records;
              this.ipagination.total = res.result.total;
            }
          })
        }
      },
      getQueryParams() {
        var param = Object.assign({}, this.queryParam, this.isorter);
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },
      getQueryField() {
        //TODO 字段权限控制
      },
      onSelect(record, selected){
        this.clearSelect();
        this.dataSource2[0] = record;
        this.selectedRowKeys[0] = record.id;
        this.selectedRows[0] = record;
        this.$emit("onSelectRes",record,selected);
      },
      onSelectAll(){
        this.clearSelect();
      },
      onClearSelected() {
        this.clearSelect();
      },
      onSelectChange(selectedRowKeys, selectedRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectedRows;
      },
      clearSelect(){
        this.dataSource2 = [];
        this.selectedRowKeys = [];
        this.selectedRows = [];
      },
      //点击重置按钮清空授课教师选择框
      mySearchReset(){
        this.queryParam = {};
        this.selectTeacherName = "";
        this.$refs.selectUserModal.onClearSelected();
        this.loadData(1);
      },
      handleTableChange(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        console.log(sorter);
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
        }
        this.ipagination = pagination;
        this.loadData();
      },
      //设置用户单选
      selectUser(record){
        console.log(record);
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
      onSelectUserAll(){
        this.selectTeacherName = ""
        delete this.queryParam.courseTeacherid;
        this.$refs.selectUserModal.selectedRowKeys = [];
      },
      onSearchTeacher(){
        this.$refs.selectUserModal.visible = true;
      }
    }
  }
</script>
<style lang="less" scoped>
  .ant-card-body .table-operator {
    margin-bottom: 18px;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 15px;
    padding-bottom: 15px;
  }

  .anty-row-operator button {
    margin: 0 5px
  }

  .ant-btn-danger {
    background-color: #ffffff
  }

  .ant-modal-cust-warp {
    height: 100%
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden
  }
</style>