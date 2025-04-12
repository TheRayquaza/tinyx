import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import postService from '../services/postService';
import LikeButton from './LikeButton';

const Post = ({ post, showActions = true, onDelete }) => {
  const { currentUser } = useAuth();
  const [likes, setLikes] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchLikes = useCallback(async () => {
    if (post?.id) {
      try {
        setLoading(true);
        const likesData = await postService.getLikes(post.id);
        setLikes(likesData);
      } catch (error) {
        console.error('Error fetching likes:', error);
      } finally {
        setLoading(false);
      }
    }
  }, [post?.id]);

  useEffect(() => {
    fetchLikes();
  }, [fetchLikes]);

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this post?')) {
      try {
        await postService.deletePost(post.id);
        if (onDelete) {
          onDelete(post.id);
        }
      } catch (error) {
        console.error('Error deleting post:', error);
      }
    }
  };

  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleString();
  };

  if (!post) return null;

  return (
    <div className="border rounded-lg p-4 mb-4 bg-white shadow">
      <div className="flex justify-between items-start">
        <Link to={`/profile/${post.ownerId}`} className="font-bold text-blue-500 hover:underline">
          @{post.username || 'User ' + post.ownerId}
        </Link>
        <span className="text-gray-500 text-sm">{formatDate(post.createdAt)}</span>
      </div>
      
      <div className="my-2">
        <p className="text-gray-800">{post.text}</p>
        
        {post.media && (
          <div className="mt-2">
            <img 
              src={`/minio/${post.media}`} 
              alt="Post media" 
              className="rounded-lg max-h-96 object-contain"
            />
          </div>
        )}
      </div>
      
      {showActions && (
        <div className="flex mt-4 space-x-4 text-gray-500">
          <Link to={`/post/${post.id}`} className="flex items-center hover:text-blue-500">
            <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
            </svg>
            Reply
          </Link>
          
          <LikeButton 
            postId={post.id} 
            likes={likes}
            onLikeUpdated={fetchLikes}
          />
          
          {currentUser && currentUser.id === post.ownerId && (
            <button
              onClick={handleDelete}
              className="flex items-center hover:text-red-500"
            >
              <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
              </svg>
              Delete
            </button>
          )}
        </div>
      )}
      
      {loading && (
        <div className="text-center py-4 text-gray-500">
          <p>Loading likes...</p>
        </div>
      )}
    </div>
  );
};

export default Post;
