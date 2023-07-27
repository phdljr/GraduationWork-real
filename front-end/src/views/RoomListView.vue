<template>
  <div class="searchBox">
    <b-form-input v-model="searchText" placeholder="방 제목 검색"/>
    <b-button variant="success" @click="searchRoomByTitle">검색</b-button>
    <button style="width:100%;font-size: 1em;height: fit-content;border: 0px;text-align: left;" v-b-toggle:locationOption>▷ 지역별 검색</button>
    <b-collapse id="locationOption">
      <b-form-select v-model="state" :options="stateData"></b-form-select>
      <b-form-select v-model="full_addr" :options="cityData"></b-form-select>
    </b-collapse>
  </div>
  <div :key="key" v-for="(room, key) in roomData">
    <table class="roomListTable">
      <td @click="enterRoom(room)">
        <room-list-room-comp>
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
  <b-button @click="getRoomList" v-if="showMoreBtn">더보기</b-button>
  <room-list-detail-comp :roomId="roomId" ref="detailRoom" />
  <b-modal id="MapModal" hide-footer title="위치">
    <kakao-map-comp-vue ref="createMap"></kakao-map-comp-vue>
  </b-modal>
</template>

<script>
import RoomListDetailComp from '../components/RoomListDetailComp.vue';
import RoomListRoomComp from "../components/RoomListRoomComp.vue";
import KakaoMapCompVue from '../components/KakaoMapComp.vue';
// import roomList from "../json/roomList.json";
import axios from 'axios';

export default {
  name: "RoomListView",
  components: {
    RoomListRoomComp,
    RoomListDetailComp,
    KakaoMapCompVue
  },
  data() {
    return {
      roomData: "",
      roomId: 0,
      roomLimit: 0,
      roomLimit_title: 0,
      roomLimit_location: 0,
      showMoreBtn: true,
      searchText:"",
      searchType:"",
      stateData:[
        {value: null, text:'시/도'}
      ],
      cityData:[
        {value: null, text:'시/군/구'}
      ],
      villageData:[
        {value: null, text:'읍/면/동'}
      ],
      state:null,
      full_addr:null,
    };
  },
  methods: {
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
          this.$store.commit('setSubscribe', true)
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
    searchRoomByTitle(){
      if(this.searchText == ''){
        alert("값을 입력해주세요.")
        return;
      }

      this.searchType = "title"
      this.roomLimit_title += 5
      axios.get(this.HOST+"/room/title/"+this.searchText + "/" +  this.roomLimit_title).then(res=>{
        this.roomData = res.data
        console.log(res.data)
        if(this.roomLimit_title > res.data.length){
          this.showMoreBtn = false
          this.roomLimit_title -= 5
        }
      }).catch(err=>{
        console.log(err)
      })
    },
    searchRoomByLocation(location){
      this.searchType = "location"
      this.roomLimit_location += 5
      axios.get(this.HOST+"/room/location/"+ location + "/" +  this.roomLimit_location).then(res=>{
        this.roomData = res.data
        console.log(res.data)
        if(this.roomLimit_location > res.data.length){
          this.showMoreBtn = false
          this.roomLimit_location -= 5
        }
      }).catch(err=>{
        console.log(err)
      })
    },
    getRoomList(){
      /* 
      변경해야할 사항 - 마운트시 파라미터확인하여 리스트가져오기
      검색시 페이지 이동
      */

      if(this.searchType === "title"){
        this.searchRoomByTitle()
      }
      else if(this.searchType === "location"){
        this.searchRoomByLocation()
      }
      else{
        this.roomLimit += 5
        axios.get(this.HOST+"/room/" + this.roomLimit).then(res=>{
          this.roomData = res.data
          // console.log("roomLimit: " + this.roomLimit + ", length: " + res.data.length)
          if(this.roomLimit > res.data.length){
            this.showMoreBtn = false
            this.roomLimit -= 5
          }
        })
      }
    },
    getSgisAccessToken(){
      const key = "consumer_key="+process.env.VUE_APP_SGIS_CONSUMER_KEY+"&consumer_secret="+process.env.VUE_APP_SGIS_CONSUMER_SECRET
      axios.get("https://sgisapi.kostat.go.kr/OpenAPI3/auth/authentication.json?" + key).then(res=>{
        this.$store.commit("setSgisAccessToken", res.data.result.accessToken)
        console.log(this.$store.state.sgisAccessToken)
      })
    },
    //시,도 데이터 가져오기
    getStateData(){
      if(this.$store.state.sgisAccessToken==''){
        this.getSgisAccessToken()
      }
      else{
        var errCnt = 0;
        axios.get("https://sgisapi.kostat.go.kr/OpenAPI3/addr/stage.json?accessToken=" + this.$store.state.sgisAccessToken).then(res=>{
          switch (parseInt(res.data.errCd)){
            case 0:
              this.stateData = [{value: null, text:'시/도'}]
              for (var temp of Object.entries(res.data.result)){
                var state = {text:'',value:0}
                state.text = temp[1].full_addr
                state.value = temp[1].cd
                this.stateData.push(state)
              }
            break;

            case -401:
              errCnt ++;
              if(errCnt<200){
                this.getSgisAccessToken();
              }
            break;
          }
        })
      }
    },
    getCityData(state){
      if(this.$store.state.sgisAccessToken==''){
        this.getSgisAccessToken()
      }
      else{
        var errCnt = 0;
        axios.get("https://sgisapi.kostat.go.kr/OpenAPI3/addr/stage.json?accessToken=" + this.$store.state.sgisAccessToken + "&cd=" + state).then(res=>{
          switch (parseInt(res.data.errCd)){
            case 0:
              this.cityData = [{value: null, text:'시/도'}]
              for (var temp of Object.entries(res.data.result)){
                var city = {text:'',value:0}
                city.text = temp[1].addr_name
                city.value = temp[1].full_addr
                this.cityData.push(city)
              }
            break;

            case -401:
              errCnt ++;
              if(errCnt<200){
                this.getSgisAccessToken();
              }
            break;
          }
        })
      }
    },
  },
  mounted(){
    this.getRoomList()
    this.getStateData()

    if(this.$store.state.subscribeList.connect == true){
      setTimeout(()=>this.$refs.detailRoom.subscribeRoom(), 500)
    }

    // console.log("mounted 실행")
    let token = this.$route.query.pg_token
    let process = this.$route.query.process
    if(process === "cancel" || process === "fail"){
      alert("결제가 취소됨")
      return;
    }
    
    if(token != null){
      console.log(token)
      axios.post(this.HOST + "/kakaoPaySuccess", token).then(res=>{
        console.log(res)
        this.$store.commit("setCash", res.data)
        this.$router.push('/kakaoPaysuccessView')
      })
    }
  },
  watch:{
    state:function(val){
      this.getCityData(val)
    },
    full_addr:function(val){
      console.log(val)
      if(val.split(' ')[0].length == 5){
        //console.log(val.split(' ')[0][0]+val.split(' ')[0][1])
        console.log(val.replace(val.split(' ')[0], val.split(' ')[0][0]+val.split(' ')[0][1]))
        this.searchRoomByLocation(this.searchAddr)
      }
      else if(val.split(' ')[0].length == 4){
        console.log(val.replace(val.split(' ')[0], val.split(' ')[0][0]+val.split(' ')[0][2]))
        this.searchRoomByLocation(this.searchAddr)
      }
      else{
        //do nothing
      }
    }
  },
  computed:{
    //이거써서 검색하면 됨
    searchAddr(){
      if(this.full_addr.split(' ')[0].length == 5){
        return this.full_addr.replace(this.full_addr.split(' ')[0], this.full_addr.split(' ')[0][0]+this.full_addr.split(' ')[0][1]);
      }
      else if(this.full_addr.split(' ')[0].length == 4){
        return this.full_addr.replace(this.full_addr.split(' ')[0], this.full_addr.split(' ')[0][0]+this.full_addr.split(' ')[0][2]);
      }
      else{
        return this.full_addr;
      }
    },
  }
};
</script>

<style scoped>
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
</style>
