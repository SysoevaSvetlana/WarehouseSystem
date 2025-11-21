import React, { useState, useEffect } from 'react';
import { userService } from '../services/userService';
import './Common.css';

const Users = () => {
  const [users, setUsers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [newRole, setNewRole] = useState('');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const data = await userService.getAll({ page: 0, size: 100 });
      setUsers(data.content || []);
    } catch (error) {
      console.error('Error loading users:', error);
    }
  };

  const handleRoleChange = async () => {
    if (!selectedUser || !newRole) return;
    
    try {
      await userService.updateRole(selectedUser.id, newRole);
      setShowModal(false);
      setSelectedUser(null);
      setNewRole('');
      loadUsers();
    } catch (error) {
      alert('Ошибка: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Удалить пользователя?')) {
      try {
        await userService.delete(id);
        loadUsers();
      } catch (error) {
        alert('Ошибка: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  const openRoleModal = (user) => {
    setSelectedUser(user);
    setNewRole(user.role);
    setShowModal(true);
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Пользователи</h1>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Имя пользователя</th>
            <th>Email</th>
            <th>Роль</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.username}</td>
              <td>{user.email}</td>
              <td>{user.role === 'ROLE_ADMIN' ? 'ADMIN' : 'STOREKEEPER'}</td>
              <td>
                <button onClick={() => openRoleModal(user)} className="btn-secondary" style={{ marginRight: '0.5rem' }}>
                  Изменить роль
                </button>
                <button onClick={() => handleDelete(user.id)} className="btn-danger">
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
            <h2>Изменить роль пользователя</h2>
            <div className="form-group">
              <label>Пользователь</label>
              <input type="text" value={selectedUser?.username} disabled />
            </div>

            <div className="form-group">
              <label>Роль</label>
              <select value={newRole} onChange={(e) => setNewRole(e.target.value)}>
                <option value="ROLE_STOREKEEPER">STOREKEEPER</option>
                <option value="ROLE_ADMIN">ADMIN</option>
              </select>
            </div>

            <div className="modal-actions">
              <button onClick={handleRoleChange} className="btn-primary">
                Сохранить
              </button>
              <button onClick={() => setShowModal(false)} className="btn-secondary">
                Отмена
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Users;

