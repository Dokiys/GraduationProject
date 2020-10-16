<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
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
                @search="onSearch">
                <a-button slot="enterButton" icon="search">选择</a-button>
              </a-input-search>
            </a-form-item>
          </a-col>
          
        </a-row>
        
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('bysj_course_info')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
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

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无此图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="图片不存在" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无此文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="uploadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record" >
          <!-- 根据课程状态显示不同操作 -->
          <span v-if="record.status == 0">
            <a @click="handleEdit(record)" >编辑</a>
            <a-divider type="vertical" />
            <a-dropdown>
              <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
              <a-menu slot="overlay">
                <a-menu-item>
                  <a-popconfirm title="确定删除吗?" @confirm="() => HandleDelete(record.id)">
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

    <bysjCourseInfo-modal ref="modalForm" @ok="modalFormOk"></bysjCourseInfo-modal>
    <Select-User-Modal ref="selectUserModal" 
                       urlList="/sys/user/listOnlyTeacher" 
                       @selectFinished="selectOK" 
                       @selected="selected" 
                       @onSelectAll="onSelectAll"/>

  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction } from '@/api/manage'
  import JInput from '@/components/jeecg/JInput'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import SelectUserModal from '@views/system/modules/SelectUserModal'
  import BysjCourseInfoModal from './modules/BysjCourseInfoModal'

  export default {
    name: "BysjCourseInfoList",
    mixins:[JeecgListMixin],
    components: {
      JInput,
      JDictSelectTag,
      SelectUserModal,
      BysjCourseInfoModal,
    },
    data () {
      return {
        description: 'bysj_course_info管理页面',
        selectTeacherName: "",
        hideDefaultSelections: true,
        dictOptions:{},
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
            title:'所属院系',
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
          list: "/bysj/bysjCourseInfo/list",
          delete: "/bysj/bysjCourseInfo/delete",
          deleteBatch: "/bysj/bysjCourseInfo/deleteBatch",
          exportXlsUrl: "/bysj/bysjCourseInfo/exportXls",
          importExcelUrl: "bysj/bysjCourseInfo/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    created(){
    },
    methods: {
      initDictConfig(){
      },
      //在打开添加前设置初始值
      handleAdd(){
        let data = {};
        //通过token获取登录用户信息
        getAction('sys/user/getUserSectionInfoByToken', { token: this.tokenHeader['X-Access-Token'] }).then(res => {
        if (res.success) {
          let userId = res.result.sysUserId;
          //通过当前用户id查询部门信息
          getAction('sys/sysDepart/queryByUserId',{userId:userId}).then(res => {
              if (res.success) {
                console.log(res.result);
                //将查询到的部门名称设置给data的departId
                data.departId = res.result.departName;
                //将查询到的部门id设置给data的dapartRealId
                data.departRealId = res.result.id;
                data.status = "0";
                this.$refs.modalForm.add(data);
              }
            })
          }
        })
      },
      //点击用户选择框中的确定时的回调函数
      selectOK(data) {
      },
      //打开用户选择框
      onSearch(){
        this.$refs.selectUserModal.visible = true;
      },
      //设置用户单选
      selected(record){
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
      //全选时直接取消所以选项
      onSelectAll(){
        this.selectTeacherName = ""
        delete this.queryParam.courseTeacherid;
        this.$refs.selectUserModal.selectedRowKeys = [];
      },
      //点击重置按钮清空授课教师选择框
      mySearchReset(){
        this.selectTeacherName = ""
        this.$refs.selectUserModal.selectedRowKeys = [];
      },
      //在点击详情打开编辑框之前使其不可编辑
      handleBeforDetail(record){
        this.$refs.modalForm.isEditable = false;
        this.handleDetail(record);
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>