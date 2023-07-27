<template>
  <div class="app">
    <b-button id="sidebar-hide" @click="sidebarOn = !sidebarOn" v-if="sidebarOn"
      >&lt;</b-button
    >
    <b-button id="sidebar-show" @click="sidebarOn = !sidebarOn" v-else
      >&gt;</b-button
    >
    <div class="sidebar" v-if="sidebarOn">
      <AppSidebarComp />
    </div>
    <div :class="[sidebarOn ? 'content' : 'content-no-sidebar']">
      <!-- 동적 클래스 전환 -->
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import AppSidebarComp from "./components/AppSidebarComp.vue";
import { store } from "./store/store.js"

export default {
  name: "App",
  components: {
    AppSidebarComp,
  },
  data() {
    return {
      sidebarOn: true,
      latitude: "",
      longitude: "",
      textContent: "",
      address: "",
    };
  },
  methods: {
    testData() {
      console.log("mount");
      this.$store.commit("login", {
        userNickname: "나재현",
        userEmail: "zkxmwogus@naver.com",
        userCash: 30000,
        chargedCash: null,
      });
    },
    connectSocket() {
      // console.log("소켓 연결 시도11")
      // console.log(this.HOST+"/connect")
      // let sock = new SockJS(this.HOST+"/connect");
      let sock = new SockJS("http://localhost:8080/connect");
      this.$store.commit("connectSocket", Stomp.over(sock));
      this.$store.state.stompSocket.connect(
        {},
        (frame) => {
          console.log("소캣 연결 완료, " + frame);
        },
        (err) => {
          console.log(err);
        }
      );
    },
    async geofind() {
      // get position
      await this.$getLocation()
      .then((coordinates) => {
        this.latitude = coordinates.lat
        this.longitude = coordinates.lng
      })
      .catch((error) => {
        console.log(error);
      });
    },
    showAddr() {
      var geocoder = new kakao.maps.services.Geocoder();
      geocoder.coord2Address(
        this.longitude,
        this.latitude,
        function (result, status) {
          if (status === kakao.maps.services.Status.OK) {
            if (result[0].road_address != null) {
              console.log(result[0].road_address.address_name);
              store.commit(
                "setLocation",
                result[0].road_address.address_name
              );
            } else {
              console.log(result[0].address.address_name);
              store.commit("setLocation", result[0].address.address_name);
            }
          }
        }
      );
    },
  },
  created() {
    // this.testData()
    this.connectSocket();
  },
  watch: {
    latitude: function () {
      console.log("lat changed");
      this.showAddr();
    },
    textContent: function(val){
      console.log(val)
    }
  },
  mounted() {
    if (!window.kakao || window.kakao.maps) {
      const script = document.createElement("script");
      script.src =
        "//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=59a464bf1d4f9a4bf5530d15b31ca9f2&libraries=services";
      /* global kakao*/
      script.onload = () => kakao.maps.load(this.geofind);
      document.head.appendChild(script);
      console.log("now mount");
      //this.geofind()
    }
  },
};
</script>

<style>
.app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  background-color: #f0f0f0;
  margin-top: 0px;
  width: 100%;
  height: 100%;
  position: fixed;
}
.sidebar {
  width: 320px;
  box-sizing: border-box;
  position: fixed;
  left: 0;
  height: 100%;
  z-index: 1;
}
.content {
  margin-left: 320px;
  position: fixed;
  height: 100%;
  padding: 100px;
  padding-top: 50px;
  width: calc(100% - 320px);
  overflow: auto;
}
.content-no-sidebar {
  margin-left: 0px;
  position: fixed;
  height: 100%;
  padding: 100px;
  padding-top: 50px;
  width: calc(100%);
  overflow: auto;
}
#sidebar-show {
  position: fixed;
  top: calc(50% - 20px);
  left: 0px;
  z-index: 3;
}
#sidebar-hide {
  position: fixed;
  top: calc(50% - 20px);
  left: 320px;
  z-index: 3;
}
</style>
