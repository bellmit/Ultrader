import axios from "axios";

const axiosInstance = axios.create({});

/**
  * @description if any of the API gets 401 status code, this method
   calls getAuthToken method to renew accessToken
  * updates the error configuration and retries all failed requests
  again
*/

axiosInstance.interceptors.response.use(undefined, err => {
  const error = err.response;
  // if error is 401
  if (error.status === 401 || error.status === 403) {
    // auto logout if 401 response returned from api
    localStorage.removeItem("user");
    window.location.reload(true);
  }
});

export function getAuthHeader() {
  // return authorization header with jwt token
  let user = JSON.parse(localStorage.getItem("user"));
  let headers = {};

  if (user && user.token) {
    headers["Authorization"] = "Bearer " + user.token;
  }

  return headers;
}

export function axiosGetWithAuth(url) {
  // return authorization header with jwt token
  let user = JSON.parse(localStorage.getItem("user"));
  let headers = {};

  if (user && user.token) {
    headers["Authorization"] = "Bearer " + user.token;
  }

  return axiosInstance.get(url, { headers: headers });
}

export function axiosPostWithAuth(url, data) {
  // return authorization header with jwt token
  let user = JSON.parse(localStorage.getItem("user"));
  let headers = { withCredentials: true };

  if (user && user.token) {
    headers["Authorization"] = "Bearer " + user.token;
  }

  return axiosInstance.post(url, data, { headers: headers });
}

export function axiosDeleteWithAuth(url, data) {
  // return authorization header with jwt token
  let user = JSON.parse(localStorage.getItem("user"));
  let headers = { withCredentials: true };

  if (user && user.token) {
    headers["Authorization"] = "Bearer " + user.token;
  }

  return axiosInstance.delete(url, data, { headers: headers });
}

export function handleResponse(response) {
  if (!response.statusText === "OK") {
    if (response.status === 401 || response.status === 403) {
      // auto logout if 401 response returned from api
      localStorage.removeItem("user");
      window.location.reload(true);
    }

    const error = (response && response.message) || response.statusText;
    return Promise.reject(error);
  }

  return Promise.resolve(response);
}
