import React from 'react';
import { useAuth } from '../context/AuthContext';

const ProfilePage = () => {
  const { currentUser } = useAuth();

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-2xl font-bold">Profile</h1>
      <div className="mb-4">
        <p><strong>Username:</strong> {currentUser.username}</p>
        <p><strong>Email:</strong> {currentUser.email}</p>
        {/* Add additional user details or stats here */}
      </div>

      <div>
        <h2 className="text-xl font-semibold">Your Tweets</h2>
        {/* Map through and display user's tweets */}
      </div>
    </div>
  );
};

export default ProfilePage;
