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

        <a-form-item label="选择班级" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-search
            placeholder="点击右侧按钮选择班级"
            v-decorator="[ 'className', validatorRules.className]"
            disabled
            @search="onSearchClass">
            <a-button slot="enterButton" icon="search">选择</a-button>
          </a-input-search>
        </a-form-item>

        <a-form-item label="选择课程" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-search
            placeholder="点击右侧按钮选择课程"
            v-decorator="[ 'courseName', validatorRules.courseName]"
            disabled
            @search="onSearchCourse">
            <a-button slot="enterButton" icon="search">选择</a-button>
          </a-input-search>
        </a-form-item>


        <a-form-item label="选择教室" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-search 
            placeholder="点击右侧按钮选择授课教室"
            v-decorator="[ 'classroomName', validatorRules.classroomName]"
            disabled
            @search="onSearchClassroom">
            <a-button slot="enterButton" icon="search">选择</a-button>
          </a-input-search>
        </a-form-item>

        <a-form-item label="课表开始时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="[ 'timetableStartCode', validatorRules.timetableStartCode]" placeholder="请输入课表开始时间" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="课表结束时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="[ 'timetableEndCode', validatorRules.timetableEndCode]" placeholder="请输入课表结束时间" style="width: 100%"/>
        </a-form-item>

      </a-form>
    </a-spin>

    <classSelect-modal ref="classSelectModal" queryCourseStatus="0" @onSelectRes="handleClassSelectRes"  ></classSelect-modal>
    <classroomSelect-modal ref="classroomSelectModal" @onSelectRes="handleClassroomSelectRes"></classroomSelect-modal>
    <courseSelect-modal ref="courseSelectModal" @onSelectRes="handleCourseSelectRes"></courseSelect-modal>
  </a-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JDate from '@/components/jeecg/JDate'  
  import ClassSelectModal from '@/views/bysj/components/ClassSelectModal'
  import ClassroomSelectModal from '@/views/bysj/components/ClassroomSelectModal'
  import CourseSelectModal from '@/views/bysj/components/CourseSelectModal'

  export default {
    name: "BysjCourseArrangeModal",
    components: { 
      JDate,
      ClassSelectModal,
      ClassroomSelectModal,
      CourseSelectModal
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
          className: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          courseName: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          classroomName: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          timetableStartCode: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]},
          timetableEndCode: {rules: [
            {
              required: true,
              message: '此项必须输入!'
            }
          ]}
        },
        url: {
          add: "/bysj/bysjCourseArrange/add",
          edit: "/bysj/bysjCourseArrange/edit",
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
          this.form.setFieldsValue(pick(this.model,'className','courseName','classroomName','timetableStartCode','timetableEndCode'))
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
            //TODO 暂时不提交
            // httpAction(httpurl,formData,method).then((res)=>{
            //   if(res.success){
            //     that.$message.success(res.message);
            //     that.$emit('ok');
            //   }else{
            //     that.$message.warning(res.message);
            //   }
            // }).finally(() => {
            //   that.confirmLoading = false;
            //   that.close();
            // })
            that.confirmLoading = false;
            that.close();
          }
         
        })
      },
      handleCancel () {
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'className','courseName','classroomName','timetableStartCode','timetableEndCode'))
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
      handleClassroomSelectRes(value, selected){
        this.form.setFieldsValue({'classroomName':value.classroomName});
        this.model.classroomId = value.id;
        //添加class查询条件
      },
      handleCourseSelectRes(value, selected){
        this.form.setFieldsValue({'courseName':value.courseName});
        this.model.courseId = value.id;
      },
      handleClassSelectRes(value, node){
        this.form.setFieldsValue({'className': node.selectedNodes[0].data.props.title});
        this.model.classId = value[0];
      },
    }
  }
</script>