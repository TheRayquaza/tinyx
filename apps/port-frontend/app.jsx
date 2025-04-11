import React, { useContext } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { AuthContext } from './src/context/AuthContext'; // Import your AuthContext
import NavBar from './src/components/NavBar'; // Import the NavBar component
import HomePage from './src/pages/HomePage';
import LoginPage from './src/pages/LoginPage';
import PostDetailPage from './src/pages/PostDetailPage';
import ProfilePage from './src/pages/ProfilePage';
import SearchPage from './src/pages/SearchPage';
import SignupPage from './src/pages/SignupPage';

function App() {
  const { isAuthenticated } = useContext(AuthContext); // Check authentication status

  return (
    <Router>
      <NavBar /> {/* Your navigation bar will be shown on all pages */}
      <div>
        <Switch>
          {/* Define routes */}
          <Route path="/" exact component={HomePage} />
          <Route path="/login" component={LoginPage} />
          <Route path="/signup" component={SignupPage} />
          <Route path="/profile" component={ProfilePage} />
          <Route path="/post/:id" component={PostDetailPage} />
          <Route path="/search" component={SearchPage} />
          
          {/* Redirect to login if not authenticated */}
          {isAuthenticated === false && <Route path="/" component={LoginPage} />}
        </Switch>
      </div>
    </Router>
  );
}

export default App;
