<template>
  <div class="signUpBg">
    <img src="../assets/logo.png" width="100px" />
    <b-form class="signUp" @submit="submitForm">
      <div class="inputBox">
        <b-button id="checkEmailButton" variant="light" @click="checkEmail"
          >중복 확인</b-button
        >
        <b-form-input
          type="email"
          v-model="registerData.email"
          placeholder="이메일을 입력해 주세요"
          required
          :state="vailidEmail"
        />
        <b-form-invalid-feedback :state="vailidEmail" v-if="emailChecked">
          이미 사용중인 이메일 입니다.
        </b-form-invalid-feedback>
        <b-form-valid-feedback :state="vailidEmail">
          사용 가능한 이메일 입니다.
        </b-form-valid-feedback>
      </div>
      <div class="inputBox">
        <b-button
          id="checkNicknameButton"
          variant="light"
          @click="checkNickname"
          >중복 확인</b-button
        >
        <b-form-input
          type="text"
          v-model="registerData.nickname"
          placeholder="닉네임을 입력해 주세요"
          required
          :state="vailidNickname"
        />
        <b-form-invalid-feedback :state="vailidNickname" v-if="nicknameChecked">
          이미 사용중인 닉네임 입니다.
        </b-form-invalid-feedback>
        <b-form-valid-feedback :state="vailidNickname">
          사용가능한 닉네임 입니다.
        </b-form-valid-feedback>
      </div>
      <div class="inputBox">
        <b-form-input
          type="password"
          id="password"
          v-model="registerData.password"
          placeholder="비밀번호를 입력해 주세요"
          required
          :state="validPw"
        />
        <b-form-invalid-feedback :state="validPw">
          비밀번호는 8글자 이상이여야 합니다.
        </b-form-invalid-feedback>
        <b-form-valid-feedback :state="validPw" />
      </div>
      <div class="inputBox">
        <b-form-input
          type="password"
          id="passwordCheck"
          v-model="passwordCheck"
          placeholder="비밀번호를 재입력해 주세요"
          required
          :state="checkPw"
        />
        <b-form-invalid-feedback :state="checkPw">
          비밀번호가 일치하지 않습니다.
        </b-form-invalid-feedback>
        <b-form-valid-feedback :state="checkPw" />
      </div>
      <div class="inputBox">
        <b-button id="something" variant="light" @click="checkPhoneNumber"
          >인증번호받기</b-button
        >
        <b-form-input
          id="phone"
          type="tel"
          v-model="registerData.phoneNum"
          placeholder="휴대폰 번호를 입력해 주세요"
          pattern="[0-9]{2,3}-[0-9]{3,4}-[0-9]{3,4}"
          @keyup="chkItemPhone"
          required
        />
      </div>
      <div class="inputBox">
        <b-form-input
          type="text"
          v-model="phoneCheck"
          placeholder="인증번호를 입력해 주세요"
          required
        />
      </div>
      <b-form-checkbox
        id="checkbox-1"
        v-model="checkAccept"
        name="checkbox-1"
        value="true"
        unchecked-value="false"
      >
        약관을 모두 읽고 동의하였습니다.
      </b-form-checkbox>
      <b-button id="loginButton" type="submit"> 회원가입 </b-button>
    </b-form>

    <div class="Login">
      계정이 있으신가요?
      <a href="/login">로그인</a>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "SignUp",
  props: {},
  data() {
    return {
      registerData: {
        email: "",
        nickname: "",
        password: "",
        phoneNum: "",
      },
      passwordCheck: "",
      phoneCheck: "",
      phoneCheckData: "",
      vailidEmail: null,
      vailidNickname: null,
      checkAccept: null,
      emailChecked: null,
      nicknameChecked: null,
    };
  },
  components: {},
  methods: {
    checkPhoneNumber() {
      axios
        .post(
          this.HOST + "/sendSMS",
          { param: this.registerData.phoneNum },
          {
            headers: { "Content-Type": `application/json` },
          }
        )
        .then((res) => {
          alert(this.registerData.phoneNum + "으로 인증번호를 보냈습니다.");
          this.phoneCheckData = res.data;
          console.log(res.data);
        });
    },
    checkEmail() {
      this.emailChecked = true;
      if(this.registerData.email == ""){
        this.vailidEmail = false;
        return;
      }

      console.log(this.registerData.email);
      axios
        .post(
          this.HOST + "/email",
          { param: this.registerData.email },
          { headers: { "Content-Type": `application/json` } }
        )
        .then((res) => {
          console.log(res);
          this.vailidEmail = true;
        })
        .catch((err) => {
          console.log(err);
          alert("이미 존재하는 이메일입니다.")
          this.vailidEmail = false;
        });
    },
    checkNickname() {
      this.nicknameChecked = true;
      if(this.registerData.nickname == ""){
        this.vailidNickname = false;
        return;
      }

      console.log(this.registerData.nickname);
      axios
        .post(
          this.HOST + "/nickname",
          { param: this.registerData.nickname },
          { headers: { "Content-Type": `application/json` } }
        )
        .then((res) => {
          console.log(res);
          this.vailidNickname = true;
          if(res.data === "Fail"){
            this.vailidNickname = false;
          }
        })
        .catch((err) => {
          console.log(err);
          this.vailidNickname = false;
        });
    },
    submitForm() {
      // 이메일 중복 체크
      if (this.vailidEmail == false) {
        alert("이메일을 다시 확인해주세요.");
        return;
      }
      // 닉네임 중복 체크
      if (this.vailidNickname == false) {
        alert("닉네임을 다시 확인해주세요.");
        return;
      }
      // 휴대폰 인증번호 체크
      if (this.phoneCheck == null) {
        alert("휴대폰 인증 번호를 입력해 주세요.");
        return;
      } else {
        if (this.phoneCheck != this.phoneCheckData) {
          alert("휴대폰 인증 번호가 일치하지 않습니다.");
          return;
        }
      }

      if(!(this.registerData.password == this.passwordCheck && this.registerData.password.length > 7)){
        alert("비밀번호를 다시 확인해주세요.");
        return;
      }

      let data = {
        user_email: this.registerData.email,
        user_pw: this.registerData.password,
        user_nickname: this.registerData.nickname,
        user_phoneNumber: this.registerData.phoneNum,
      };
      console.log(data);
      axios
        .post(this.HOST + "/user", data, {
          headers: { "Content-Type": `application/json` },
        })
        .then(() => {
          alert("회원가입이 정상적으로 이루어졌습니다!");
          //로그인 창으로 라우팅
          this.$router.push("/loginView");
        })
        .catch((err) => {
          alert(err);
        });
    },
    chkItemPhone() {
      var temp = document.getElementById("phone").value;
      var number = temp.replace(/[^0-9]/g, "");
      var phone = "";

      if (number.length < 9) {
        return number;
      } else if (number.length < 10) {
        phone += number.substr(0, 2);
        phone += "-";
        phone += number.substr(2, 3);
        phone += "-";
        phone += number.substr(5);
      } else if (number.length < 11) {
        phone += number.substr(0, 3);
        phone += "-";
        phone += number.substr(3, 3);
        phone += "-";
        phone += number.substr(6);
      } else {
        phone += number.substr(0, 3);
        phone += "-";
        phone += number.substr(3, 4);
        phone += "-";
        phone += number.substr(7);
      }
      document.getElementById("phone").value = phone;
    },
  },
  computed: {
    checkPw() {
      if(this.passwordCheck == ''){
        return null
      }
      return this.registerData.password == this.passwordCheck;
    },
    validPw() {
      if(this.registerData.password == ''){
        return null
      }
      return this.registerData.password.length > 7;
    },
  },
  watch:{
    'registerData.email':function(){
      if(this.emailChecked==true){
        this.validEmail = null
        this.emailChecked = null
      }
    },
    'registerData.nickname':function(){
      if(this.nicknameChecked==true){
        this.vailidNickname = null
        this.nicknameChecked = null
      }
    }
  }
}
</script>

<style>
#submit {
  text-align: left;
}
#submit label {
  display: inline-block;
  width: 100px;
}
body {
  margin: 0;
}
div {
  box-sizing: border-box;
}
.signUpBg {
  width: 768px;
  background-color: rgb(245, 245, 245);
  border: 1px solid rgb(180, 180, 180);
  margin: auto;
  border-radius: 8px;
  margin-top: 0px;
}
.signUp {
  text-align: left;
  width: 400px;
  margin: 0 auto;
}
#loginButton {
  width: 100%;
  margin-top: 15px;
}
.inputBox {
  text-align: left;
  width: 100%;
  margin: 0 auto;
  background: rgba(223, 223, 223, 30%);
  border: rgba(80, 80, 80, 100%);
  border-radius: 6px;
  border-width: 10px;
  margin-top: 10px;
  padding: 1em;
}
.inputBox input {
  width: 70%;
  border: none;
  background: transparent;
}
.inputBox button {
  width: 27%;
  float: right;
  font-size: 0.75em;
}
.Login {
  padding: 20px;
  margin-top: 20px;
  border-top: solid rgb(190, 190, 190);
  border-width: 0.1px;
}
.form-check {
  margin-top: 20px;
  margin-bottom: 20px;
}
</style>
