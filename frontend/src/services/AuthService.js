import axios from "axios";
import VueJwtDecode from 'vue-jwt-decode';

const API_URL = '/api/v1/auth';
const jwtDecoder = VueJwtDecode;

function saveUserData (response) {
  let claims = jwtDecoder.decode(response.data.token);
  localStorage.setItem("username", JSON.stringify(claims.sub))
  localStorage.setItem("authorities", JSON.stringify(claims.authority))
  localStorage.setItem("jwt_token", JSON.stringify(response.data.token))
  localStorage.setItem("refresh_token", JSON.stringify(response.data.refreshToken))
}

function clearUserData () {
  localStorage.removeItem('authorities');
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('refresh_token');
}

class AuthService {
  login(user) {
    return axios
      .post(API_URL, {
        username: user.username,
        password: user.password
      })
      .then(response => {
        saveUserData(response);
        return response.status;
      }).catch( (error) => {
        return error.status;
      });
  }

  refresh () {
    console.log('refresh token.')
    return axios
      .post(API_URL + '/refresh', {
        refreshToken: JSON.parse(localStorage.getItem("refresh_token")) || null
      })
      .then(response => {
        saveUserData(response);
        return response.status;
      })
      .catch((error) => {
        return error.status;
      })
  }

  logout () {
    clearUserData();
  }

}

export default new AuthService();
