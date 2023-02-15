importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-messaging.js');

// Initialize Firebase
let firebaseConfig = {
  apiKey: "AIzaSyCUnOt-IbJVa8XIXm4wU_097CLWGSFFKA0",
  authDomain: "daengnyang-e1df7.firebaseapp.com",
  projectId: "daengnyang-e1df7",
  storageBucket: "daengnyang-e1df7.appspot.com",
  messagingSenderId: "685788055963",
  appId: "1:685788055963:web:e6e38b48dd874d7d80393e",
  measurementId: "G-98R8NEJEF1"
};
firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();
