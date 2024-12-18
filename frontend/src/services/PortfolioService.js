import Client from "src/services/Client";

const API_URL = '/api/v1/portfolio'

class PortfolioService {

  gerView () {
    return Client.executeGet(API_URL + '/view').then((response) => {
      return response.data;
    })
  }

  updateBalances() {
    return Client.executeGet(API_URL + '/updateBalances').then((response) => {
      return response.status;
    })
  }
}

export default new PortfolioService();
