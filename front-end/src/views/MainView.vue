<template>
  <div class="main">
    <b-button @click="goRoomList">
      <h3>방 찾으러 이동!</h3>
    </b-button>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: "MainView",
  components:{
  },
  data() {
    return{
    }
  },
  methods:{
    async goRoomList(){
      await this.$router.push("/roomlist")
      this.$router.go()
    }
  },
  mounted(){
    // console.log("mounted 실행")
    // let token = this.$route.query.pg_token
    // let process = this.$route.query.process
    // if(process === "cancel" || process === "fail"){
    //   alert("결제가 취소됨")
    //   return;
    // }
    
    // if(token != null){
    //   console.log(token)
    //   axios.post(this.HOST + "/kakaoPaySuccess", token).then(res=>{
    //     console.log(res)
    //     this.$store.commit("chargeCash", res.data)
    //     this.$router.push('/kakaoPaysuccess')
    //   })
    // }
  }
}
</script>

<style>
.main{

}
</style>