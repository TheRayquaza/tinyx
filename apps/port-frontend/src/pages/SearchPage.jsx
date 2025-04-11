import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import PostList from '../components/PostList';
import postService from '../services/postService';
import api from '../services/api';

const SearchPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  const [activeTab, setActiveTab] = useState('posts');
  
  const [posts, setPosts] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState(query);

  useEffect(() => {
    if (query) {
      performSearch();
    }
  }, [query, activeTab]);

  const performSearch = async () => {
    try {
      setLoading(true);
      setError(null);

      if (activeTab === 'posts' || activeTab === 'all') {
        const postsResult = await postService.searchPosts(query);
        setPosts(postsResult);
      }

      if (activeTab === 'users' || activeTab === 'all') {
        const usersResult = await api.get(`/user/search?query=${encodeURIComponent(query)}`);
        setUsers(usersResult);
      }

      setLoading(false);
    } catch (err) {
      setError('Failed to perform search');
      setLoading(false);
      console.error('Search error:', err);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      setSearchParams({ q: searchTerm });
    }
  };

  return (
    <div className="container mx-auto max-w-3xl p-4">
      <h1 className="text-2xl font-bold mb-4">Search</h1>
      
      <form onSubmit={handleSearch} className="mb-6">
        <div className="flex">
          <input
            type="text"
            className="flex-grow border rounded-l-lg p-3"
            placeholder="Search for posts, users, or topics..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button 
            type="submit"
            className="bg-blue-500 text-white px-6 py-3 rounded-r-lg hover:bg-blue-600"
          >
            Search
          </button>
        </div>
      </form>

      {query && (
        <div className="mb-6">
          <h2 className="text-lg mb-2">Results for "{query}"</h2>
          
          <div className="flex border-b mb-4">
            <button
              className={`px-4 py-2 font-medium ${activeTab === 'all' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500'}`}
              onClick={() => setActiveTab('all')}
            >
              All
            </button>
            <button
              className={`px-4 py-2 font-medium ${activeTab === 'posts' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500'}`}
              onClick={() => setActiveTab('posts')}
            >
              Posts
            </button>
            <button
              className={`px-4 py-2 font-medium ${activeTab === 'users' ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500'}`}
              onClick={() => setActiveTab('users')}
            >
              Users
            </button>
          </div>
        </div>
      )}

      {loading ? (
        <div className="text-center py-8">Searching...</div>
      ) : error ? (
        <div className="text-red-500 p-4">{error}</div>
      ) : (
        <div>
          {(activeTab === 'users' || activeTab === 'all') && (
            <div className="mb-8">
              {activeTab === 'all' && <h3 className="text-lg font-medium mb-3">Users</h3>}
              
              {users.length === 0 ? (
                <div className="text-center py-4 bg-gray-50 rounded-lg">
                  <p className="text-gray-500">No users found</p>
                </div>
              ) : (
                <div className="grid gap-3">
                  {users.map(user => (
                    <div key={user.id} className="border rounded-lg p-4 flex items-center">
                      <img 
                        src={user.avatar || "/api/placeholder/48/48"} 
                        alt={user.username} 
                        className="w-12 h-12 rounded-full mr-4" 
                      />
                      <div>
                        <Link to={`/ui/profile/${user.id}`} className="font-medium text-lg hover:underline">
                          {user.username}
                        </Link>
                        <p className="text-gray-500">{user.bio || ''}</p>
                        <div className="text-sm text-gray-500 mt-1">
                          <span>{user.followersCount || 0} followers</span>
                          <span className="mx-2">•</span>
                          <span>{user.postsCount || 0} posts</span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
              
              {activeTab === 'all' && <div className="my-6 border-t"></div>}
            </div>
          )}

          {(activeTab === 'posts' || activeTab === 'all') && (
            <div>
              {activeTab === 'all' && <h3 className="text-lg font-medium mb-3">Posts</h3>}
              
              {posts.length === 0 ? (
                <div className="text-center py-4 bg-gray-50 rounded-lg">
                  <p className="text-gray-500">No posts found</p>
                </div>
              ) : (
                <PostList posts={posts} />
              )}
            </div>
          )}
          
          {query && posts.length === 0 && users.length === 0 && (
            <div className="text-center py-8 bg-gray-50 rounded-lg">
              <p className="text-gray-600">No results found for "{query}"</p>
              <p className="text-gray-500 mt-2">Try different keywords or check your spelling</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default SearchPage;