<template>
  <table>
    <tr class="title">
      <slot name="roomTitle"></slot>
    </tr>
  </table>
  <table class="roomDetailTable">
    <tr>
    <table>
      <td class="num-price">💰 <slot name="price"></slot></td>
      <td class="location">🚩 <slot name="location"></slot></td>
    </table>
    </tr>
  </table>
</template>

<script>
export default {
  name: "RoomListRoomComp",
  components: {},
  data() {
    return {
      title: "",
      numPeople: "",
      numPeopleMax: "",
      location: "",
      currentPrice: "",
      needPrice: "",
    };
  },
  methods: {},
};
</script>

<style scoped>
td {
  padding-left: 0.8em;
  padding-right: 0.8em;
}
table{
  width: 100%;
  border: 0px;
}
.title {
  text-align: left;
  font-size: 2.7em;
}
.num-people {
  text-align: right;
}
.location {
  text-align: right;
}
.num-price {
  text-align: left;
}
.roomDetailTable{
  margin-top: 0.8em;
}
</style>