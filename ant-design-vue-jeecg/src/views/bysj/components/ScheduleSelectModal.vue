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
      <a-card title="课表选择" style="text-align:center">
        <div v-for="(rItem,rIndex) in rArray" :key="rIndex">
          <span v-if="(rIndex == 6) || (rIndex == 10)">
            <a-divider />
          </span>
          <span>
            <a-card-grid :style="isCoursExist(cIndex,rIndex)?unCardStyle:cardStyle" 
            v-for="(cItem,cIndex) in cArray" 
            :key="cIndex+'-'+rIndex"
            :ref="cIndex+'-'+rIndex"
            @click="onClick(rIndex,cIndex)">
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

  </a-modal>
</template>

<script>
  import MyEllipsis from '@/views/bysj/components/MyEllipsis'

  export default {
    name: "ScheduleSelectModal",
    components: { 
      MyEllipsis
    },
    props:{
      courseList: {
      type: Array,
      required: false,
      default: () =>  
        [
          {
            timetableStartCode:"1-2",
            timetableEndCode:"1-4",
            courseName:"马克思主义哲学",
          },
          {
            timetableStartCode:"3-8",
            timetableEndCode:"3-9",
            courseName:"java基础",
          } 
        ]
    },
    },
    data () {
      return {
        title:"课表选择",
        width:"100%",
        visible: false,
        confirmLoading: false,
        cardStyle:"width:12.5%;height:10px;text-align:center",
        unCardStyle:"width:12.5%;height:10px;text-align:center;background:#DCDCDC;color:#2F4F4F",
        rArray:["节数","一","二","三","四","五","六","七","八","九","十","十一","十二"],
        cArray:["周","一","二","三","四","五","六","日"],
        firstNode:"",
        secondeNode:"",
      }
    },
    created () {
    },
    methods: {
      initSchedule(firstNode,secondeNode,courseId){
        //删除courseList中的当前选项
        console.log(this.courseList);
        // let i = -1;
        // this.courseList.forEach((item,index) => {
        //   if(item.courseId == courseId && item.timetableStartCode == firstNode && item.timetableStartCode == secondeNode){
        //     i = index;
        //   }
        // })
        // if(i != -1) this.courseList.splice(i,1);
        this.firstNode = firstNode;
        this.secondeNode = secondeNode;
        this.changeNodeValue(firstNode,secondeNode,"√")
      },
      handleOk () {
        this.$emit("ok",this.firstNode,this.secondeNode);
        this.visible = false;
      },
      handleCancel () {
        this.visible = false;
        //清除所选
        this.clearSelected();
      },
      onClick(rIndex,cIndex){
        if(rIndex == 0 || cIndex == 0 ) return;
        if(!!this.isCoursExist(cIndex,rIndex)) return;
        if(this.firstNode != "" && this.firstNode.split('-')[0] != cIndex){
          alert("请选择同一天上课！");
          return;
        }
        let refStr = cIndex + '-' + rIndex;
        //第一次选择直接赋值给第一个节点
        if(this.firstNode == "") {
          this.firstNode = refStr;
          this.$refs[refStr][0].$children[0].changeValue("√")
          return;
        }
        //如果勾选的是第一个节点,判断第二个节点是否存在
        if(this.firstNode == refStr){
          //不存在直接取消
          if(this.secondeNode == ""){
            this.firstNode = "";
            this.$refs[refStr][0].$children[0].changeValue("")
            return; 
          }
          //存在则取消勾选，并将第二个节点的值赋给第一个节点，并删除第二个节点
          else{
            this.changeNodeValue(this.firstNode,this.secondeNode,"");
            this.firstNode = this.secondeNode;
            this.$refs[this.firstNode][0].$children[0].changeValue("√");
            this.secondeNode = "";
            return;
          }
        }
        //如果是第二个节点则判断第二个节点是否有值
        //无值，则直接勾选
        if(this.secondeNode == ""){
          //调整第两个节点顺序，小的在前面
          //如果第二次选择节点在第一个节点之前，则将第一个选择的节点设置成第二个
          if(this.firstNode.split('-')[1] > rIndex){
            //判断是否包含不可选节点
            if(this.isContainDisable(refStr,this.firstNode)) return;
            this.secondeNode = this.firstNode;
            this.firstNode = refStr;
            this.changeNodeValue(this.firstNode,this.secondeNode,"√")
            return;
          }else{
            //判断是否包含不可选节点
            if(this.isContainDisable(this.firstNode,refStr)) return;
            this.secondeNode = refStr;
            this.changeNodeValue(this.firstNode,this.secondeNode,"√")
            return;
          }
        }
        //有值，再判断勾选是否为第二个节点
        //是则取消所有勾选，再勾选第一个节点,并将第二个节点赋值""
        if(this.secondeNode == refStr){
          this.changeNodeValue(this.firstNode,this.secondeNode,"");
          this.$refs[this.firstNode][0].$children[0].changeValue("√");
          this.secondeNode = "";
          return;
        }
        //不是则判断点击的节点是否在第二个之前
        //如果在则将点击节点赋值给第一个节点
        if(!this.compareNodes(refStr,this.secondeNode)){
          //判断是否包含不可选节点
          if(this.isContainDisable(refStr,this.secondeNode)) return;
          this.changeNodeValue(this.firstNode,this.secondeNode,"");
          this.firstNode = refStr
          this.changeNodeValue(this.firstNode,this.secondeNode,"√");
          return;
        }
        //判断是否包含不可选节点
        if(this.isContainDisable(this.secondeNode,refStr)) return;
        this.changeNodeValue(this.firstNode,this.secondeNode,"");
        this.firstNode = this.secondeNode;
        this.secondeNode = refStr;
        this.changeNodeValue(this.firstNode,this.secondeNode,"√");
      },
      isContainDisable(firstNode,secondeNode){
        let flag = false;
        let cIndex = firstNode.split('-')[0];
        let firstRow = firstNode.split('-')[1]*1;
        let secondeRow = secondeNode.split('-')[1]*1;
        if(!!this.courseList){
          this.courseList.some(course => {
            if(cIndex == course.timetableStartCode.split('-')[0]){
              let unFirstRow = course.timetableStartCode.split('-')[1]*1;
              let unSecondeRow =course.timetableEndCode.split('-')[1]*1;
              if(unFirstRow > firstRow && unSecondeRow < secondeRow){
                flag = true;
                return true;
              }
            }   
          })
        }
        return flag;
      },
      compareNodes(nodeOne,nodeTwo){
        let oneRow = nodeOne.split('-')[1]*1;
        let twoRow = nodeTwo.split('-')[1]*1;
        if(oneRow > twoRow) return true;
        else return false
      },
      clearSelected(){
        //判断是否存在两个节点
        //如果存在调用changeNodeValue取消勾选
        if(this.firstNode != "" && this.secondeNode != ""){
          this.changeNodeValue(this.firstNode,this.secondeNode,"")
          this.firstNode = "";
          this.secondeNode = "";
        }
        //如果只存在第一个节点则删除第一个节点
        else if(this.firstNode != "" && this.secondeNode == ""){
          this.$refs[this.firstNode][0].$children[0].changeValue("");
          this.firstNode = "";
        }
      },
      changeNodeValue(firstNode,secondeNode,str){
        let cIndex = firstNode.split('-')[0];
        let firstRow = firstNode.split('-')[1]*1;
        let secondeRow = secondeNode.split('-')[1]*1;
        for(let i=0;i<=Math.abs(secondeRow-firstRow);i++){
          let index = firstRow;
          if(firstRow < secondeRow) index +=i;
          else index -=i;
          let nodeStr = cIndex + '-' + index;
          this.$refs[nodeStr][0].$children[0].changeValue(str)
        }
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