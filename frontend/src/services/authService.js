import api from './api';

export const authService = {
  async login(username, password) {
    const response = await api.post('/auth/sign-in', { username, password });
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      // Декодируем токен для получения информации о пользователе
      const user = this.parseJwt(response.data.token);
      localStorage.setItem('user', JSON.stringify(user));
    }
    return response.data;
  },

  async register(data) {
    const response = await api.post('/auth/sign-up', data);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      const user = this.parseJwt(response.data.token);
      localStorage.setItem('user', JSON.stringify(user));
    }
    return response.data;
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  },

  isAdmin() {
    const user = this.getCurrentUser();
    return user && user.role === 'ROLE_ADMIN';
  },

  isStorekeeper() {
    const user = this.getCurrentUser();
    return user && (user.role === 'ROLE_STOREKEEPER' || user.role === 'ROLE_ADMIN');
  },

  hasRole(role) {
    const user = this.getCurrentUser();
    return user && user.role === role;
  },

  parseJwt(token) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (e) {
      return null;
    }
  },
};

