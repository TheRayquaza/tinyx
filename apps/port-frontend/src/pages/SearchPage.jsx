import React, { useState } from 'react';
import api from '../services/api';

const SearchPage = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  
  const handleSearch = async () => {
    if (!query) return;
    console.log(`Searching for: ${query}`);
    api.get(`/search?query=${query}`)
      .then(response => {
        setResults(response.data);
      })
      .catch(error => {
        console.error('Error fetching search results:', error);
      });
  };

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-2xl font-bold">Search Tweets/Users</h1>
      <div className="mt-4 mb-6">
        <input
          type="text"
          placeholder="Search..."
          className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <button
          className="mt-2 w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600"
          onClick={handleSearch}
        >
          Search
        </button>
      </div>

      <div>
        {results.map((result, index) => (
          <div key={index} className="mb-4 p-4 bg-gray-100 rounded-lg">
            <p><strong>{result.username}</strong></p>
            <p>{result.tweet}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default SearchPage;
