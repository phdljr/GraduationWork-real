<template>
    <div class="UserDataDiv">
      <b-form>
        <p>이메일</p>
        <b-form-input id="EmailInput" readonly></b-form-input>
      </b-form>
      <b-form>
        <p>닉네임</p>
        <b-form-input id="NicknameInput"></b-form-input>
        <b-button>변경</b-button>
      </b-form>
      <b-form>
        <p>비밀번호 변경</p>
        <b-form-input type="password"/>
        <p style="float: left;">비밀번호 확인</p>
        <b-form-input/>
        <b-button>변경</b-button>
      </b-form>
    </div>
    <div class="GoBackButton">
      <b-button @click="this.$router.go(-1);">돌아가기</b-button>
    </div>
</template>

<script>
export default {
    data() {
    return {
      cash: 0,
    };
  },
  methods: {
    
  },
  computed:{
    showEmail(){
      return this.$store.state.userData.userEmail
    },
    showNickname(){
      return this.$store.state.userData.userNickname
    }
  },
  mounted(){
    document.getElementById("EmailInput").value = this.showEmail
    console.log(this.showNickname)
    document.getElementById("NicknameInput").value = this.showNickname
  }
}
</script>

<style>
form{
  clear:both; /* 겹침 해결 */
}
.UserDataDiv{
  padding-top: 2em;
}
.UserDataDiv p{
  margin-bottom: 0px;
}
.UserDataDiv input{
  margin-bottom: 2.5em;
  width: calc(100% - 5em);
  float: left;
}
.UserDataDiv button{
  float: right;
  width: 4em;
}
.GoBackButton{
  text-align: center;
}
</style>