import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import PrivateRoute from './components/PrivateRoute';
import AdminRoute from './components/AdminRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Shipments from './pages/Shipments';
import Warehouses from './pages/Warehouses';
import Stock from './pages/Stock';
import Products from './pages/Products';
import Users from './pages/Users';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route
          path="/"
          element={
            <PrivateRoute>
              <Layout />
            </PrivateRoute>
          }
        >
          <Route index element={<Navigate to="/shipments" replace />} />
          <Route path="shipments" element={<Shipments />} />
          <Route path="warehouses" element={<Warehouses />} />
          <Route path="stock" element={<Stock />} />
          <Route path="products" element={<Products />} />
          <Route
            path="users"
            element={
              <AdminRoute>
                <Users />
              </AdminRoute>
            }
          />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;

