import api from './api';

export const userService = {
  async getAll(params = {}) {
    const response = await api.get('/users', { params });
    return response.data;
  },

  async updateRole(id, role) {
    const response = await api.patch(`/users/${id}/role`, { role });
    return response.data;
  },

  async delete(id) {
    const response = await api.delete(`/users/${id}`);
    return response.data;
  },
};

