<template>
    <div class="createRoomBg">
        <b-form @submit="onSubmit">
            <b-form-input id="title" placeholder="제목을 입력해 주세요." v-model="RoomData.title" required></b-form-input>
            <b-form-group class="roomDetailForm">
                <b-form-input id="place" placeholder="분배 장소" v-model="RoomData.place" readonly required></b-form-input>
                <b-button @click="showDaumPostcode">🔍</b-button>
                <b-form-input id="placeDetail" placeholder="세부 주소" v-model="RoomData.placeDetail"></b-form-input>
                <b-form-input id="restaurant" placeholder="음식점" v-model="RoomData.restaurant" required></b-form-input>
                <b-button>🔍</b-button>
                <b-form-input id="money" placeholder="금액" type="number" v-model="RoomData.money" min="0" required></b-form-input>
                <b-form-input id="person" placeholder="인원 수" type="number" v-model="RoomData.person" min="1" max="10" required></b-form-input>
            </b-form-group>
            <b-form-group class="submitButton">
                <b-button type="submit" variant="primary">방 만들기</b-button>
            </b-form-group>      
      </b-form>
      <div ref="embed"></div>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: "CreateRoomView",
  props: {},
  data() {
    return {
      RoomData:{
          title:'',
          place:'',
          placeDetail:'',
          restaurant:'',
          money:null,
          person:null
      },
      zip: '',
      addr1: '',
      addr2: ''
    };
  },
  components: {},
  created(){
    if(this.$store.state.login == false){
        alert("로그인이 필요한 서비스입니다.");
        this.$router.push('/loginView');
    }
  },
  methods: {
    onSubmit(event) {
        event.preventDefault()
        if(this.RoomData.place == ""){
            alert("분배 장소를 입력해 주세요.")
            return
        }
        // alert(JSON.stringify(this.RoomData))
        // this.$router.push('/roomList')


        axios.post(this.HOST+"/room", {
            title: this.RoomData.title,
            gatheringPlace: this.RoomData.place,
            gatheringDetailPlace: this.RoomData.placeDetail,
            createdBy: this.$store.state.userData.userNickname,
            minimumOrderAmount: this.RoomData.money,
            numOfPeople: this.RoomData.person
        }).then(res=>{
            console.log(res)
            this.goRoomList();
        }).catch(err=>{
            console.log(err);
            console.log(err.response.data)
            let status = err.response.status;
            if(status == 404){
                alert("로그인이 필요한 서비스입니다.")
                this.$router.push('/loginView')
            }
            else{
                alert(err.response.data.message)
            }
        })
    },
    async goRoomList(){
        await this.$router.push('/roomlist')
        this.$router.go()
    },
    showDaumPostcode() { 
        new window.daum.Postcode({
            oncomplete: (data) => {
                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.
                // 도로명 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                let fullRoadAddr = data.roadAddress;
                // 도로명 주소 변수
                let extraRoadAddr = '';
                // 도로명 조합형 주소 변수 // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraRoadAddr += data.bname;
                } 
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 도로명, 지번 조합형 주소가 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if(extraRoadAddr !== ''){
                    extraRoadAddr = ' (' + extraRoadAddr + ')';
                }
                // 도로명, 지번 주소의 유무에 따라 해당 조합형 주소를 추가한다.
                if(fullRoadAddr !== ''){
                    fullRoadAddr += extraRoadAddr;
                }
                // 우편번호와 주소 정보를 해당 필드에 넣는다.
                this.zip = data.zonecode;
                //5자리 새우편번호 사용
                this.addr1 = fullRoadAddr;
                console.log(data.jibunAddress)
                console.log(this.zip)
                console.log(this.addr1)
                this.RoomData.place = this.addr1
            } }).open()
    }
  },
};
</script>

<style>
.createRoomBg{
    width: 100%;
    background-color: rgb(245, 245, 245);
    margin: auto;
    border-radius: 13px;
    margin-top: 0px;
    margin-bottom: 30px;
    padding: 40px;
}
.createRoomBg *{
    font-size: 1.5rem;
}
.createRoomBg input{
    margin-bottom: 10px;
}
.createRoomBg #title{
    margin-bottom: 30px;
}
.roomDetailForm input{
    width: calc(100% - 3.8em);
    float: left;
}
.roomDetailForm button{
    width: 3em;
    float: right;
    margin-left: 0.8em;
    text-align: center;
}

.createRoomBg.submitButton button{
    margin-top: 20px;
}
</style>
