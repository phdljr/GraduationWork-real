import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

export const store = new Vuex.Store({
  state: {
    login: false,
    stompSocket: null,

    // 이거 mutation 추가해두기
    subscribeList: {
      connect: null, // 연결됐다면 true로 표시
      menu: null,
      chat: null,
      notify: null,
    },
    //

    userData: {
      userNickname: null,
      userEmail: null,
      userCash: 0,
      chargedCash: 0,
      enterRoomId: null,
    },
    sgisAccessToken: "",
    location: "",
  },
  mutations: {
    login(state, userData) {
      state.login = true;
      state.userData = userData;
    },
    logout(state) {
      state.login = false;
      state.userData = {
        userNickname: null,
        userEmail: null,
        userCash: 0,
        chargedCash: 0,
        enterRoomId: null,
      };
    },
    setCash(state, cash) {
      state.userData.userCash = cash;
    },
    tryAddCash(state, cash){
      state.userData.chargedCash = cash;
    },
    enterRoom(state, roomId) {
      state.userData.enterRoomId = roomId;
      state.subscribeList.connect = true;
    },
    exitRoom(state) {
      state.userData.enterRoomId = null;
      state.subscribeList.connect = false;
      state.subscribeList.chat = null;
      state.subscribeList.menu = null;
      state.subscribeList.notify = null;
    },
    connectSocket(state, socket) {
      state.stompSocket = socket;
    },
    setSgisAccessToken(state, token) {
      state.sgisAccessToken = token;
    },
    setLocation(state, locationString) {
      state.location = locationString;
    },
    setSubscribe(state, connect){
      state.subscribeList.connect = connect;
    },
    setSubscribeChat(state, chat) {
      state.subscribeList.chat = chat;
    },
    setSubscribeMenu(state, menu) {
      state.subscribeList.menu = menu;
    },
    setSubscribeNotify(state, notify) {
      state.subscribeList.notify = notify;
    },
  },
  plugins: [createPersistedState()], //vuex 데이터 지속
});
