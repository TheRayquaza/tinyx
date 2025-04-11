import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import postService from '../services/postService';

const LikeButton = ({ postId, likes = [], onLikeUpdated }) => {
  const { currentUser, isAuthenticated } = useAuth();
  const [isLiked, setIsLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(likes.length);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (currentUser && likes.length > 0) {
      const userLiked = likes.some(like => like.id === currentUser.id);
      setIsLiked(userLiked);
    }
    setLikeCount(likes.length);
  }, [likes, currentUser]);

  const handleLike = async () => {
    if (!isAuthenticated) {
      alert('Please login to like posts');
      return;
    }

    try {
      setLoading(true);
      if (isLiked) {
        await postService.unlikePost(postId);
        setIsLiked(false);
        setLikeCount(prev => prev - 1);
      } else {
        await postService.likePost(postId);
        setIsLiked(true);
        setLikeCount(prev => prev + 1);
      }
      if (onLikeUpdated) {
        onLikeUpdated();
      }
    } catch (error) {
      console.error('Error updating like:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <button
      onClick={handleLike}
      disabled={loading}
      className={`flex items-center ${isLiked ? 'text-red-500' : 'hover:text-red-500'}`}
    >
      <svg className="w-4 h-4 mr-1" fill={isLiked ? 'currentColor' : 'none'} stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
      </svg>
      {likeCount > 0 && <span>{likeCount}</span>}
    </button>
  );
};

export default LikeButton;
