// src/services/api.js
import axios from 'axios';
import { handleMockRequest } from './mockBackend';

const api = axios.create({
  baseURL: 'http://localhost:9090', // Bytt ut med din backend-URL
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ---------------------------------------------------------------------------
// Hybrid adapter
//
// The real API is a Spring Boot backend on http://localhost:9090. When that
// backend is running, every request goes straight to it and behaviour is
// unchanged. When it is NOT reachable (for example inside the v0 preview, which
// has no Java/PostgreSQL runtime), we transparently fall back to an in-memory
// mock backend so the app stays fully usable with realistic demo data.
// ---------------------------------------------------------------------------

const realAdapter = axios.getAdapter(axios.defaults.adapter);
let useMock = false;

async function resolveMock(config) {
  const response = await handleMockRequest(config);
  return { ...response, config, request: {} };
}

api.defaults.adapter = async function hybridAdapter(config) {
  if (useMock) {
    return resolveMock(config);
  }

  try {
    return await realAdapter(config);
  } catch (error) {
    const backendUnreachable =
      !error.response &&
      (error.code === 'ERR_NETWORK' ||
        error.code === 'ECONNREFUSED' ||
        error.code === 'ECONNABORTED' ||
        error.message === 'Network Error');

    if (backendUnreachable) {
      useMock = true;
      console.warn(
        '[api] Backend på http://localhost:9090 er ikke tilgjengelig – bruker innebygd demo-data (mock backend).',
      );
      return resolveMock(config);
    }

    throw error;
  }
};

export default api;
