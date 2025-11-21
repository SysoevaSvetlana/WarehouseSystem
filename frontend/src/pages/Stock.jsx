import React, { useState, useEffect } from 'react';
import { stockService } from '../services/stockService';
import { warehouseService } from '../services/warehouseService';
import './Common.css';

const Stock = () => {
  const [stocks, setStocks] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [filters, setFilters] = useState({
    productName: '',
    warehouseId: '',
  });

  useEffect(() => {
    loadWarehouses();
  }, []);

  useEffect(() => {
    loadStocks();
  }, [filters]);

  const loadStocks = async () => {
    try {
      const params = { page: 0, size: 100 };
      if (filters.productName) params.productName = filters.productName;
      if (filters.warehouseId) params.warehouseId = filters.warehouseId;

      const data = await stockService.getAll(params);
      setStocks(data.content || []);
    } catch (error) {
      console.error('Error loading stocks:', error);
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

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Остатки на складах</h1>
      </div>

      {/* Фильтры */}
      <div className="filters-container">
        <div className="filter-group">
          <label>Поиск по товару:</label>
          <input
            type="text"
            value={filters.productName}
            onChange={(e) => setFilters({ ...filters, productName: e.target.value })}
            placeholder="Введите название товара"
          />
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

        <button
          className="btn-secondary"
          onClick={() => setFilters({ productName: '', warehouseId: '' })}
        >
          Сбросить
        </button>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Товар</th>
            <th>Склад</th>
            <th>Количество</th>
            <th>Последнее обновление</th>
          </tr>
        </thead>
        <tbody>
          {stocks.map((stock) => (
            <tr key={stock.id}>
              <td>{stock.id}</td>
              <td>{stock.product?.name}</td>
              <td>{stock.warehouse?.name}</td>
              <td>{stock.count}</td>
              <td>{new Date(stock.lastUpdate).toLocaleString('ru-RU')}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Stock;

