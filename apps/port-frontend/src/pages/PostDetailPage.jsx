import { useState, useEffect, useContext } from 'react';
import { useParams, Link } from 'react-router-dom';
import Post from '../components/Post';
import LikeButton from '../components/LikeButton';
import { AuthContext } from '../context/AuthContext';
import postService from '../services/postService';

const PostDetailPage = () => {
  const { postId } = useParams();
  const [post, setPost] = useState(null);
  const [replies, setReplies] = useState([]);
  const [newReply, setNewReply] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [likes, setLikes] = useState([]);
  const { currentUser } = useContext(AuthContext);

  useEffect(() => {
    const fetchPostDetails = async () => {
      try {
        setLoading(true);
        const postData = await postService.getPost(postId);
        setPost(postData);
        
        // Fetch replies
        const repliesData = await postService.getReplies(postId);
        setReplies(repliesData);
        
        // Fetch likes
        const likesData = await postService.getLikes(postId);
        setLikes(likesData);
        
        setLoading(false);
      } catch (err) {
        setError('Failed to load post details');
        setLoading(false);
        console.error('Error fetching post:', err);
      }
    };

    fetchPostDetails();
  }, [postId]);

  const handleAddReply = async (e) => {
    e.preventDefault();
    if (!newReply.trim()) return;

    try {
      // Create FormData for the reply
      const formData = new FormData();
      formData.append('content', newReply);
      
      const response = await postService.replyToPost(postId, formData);
      setReplies([...replies, response]);
      setNewReply('');
    } catch (err) {
      console.error('Error adding reply:', err);
      setError('Failed to add reply');
    }
  };

  const handleLikeToggle = async (isLiked) => {
    try {
      if (isLiked) {
        await postService.unlikePost(postId);
        // Update likes state
        setLikes(likes.filter(like => like.userId !== currentUser.id));
      } else {
        const response = await postService.likePost(postId);
        setLikes([...likes, response]);
      }
    } catch (err) {
      console.error('Error toggling like:', err);
    }
  };

  if (loading) return <div className="flex justify-center p-8">Loading post...</div>;
  if (error) return <div className="text-red-500 p-4">{error}</div>;
  if (!post) return <div className="p-4">Post not found</div>;

  const isLiked = currentUser && likes.some(like => like.userId === currentUser.id);

  return (
    <div className="container mx-auto max-w-2xl p-4">
      <div className="mb-6">
        <Link to="/" className="text-blue-500 hover:underline mb-4 inline-block">
          &larr; Back to Timeline
        </Link>
        
        <div className="border rounded-lg p-4 mb-6 bg-white shadow">
          {post && <Post post={post} showFullContent={true} />}
        </div>
        
        <div className="mt-4 flex items-center gap-4">
          <LikeButton 
            postId={postId} 
            isLiked={isLiked}
            likesCount={likes.length} 
            onLikeToggle={handleLikeToggle}
          />
          <span className="text-gray-500">{replies.length} Replies</span>
        </div>
      </div>

      {currentUser ? (
        <form onSubmit={handleAddReply} className="mb-6">
          <textarea
            className="w-full border rounded-lg p-3 mb-2"
            rows="3"
            placeholder="Write a reply..."
            value={newReply}
            onChange={(e) => setNewReply(e.target.value)}
          ></textarea>
          <button 
            type="submit"
            className="bg-blue-500 text-white px-4 py-2 rounded-full hover:bg-blue-600"
          >
            Reply
          </button>
        </form>
      ) : (
        <div className="mb-6 p-4 bg-gray-100 rounded-lg text-center">
          <Link to="/login" className="text-blue-500 hover:underline">
            Log in
          </Link> to reply to this post
        </div>
      )}

      <div className="replies-section">
        <h2 className="text-xl font-bold mb-4">Replies</h2>
        {replies.length === 0 ? (
          <p className="text-gray-500">No replies yet</p>
        ) : (
          <div className="space-y-4">
            {replies.map((reply) => (
              <div key={reply.id} className="border-b pb-4">
                <div className="flex items-center mb-2">
                  <Link to={`/ui/profile/${reply.user.id}`} className="font-medium text-blue-500 hover:underline">
                    {reply.user.username}
                  </Link>
                  <span className="text-gray-500 text-sm ml-2">
                    {new Date(reply.createdAt).toLocaleString()}
                  </span>
                </div>
                <p>{reply.content}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default PostDetailPage;
