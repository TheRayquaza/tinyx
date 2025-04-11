import React from 'react';
import { useParams } from 'react-router-dom';

const PostDetailPage = () => {
  const { postId } = useParams();
  
  return (
    <div className="container mx-auto p-6">
      <h1 className="text-2xl font-bold">Tweet Details</h1>
      <p>Details of post {postId}</p>
      {/* Add details, comments, and like buttons */}
    </div>
  );
};

export default PostDetailPage;
