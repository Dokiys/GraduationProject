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
          <a-col :span="span">
            <a-form-item label="课程名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'courseName', validatorRules.courseName]" placeholder="请输入课程名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="span">
            <a-form-item label="课程类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag v-decorator="[ 'courseType', validatorRules.courseType]" placeholder="请选择课程类型" 
                :trigger-change="true"
                dictCode="course_type"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24" type="flex" justify="start">
          <a-col :span="span">
            <a-form-item label="开课时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date ref="JDate"
                      placeholder="请选择开课时间" 
                      v-decorator="[ 'startTime', validatorRules.startTime]" 
                      :trigger-change="true" 
                      style="width: 100%"
                      :disabledDate="disabledDate"/>
            </a-form-item>
          </a-col>
          <a-col :span="span" >
            <a-form-item label="结课时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date ref="JDate"
                      placeholder="请选择结课时间" 
                      v-decorator="[ 'endTime', validatorRules.endTime]" 
                      :trigger-change="true" 
                      style="width: 100%"
                      :disabledDate="disabledDate"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24" type="flex" justify="start">
          <a-col :span="span">
            <a-form-item label="学分" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'courseScore', validatorRules.courseScore]" placeholder="请输入学分"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="span" >
            <a-form-item label="课程状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <!-- <a-input  v-decorator="[ 'status', validatorRules.status]" placeholder="未排课" disabled ></a-input> -->
              <j-dict-select-tag v-decorator="[ 'status', validatorRules.status]" placeholder="未排课" 
                :trigger-change="true"
                disabled
                dictCode="course_status"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24" type="flex" justify="start">
          <a-col :span="24" >
            <a-form-item label="授课教师" 
            :labelCol="{xs: { span: 4 },sm: { span: 4 },}" 
            :wrapperCol="{xs: { span: 12 },sm: { span: 12 }, }">
              <a-input-search
                v-decorator="[ 'courseTeacherName', validatorRules.courseTeacherName]"
                placeholder="请选择授课教师"
                disabled
                @search="onSearch">
                <a-button slot="enterButton" icon="search">选择</a-button>
              </a-input-search>
             </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24" type="flex" justify="start">
          <a-col :span="24" >
            <a-form-item label="所属院校" 
            :labelCol="{xs: { span: 4 },sm: { span: 4 },}" 
            :wrapperCol="{xs: { span: 12 },sm: { span: 12 }, }">
            <a-input v-decorator="[ 'departId', validatorRules.departId]" :placeholder="collegeName" disabled></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        
      </a-form>
    </a-spin>
    <Select-User-Modal ref="selectUserModal" urlList="/sys/user/listOnlyTeacher" @selectOK="selectOK" @selectCancel="selectCancel" @selected="selected" @onSelectAll="onSelectAll"></Select-User-Modal>
    <template slot="footer">
      <div v-if="!isEditable">
        <a-button key="back" @click="handleCancel">返回</a-button>
      </div>
      <div v-else>
        <a-button key="back" @click="handleCancel">返回</a-button>
        <a-button key="submit" type="primary" :loading="false" @click="handleOk">
          确定
        </a-button>
      </div>
    </template>
  </a-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JDate from '@/components/jeecg/JDate'  
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import SelectUserModal from '@views/system/modules/SelectUserModal'

  export default {
    name: "BysjCourseInfoModal",
    components: { 
      JDate,
      JDictSelectTag,
      SelectUserModal,
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
        isEditable: true,
        model: {},
        confirmLoading: false,
        collegeName:"未指定院系",
        span: 11,
        labelCol: {
          xs: { span: 8 },
          sm: { span: 8 },
        },
        wrapperCol: {
          xs: { span: 16 },
          sm: { span: 16 },
        },
        validatorRules: {
          courseName: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          courseType: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          courseTeacherName: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          courseScore: {rules: [
            {
              required: true,
              pattern: /^\d{1,2}$/, message: '请输入正确的两位数学分' 
            }
          ]},
          departId: {rules: [
          ]},
          startTime: {rules: [
            { validator:this.handleTimeValidate }
          ]},
          endTime: {rules: [
            { validator:this.handleTimeValidate }
          ]},
          status:{rules:[
            {
              required: false,
              massage: ' '  
            }
          ]}
        },
        url: {
          add: "/bysj/bysjCourseInfo/add",
          edit: "/bysj/bysjCourseInfo/edit",
        }
      }
    },
    created () {
    },
    methods: {
      // 开始时间验证
      handleTimeValidate(rule,value, callback){
        if(!!!value){
          callback("此项必须填写")
        }else if(!!this.form.getFieldValue("endTime") && this.form.getFieldValue("endTime") < value){
          callback("开始时间必须早于结束时间")
        }else if(!!this.form.getFieldValue("startTime") && this.form.getFieldValue("startTime") > value){
          callback("结束时间必须晚于开始时间")
        }
        callback();
      },
      add (data) {
        //通过添加传入初始值
        this.edit(data);
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);

        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'courseName','courseType','courseTeacherName','courseScore','departId','startTime','endTime','status'))
          //判断record是否存在departRealId，有则将值设置给this.model
          if(!!record.departRealId){
            this.model.departId = record.departRealId
          }
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
            formData.departId = formData.departRealId;
            //TODO 暂时不向后端发送请求
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.isEditable = true;
              that.$refs.selectUserModal.selectedRowKeys = [];
              that.close();
            })
            // that.confirmLoading = false;
            // that.isEditable = true;
            // that.$refs.selectUserModal.selectedRowKeys = [];
            // that.close();
          }
         
        })
      },
      handleCancel () {
        this.isEditable = true;
        // this.model.courseTeacherid = null;
        // this.model.courseTeacherName = null;
        this.$refs.selectUserModal.onClearSelected();
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'courseName','courseType','courseTeacherid','courseTeacherName','courseScore','departId','startTime','endTime','status','createBy','createTime','updateBy','updateTime'))
      },
      onSearch(){
        this.$refs.selectUserModal.visible = true;
      },
      selectOK(data) {
        console.log(this.model);
      },
      selectCancel(data){
        this.$refs.selectUserModal.onClearSelected();
      },
      //设置用户单选
      selected(record){
        if(!!record){
          this.model.courseTeacherid = record.id;
          this.model.courseTeacherName = record.realname;
          this.form.setFieldsValue({courseTeacherName:record.realname});
          this.$refs.selectUserModal.onClearSelected();
          this.$refs.selectUserModal.selectedRowKeys[0] = record.id;
          this.$refs.selectUserModal.selectionRows[0] = record;
        }else{
          // this.model.courseTeacherid = null;
          // this.model.courseTeacherName = null;
          this.$refs.selectUserModal.onClearSelected();
        }
      },
      //全选时直接取消所以选项
      onSelectAll(){
        // this.model.courseTeacherid = null;
        // this.model.courseTeacherName = null;
        this.$refs.selectUserModal.onClearSelected();
        // this.$refs.selectUserModal.selectedRowKeys = [];
        // this.$refs.selectUserModal.selectionRows = [];
      },
      disabledDate(current){
        return current < this.$refs.JDate.moment().endOf('day');
      }
    }
  }
</script>