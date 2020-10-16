<template>
  <a-card >
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="handleSearchQuery">
        <a-row :gutter="24">
          
          <a-col :md="10" :sm="10">
            <a-form-item label="选择日期">
              <a-date-picker placeholder="选择查询日期" v-model="queryParam.date" />
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

    <a-spin :spinning="confirmLoading">
      <a-card title="我的课表" style="text-align:center" :loading="loading">
        <div v-for="(rItem,rIndex) in rArray" :key="rIndex">
          <span v-if="(rIndex == 6) || (rIndex == 10)">
            <a-divider />
          </span>
          <span>
            <a-card-grid :style="isCoursExist(cIndex,rIndex)?unCardStyle:cardStyle" 
            v-for="(cItem,cIndex) in cArray" 
            :key="cIndex+'-'+rIndex"
            :ref="cIndex+'-'+rIndex">
              <div v-if="rIndex == 0 && cIndex != 0"> 
                周{{cItem}}
              </div>
              <div v-if="cIndex == 0 && rIndex != 0"> 
                第{{rItem}}节
              </div>
              <div v-if="cIndex != 0 && rIndex != 0">
                <my-ellipsis ref="ellipsis" :value="showCourseNameIfExist(cIndex,rIndex)" :length="6"/>
              </div>
            </a-card-grid>
          </span>
        </div>
      </a-card>
    </a-spin>

  </a-card>
</template>

<script>

  import MyEllipsis from '@/views/bysj/components/MyEllipsis'
  import { getAction } from '@/api/manage'
  import moment from 'dayjs'

  export default {
    name: "BysjScheduleCard",
    components: {
      MyEllipsis
    },
    data () {
      return {
        description: '课表页面',
        confirmLoading: false,
        loading:false,
        queryParam:{},
        cardStyle:"width:12.5%;height:10px;text-align:center",
        unCardStyle:"width:12.5%;height:10px;text-align:center;background:#DCDCDC;color:#2F4F4F",
        rArray:["节数","一","二","三","四","五","六","七","八","九","十","十一","十二"],
        cArray:["周","一","二","三","四","五","六","日"],
        courseList:[],
        url: {
          schedule: "/bysj/bysjCourseArrange/schedule"
        },
      }
    },
    created() {
      this.loadData();
    },
    computed:{

    },
    methods: {
      handleSearchQuery(){
        this.loadData();
      },
      handleSearchReset(){
        this.queryParam = {};
        this.loadData();
      },
      loadData(){
        if(!this.url.schedule){
          this.$message.error("请设置url.schedule!")
          return
        }
        let params = {};
        if(!!this.queryParam.date){
          let date = moment(this.queryParam.date).format("YYYY-MM-DD")
          params.date = date;
        }
        this.loading = true;
        this.courseList = [];
        getAction(this.url.schedule,params).then((res) => {
          if (res.success) {
            this.courseList = res.result;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      showCourseNameIfExist(cIndex,rIndex){
        let result = this.isCoursExist(cIndex,rIndex);
        if(result){
          return this.isCoursExist(cIndex,rIndex).courseName;
        }
        return "";
      },
      //判断当前card是否有课程，有则返回课程对象
      isCoursExist(cIndex,rIndex){
        if(!!this.courseList){
          let currentCourse = {};
          let flag = false; //当前card是否有课程
          this.courseList.forEach(course => {
            let startArray = course.timetableStartCode.split("-");   
            let endArray = course.timetableEndCode.split("-");   
            let startWeek = startArray[0];
            let startNode = startArray[1];
            let endWeek = endArray[0];
            let endNode = endArray[1];

            if(cIndex == startWeek && cIndex == endWeek && rIndex <= endNode && rIndex >= startNode){
              flag = true;
              currentCourse = course;
            }
            return !flag;
          })
          if(flag){
            return currentCourse;
          }else{
            return false;
          }
        }
        return false;
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>