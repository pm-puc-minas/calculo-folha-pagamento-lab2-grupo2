import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const api = axios.create({
  baseURL: API_URL
});

api.interceptors.request.use((config) => {
  return config;
});

export async function downloadRelatorioPdf(login) {
  const response = await api.get(`/folha/relatorio/${encodeURIComponent(login)}`, {
    responseType: 'blob',
  });
  return response.data;
}
