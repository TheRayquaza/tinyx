import { useState, useEffect, useContext } from 'react';
import { useParams, Link } from 'react-router-dom';
import PostList from '../components/PostList';
import UserProfile from '../components/UserProfile';
import { AuthContext } from '../context/AuthContext';
import postService from '../services/postService';
import api from '../services/api';

const ProfilePage = () => {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('posts');
  const { currentUser } = useContext(AuthContext);
  const [followers, setFollowers] = useState([]);
  const [following, setFollowing] = useState([]);
  const [isFollowing, setIsFollowing] = useState(false);

  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        setLoading(true);
        // Fetch user profile by id
        const userData = await api.get(`/user/${id}`);
        setUser(userData);
        
        // Check if current user is following this profile
        if (currentUser) {
          const followStatus = await api.get(`/social/follows/${userData.id}`); // TODO: fix this
          setIsFollowing(followStatus.isFollowing);
        }
        
        // Fetch user's posts
        const userPosts = await postService.getUserTimeline(userData.id);
        setPosts(userPosts);
        
        // Fetch followers and following
        const followersData = await api.get(`/social/followers/${userData.id}`);
        const followingData = await api.get(`/social/following/${userData.id}`);
        setFollowers(followersData);
        setFollowing(followingData);
        
        setLoading(false);
      } catch (err) {
        setError('Failed to load profile');
        setLoading(false);
        console.error('Error fetching profile:', err);
      }
    };

    fetchUserProfile();
  }, [id, currentUser]);

  const handleFollowToggle = async () => {
    if (!currentUser) return;

    try {
      if (isFollowing) {
        await api.delete(`/social/follow/${user.id}`);
        setIsFollowing(false);
        setFollowers(followers.filter(f => f.id !== currentUser.id));
      } else {
        await api.post(`/social/follow/${user.id}`);
        setIsFollowing(true);
        // Add current user to followers list
        if (currentUser) {
          setFollowers([...followers, {
            id: currentUser.id,
            username: currentUser.username,
            avatar: currentUser.avatar
          }]);
        }
      }
    } catch (err) {
      console.error('Error updating follow status:', err);
    }
  };

  if (loading) return <div className="flex justify-center p-8">Loading profile...</div>;
  if (error) return <div className="text-red-500 p-4">{error}</div>;
  if (!user) return <div className="p-4">User not found</div>;

  return (
    <div className="container mx-auto max-w-3xl p-4">
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <UserProfile user={user} />
        
        {currentUser && currentUser.id !== user.id && (
          <button
            onClick={handleFollowToggle}
            className={`mt-4 px-6 py-2 rounded-full ${
              isFollowing 
                ? 'bg-gray-200 text-gray-800 hover:bg-gray-300' 
                : 'bg-blue-500 text-white hover:bg-blue-600'
            }`}
          >
            {isFollowing ? 'Unfollow' : 'Follow'}
          </button>
        )}

        <div className="flex mt-6 border-b">
          <button
            className={`px-4 py-2 font-medium ${activeTab === 'posts' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500'}`}
            onClick={() => setActiveTab('posts')}
          >
            Posts ({posts.length})
          </button>
          <button
            className={`px-4 py-2 font-medium ${activeTab === 'followers' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500'}`}
            onClick={() => setActiveTab('followers')}
          >
            Followers ({followers.length})
          </button>
          <button
            className={`px-4 py-2 font-medium ${activeTab === 'following' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500'}`}
            onClick={() => setActiveTab('following')}
          >
            Following ({following.length})
          </button>
        </div>
      </div>

      {activeTab === 'posts' && (
        <div className="posts-container">
          {posts.length === 0 ? (
            <div className="text-center py-6 bg-gray-50 rounded-lg">
              <p className="text-gray-500">No posts yet</p>
            </div>
          ) : (
            <PostList posts={posts} />
          )}
        </div>
      )}
      
      {activeTab === 'followers' && (
        <div className="followers-container">
          {followers.length === 0 ? (
            <div className="text-center py-6 bg-gray-50 rounded-lg">
              <p className="text-gray-500">No followers yet</p>
            </div>
          ) : (
            <div className="grid gap-3">
              {followers.map(follower => (
                <div key={follower.id} className="border rounded-lg p-3 flex items-center">
                  <img 
                    src={follower.avatar || "/api/placeholder/40/40"} 
                    alt={follower.username} 
                    className="w-10 h-10 rounded-full mr-3" 
                  />
                  <div>
                    <Link to={`/ui/profile/${follower.id}`} className="font-medium hover:underline">
                      {follower.username}
                    </Link>
                    <p className="text-gray-500 text-sm">{follower.bio || ''}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
      
      {activeTab === 'following' && (
        <div className="following-container">
          {following.length === 0 ? (
            <div className="text-center py-6 bg-gray-50 rounded-lg">
              <p className="text-gray-500">Not following anyone yet</p>
            </div>
          ) : (
            <div className="grid gap-3">
              {following.map(followed => (
                <div key={followed.id} className="border rounded-lg p-3 flex items-center">
                  <img 
                    src={followed.avatar || "/api/placeholder/40/40"} 
                    alt={followed.username} 
                    className="w-10 h-10 rounded-full mr-3" 
                  />
                  <div>
                    <Link to={`/profile/${followed.username}`} className="font-medium hover:underline">
                      {followed.username}
                    </Link>
                    <p className="text-gray-500 text-sm">{followed.bio || ''}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default ProfilePage;
