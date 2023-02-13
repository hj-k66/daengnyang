//로그인 이후 화면에 붙이기
const firebaseModule = (function () {
  async function init() {
    // Your web app's Firebase configuration
    if ('serviceWorker' in navigator) {
      window.addEventListener('load', function () {
        navigator.serviceWorker.register('/firebase-messaging-sw.js')
        .then(registration => {
          const firebaseConfig = {
            apiKey: "AIzaSyCUnOt-IbJVa8XIXm4wU_097CLWGSFFKA0",
            authDomain: "daengnyang-e1df7.firebaseapp.com",
            projectId: "daengnyang-e1df7",
            storageBucket: "daengnyang-e1df7.appspot.com",
            messagingSenderId: "685788055963",
            appId: "1:685788055963:web:e6e38b48dd874d7d80393e",
            measurementId: "G-98R8NEJEF1"
          };
          // Initialize Firebase
          firebase.initializeApp(firebaseConfig);

          // Show Notificaiton Dialog
          const messaging = firebase.messaging();
          messaging.requestPermission() //권한 요청 화면
          .then(function () {
            return messaging.getToken();
          })
          .then(async function (token) {
            console.log(token);
            await fetch('/api/v1/notification',
                {
                  method: 'post',
                  headers: {
                    Authorization: "Bearer " + localStorage.getItem(
                        "accessToken"),
                  },
                  body: token
                })
            messaging.onMessage(payload => {
              const title = payload.notification.title
              const options = {
                body: payload.notification.body
              }
              navigator.serviceWorker.ready.then(registration => {
                registration.showNotification(title, options);
              })
            })
          })
          .catch(function (err) {
            console.log("Error Occured");
          })
        })
      })
    }
  }

  return {
    init: function () {
      init()
    }
  }
})()

firebaseModule.init()
