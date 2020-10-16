<template>
  <a-modal
    :width="modalWidth"
    :visible="visible"
    title="班级选择"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    @ok="handleOk"
    cancelText="关闭"
    wrapClassName="ant-modal-cust-warp"
    >
    <!--班级树-->
    <template>
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="班级">
            <a-tree
                allowClear="true"
                :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
                :treeData="departTree"
                @select="onSelect"
                placeholder="请选择班级"
                >
            </a-tree>
        </a-form-item>
      </a-form>
    </template>
  </a-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import { getAction } from '@/api/manage'
  import { queryMyIdTree } from '@/api/api'
  
  export default {
    name: "ClassSelectModal",
    components: {
    },
    data () {
      return {
        modalWidth:400,
        departTree: [],
        title:"操作",
        visible: false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        headers:{},
        form:this.$form.createForm(this),
        url: {
        //   userId:"/sys/user/generateUserId", // 引入生成添加用户情况下的url
        },
      }
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.queryDepartTree();
        this.form.resetFields();
        this.visible = true;
        this.model = Object.assign({}, record);
        let filedsVal = pick(this.model,'id','departIdList');
        this.$nextTick(() => {
          this.form.setFieldsValue(filedsVal);
        });
      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.departList = [];
        this.checkedKeys = [];
      },
      onSelect(value, node, extra){
          this.$emit('onSelectRes',value, node)
      },
      handleCancel () {
        this.close()
      },
      handleOk(){
        this.close()
      },
      queryDepartTree(){
        queryMyIdTree().then((res)=>{
          if(res.success){
            this.departTree = res.result;
          }
        })
      }
    }
  }
</script>

<style scoped>
  .ant-table-tbody .ant-table-row td{
    padding-top:10px;
    padding-bottom:10px;
  }
</style>