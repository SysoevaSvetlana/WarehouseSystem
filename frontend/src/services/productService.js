import api from './api';

export const productService = {
  async getAll(params = {}) {
    const response = await api.get('/products', { params });
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`/products/${id}`);
    return response.data;
  },

  async create(product) {
    const response = await api.post('/products', product);
    return response.data;
  },

  async update(id, product) {
    const response = await api.put(`/products/${id}`, product);
    return response.data;
  },

  async delete(id) {
    const response = await api.delete(`/products/${id}`);
    return response.data;
  },
};

