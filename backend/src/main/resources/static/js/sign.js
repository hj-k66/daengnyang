async function logout() {

  /** 로컬스토리지에 담긴 토큰 꺼내기 */
  const accessToken = localStorage.getItem('accessToken');

  /** 쿠키 삭제  */
  function deleteCookie(cName) {
    var expireDate = new Date();
    expireDate.setDate(expireDate.getDate() - 1);
    document.cookie = cName + "= " + "; expires=" + expireDate.toGMTString() + "; path=/";
  }

  /** 로그아웃 */
  await axios.post(`/api/v1/users/logout`, {
    "accessToken": accessToken,
  }, {
    headers: {
      "Content-Type": `application/json`,
      Authorization: "Bearer " + localStorage.getItem("accessToken"),
    },
  }).then(res => {
    console.log(res)
    if (res.data.resultCode == 'SUCCESS') {
      console.log("바로 실행 로그");
      console.log(res.data.resultCode);

      localStorage.clear();
      sessionStorage.clear();
      localStorage.removeItem('accessToken')
      deleteCookie('refreshToken');

      document.location.href = "/view/users/login";
    } else {
      console.log("실패" + res.resultCode)
    }
  });
}

/** 헤더에 토큰값이 없으면 담아줌 */
axios.interceptors.request.use(function (config) {
  console.log("인터셉터 시작")
  const accessToken = localStorage.getItem('accessToken');

  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
})

// /** 토큰 만료시 재발급 */
// axios.interceptors.response.use(
//     (response) => {
//       return response;
//     },
//     async (error) => {
//       const {
//         config,
//         response,
//         response: { status },
//       } = error;
//
//       const originalRequest = config;
//       console.log("if문 가기 전")
//       console.log("config" + JSON.stringify(config));
//       console.log("data" + JSON.stringify(response.data));
//       const errorCode = response.data.result.errorCode;
//
//       function deleteCookie(cName) {
//         var expireDate = new Date();
//         expireDate.setDate(expireDate.getDate() - 1);
//         document.cookie = cName + "= " + "; expires=" + expireDate.toGMTString() + "; path=/";
//       }
//
//       if(response.data.result.message === '잘못된 토큰입니다. 서명 불일치') {
//         localStorage.clear()
//         deleteCookie('refreshToken')
//         return document.location.reload()
//       }
//
//       console.log("errorCode" + JSON.stringify(errorCode))
//
//       if (status === 401 && errorCode === 'INVALID_TOKEN' ) {
//         console.log("if문 시작")
//         const accessToken = localStorage.getItem('ACCESS_TOKEN');
//         const cookie = document.cookie;
//
//         try {
//           await axios.post(`/api/v1/users/new-token`, {
//             "accessToken": accessToken,
//             "refreshToken": cookie
//           }, {
//             headers: {
//               null,
//             },
//           }).then(res => {
//             console.log('들어오는지 확인을 위한 콘솔')
//             const newAccessToken = res.data.result.accessToken;
//             originalRequest.headers = {
//               'Content-Type': 'application/json',
//               Authorization: 'Bearer ' + newAccessToken,
//             };
//             console.log('토큰이 정상인지 확인' + newAccessToken);
//             localStorage.setItem('ACCESS_TOKEN', newAccessToken);
//           })
//           return await axios(originalRequest);
//
//         } catch (err) {
//           new Error(err);
//         }
//       }
//       return Promise.reject(error);
//     }
// );