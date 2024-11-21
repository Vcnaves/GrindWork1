import axios from "axios";

const Api = axios.create({
  baseURL: 'https://grindwork-api.onrender.com',
  timeout: 20000,
  headers: { "Content-Type": "application/json" },
});

// Adicione o interceptor aqui
Api.interceptors.request.use(
  (config) => {
    
    const token = localStorage.getItem("authToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default Api;
