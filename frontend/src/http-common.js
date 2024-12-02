import axios from "axios";

export default axios.create({
  baseURL: "http://${SERVER_HOST:localhost}:${SERVER_PORT:8081}/api/v1/",
  headers: {
    "Content-type": "application/json"
  }
})
