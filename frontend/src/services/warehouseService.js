import api from './api';

export const warehouseService = {
  async getAll(params = {}) {
    const response = await api.get('/warehouses', { params });
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`/warehouses/${id}`);
    return response.data;
  },

  async create(warehouse) {
    const response = await api.post('/warehouses', warehouse);
    return response.data;
  },

  async update(id, warehouse) {
    const response = await api.put(`/warehouses/${id}`, warehouse);
    return response.data;
  },

  async delete(id) {
    const response = await api.delete(`/warehouses/${id}`);
    return response.data;
  },
};

