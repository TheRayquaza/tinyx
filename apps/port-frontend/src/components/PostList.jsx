import React from 'react';
import Post from './Post';

const PostList = ({ posts, onPostDelete }) => {
  return (
    <div className="space-y-4">
      {posts.length === 0 ? (
        <div className="text-center text-gray-500">No posts to show.</div>
      ) : (
        posts.map((post) => (
          <Post key={post.id} post={post} onDelete={onPostDelete} />
        ))
      )}
    </div>
  );
};

export default PostList;
