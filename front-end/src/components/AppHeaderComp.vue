<template>
  <div class="header">
    <a href="/home"><img src="../assets/premium-icon-food-5134814.png" width="50"></a>
    <!-- https://www.flaticon.com/kr/premium-icon/food-delivery_5873201?term=%EC%9D%8C%EC%8B%9D&page=1&position=31&page=1&position=31&related_id=5873201&origin=search --> 

    <b-button id="login" size="sm" @click="$router.push('login')">login</b-button>
  </div>
</template>

<script>
export default {

}
</script>
<style>
.header{
    margin-bottom: 20px;
    height: 110px;
    padding: 30px;
    background: whitesmoke;
    vertical-align: middle;
}
.header img{
    float: left;
    border-right: 100 solid black;
}
#login{
    float: right;
}
</style>

