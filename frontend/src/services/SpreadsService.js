import Client from "src/services/Client";

const API_URL = '/api/v1/spreads'

class SpreadsService {

  gerSpreads () {
    return Client.executeGet(API_URL).then((response) => {
      console.log(response.data)
      return response.data;
    })
  }
}

export default new SpreadsService();
