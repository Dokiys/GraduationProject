<template>
     <div class="editable-cell">
        <div v-if="editable" class="editable-cell-input-wrapper">
          <a-input style="width:50px;" :value="value" @change="handleChange" @pressEnter="check" /><a-icon
            type="check"
            class="editable-cell-icon-check"
            @click="check"
          />
        </div>
        <div v-else class="editable-cell-text-wrapper">
          {{ value || ' ' }}
          <a-icon type="edit" v-if="isIconShow && !!!value" class="editable-cell-icon" @click="edit" />
          <a-icon type="edit" v-if="isIconShow && !!value" class="editable-cell-icon-value" @click="edit" />
        </div>
    </div>
</template>

<script>
  export default {
    name: 'MyEditableCellModal',
    props: {
      text: Number,
      isIconShow:{
        type: Boolean,
        required: false,
        default: false
      },
      isConfirmModal: {
        type: Boolean,
        required: false,
        default: false
      }
    },
    data() {
      return {
        value: this.text,
        temValue: this.text,
        editable: false,
      };
    },
    methods: {
      handleChange(e) {
        let value = e.target.value;
        this.value = value;
      },
      check() {
        let re=/^(?:[1-9]?\d|100)$/;
        if(re.test(this.value) || !!!this.value){
          if(!!this.isConfirmModal){
            this.$confirm({
              content: `确认修改成绩？`,
              onOk: () => {
                this.$emit('change', this.value)
                this.temValue = this.value;
                this.editable = false;
              },
              onCancel: () => {
                this.value = this.temValue;
                this.editable = false;
              }
            })
          }else{
            this.$emit('change', this.value)
            this.editable = false;
          }
        }else{
          this.$message.warning("请正确输入成绩！");
        }
      },
      edit() {
        this.editable = true;
      },
    }
  }
</script>

<style scoped>
  .editable-cell {
    position: relative;
  }
  .editable-cell-input-wrapper,
  .editable-cell-text-wrapper {
    padding-right: 24px;
  }
  .editable-cell-text-wrapper {
    padding: 5px 24px 5px 5px;
  }
  .editable-cell-icon,
  .editable-cell-icon-value,
  .editable-cell-icon-check {
    position: absolute;
    right: 0;
    width: 10px;
    cursor: pointer;
  }
  .editable-cell-icon {
    line-height: 18px;
    display: inline-block;
  }
  .editable-cell-icon-value {
    line-height: 18px;
    display: none;
  }
  .editable-cell-icon-check {
    line-height: 28px;
  }
  .editable-cell:hover .editable-cell-icon-value {
    display: inline-block;
  }
  .editable-cell-icon:hover,
  .editable-cell-icon-check:hover {
    color: #108ee9;
  }
  .editable-add-btn {
    margin-bottom: 8px;
  }
</style>
