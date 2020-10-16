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

        <a-form-item label="教室名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'classroomName', validatorRules.classroomName]" placeholder="请输入教室名称"></a-input>
        </a-form-item>
        <a-form-item label="可容纳人数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="[ 'holdNumber', validatorRules.holdNumber]" placeholder="请输入可容纳人数" style="width: 100%"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JDate from '@/components/jeecg/JDate'  
  import { getAction } from '@/api/manage'


  export default {
    name: "BysjClassroomInfoModal",
    components: { 
      JDate,
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules: {
          classroomName: {rules: [
            {
              required: true,
              validator:this.validateClassroomName
            }
          ]},
          holdNumber: {rules: [
            {
              required: true,
              pattern: /^\d{1,4}$/, 
              message: '请输入最多四位数字' 
            }
          ]},
        },
        url: {
          add: "/bysj/bysjClassroomInfo/add",
          edit: "/bysj/bysjClassroomInfo/edit",
          validateClassroomName: "/bysj/bysjClassroomInfo/validateClassroomName"
        }
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'classroomName','holdNumber','createBy','createTime','updateBy','updateTime'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
         
        })
      },
      handleCancel () {
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'classroomName','holdNumber','createBy','createTime','updateBy','updateTime'))
      },
      validateClassroomName(rule,value, callback){
        if(!!!value){
          callback("此项必须填写")
        }else {
        //发送请求查询classroomName是否存在 validateClassroomName
          getAction(this.url.validateClassroomName,{classroomName:value}).then(res => {
            if (res.success) {
              //如果验证通过
              callback();
            }else{
              //如果存在则
              callback("教室名称已重复！")
            }
          })
        }
      }
    }
  }
</script>