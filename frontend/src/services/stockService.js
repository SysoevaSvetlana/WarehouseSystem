import api from './api';

export const stockService = {
  async getAll(params = {}) {
    const response = await api.get('/stock', { params });
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`/stock/${id}`);
    return response.data;
  },
};

