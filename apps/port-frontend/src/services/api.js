const API_URL = 'http://localhost:8080'; // Replace with your API base URL

const getHeaders = () => {
  const token = localStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json' };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  return headers;
};

const api = {
  get: async (endpoint) => {
    try {
      const response = await fetch(`${API_URL}${endpoint}`, {
        method: 'GET',
        headers: getHeaders(),
      });
      
      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('API GET error:', error);
      throw error;
    }
  },
  
  post: async (endpoint, data, isFormData = false) => {
    try {
      const headers = isFormData ? {} : getHeaders();
      
      const options = {
        method: 'POST',
        headers: !isFormData ? headers : undefined,
        body: isFormData ? data : JSON.stringify(data),
      };
      
      const response = await fetch(`${API_URL}${endpoint}`, options);
      
      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('API POST error:', error);
      throw error;
    }
  },
  
  put: async (endpoint, data, isFormData = false) => {
    try {
      const headers = isFormData ? {} : getHeaders();
      
      const options = {
        method: 'PUT',
        headers: !isFormData ? headers : undefined,
        body: isFormData ? data : JSON.stringify(data),
      };
      
      const response = await fetch(`${API_URL}${endpoint}`, options);
      
      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('API PUT error:', error);
      throw error;
    }
  },
  
  delete: async (endpoint) => {
    try {
      const response = await fetch(`${API_URL}${endpoint}`, {
        method: 'DELETE',
        headers: getHeaders(),
      });
      
      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }
      
      return response;
    } catch (error) {
      console.error('API DELETE error:', error);
      throw error;
    }
  },
};

export default api;
