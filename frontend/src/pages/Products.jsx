import React, { useState, useEffect } from 'react';
import { productService } from '../services/productService';
import './Common.css';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [searchName, setSearchName] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    unit: '',
    description: '',
  });

  useEffect(() => {
    loadProducts();
  }, [searchName]);

  const loadProducts = async () => {
    try {
      const params = { page: 0, size: 100 };
      if (searchName) params.name = searchName;

      const data = await productService.getAll(params);
      setProducts(data.content || []);
    } catch (error) {
      console.error('Error loading products:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      await productService.create(formData);
      setShowModal(false);
      setFormData({ name: '', unit: '', description: '' });
      loadProducts();
    } catch (error) {
      alert('Ошибка: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Удалить товар?')) {
      try {
        await productService.delete(id);
        loadProducts();
      } catch (error) {
        alert('Ошибка: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Товары</h1>
        <button onClick={() => setShowModal(true)} className="btn-primary">
          Создать товар
        </button>
      </div>

      {/* Фильтр поиска */}
      <div className="filters-container">
        <div className="filter-group">
          <label>Поиск по названию:</label>
          <input
            type="text"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            placeholder="Введите название товара"
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
            <th>Единица измерения</th>
            <th>Описание</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              <td>{product.id}</td>
              <td>{product.name}</td>
              <td>{product.unit}</td>
              <td>{product.description}</td>
              <td>
                <button onClick={() => handleDelete(product.id)} className="btn-danger">
                  Удалить
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Создать товар</h2>
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
                <label>Единица измерения</label>
                <input
                  type="text"
                  value={formData.unit}
                  onChange={(e) => setFormData({ ...formData, unit: e.target.value })}
                  placeholder="шт, кг, л и т.д."
                />
              </div>

              <div className="form-group">
                <label>Описание</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  rows="3"
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

export default Products;

