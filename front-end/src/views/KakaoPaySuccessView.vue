<template>
  <div class="chargedBg">
    <h3>충전완료!</h3>
    <h1>{{ $store.state.userData.chargedCash }}원</h1>
    <h4></h4>
    <b-form-input id="userCashInput" readonly></b-form-input>
    <b-button id="loginButton" @click="goRoomList">
      돌아가기
    </b-button>
  </div>
</template>
<script>
export default {
  name: "CashView",
  components: {},
  data() {
    return {};
  },
  methods: {
    async goRoomList(){
      await this.$router.push('/roomlist')
      this.$router.go()
    },
  },
  mounted(){
    document.getElementById("userCashInput").value = "잔여 캐시 : " + this.$store.state.userData.userCash + "원"
  },

};
</script>

<style scoped>
div {
  box-sizing: border-box;
}
.chargedBg {
  width: 768px;
  background-color: rgb(245, 245, 245);
  margin: auto;
  border-radius: 8px;
  padding: 5em;
  text-align: left;

}
.chargedBg input{
  margin-top: 1.5em;
  text-align: right;
  padding: 0.7em;
}
.chargedBg button{
  margin-top: 2em;
  display: flex;
}

</style>
