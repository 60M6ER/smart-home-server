import axios from "axios";
import AuthService from "src/services/AuthService";
import authHeader from "src/services/auth-header";

class Client {
  executeGet (url, refresh = true) {
    return axios.get(url, { headers: authHeader() })
      .then((response) => {
        console.log('response status: ' + response.status)
        return response;
      })
      .catch((error) => {
        if (error.status === 401 || error.status === 403) {
          console.log('error status: ' + error.status)
          if (refresh){
            AuthService.refresh().then((code) => {
              if (code === 200){
                return this.executeGet(url, false);
              } else {
                AuthService.logout()
              }
            })
          } else {
            AuthService.logout();
          }
        }
      })
  }

  executePost (url, body, refresh = true) {
    return axios.post(url, body,{ headers: authHeader() })
      .then((response) => {
        console.log('response status: ' + response.status)
        return response;
      })
      .catch((error) => {
        console.log('error status: ' + error.status)
        if (error.status === 401 || error.status === 403) {
          if (refresh){
            AuthService.refresh().then((code) => {
              if (code === 200){
                return this.executePost(url, body, false);
              } else {
                AuthService.logout()
              }
            })
          } else {
            AuthService.logout();
          }
        }
      })
  }
}

export default new Client();


