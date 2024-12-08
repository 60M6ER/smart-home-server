import Client from "src/services/Client";

const API_URL = '/api/v1/exchanges'

class ExchangeService {

  gerAll () {
    return Client.executeGet(API_URL).then((response) => {
      return response.data;
    })
  }

  getExchange (vendor) {
    return Client.executeGet(API_URL + '/' + vendor)
      .then((response) => {
        return response.data;
      })
  }

  saveExchange (exchange) {
    return Client.executePost(API_URL, exchange)
      .then((response) => {
        return response.status;
      })
  }

}

export default new ExchangeService();
