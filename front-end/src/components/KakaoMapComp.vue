<template>
  <div id="map" style="width:100%;height:400px;"/>
</template>

<script>
export default {
  data() {
    return { //https://codesandbox.io/s/nervous-keldysh-87yxg?file=/src/components/KakaoMap.vue:546-3501
      mapLat: {
          x: 35.8487259,
          y: 128.5585313
      } 
    };
  },
  methods:{
    initMap(){
      var map = null
      const container = document.getElementById("map");
      const options = {
        center: new kakao.maps.LatLng(this.mapLat.x, this.mapLat.y), // 지도의 중심좌표
        level: 5 // 지도의 확대 레벨
      };
      map = new kakao.maps.Map(container, options)
      map
    },
    changeMap(addr){
      console.log(addr)
      var map = null;
      const container = document.getElementById("map");
      const options = {
        center: new kakao.maps.LatLng(this.mapLat.x, this.mapLat.y), // 지도의 중심좌표
        level: 5 // 지도의 확대 레벨
      };
      map = new kakao.maps.Map(container, options)
      var geocoder = new kakao.maps.services.Geocoder();
      // 주소로 좌표를 검색합니다
      geocoder.addressSearch(addr, function(result, status) {
          // 정상적으로 검색이 완료됐으면 
          if (status === kakao.maps.services.Status.OK) {
              var coords = new kakao.maps.LatLng(result[0].y, result[0].x);
              // 결과값으로 받은 위치를 마커로 표시합니다
              var marker = new kakao.maps.Marker({
                  map: map,
                  position: coords
              });
              // 인포윈도우로 장소에 대한 설명을 표시합니다
              var infowindow = new kakao.maps.InfoWindow({
                  content: '<div style="width:150px;text-align:center;padding:6px 0;" id="addrMarker">'+ addr + '</div>'
              });
              infowindow.open(map, marker);

              // 지도의 중심을 결과값으로 받은 위치로 이동시킵니다
              map.setCenter(coords);
          } 
      });
      // 일반 지도와 스카이뷰로 지도 타입을 전환할 수 있는 지도타입 컨트롤을 생성합니다
      var mapTypeControl = new kakao.maps.MapTypeControl();
      // 지도에 컨트롤을 추가해야 지도위에 표시됩니다
      // kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 TOPRIGHT는 오른쪽 위를 의미합니다
      map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);
      
      var zoomControl = new kakao.maps.ZoomControl();
      map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
      // 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 생성합니다  
    },
    showAddr(Lng, Lat){
      var geocoder = new kakao.maps.services.Geocoder();
      geocoder.coord2Address(Lng, Lat, function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
          if(result[0].road_address!=null){
            return result[0].road_address.address_name
          }
          else{
            result[0].address.address_name
          }
        }
      });
    }
  },
  mounted() {
    if(!window.kakao || window.kakao.maps){
      const script = document.createElement("script");
      script.src = "//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=59a464bf1d4f9a4bf5530d15b31ca9f2&libraries=services"
      /* global kakao*/
      script.onload = () => kakao.maps.load(this.initMap);
      document.head.appendChild(script);
      console.log("now mount")
    }
  },
}
</script>