import axios from "axios";

export function axiosGetWithAuth(url) {
  // return authorization header with jwt token
  let user = JSON.parse(localStorage.getItem("user"));
  let headers = {};

  if (user && user.token) {
    headers["Authorization"] = "Bearer " + user.token;
  }

  return axios.get(url, { headers: headers });
}

export function axiosPostWithAuth(url, data) {
  // return authorization header with jwt token
  let user = JSON.parse(localStorage.getItem("user"));
  let headers = { withCredentials: true };

  if (user && user.token) {
    headers["Authorization"] = "Bearer " + user.token;
  }

  return axios.post(url, data, { headers: headers });
}

export function handleResponse(response) {
  console.log(response);
  if (!response.statusText === "OK") {
    if (response.status === 401) {
      // auto logout if 401 response returned from api
      localStorage.removeItem("user");
      window.location.reload(true);
    }

    const error = (response && response.message) || response.statusText;
    return Promise.reject(error);
  }

  return Promise.resolve(response);
}
