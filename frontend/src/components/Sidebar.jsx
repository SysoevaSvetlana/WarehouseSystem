import React from 'react';
import { NavLink } from 'react-router-dom';
import { authService } from '../services/authService';
import './Sidebar.css';

const Sidebar = () => {
  const isAdmin = authService.isAdmin();

  return (
    <aside className="sidebar">
      <nav className="sidebar-nav">
        <NavLink to="/shipments" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
          Поставки
        </NavLink>
        <NavLink to="/warehouses" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
          Склады
        </NavLink>
        <NavLink to="/stock" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
          Остатки
        </NavLink>
        <NavLink to="/products" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
          Товары
        </NavLink>
        {isAdmin && (
          <NavLink to="/users" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
            Пользователи
          </NavLink>
        )}
      </nav>
    </aside>
  );
};

export default Sidebar;

