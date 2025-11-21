import api from './api';

export const shipmentService = {
  async getAll(params = {}) {
    const response = await api.get('/shipments', { params });
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`/shipments/${id}`);
    return response.data;
  },

  async createIncoming(warehouseId, items) {
    const response = await api.post('/shipments/incoming', {
      warehouseId,
      items,
    });
    return response.data;
  },

  async createWriteOff(warehouseId, items) {
    const response = await api.post('/shipments/write-off', {
      warehouseId,
      items,
    });
    return response.data;
  },

  async createTransfer(fromWarehouseId, toWarehouseId, items) {
    const response = await api.post('/shipments/transfer', {
      fromWarehouseId,
      toWarehouseId,
      items,
    });
    return response.data;
  },
};

