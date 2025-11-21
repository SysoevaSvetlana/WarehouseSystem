import React from 'react';
import { authService } from '../services/authService';
import { useNavigate } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const user = authService.getCurrentUser();

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  const getRoleDisplay = () => {
    if (user?.role === 'ROLE_ADMIN') return 'Администратор';
    if (user?.role === 'ROLE_STOREKEEPER') return 'Кладовщик';
    return 'Пользователь';
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">Складской учет</div>
      <div className="navbar-user">
        <span className="user-info">
          <span className="user-role">{getRoleDisplay()}</span>
          <span className="user-name">{user?.sub || 'Unknown'}</span>
        </span>
        <button onClick={handleLogout} className="btn-logout">
          Выход
        </button>
      </div>
    </nav>
  );
};

export default Navbar;

