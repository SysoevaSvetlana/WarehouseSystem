import React, { useState, useEffect } from 'react';
import { warehouseService } from '../services/warehouseService';
import { authService } from '../services/authService';
import './Common.css';

const Warehouses = () => {
  const [warehouses, setWarehouses] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [searchName, setSearchName] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    location: '',
  });

  const isAdmin = authService.isAdmin();

  useEffect(() => {
    loadWarehouses();
  }, [searchName]);

  const loadWarehouses = async () => {
    try {
      const params = { page: 0, size: 100 };
      if (searchName) params.name = searchName;

      const data = await warehouseService.getAll(params);
      setWarehouses(data.content || []);
    } catch (error) {
      console.error('Error loading warehouses:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      await warehouseService.create(formData);
      setShowModal(false);
      setFormData({ name: '', location: '' });
      loadWarehouses();
    } catch (error) {
      alert('Ошибка: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Удалить склад?')) {
      try {
        await warehouseService.delete(id);
        loadWarehouses();
      } catch (error) {
        alert('Ошибка: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Склады</h1>
        {isAdmin && (
          <button onClick={() => setShowModal(true)} className="btn-primary">
            Создать склад
          </button>
        )}
      </div>

      {/* Фильтр поиска */}
      <div className="filters-container">
        <div className="filter-group">
          <label>Поиск по названию:</label>
          <input
            type="text"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            placeholder="Введите название склада"
          />
        </div>
        <button
          className="btn-secondary"
          onClick={() => setSearchName('')}
        >
          Сбросить
        </button>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Название</th>
            <th>Адрес</th>
            {isAdmin && <th>Действия</th>}
          </tr>
        </thead>
        <tbody>
          {warehouses.map((warehouse) => (
            <tr key={warehouse.id}>
              <td>{warehouse.id}</td>
              <td>{warehouse.name}</td>
              <td>{warehouse.location}</td>
              {isAdmin && (
                <td>
                  <button onClick={() => handleDelete(warehouse.id)} className="btn-danger">
                    Удалить
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Создать склад</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Название</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Адрес / Локация</label>
                <input
                  type="text"
                  value={formData.location}
                  onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                />
              </div>

              <div className="modal-actions">
                <button type="submit" className="btn-primary" disabled={loading}>
                  {loading ? 'Сохранение...' : 'Создать'}
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

export default Warehouses;

