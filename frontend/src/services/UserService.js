import Client from "src/services/Client";

const API_URL = '/api/v1/users';


class UserService {
  getUser(username) {
    return Client.executeGet(API_URL + '/' + username)
      .then((response) => {
      return response.data;
    })
  }

  getTelegramCode (regCodeBody) {
    console.log('refresh token.')
    return Client.executePost(API_URL + '/registration_telegram', regCodeBody)
      .then((response) => {
      return response.data;
    });
  }
}

export default new UserService();
