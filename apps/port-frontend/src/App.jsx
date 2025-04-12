import React, { useContext } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { AuthContext } from './context/AuthContext';
import NavBar from './components/NavBar';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import PostDetailPage from './pages/PostDetailPage';
import ProfilePage from './pages/ProfilePage';
import SearchPage from './pages/SearchPage';
import SignupPage from './pages/SignupPage';

function App() {
  const { isAuthenticated } = useContext(AuthContext);

  return (
    <Router>
      <NavBar />
      <div>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/ui/login" element={<LoginPage />} />
          <Route path="/ui/signup" element={<SignupPage />} />
          <Route path="/ui/profile" element={<ProfilePage />} />
          <Route path="/ui/post/:id" element={<PostDetailPage />} />
          <Route path="/ui/search" element={<SearchPage />} />
          
          {!isAuthenticated && <Route path="/" element={<LoginPage />} />}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
