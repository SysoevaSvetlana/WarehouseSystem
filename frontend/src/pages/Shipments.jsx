import React, { useState, useEffect } from 'react';
import { shipmentService } from '../services/shipmentService';
import { warehouseService } from '../services/warehouseService';
import { productService } from '../services/productService';
import './Common.css';

const Shipments = () => {
  const [shipments, setShipments] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [products, setProducts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);

  // Фильтры
  const [filters, setFilters] = useState({
    transactionType: '',
    warehouseId: '',
    fromDate: '',
    toDate: '',
  });

  const [formData, setFormData] = useState({
    warehouseId: '',
    type: 'incoming',
    items: [],
  });

  const [newItem, setNewItem] = useState({
    productId: '',
    count: 1,
  });

  useEffect(() => {
    loadShipments();
    loadWarehouses();
    loadProducts();
  }, [filters]);

  const loadShipments = async () => {
    try {
      const params = { page: 0, size: 100 };
      if (filters.transactionType) params.transactionType = filters.transactionType;
      if (filters.warehouseId) params.warehouseId = filters.warehouseId;
      if (filters.fromDate) params.fromDate = filters.fromDate;
      if (filters.toDate) params.toDate = filters.toDate;

      const data = await shipmentService.getAll(params);
      setShipments(data.content || []);
    } catch (error) {
      console.error('Error loading shipments:', error);
    }
  };

  const loadWarehouses = async () => {
    try {
      const data = await warehouseService.getAll({ page: 0, size: 100 });
      setWarehouses(data.content || []);
    } catch (error) {
      console.error('Error loading warehouses:', error);
    }
  };

  const loadProducts = async () => {
    try {
      const data = await productService.getAll({ page: 0, size: 100 });
      setProducts(data.content || []);
    } catch (error) {
      console.error('Error loading products:', error);
    }
  };

  const addItemToShipment = () => {
    if (newItem.productId && newItem.count > 0) {
      setFormData({
        ...formData,
        items: [...formData.items, { ...newItem }],
      });
      setNewItem({ productId: '', count: 1 });
    }
  };

  const removeItem = (index) => {
    setFormData({
      ...formData,
      items: formData.items.filter((_, i) => i !== index),
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      if (formData.type === 'incoming') {
        await shipmentService.createIncoming(formData.warehouseId, formData.items);
      } else if (formData.type === 'write-off') {
        await shipmentService.createWriteOff(formData.warehouseId, formData.items);
      }
      
      setShowModal(false);
      setFormData({ warehouseId: '', type: 'incoming', items: [] });
      loadShipments();
    } catch (error) {
      alert('Ошибка: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Поставки</h1>
        <button onClick={() => setShowModal(true)} className="btn-primary">
          Создать поставку
        </button>
      </div>

      {/* Фильтры */}
      <div className="filters-container">
        <div className="filter-group">
          <label>Тип операции:</label>
          <select
            value={filters.transactionType}
            onChange={(e) => setFilters({ ...filters, transactionType: e.target.value })}
          >
            <option value="">Все</option>
            <option value="incoming">Приход</option>
            <option value="write-off">Списание</option>
            <option value="outgoing">Отгрузка</option>
            <option value="transfer">Перемещение</option>
          </select>
        </div>

        <div className="filter-group">
          <label>Склад:</label>
          <select
            value={filters.warehouseId}
            onChange={(e) => setFilters({ ...filters, warehouseId: e.target.value })}
          >
            <option value="">Все</option>
            {warehouses.map((w) => (
              <option key={w.id} value={w.id}>{w.name}</option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label>Дата от:</label>
          <input
            type="date"
            value={filters.fromDate}
            onChange={(e) => setFilters({ ...filters, fromDate: e.target.value })}
          />
        </div>

        <div className="filter-group">
          <label>Дата до:</label>
          <input
            type="date"
            value={filters.toDate}
            onChange={(e) => setFilters({ ...filters, toDate: e.target.value })}
          />
        </div>

        <button
          className="btn-secondary"
          onClick={() => setFilters({ transactionType: '', warehouseId: '', fromDate: '', toDate: '' })}
        >
          Сбросить
        </button>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Тип</th>
            <th>Склад</th>
            <th>Дата</th>
            <th>Пользователь</th>
          </tr>
        </thead>
        <tbody>
          {shipments.map((shipment) => (
            <tr key={shipment.id}>
              <td>{shipment.id}</td>
              <td>{shipment.transactionType}</td>
              <td>{shipment.warehouse?.name}</td>
              <td>{shipment.date}</td>
              <td>{shipment.user?.username}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Создать поставку</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Склад</label>
                <select
                  value={formData.warehouseId}
                  onChange={(e) => setFormData({ ...formData, warehouseId: e.target.value })}
                  required
                >
                  <option value="">Выберите склад</option>
                  {warehouses.map((w) => (
                    <option key={w.id} value={w.id}>{w.name}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Тип операции</label>
                <select
                  value={formData.type}
                  onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                >
                  <option value="incoming">Приход</option>
                  <option value="write-off">Списание</option>
                </select>
              </div>

              <div className="form-group">
                <label>Товары</label>
                <div className="item-selector">
                  <select
                    value={newItem.productId}
                    onChange={(e) => setNewItem({ ...newItem, productId: e.target.value })}
                  >
                    <option value="">Выберите товар</option>
                    {products.map((p) => (
                      <option key={p.id} value={p.id}>{p.name}</option>
                    ))}
                  </select>
                  <input
                    type="number"
                    min="1"
                    value={newItem.count}
                    onChange={(e) => setNewItem({ ...newItem, count: parseInt(e.target.value) })}
                    placeholder="Количество"
                  />
                  <button type="button" onClick={addItemToShipment} className="btn-secondary">
                    Добавить
                  </button>
                </div>

                <ul className="items-list">
                  {formData.items.map((item, index) => {
                    const product = products.find(p => p.id == item.productId);
                    return (
                      <li key={index}>
                        {product?.name} - {item.count} шт.
                        <button type="button" onClick={() => removeItem(index)} className="btn-remove">
                          ✕
                        </button>
                      </li>
                    );
                  })}
                </ul>
              </div>

              <div className="modal-actions">
                <button type="submit" className="btn-primary" disabled={loading || formData.items.length === 0}>
                  {loading ? 'Сохранение...' : 'Сохранить'}
                </button>
                <button type="button" onClick={() => setShowModal(false)} className="btn-secondary">
                  Отмена
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Shipments;

