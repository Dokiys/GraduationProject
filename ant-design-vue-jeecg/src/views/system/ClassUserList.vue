<template>
  <a-row :gutter="10">
    <a-col :md="8" :sm="24">
      <a-card :bordered="false">
        <div style="background: #fff;padding-left:16px;height: 100%; margin-top: 5px">
         
          <template v-if="userIdentity === '2' && departTree.length>0">
            <!--组织机构-->
            <a-tree
              showLine
              :selectedKeys="selectedKeys"
              :checkStrictly="true"
              @select="onSelect"
              :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
              :treeData="departTree"
              :autoExpandParent="autoExpandParent"
            />

          </template>
          <div style="margin-top: 24px;" v-else-if="userIdentity === '2' && departTree.length==0">
            <h3><span>暂无有效信息</span></h3>
          </div>
          <div style="margin-top: 24px;" v-else><h3>普通角色暂此权限</h3></div>
        </div>
      </a-card>
    </a-col>
    <a-col :md="16" :sm="24">
      <a-card :bordered="false">
        <a-tabs defaultActiveKey="2" @change="callback">
          <a-tab-pane tab="基本信息" key="1" forceRender>
            <Dept-Base-Info ref="DeptBaseInfo"></Dept-Base-Info>
          </a-tab-pane>
          <a-tab-pane tab="人员信息" key="2">
            <Dept-User-Info ref="DeptUserInfo" @clearSelectedDepartKeys="clearSelectedDepartKeys"></Dept-User-Info>
          </a-tab-pane>
          <a-tab-pane tab="角色管理" key="3" forceRender v-if="isRoleManagementVisible">
            <dept-role-info ref="DeptRoleInfo" @clearSelectedDepartKeys="clearSelectedDepartKeys"/>
          </a-tab-pane>
        </a-tabs>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
  import DeptBaseInfo from './modules/DeptBaseInfo'
  import DeptUserInfo from './modules/DeptUserInfo'
  import {queryMyDepartTreeList, searchByKeywords} from '@/api/api'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import DeptRoleInfo from './modules/DeptRoleInfo'
  import {getAction } from '@/api/manage'
  import {queryUserRole } from '@/api/api'


  export default {
    name: 'ClassUserList',
    mixins: [JeecgListMixin],
    components: {
      DeptRoleInfo,
      DeptBaseInfo,
      DeptUserInfo,
    },
    data() {
      return {
        currentDeptId: '',
        iExpandedKeys: [],
        loading: false,
        autoExpandParent: true,
        currFlowId: '',
        currFlowName: '',
        disable: true,
        treeData: [],
        visible: false,
        departTree: [],
        rightClickSelectedKey: '',
        hiding: true,
        model: {},
        dropTrigger: '',
        depart: {},
        disableSubmit: false,
        checkedKeys: [],
        selectedKeys: [],
        autoIncr: 1,
        currSelected: {},
        form: this.$form.createForm(this),
        labelCol: {
          xs: {span: 24},
          sm: {span: 5}
        },
        wrapperCol: {
          xs: {span: 24},
          sm: {span: 16}
        },
        graphDatasource: {
          nodes: [],
          edges: []
        },
        userIdentity:"",
        //add properties
        isRoleManagementVisible:false,
      }
    },
    methods: {
      callback(key) {
        console.log(key)
      },
      loadData() {
        this.refresh();
      },
      clearSelectedDepartKeys() {
        this.checkedKeys = [];
        this.selectedKeys = [];
        this.currentDeptId = '';
        this.$refs.DeptUserInfo.currentDeptId='';
        this.$refs.DeptRoleInfo.currentDeptId='';
      },
      loadTree() {
        var that = this
        that.treeData = []
        that.departTree = []
        queryMyDepartTreeList().then((res) => {
          if (res.success && res.result ) {
            for (let i = 0; i < res.result.length; i++) {
              let temp = res.result[i]
              that.treeData.push(temp)
              that.departTree.push(temp)
              that.setThisExpandedKeys(temp)
              // console.log(temp.id)
            }
            this.loading = false
          }
          that.userIdentity = res.message
        })
      },
      setThisExpandedKeys(node) {
        if (node.children && node.children.length > 0) {
          this.iExpandedKeys.push(node.key)
          for (let a = 0; a < node.children.length; a++) {
            this.setThisExpandedKeys(node.children[a])
          }
        }
      },
      refresh() {
        this.loading = true
        this.loadTree()
      },

      onExpand(expandedKeys) {
        // console.log('onExpand', expandedKeys)
        // if not set autoExpandParent to false, if children expanded, parent can not collapse.
        // or, you can remove all expanded children keys.
        this.iExpandedKeys = expandedKeys
        this.autoExpandParent = false
      },

      onSearch(value) {
        let that = this
        if (value) {
          searchByKeywords({keyWord: value}).then((res) => {
            if (res.success) {
              that.departTree = []
              for (let i = 0; i < res.result.length; i++) {
                let temp = res.result[i]
                that.departTree.push(temp)
              }
            } else {
              that.$message.warning(res.message)
            }
          })
        } else {
          that.loadTree()
        }

      },
      onCheck(checkedKeys, e) {
        let record = e.node.dataRef;
        // console.log('onCheck', checkedKeys, e);
        this.checkedKeys = [];
        // if (e.checked === true) {
        this.currentDeptId = record.id;
        this.checkedKeys.push(record.id);

        this.$refs.DeptBaseInfo.open(record);
        this.$refs.DeptUserInfo.open(record);
        this.$refs.DeptRoleInfo.open(record);
        // }
        // else {
        //   this.checkedKeys = [];
        //   this.$refs.DeptBaseInfo.clearForm();
        //   this.$refs.DeptUserInfo.clearList();
        // }

        this.hiding = false;
        // this.checkedKeys = checkedKeys.checked
      },
      onSelect(selectedKeys, e) {
        if (this.selectedKeys[0] !== selectedKeys[0]) {
          this.selectedKeys = [selectedKeys[0]];
        }
        let record = e.node.dataRef;
        this.checkedKeys.push(record.id);
        this.$refs.DeptBaseInfo.open(record);
        this.$refs.DeptUserInfo.onClearSelected();
        this.$refs.DeptUserInfo.open(record);
        this.$refs.DeptRoleInfo.onClearSelected();
        this.$refs.DeptRoleInfo.open(record);
      },
    },
    created() {
      this.currFlowId = this.$route.params.id
      this.currFlowName = this.$route.params.name
      // this.loadTree()
      getAction('sys/user/getUserSectionInfoByToken', { token: this.tokenHeader['X-Access-Token'] }).then(res => {
        if (res.success) {
          queryUserRole({userid:res.result.sysUserId}).then((res)=>{
          if(res.success){
            let currentRoleSet = new Set(res.result);
            //判断是否为班主任或者院系负责人来显示角色管理
            let roleArray = ['1253856742812385281','1253856817919787009'];
            let result = roleArray.filter(item => currentRoleSet.has(item))
            if(result.length > 0){
              this.isRoleManagementVisible = true;
            }else{
              this.$refs.DeptUserInfo.isManagementVisible = false;
            }
            //禁止添加用户
            this.$refs.DeptUserInfo.isAddUserVisible = false;
          }else{
            console.log(res.message);
          }
        });
        }
      })
    },
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>