import api from './api';

const postService = {
  createPost: async (formData) => {
    try {
      return await api.post('/post', formData, true);
    } catch (error) {
      console.error('Create post error:', error);
      throw error;
    }
  },
  
  getPost: async (postId) => {
    try {
      return await api.get(`/post/${postId}`);
    } catch (error) {
      console.error('Get post error:', error);
      throw error;
    }
  },
  
  editPost: async (postId, formData) => {
    try {
      return await api.put(`/post/${postId}`, formData, true);
    } catch (error) {
      console.error('Edit post error:', error);
      throw error;
    }
  },
  
  deletePost: async (postId) => {
    try {
      return await api.delete(`/post/${postId}`);
    } catch (error) {
      console.error('Delete post error:', error);
      throw error;
    }
  },
  
  getReplies: async (postId) => {
    try {
      return await api.get(`/post/${postId}/reply`);
    } catch (error) {
      console.error('Get replies error:', error);
      throw error;
    }
  },
  
  replyToPost: async (postId, formData) => {
    try {
      return await api.post(`/post/${postId}/reply`, formData, true);
    } catch (error) {
      console.error('Reply to post error:', error);
      throw error;
    }
  },
  
  likePost: async (postId) => {
    try {
      return await api.post(`/social/post/${postId}/like`);
    } catch (error) {
      console.error('Like post error:', error);
      throw error;
    }
  },
  
  unlikePost: async (postId) => {
    try {
      return await api.delete(`/social/post/${postId}/like`);
    } catch (error) {
      console.error('Unlike post error:', error);
      throw error;
    }
  },
  
  getLikes: async (postId) => {
    try {
      return await api.get(`/social/post/${postId}/like`);
    } catch (error) {
      console.error('Get likes error:', error);
      throw error;
    }
  },
  
  getHomeTimeline: async (userId) => {
    try {
      return await api.get(`/home-timeline/${userId}`);
    } catch (error) {
      console.error('Get home timeline error:', error);
      throw error;
    }
  },
  
  getUserTimeline: async (userId) => {
    try {
      return await api.get(`/user-timeline/${userId}`);
    } catch (error) {
      console.error('Get user timeline error:', error);
      throw error;
    }
  },
  
  searchPosts: async (query) => {
    try {
      return await api.get(`/search?query=${encodeURIComponent(query)}`);
    } catch (error) {
      console.error('Search posts error:', error);
      throw error;
    }
  },
};

export default postService;
