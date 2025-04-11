import React from 'react';
import { useAuth } from '../context/AuthContext';

const UserProfile = ({ user, onFollowToggle, isFollowing, followersCount, followingCount }) => {
  const { currentUser } = useAuth();
  const isCurrentUser = currentUser && user && currentUser.id === user.id;

  if (!user) return <div>Loading user profile...</div>;

  return (
    <div className="bg-white shadow rounded-lg p-6">
      <div className="flex flex-col sm:flex-row items-center">
        <div className="flex-shrink-0 mb-4 sm:mb-0 sm:mr-4">
          {user.profileImage ? (
            <img
              src={`/minio/${user.profileImage}`}
              alt={`${user.username}'s profile`}
              className="w-24 h-24 rounded-full object-cover border-2 border-blue-500"
            />
          ) : (
            <div className="w-24 h-24 rounded-full bg-gray-300 flex items-center justify-center">
              <span className="text-2xl font-bold text-gray-600">{user.username?.charAt(0).toUpperCase()}</span>
            </div>
          )}
        </div>
        
        <div className="flex-1 text-center sm:text-left">
          <h2 className="text-2xl font-bold">{user.username}</h2>
          {user.bio && <p className="text-gray-600 mt-1">{user.bio}</p>}
          
          <div className="flex mt-2 justify-center sm:justify-start space-x-4">
            <div className="text-gray-600">
              <span className="font-bold">{followersCount || 0}</span> Followers
            </div>
            <div className="text-gray-600">
              <span className="font-bold">{followingCount || 0}</span> Following
            </div>
          </div>
        </div>
        
        <div className="ml-auto mt-4 sm:mt-0">
          {!isCurrentUser && currentUser && (
            <button
              onClick={onFollowToggle}
              className={`px-4 py-2 rounded-full ${
                isFollowing
                  ? 'bg-white text-blue-500 border border-blue-500'
                  : 'bg-blue-500 text-white'
              }`}
            >
              {isFollowing ? 'Unfollow' : 'Follow'}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default UserProfile;
