<template>
  <div class="cashBg">
    <b-form class="cashForm">
      <h3>충전할 금액을 입력하세요.</h3>
      <b-form-input type="number" v-model="cash" />
      <div class="moneyButtonGroup">
        <b-button :key="key" v-for="(data, key) in moneyGroup" @click="addCash(data)" variant="outline-dark">+ {{data}}원</b-button>
      </div>
      <b-button @click="chargeCash" variant="success">충전하기</b-button>
    </b-form>
  </div>
</template>
<script>
import axios from "axios";
export default {
  name: "CashView",
  components: {},
  data() {
    return {
      cash: 0,
      moneyGroup:[
        1000,5000,10000,30000,50000
      ]
    };
  },
  methods: {
    chargeCash() {
      let data = {
        user_nickname: this.$store.state.userData.userNickname,
        chargingAmount: this.cash,
      };
      axios
        .post(this.HOST+"/kakaoPay", data)
        .then((res) => {
          this.$store.commit('tryAddCash', this.cash)
          location.href = res.data
        })
        .catch((err) => {
          console.log(err);
        });
    },
    addCash(addedCash){
      this.cash = this.cash + addedCash
    }
  },
};
</script>

<style scoped>
div {
  box-sizing: border-box;
}
.cashBg {
  width: 768px;
  background-color: rgb(245, 245, 245);
  margin: auto;
  border-radius: 8px;
  padding: 5em;
}
.cashForm {
  text-align: left;
  width: 100%;
  margin: 0 auto;
  margin-top: 10px;
}
.cashForm h3{
  margin-bottom: 1em;
}
.cashForm input {
  width: calc(100% - 0.6em);
  margin-bottom: 1em;
  margin: 0.3em;
}
.cashForm button {
  width: calc(100% - 0.6em);
  margin: 0.3em;
  margin-top: 1em;
}
.moneyButtonGroup{
  width: 100%;
}
.moneyButtonGroup button{
  width: calc((100% - (0.3em * 10))/5);
  margin: 0.3em;
  margin-top: 1em;
}
</style>