import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import postService from '../services/postService';
import PostList from '../components/PostList';

const HomePage = () => {
  const { currentUser, isAuthenticated } = useAuth();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [postText, setPostText] = useState('');
  const [postMedia, setPostMedia] = useState(null);

  useEffect(() => {
    if (isAuthenticated && currentUser) {
      fetchHomeTimeline();
    }
  }, [currentUser, isAuthenticated]);

  const fetchHomeTimeline = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await postService.getHomeTimeline(currentUser.id);
      setPosts(response.hometimeline || []);
    } catch (err) {
      setError('Failed to load timeline. Please try again later.');
      console.error('Error fetching home timeline:', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePostSubmit = async (e) => {
    e.preventDefault();
    
    if (!postText.trim() && !postMedia) {
      return;
    }
    
    try {
      const formData = new FormData();
      formData.append('text', postText);
      
      if (postMedia) {
        formData.append('media', postMedia);
        formData.append('extension', postMedia.name.split('.').pop());
      }
      
      await postService.createPost(formData);
      setPostText('');
      setPostMedia(null);
      fetchHomeTimeline();
    } catch (err) {
      setError('Failed to create post. Please try again.');
      console.error('Error creating post:', err);
    }
  };

  const handleDeletePost = (postId) => {
    setPosts(posts.filter(post => post.id !== postId));
  };

  if (!isAuthenticated) {
    return (
      <div className="container mx-auto p-4">
        <div className="bg-white shadow rounded-lg p-6 text-center">
          <h2 className="text-2xl font-bold mb-4">Welcome to EpiTweet</h2>
          <p className="mb-4">Please log in or sign up to see your timeline and start posting.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4">
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}
      
      <div className="bg-white shadow rounded-lg p-4 mb-6">
        <form onSubmit={handlePostSubmit}>
          <textarea
            className="w-full p-2 border rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
            rows="3"
            placeholder="What's happening?"
            value={postText}
            onChange={(e) => setPostText(e.target.value)}
          ></textarea>
          
          {postMedia && (
            <div className="mt-2 relative">
              <img
                src={URL.createObjectURL(postMedia)}
                alt="Post preview"
                className="h-20 rounded border"
              />
              <button
                type="button"
                className="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1"
                onClick={() => setPostMedia(null)}
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
              </button>
            </div>
          )}
          
          <div className="flex justify-between items-center mt-4">
            <label className="cursor-pointer text-blue-500 hover:text-blue-700">
              <svg className="w-6 h-6 inline-block" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
              </svg>
              <input
                type="file"
                className="hidden"
                accept="image/*"
                onChange={(e) => setPostMedia(e.target.files[0])}
              />
            </label>
            
            <button
              type="submit"
              className="bg-blue-500 text-white px-4 py-2 rounded-full hover:bg-blue-600 focus:outline-none"
              disabled={(!postText.trim() && !postMedia) || loading}
            >
              {loading ? 'Posting...' : 'Post'}
            </button>
          </div>
        </form>
      </div>
      
      {loading && !posts.length ? (
        <div className="text-center py-8">Loading posts...</div>
      ) : (
        <PostList posts={posts} onPostDelete={handleDeletePost} />
      )}
    </div>
  );
};

export default HomePage;
