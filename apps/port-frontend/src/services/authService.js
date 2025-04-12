import api from './api';

const authService = {
  login: async (username, password) => {
    try {
      const data = await api.post('/login', { username, password });
      localStorage.setItem('token', btoa(data.id + "," + data.username));
      localStorage.setItem('user', JSON.stringify(data));
      return data;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  },
  
  signup: async (username, password, email) => {
    try {
      const data = await api.post('/user', { username, password, email });
      localStorage.setItem('token', btoa(data.id + "," + data.username));
      localStorage.setItem('user', JSON.stringify(data));
      return data;
    } catch (error) {
      console.error('Signup error:', error);
      throw error;
    }
  },
  
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
  
  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  },
  
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },
  
  updateProfile: async (userData) => {
    try {
      const data = await api.put('/user', userData);
      localStorage.setItem('user', JSON.stringify(data));
      return data;
    } catch (error) {
      console.error('Update profile error:', error);
      throw error;
    }
  },
  
  uploadProfileImage: async (formData) => {
    try {
      const data = await api.put('/user/image', formData, true);
      localStorage.setItem('user', JSON.stringify(data));
      return data;
    } catch (error) {
      console.error('Upload profile image error:', error);
      throw error;
    }
  },
};

export default authService;
