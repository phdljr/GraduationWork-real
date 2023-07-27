<template>
  <b-modal id="chatModal" title="채팅" scrollable>
    <div class="ChatLog">
      <div
        :key="key"
        v-for="(chat, key) in receivedChatData"
        :class="
          chat.sender == $store.state.userData.userNickname
            ? 'MychatBox'
            : 'OpponentchatBox'
        "
      >
        <div v-if="chat.sender != $store.state.userData.userNickname">
          {{ chat.sender }}
        </div>
        <div
          :class="
            chat.sender == $store.state.userData.userNickname
              ? 'MyChat'
              : 'OpponentChat'
          "
        >
          {{ chat.content }}
        </div>
      </div>
    </div>
  </b-modal>
</template>

<script>
export default {
  name: "chatComp",
  components: {},
  data() {
    return {
      // sendChatData: [],
      receivedChatData: [],
    };
  },
  methods: {
    sendMsg() {
      const msg = document.getElementById("MsgInput").value;
      if (msg != "\n" || msg != "") {
        console.log(msg);
        console.log("send");
      }
      document.getElementById("MsgInput").value = "";
      // console.log(this.$store.state.userData.enterRoomId)
      
      // 공백은 못보내게 하기
      if(msg == ""){
        return
      }

      // 서버로 메세지 보내기
      this.$store.state.stompSocket.send(
        "/chat/send/" + this.$store.state.userData.enterRoomId,
        JSON.stringify({
          sender: this.$store.state.userData.userNickname,
          content: msg,
        }),
        {}
      );
      // this.sendChatData.push(msg)
    },
    receivedcMsg(msg) {
      this.receivedChatData.push(msg);
    },
    pressEnter(e) {
      if (e.keyCode == 13 && !e.shiftKey) {
        this.sendMsg();
      }
    },
    clearEnter(e){
      if (e.keyCode == 13 && !e.shiftKey) {
        document.getElementById("MsgInput").value = "";
      }
    }
  },
  mounted() {
    const footerHtml =
      '<div class="MsgSender"><textarea id="MsgInput" rows="3"></textarea><button type="button" id="MsgSendButton" class="btn btn-success">전송</button></div>';
    document
      .getElementById("chatModal")
      .getElementsByClassName("modal-footer")[0].innerHTML = footerHtml;
    document.getElementById("MsgSendButton").onclick = this.sendMsg;
    document.getElementById("MsgInput").onkeypress = this.pressEnter;
    document.getElementById("MsgInput").onkeyup = this.clearEnter;
  },
};
</script>

<style>
.ChatLog{
  height: 70vh;
}
.chatModal{
  min-height: 70vh;
  max-height: 70vh;
}
.OpponentchatBox {
  text-align: left;
  clear: both;
}
.MychatBox {
  text-align: right;
  clear: both;
}
.MyChat {
  border-radius: 0.7em;
  border-bottom-right-radius: 0px;
  padding: 0.6em;
  text-align: right;
  width: fit-content;
  min-width: 30px;
  max-width: 90%;
  text-overflow: clip;
  background: #afffba;
  float: right;
  clear: both;
}

.OpponentChat {
  border-radius: 0.7em;
  border-bottom-left-radius: 0px;
  padding: 0.6em;
  text-align: left;
  width: fit-content;
  min-width: 30px;
  max-width: 90%;
  text-overflow: clip;
  background: #e3e3e3;
  float: left;
  clear: both;
}

.OpponentInfo {
  float: left;
  clear: both;
}
.ChatTime {
  clear: both;
  margin-bottom: 1em;
  font-size: 0.7em;
}
.MsgSender {
  width: 100%;
  height: 5em;
}
.MsgSender textarea {
  width: calc(100% - 5em);
  resize: none;
  overflow: auto;
  height: 100%;
}
.MsgSender button {
  width: 4.5em;
  height: 100%;
  float: right;
}
</style>
