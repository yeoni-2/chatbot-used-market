let map;

function sample4_execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function(data) {
      // get dong name
      if (data.jibunAddress === '' && data.autoJibunAddress === '') console.error("no data");
      const jAddr = data.jibunAddress || data.autoJibunAddress;
      const jAddrArr = jAddr.split(" ");
      let p = -1;
      for (let i=jAddrArr.length-1; i>=0; --i){
        if (/동$/gi.test(jAddrArr[i])){
          p = i;
          break;
        }
      }
      const dongName = jAddrArr.slice(0, p+1).join(" ");
      if (!!dongName){
        document.querySelector("#dongNameResult").value = dongName;
        document.querySelector("#dongName").value = dongName;
        document.querySelector("#submit").removeAttribute("disabled");
      }

      let roadAddr = data.roadAddress;

      document.querySelector("#location").value = roadAddr;
    }
  }).open();
}

function getCurrentLocation(){
  const status = document.querySelector("#status");

  function success(position){
    const latitude = position.coords.latitude;
    const longitude = position.coords.longitude;

    status.textContent = "";

    // document.querySelector("#result").textContent = `latitude: ${latitude}, longitude: ${longitude}`;

    createMapMarker(latitude, longitude);
    changeMapCenter({ lat: latitude, lng: longitude });
    document.querySelector("#lat").value = latitude;
    document.querySelector("#lng").value = longitude;
  }

  function error(error){
    status.textContent = "unable to retrieve your current location";
    console.error(error);
  }

  if (!navigator.geolocation) status.textContent = "geolocation is not supported by your browser";
  else {
    status.textContent = "Locating...";
    navigator.geolocation.getCurrentPosition(success, error);
  }
}

function userAgreeHandler(e){
  if (e.target.checked){
    document.querySelector("#inputLocationBtn").removeAttribute("disabled");
    getCurrentLocation();
  }else {
    document.querySelector("#inputLocationBtn").setAttribute("disabled", "");
    document.querySelector("#submit").setAttribute("disabled", "");
  }
}

function initMap(){
  map = new google.maps.Map(document.querySelector('#map'), {
    zoom: 15,
    center: { lat: 37.579795, lng: 126.977062 },
    mapId: "USER_LOCATION_MAP",
    cameraControl: false,
    mapTypeControl: false,
    streetViewControl: false,
    rotateControl: false,
    fullscreenControl: false,
  });
}

function createMapMarker(lat, lng){
  const marker = new google.maps.marker.AdvancedMarkerElement({
    map: map,
    position: { lat, lng },
  });
}

function changeMapCenter(coord){
  map.panTo(coord);
  map.setZoom(15);
}

addEventListener("load", (e) => {
  document.querySelector("#userAgree").addEventListener("change", userAgreeHandler);
  initMap();
});