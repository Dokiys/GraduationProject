<template>
    <div>
        <a-tooltip placement="topLeft" v-if="isValueShow">
            <template slot="title">
            <span>{{value}}</span>
            </template>
            {{ value | ellipsis(length) }}
        </a-tooltip>
        <a-tooltip placement="topLeft" v-if="!isValueShow">
            <template slot="title">
            <span>{{subValue}}</span>
            </template>
            {{ subValue | ellipsis(length) }}
        </a-tooltip>
    </div>
</template>

<script>
  export default {
    name: 'MyEllipsis',
    props: {
      value: {
        type: String,
        required: false,
        default: ""
      },
      length: {
        type: Number,
        required: false,
        default: 25,
      }
    },
    data () {
      return {
        isValueShow:true,
        subValue:""
      }
    },
    methods:{
        changeValue(newValue){
            if(newValue != ''){
                this.subValue = newValue;
                this.isValueShow = false;
            }else{
                this.subValue = '';
                this.isValueShow = true;
            }
        }
    }
  }
</script>

<style scoped>

</style>
