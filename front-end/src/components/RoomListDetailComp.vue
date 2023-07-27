<template>
  <b-modal
    id="roomDetailModal"
    hide-footer
    size="xl"
    :title="roomDetail.title"
    @hidden="exitRoom"
  >
    <div class="roomDetailLeft">
      <div class="RoomDetailPlace">
        <h3 style="margin-bottom: 30px">분배 장소: {{ roomDetail.gatheringPlace }}</h3>
      </div>
      <div class="SelectedMenuList">
        <b-list-group>
          <b-list-group-item variant="dark">주문 메뉴</b-list-group-item>
          <b-list-group-item
            variant="light"
            :key="nickname"
            v-for="(menus, nickname) in roomDetail.userMenus"
          >
            <!-- asaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa -->
            <p>
              {{ nickname }}
              <b-badge variant="success" pill
                v-if="(roomDetail.createdBy == nickname)">
                <b-icon icon="star-fill" />
              </b-badge>
              <b-badge variant="warning" pill
                v-if="(roomDetail.userStatus[nickname] == 'ORDER')&&(roomDetail.createdBy != nickname)">
                <b-icon icon="check" />
              </b-badge>
              <b-badge variant="success" pill
                v-if="(roomDetail.userStatus[nickname] == 'READY')&&(roomDetail.createdBy != nickname)">
                <b-icon icon="check" />
              </b-badge>
              <b-badge variant="primary" pill
                v-if="(roomDetail.userStatus[nickname] == 'DISTRIBUTE')&&(roomDetail.createdBy != nickname)">
                <b-icon icon="check" />
              </b-badge>
              <b-button v-if="($store.state.userData.userNickname != nickname)&&(roomDetail.createdBy == $store.state.userData.userNickname) && (roomDetail.roomStatus !== 'RECRUIT')" 
              variant="danger" @click="kickOut(nickname)" style="margin-left:20px">X</b-button>
            </p>
            <b-list-group-item
              class="MenuDetail"
              variant="info"
              :key="menuNum"
              v-for="(menu, menuNum) in menus"
            >
              {{ menu["menuName"] }} : {{ menu["price"] }}원
              <b-badge variant="success" pill>{{ menu["quantity"] }}</b-badge>
            </b-list-group-item>
          </b-list-group-item>
        </b-list-group>
      </div>
      <b-button
        id="menuSelect"
        :disabled="selectMenuButtonStatus"
        @click="
          openMenu(
            roomDetail.userMenus[this.$store.state.userData.userNickname]
          )
        "
        >메뉴선택</b-button
      >
      <h3 style="margin-top: 30px">주문 금액: {{ roomDetail.currAmount }}원</h3>
    </div>
    <div class="roomDetailRight">
      <div class="chatButton">
        <b-button @click="showChatModal">
          <b-icon icon="chat-dots" size="3x" />
        </b-button>
      </div>
    </div>
    <div class="orderButton">
      <div class="buttonCreater" v-if="isCreatByMe">
        <!-- <div class="buttonStatus1" v-if="roomDetail.roomStatus=='RECRUIT'">
          <b-button variant="success" disabled>결제 요청</b-button>
        </div> -->

        <div class="buttonStatus2" v-if="roomDetail.roomStatus=='RECRUIT'">
          <b-button id="requestPayButton" variant="success" :disabled="requestPayButtonStatus" @click="requestPay">결제 요청</b-button>
        </div>

        <div class="buttonStatus2" v-else-if="roomDetail.roomStatus=='READY' && !orderButtonStatus">
          <b-button id="requestPayButton" variant="success" disabled>결제 요청</b-button>
        </div>

        <div class="buttonStatus3" v-else-if="orderButtonStatus">
          <b-button variant="success" @click="orderDelivery">주문하기</b-button>
        </div>

        <div class="buttonStatus4" v-else-if="roomDetail.roomStatus=='ORDER'">
          <b-button variant="success" @click="notifyDelivery">음식 도착 알림</b-button>
        </div>

        <div class="buttonStatus4" v-else-if="roomDetail.roomStatus=='DISTRIBUTE'">
          <b-button variant="success" disabled>음식 분배 중</b-button>
        </div>
      </div>

      <div class="buttonUser" v-else>
        <div class="buttonStatus1" v-if="roomDetail.roomStatus=='RECRUIT'">
          <b-button variant="success" v-if="roomDetail.userStatus[$store.state.userData.userNickname]=='RECRUIT'" @click="readyButton">준비 완료</b-button>
          <b-button variant="danger" v-else @click="readyButton">준비 해제</b-button>
        </div>

        <div class="buttonStatus2" v-else-if="roomDetail.roomStatus=='READY'">
          <b-button variant="success" @click="pay" :disabled="roomDetail.userStatus[$store.state.userData.userNickname]=='ORDER'">결제</b-button>
        </div>

        <div class="buttonStatus2" v-else-if="roomDetail.roomStatus=='ORDER'">
          <b-button variant="success" disabled>배달 중...</b-button>
        </div>

        <div class="buttonStatus4" v-else-if="roomDetail.roomStatus=='DISTRIBUTE'">
          <b-button variant="primary" @click="recieveDelivery">음식 수령</b-button>
        </div>
      </div>
    </div>
    <room-list-menu-comp-vue ref="menuModal"></room-list-menu-comp-vue>
    <room-list-chat-comp ref="chatModal" />
  </b-modal>
</template>

<script>
// import roomDetail from "../json/roomDetail.json";
import RoomListMenuCompVue from "./RoomListMenuComp.vue";
import RoomListChatComp from "./RoomListChatComp.vue";
import axios from "axios";

export default {
  name: "RoomListDetailComp",
  props: ["roomId"],
  components: {
    RoomListMenuCompVue,
    RoomListChatComp,
  },
  data() {
    return {
      roomDetail: {
        roomId: "",
        title: "",
        gatheringPlace: "",
        currNumOfPeople: 0,
        maximumPeople: 0,
        minimumOrderAmount: 0,
        currAmount: 0,
        createdBy: "",
        roomStatus: "",
        userMenus: {
          //김민수: [
          //  {menuName: 'AA', price: 5000, quantity: 2},
          //  {menuName: 'AA', price: 5000, quantity: 2}
          //]
        },
        userStatus: {
          //김민수: "RECRUIT",
          //이종렬: "READY",
        },
      },
      readyStatus: false,
      requestPayButtonStatus: true,
      orderButtonStatus: false,
      selectMenuButtonStatus: false
    };
  },
  methods: {
    // 준비 완료/해제 버튼
    readyButton(){
      this.readyStatus = !this.readyStatus
      // 준비 완료 상태로 바꼈다면
      if(this.readyStatus == true && !confirm("정말로 준비를 마치셨나요?\n주문이 진행하고 나서는 방에서 나갈 수 없습니다!")){
        this.readyStatus = !this.readyStatus
        return;
      }

      if(this.readyStatus == true){
        this.selectMenuButtonStatus = true; // 메뉴 선택 비활성화
        this.roomDetail.userStatus[this.$store.state.userData.userNickname] = "READY"

        this.$store.state.stompSocket.send(
        "/status/notify/" + this.$store.state.userData.enterRoomId,
        JSON.stringify({
          param: "100/"+this.$store.state.userData.userNickname
        }),
        {}
      );
      }
      // 준비 해제 상태로 바꼈다면
      else{
        this.selectMenuButtonStatus = false; // 메뉴 선택 활성화
        this.roomDetail.userStatus[this.$store.state.userData.userNickname] = "RECRUIT"
        this.$store.state.stompSocket.send(
        "/status/notify/" + this.$store.state.userData.enterRoomId,
        JSON.stringify({
          param: "101/"+this.$store.state.userData.userNickname
        }),
        {}
      );
      }
    },

    //결제 요청 함수
    requestPay(){
      // 최소 주문 금액보다 적은 경우
      if(this.roomDetail.currAmount < this.roomDetail.minimumOrderAmount){
        alert("주문 금액이 부족합니다.")
        return;
      }

      //숫자만 입력하도록
      let fee = prompt("배달비를 입력해주세요.", "")
      while(isNaN(Number(fee)) || Number(fee) < 0){
        alert("0보다 큰 숫자만 입력해 주세요.")
        fee = prompt("배달비를 입력해주세요.", "")
      }

      let feeShare = parseInt(Number(fee)/this.roomDetail.currNumOfPeople)
      let feeMod = Number(fee)%this.roomDetail.currNumOfPeople
      if(feeMod > 0){
        alert(`배달비의 나머지 부분 ${feeMod}원은\n여기모여가 쏜다!`)
      }

      // document.getElementById("requestPayButton").disabled = true
      this.requestPayButtonStatus = true
      this.$store.state.stompSocket.send(
        "/status/notify/" + this.$store.state.userData.enterRoomId,
        JSON.stringify({
            param: "201/"+feeShare
          }),
        {}
      );
    },

    // 결제 함수
    pay(){
      if(!confirm(`결제하겠습니까?(배달비 포함)`)){
        return;
      }

      axios.post(this.HOST+"/room/pay", {
        param: this.$store.state.userData.userNickname
      }).then(res=>{
        res
        // this.roomDetail.userStatus[this.$store.state.userData.userNickname] = "ORDER"
        this.$store.state.stompSocket.send(
            "/status/notify/" + this.$store.state.userData.enterRoomId,
            JSON.stringify({
              param: "202/"+this.$store.state.userData.userNickname
            }),
          {}
        );
      }).catch(err=>{
        if(err.response.status === 402){
          alert("금액이 부족합니다.")
        }
        console.log(err.response)
      })
    },

    // 방장이 음식 주문
    orderDelivery() {
      this.orderButtonStatus = false
      this.$store.state.stompSocket.send(
            "/status/notify/" + this.$store.state.userData.enterRoomId,
            JSON.stringify({
              param: "300/" + this.roomDetail.createdBy
            }),
          {}
        );
      alert("주문 상태로 변경하였습니다.\n음식이 도착하면 '음식 도착 알림' 버튼을 눌러주세요!")
    },

    // 음식 도착 알림 함수
    notifyDelivery(){
      this.$store.state.stompSocket.send(
            "/status/notify/" + this.$store.state.userData.enterRoomId,
            JSON.stringify({
              param: "400/" + this.roomDetail.createdBy
            }),
          {}
        );
    },

    // 음식 수령 함수
    recieveDelivery(event){
      event.target.value = "음식 수령 완료"
      event.target.disabled = true
      this.$store.state.stompSocket.send(
            "/status/notify/" + this.$store.state.userData.enterRoomId,
            JSON.stringify({
              param: "401/" + this.$store.state.userData.userNickname
            }),
          {}
        );
    },

    openMenu(userMenu) {
      // console.log("내 메뉴", this.roomDetail.userMenus[this.$store.state.userData.userNickname])
      console.log("유저 메뉴", userMenu);
      this.$refs.menuModal.setMenu(userMenu);
      this.$bvModal.show("MenuModal");
    },
    showChatModal() {
      this.$bvModal.show("chatModal");
    },

    // 채팅 구독
    subscribeChat() {
      console.log("방 입장 시, chat 구독 함수 호출");
      this.$store.commit("setSubscribeChat", 
        this.$store.state.stompSocket.subscribe(
          "/chat/receive/" + this.roomDetail.roomId,
          (res) => {
            // 서버로부터 채팅 내용을 res에 담아서 받아옴
            // console.log(res)
            let message = JSON.parse(res.body);
            if(message.sender === ""){
              this.roomDetail.currAmount = message.content; // 꼼수. 현재 주문금액 업데이트
            }
            else{
              this.$refs.chatModal.receivedcMsg(message);
            }
            // if(message.sender !== this.$store.state.userData.userNickname)
            // this.$refs.chatModal.receivedcMsg(message);
          }
        )
      )
    },

    // 메뉴 데이터 구독
    subscribeMenu() {
      console.log("방 입장 시, room 구독 함수 호출");
      this.$store.commit("setSubscribeMenu", this.$store.state.stompSocket.subscribe(
          "/room/" + this.roomDetail.roomId,
          (res) => {
            console.log("메뉴 데이터 받음");
            let menuData = JSON.parse(res.body);
            this.roomDetail.currNumOfPeople = menuData.currNumOfPeople;
            this.roomDetail.currAmount = menuData.currAmount;
            this.roomDetail.userMenus = menuData.userMenus;
            this.roomDetail.userStatus = menuData.userStatus;
            console.log(menuData.userStatus)
          }
        )
      )
    },
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 방, 유저 상태 데이터 구독
    subscribeNotify() {
      console.log("방 입장 시, notify 구독 함수 호출");
      this.$store.commit("setSubscribeNotify", this.$store.state.stompSocket.subscribe(
          "/status/notify/" + this.roomDetail.roomId,
          (res) => {
            console.log(res.body)
            let data = res.body.split('/')
            let protocol = data[0]
            let content = data[1]

            switch(protocol){
              case "100": // 준비 완료
                this.roomDetail.userStatus[content] = "READY" // 사용자 상태를 READY로 변경
                console.log(this.roomDetail.userStatus)
                break;
              case "101": // 준비 해제
                this.roomDetail.userStatus[content] = "RECRUIT"
                this.roomDetail.roomStatus = "RECRUIT"; // 방 상태도 다시 되돌림
                this.requestPayButtonStatus = true; // 결제 요청 버튼 비활성화
                console.log(this.roomDetail.userStatus)
                break;
              case "200": // 모든 참가자가 준비 완료인 상태일 때, 결제 요청 버튼 활성화 : 방장일때만 해당
                if(this.roomDetail.createdBy === this.$store.state.userData.userNickname){
                  // this.roomDetail.roomStatus = "READY";
                  this.requestPayButtonStatus = false; // 결제 요청 버튼 활성화
                }
                break;
              case "201": // 방장이 결제 요청 버튼을 눌렀을 때, 참여자의 '결제하기' 버튼 활성화
                this.selectMenuButtonStatus = true; // 메뉴 선택 비활성화
                this.roomDetail.roomStatus = "READY";
                break;
              case "202": // 참여자가 결제를 완료함
                this.roomDetail.userStatus[content] = "ORDER"
                break;
              case "203": // 모든 참여자가 결제를 진행했을 때
                if(this.roomDetail.createdBy === this.$store.state.userData.userNickname){
                  // 방장의 '주문하기' 버튼 활성화
                  this.orderButtonStatus = true
                }
                break;
              case "300": // 방장이 '주문하기' 버튼을 클릭
                this.roomDetail.roomStatus = "ORDER"; // 방 상태를 ORDER로 변경
                this.roomDetail.userStatus[this.roomDetail.createdBy] = "ORDER" //방장의 상태도 ORDER로 변경
                // 방장의 버튼은 '음식 도착 알림'으로 변경
                // 참가자의 버튼은 '배달중...'으로 변경
                break;
              case "400": // 방장이 '음식 도착 알림' 버튼을 클릭
                this.roomDetail.roomStatus = "DISTRIBUTE"; // 방 상태를 DISTRIBUTE로 변경
                this.roomDetail.userStatus[this.roomDetail.createdBy] = "DISTRIBUTE" //방장의 상태를 DISTRIBUTE로 변경
                alert("음식이 도착하였습니다.\n분배장소로 모여주세요!")
                // 방장의 버튼은 "음식 분배중..."으로 변경
                // 참가자의 버튼은 '음식 수령'으로 변경
                break;
              case "401": // 참여자가 '음식 수령' 버튼 클릭
                this.roomDetail.userStatus[content] = "DISTRIBUTE" // 참가자의 상태 변경
                // 참가자의 버튼이 '음식 수령 완료'로 변경 또는 비활성화
                break;
              case "402":
                alert("모든 참여자가 음식을 수령하였습니다.\n즐거운 시간 보내세요!\n(기존의 방은 자동으로 삭제됩니다.)")
                break;
              case "500": // 예외 발생
                console.log("예외 발생")
                break;
              case "600": // 방이 터졌을 때
                this.$store.commit('exitRoom')
                this.$router.go();
                alert("방이 삭제되었습니다.")
                break;
              case "700": //강퇴당했을 때
                if(content === this.$store.state.userData.userNickname){
                  this.exitRoomApi();
                  this.$store.commit('exitRoom')
                  this.$router.go();
                  alert("강퇴당하셨습니다.")
                }
                break;
            }
            console.log("방 상태: " + this.roomDetail.roomStatus)
            console.log(this.userStatus)
          }
        )
      )  
    },
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    subscribeRoom() {
      this.subscribeChat();
      this.subscribeMenu();
      this.subscribeNotify();
    },

    setDetailRoomInfo(roomInfo) {
      console.log("방 세부정보 가져오기", roomInfo);
      this.roomDetail = roomInfo;
      if(roomInfo.roomStatus !== "RECRUIT"){
        this.selectMenuButtonStatus = true; // 메뉴 선택 비활성화
      }

      // // 방장일 때
      // if(this.$store.state.userData.userNickname === this.roomDetail.createdBy){
      //   let usersStatus = this.roomDetail.userStatus.values()
      //   // 사용자들이 다 READY상태이고, 방 상태가 RECRUIT일 때 -> 결제 요청 활성화
        
      //   // 사용자들이 다 READY상태이고, 방 상태가 READY일 때 -> 결제 버튼 활성화(사용자)
      //   // 사용자들이 다 ORDER상태이고, 방 상태가 READY일 때 -> 주문하기 버튼 활성화
      //   // 사용자들이 다 ORDER상태이고, 방 상태가 ORDER일 때 -> 음식 도착 버튼 활성화
      //   // 사용자들이 다 ORDER상태이고, 방 상태가 DISTRIBUTE일 때 -> 음식 수령 버튼 활성화(사용자)
      //   // 사용자들이 다 DISTRIBUTE상태이고, 방 상태가 DISTRIBUTE일 때 -> 터짐
      // }
      // console.log("유저 메뉴", this.roomDetail.userMenus)
    },

    // 방을 나갔을 때 서버로 데이터 전송해주는 함수
    exitRoomApi(){
      // 방에 나가면 해당 방의 메뉴에 내 메뉴를 제외시킴
      axios
        .post(this.HOST + "/room/exit", {
          param: this.$store.state.userData.userNickname,
        })
        .catch((err) => {
          console.log(err);
        });
    },

    // 방을 삭제하는 API
    removeRoomApi(){
      axios
        .post(this.HOST + "/room/remove", {
          param: this.$store.state.userData.userNickname
        }).catch(err=>{
          console.log(err)
        })
    },
    
    // 방 모달창이 꺼졌을 때, 실행되는 함수
    exitRoom() {
      // if(this.roomDetail.roomStatus !== "RECRUIT"){
      //   alert("주문이 진행 중인 상태라면, 나갈 수 없습니다.")
      //   return;
      // }

      //방을 나갔을 때
      if (confirm("방을 나가시겠습니까? (확인을 누를 시 주문한 정보가 모두 사라집니다.)")) {
        //나간 사람이 방장이라면 방을 삭제
        if(this.$store.state.userData.userNickname === this.roomDetail.createdBy){
          if(confirm("방장이 방에 나가면 방이 삭제됩니다. 그래도 나가시겠습니까?")){
            this.removeRoomApi();
          }
        }
        //참여자라면 그냥 나가기
        else{
          this.exitRoomApi();
        }

        // 문제 : unsubscribe를 호출해서 서버에게도 알려야 함. 하지만, 어떤 상황에선 menu와 chat 등의 소캣이 존재하지 않음 -> 예외처리
        try{
          this.$store.state.subscribeList.menu.unsubscribe();
          this.$store.state.subscribeList.chat.unsubscribe();
          this.$store.state.subscribeList.notify.unsubscribe();
        }
        catch(err){
          console.log(err)
        }
        finally{
          this.$store.commit('exitRoom')
          this.$router.go();
        }
      } else {
        //방 유지입장 상태 유지 메소드
        //단순히 모달창만 내리면 됨
      }
    },
    kickOut(nickname) {
      console.log(nickname + "강퇴시킴");
      //여기에 강퇴시키는 메소드가 필요
      this.$store.state.stompSocket.send(
        "/status/notify/" + this.$store.state.userData.enterRoomId,
        JSON.stringify({
          param: "700/"+nickname
        }),
        {}
      )
    },
  },
  computed:{
    isCreatByMe(){
      if(this.roomDetail.createdBy == this.$store.state.userData.userNickname){
        return true
      }
      else{
        return false
      }
    }
  }
};
</script>

<style>
.roomDetailLeft {
  width: calc(100% - 7em);
  float: left;
  margin-bottom: 5em;
}
.roomDetailRight {
  width: max-content;
  height: 500px;
  float: right;
  position: relative;
}
.userTable tr {
  height: 45px;
}
.orderButton {
  width: calc(100% - 32px);
  position: absolute;
  bottom: 0px;
  text-align: center;
  margin-bottom: 1em;
}
.SelectedMenuList {
  height: 400px;
  overflow: auto;
}
#userName {
  width: 95%;
}
#menuSelect {
  margin-top: 0.5em;
}
.chatButton {
  position: absolute;
  bottom: 0;
  right: 0%;
}
.chatButton button {
  width: 5em;
}
</style>
