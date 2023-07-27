<template>
  <div class="MyPageBg">
    <div class="MyPageTitle">
      <h1>My Page</h1>
    </div>
    <h4>참여한 방</h4>
    <div class="myRoom" v-if="room != null">
      <table class="roomListTable">
        <td @click="enterRoom(room)">
          <room-list-room-comp >
            <!-- <template #roomTitle> {{ room.title }}</template>
          <template #numPeople>
            {{ room.currentPerson }}/{{ room.maxPerson }}
          </template>
          <template #location> {{ room.location }} </template>
          <template #price> {{ room.fundedPrice }}/{{ room.price }} </template> -->
            <!-- =======================테스트====================== -->
            <template #roomTitle> {{ room.title }}</template>
            <template #numPeople>
              {{ room.currNumOfPeople }}/{{ room.maximumPeople }}
            </template>
            <template #location> {{ room.gatheringPlace }} </template>
            <template #price>
              {{ room.currAmount }}/{{ room.minimumOrderAmount }}
            </template>
          </room-list-room-comp>
        </td>
        <td id="showMapTd">
          <b-button
            variant="outline-info"
            @click="openMapModal(room.gatheringPlace)"
            ><!-- room.location 사용해야함 -->
            🗺️
          </b-button>
        </td>
      </table>
    </div>
    <h5 v-else>참여한 방이 없습니다.</h5>
    <h4>캐시 : {{money}}원</h4>
    <room-list-detail-comp :roomId="roomId" ref="detailRoom" />
    <b-modal id="MapModal" hide-footer title="위치">
      <kakao-map-comp-vue ref="createMap"></kakao-map-comp-vue>
    </b-modal>
    </div>
</template>
<script>
import RoomListDetailComp from '../components/RoomListDetailComp.vue';
import RoomListRoomComp from "../components/RoomListRoomComp.vue";
import KakaoMapCompVue from '../components/KakaoMapComp.vue';
import axios from 'axios';

export default {
  name: "MyPageView",
  components: {
    RoomListDetailComp,
    RoomListRoomComp,
    KakaoMapCompVue
  }, 
  data() {
    return {
      room:{
        currAmount: 0,
        currNumOfPeople: 0,
        gatheringDetailPlace: "",
        gatheringPlace: "",
        maximumPeople: 0,
        minimumOrderAmount: 0,
        roomStatus: "",
        title: ""
      },
      roomId:0,
      money: 0,
    }
  },
  methods:{
    enterRoom(room) {
      if(this.$store.state.login != true){ //로그인을 안할 시, 방 입장 불가능
        alert("로그인이 필요한 서비스입니다.");
        this.$router.push("/loginView")
        return;
      }

      //////////////
      axios
        .post(this.HOST + "/room/join", {
          user_nickname: this.$store.state.userData.userNickname,
          roomTitle: room.title,
        })
        .then((res) => {
          console.log("joinRoom 응답", res);
          this.$refs.detailRoom.setDetailRoomInfo(res.data);
          this.$bvModal.show("roomDetailModal");
          this.roomId = Number(res.data.roomId);
          this.$store.commit("enterRoom", this.roomId);
          // console.log("방 입장", this.$store.state.userData.enterRoomId)
          // this.$store.commit('setSubscribe', true)
          this.$refs.detailRoom.subscribeRoom();
        })
        .catch((err) => {
          console.log(err);
          let message = err.response.data.message;
          if(err.response.data.code == "NOT_FOUND_USER"){
            alert("로그인이 필요한 서비스입니다.")
            this.$router.push('/loginView')
          }
          // 사용자가 들어간 방이 이미 존재하는데, 다른방에 접속 시도를 할 때
          else if(err.response.status == 406){
            alert("이미 들어간 방이 존재합니다.")
          }
          else if(err.response.status == 403){
            alert("이미 주문이 진행 중인 방입니다.")
          }
          else{
            alert(message);
          }
        });
      //console.log(this.roomId)
    },
    openMapModal(addr){
      this.$bvModal.show('MapModal')
      setTimeout(() => this.$refs.createMap.changeMap(addr), 200);
    },
    getMyRoom(){
      //방 정보 로딩
      axios.post(this.HOST+"/mypage", {
        param: this.$store.state.userData.userNickname
      }).then(res=>{
        this.room = res.data.loadRoomDTO
        this.money = res.data.money
        console.log("방 데이테")
        console.log(res.data)
      }).catch(err=>{
        console.log(err);
      })
    }
  },
  created(){
    this.getMyRoom()
    if(this.$route.params.state === "OPEN_ROOM"){
      setTimeout(()=> this.enterRoom(this.room), 1000)
      // this.enterRoom(this.room)
    }
  }
};
</script>

<style scoped>
div {
  box-sizing: border-box;
}
.MyPageBg {
  width: 768px;
  background-color: rgb(245, 245, 245);
  margin: auto;
  border-radius: 8px;
  padding: 5em;
  text-align: left;
  overflow: hidden;/* 크기조절 해결 이유 모름 */
}
.MyPageTitle{
  border-bottom: solid gray;
  padding-bottom: 3em;
  margin-bottom: 1.2em;
}
td {
  padding-left: 0.8em;
  padding-right: 0.8em;
}
table {
  width: 100%;
  border: 0px;
}
.roomListTable {
  width: 100%;
  height: 100%;
  border: 1px solid rgb(180, 180, 180);
  border-collapse: separate;
  border-radius: 8px;
  background-color: rgb(245, 245, 245);
  padding-top: 1.1em;
  padding-bottom: 1.1em;
  vertical-align: middle;
}
#showMapTd {
  border-left: 1px solid #444444;
  width: 15%;
  padding: 10px;
}
#showMapTd button {
  height: 100%;
  width: 2em;
  font-size: 4em;
}
.roomListTable {
  margin-bottom: 1em;
}
.searchBox{
  width: 100%;
  height: 5em;
  margin-bottom: 3em;
}
.searchBox input{
  width: calc(100% - 120px);
  height: 100%;
  font-size: 2em;
  float: left;
}
.searchBox button{
  width: 120px;
  height: 100%;
  font-size: 2em;
  float: left;
}
.searchBox select{
  width: calc(100% / 3);
  float: left;
}
#locationOption{
  width: 40em;
  height: 3em;
}
#locationOption select{
  margin-bottom: 1em;
}
.myRoom{
  margin-top: 1.5em;
}
</style>