const getHeaders = (isFormData = false) => {
  const token = localStorage.getItem('token');
  const headers = {};

  if (!isFormData) {
    headers['Content-Type'] = 'application/json';
  }

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  return headers;
};

const api = {
  get: async (endpoint) => {
    try {
      const response = await fetch(`${endpoint}`, {
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
      const options = {
        method: 'POST',
        headers: getHeaders(isFormData),
        body: isFormData ? data : JSON.stringify(data),
      };

      const response = await fetch(`${endpoint}`, options);

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
      const options = {
        method: 'PUT',
        headers: getHeaders(isFormData),
        body: isFormData ? data : JSON.stringify(data),
      };

      const response = await fetch(`${endpoint}`, options);

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
      const response = await fetch(`${endpoint}`, {
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
