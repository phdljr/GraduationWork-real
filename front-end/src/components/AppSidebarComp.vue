<template>
  <div class="sidebar">
    <b-sidebar id="sidebar-1" title="Sidebar" shadow>
      <div class="logo"><img src="../assets/premium-icon-food-5134814.png" style="width:100px" /></div>
      <div id="LoginSideContent" v-if="isLogin">
        <div class="roomlistButton">
          <b-button @click="goRoomList" variant="outline-success">방 찾기</b-button>
        </div>
        <div class="roomlistButton">
          <b-button @click="goRoomCreate" variant="outline-success">방 만들기</b-button>
        </div>
        <div class="roomlistButton">
          <b-button @click="goMyPage" variant="outline-success">마이 페이지</b-button>
        </div>
        <div class="roomlistButton">
          <b-button @click="$router.push('/cash')" variant="outline-success">캐시 충전</b-button>
        </div>
        <div class="roomlistButton">
          <b-button @click="logout" variant="danger">로그아웃</b-button>
        </div>
      </div>
      <div id="NotLoginSideContent" v-else>
        <div class="roomlistButton">
          <b-button @click="goRoomList" variant="outline-success">방 찾기</b-button>
        </div>
        <div class="roomlistButton" style="visibility: hidden;">
          <b-button>nothing</b-button>
        </div>
        <div class="roomlistButton">
          <b-button @click="$router.push('/loginView')" variant="outline-success">로그인</b-button>
        </div>
        <div class="roomlistButton">
          <b-button @click="$router.push('/register')" variant="outline-success">회원가입</b-button>
        </div>
      </div>
<!--      <div>-->
<!--        <h3>현재 위치</h3>-->
<!--      </div>-->
<!--      <b-button variant="info">{{currentLocation}}</b-button>-->
      <div class="sidebarInfo">{{welcomeName}}</div>
      </b-sidebar>
  </div>
</template>

<script>

export default {
  name: "AppSidebarComp",
  components: {
  },
  data() {
    return {
      
    };
  },
  methods: {
    //페이지 이동시 모달창이 삭제되지 않고 남아있어 페이지 리로드를 하여 이 부분을 삭제함
    async goRoomList(){
      await this.$router.push('/roomlist')
      this.$router.go()
    },
    async goMyPage(){
      await this.$router.push('/mypageView')
      this.$router.go()
    },
    async goRoomCreate(){
      await this.$router.push('/createroom')
      // this.$router.go()
    },
    async logout(){
      this.$store.commit('logout')
      await this.$router.push('/')
      this.$router.go()
    },
  },
  computed: {
    isLogin(){
      return this.$store.state.login
    },
    welcomeName(){
      if(this.$store.state.userData.userNickname==null){
        return "로그인을 해주세요."
      }
      else{
        return this.$store.state.userData.userNickname + "님 환영합니다!"
      }
    },
    currentLocation(){
      return this.$store.state.location
    }
  },
};
</script>

<style>
.sidebar{
    background: #bdecb6;
    box-shadow: 1px 0px 3px 1px gray;
}
.logo{
  width: 100%;
  margin-bottom: 13px;
  margin-top: 20px;
  padding-left: 0px;
}
.roomlistButton button{
  width: 240px;
  height: 2.5em;
}
.roomlistButton{
  margin-top: 1em;
  margin-bottom: 1em;
}
.sidebarInfo{
  background: rgba(256,256,256,0.4);
  color: hsla(0, 0%, 0%, 0.8);
  font-size: 1.6em;
  bottom: 0%;
  position: absolute;
  width: 100%;
}
</style>