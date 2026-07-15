import React, { useEffect, useState, useContext, useRef } from 'react';
import { 
  Grid, Card, CardContent, Typography, Box, CircularProgress, 
  TextField, Button, List, ListItem, ListItemButton, ListItemText, 
  Paper, IconButton, Skeleton, Divider, Tooltip, Chip
} from '@mui/material';
import { Book, Search, History, Clear, OpenInNew } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { motion } from 'framer-motion';

const placeholderImage = 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=200&auto=format&fit=crop&q=60';

// Predefined bookshelves
const SHELVES = [
  { id: 'trending', title: '🔥 Trending Now', subject: 'trending' },
  { id: 'programming', title: '💻 Programming', subject: 'programming' },
  { id: 'ai', title: '🤖 Artificial Intelligence', subject: 'artificial_intelligence' },
  { id: 'science', title: '🔬 Science & Physics', subject: 'science' },
  { id: 'fiction', title: '📖 Best Fiction', subject: 'fiction' },
  { id: 'new', title: '✨ New Arrivals', subject: 'new' }
];

export default function StudentDashboard() {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  // Search State
  const [searchQuery, setSearchQuery] = useState('');
  const [debouncedQuery, setDebouncedQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [searchResults, setSearchResults] = useState([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const [recentSearches, setRecentSearches] = useState([]);

  // Bookshelves State
  const [shelfData, setShelfData] = useState({});
  const [shelvesLoading, setShelvesLoading] = useState(true);

  // Debouncing Search Input
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedQuery(searchQuery);
    }, 400); // 400ms debounce
    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Fetch Live Suggestions
  useEffect(() => {
    const getSuggestions = async () => {
      if (debouncedQuery.trim().length < 3) {
        setSuggestions([]);
        return;
      }
      try {
        const res = await api.get('/openlibrary/search', {
          params: { q: debouncedQuery, type: 'all' }
        });
        setSuggestions(res.data.slice(0, 5)); // Limit to 5 suggestions
      } catch (err) {
        console.error("Failed to load suggestions", err);
      }
    };
    getSuggestions();
  }, [debouncedQuery]);

  // Load Recent Searches on mount
  useEffect(() => {
    const history = localStorage.getItem('recentSearches');
    if (history) {
      setRecentSearches(JSON.parse(history));
    }
    
    // Fetch shelves books
    const fetchShelves = async () => {
      setShelvesLoading(true);
      const data = {};
      try {
        await Promise.all(
          SHELVES.map(async (shelf) => {
            try {
              const res = await api.get(`/openlibrary/subjects/${shelf.subject}`);
              data[shelf.id] = res.data.slice(0, 8); // Limit to 8 books per shelf
            } catch (e) {
              console.error(`Failed to load subject shelf: ${shelf.title}`, e);
              data[shelf.id] = [];
            }
          })
        );
        setShelfData(data);
      } catch (err) {
        console.error("Failed to load bookshelf data", err);
      } finally {
        setShelvesLoading(false);
      }
    };
    fetchShelves();
  }, []);

  const triggerSearch = async (query) => {
    if (!query.trim()) return;
    setSearchLoading(true);
    setShowSuggestions(false);
    
    // Add to history
    let updatedHistory = [query, ...recentSearches.filter(q => q !== query)].slice(0, 8);
    setRecentSearches(updatedHistory);
    localStorage.setItem('recentSearches', JSON.stringify(updatedHistory));

    try {
      const res = await api.get('/openlibrary/search', {
        params: { q: query, type: 'all' }
      });
      setSearchResults(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setSearchLoading(false);
    }
  };

  const handleClearSearch = () => {
    setSearchQuery('');
    setSearchResults([]);
    setSuggestions([]);
  };

  const BookCard = ({ book }) => (
    <Card 
      sx={{ 
        width: 180, 
        flexShrink: 0,
        background: 'rgba(30, 41, 59, 0.45)', 
        border: '1px solid rgba(255, 255, 255, 0.08)',
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        transition: 'transform 0.2s ease, box-shadow 0.2s ease',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: '0 8px 18px rgba(99, 102, 241, 0.2)',
          borderColor: 'rgba(99, 102, 241, 0.3)'
        }
      }}
    >
      <Box sx={{ height: 180, overflow: 'hidden', background: '#1e293b', position: 'relative' }}>
        <img 
          src={book.coverUrl || placeholderImage} 
          alt={book.title} 
          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
          onError={(e) => { e.target.src = placeholderImage; }}
        />
      </Box>
      <CardContent sx={{ p: 1.5, flexGrow: 1, display: 'flex', flexDirection: 'column', justifyBetween: 'space-between', gap: 1 }}>
        <Box>
          <Typography 
            variant="body2" 
            fontWeight="bold" 
            sx={{ 
              lineHeight: 1.2, 
              height: '2.4em', 
              overflow: 'hidden', 
              textOverflow: 'ellipsis', 
              display: '-webkit-box', 
              WebkitLineClamp: 2, 
              WebkitBoxOrient: 'vertical' 
            }}
          >
            {book.title}
          </Typography>
          <Typography variant="caption" color="text.secondary" noWrap display="block" mt={0.5}>
            By {book.authors && book.authors.length > 0 ? book.authors.join(', ') : 'Unknown'}
          </Typography>
          {book.firstPublishYear && (
            <Typography variant="caption" color="text.secondary" display="block">
              Published: {book.firstPublishYear}
            </Typography>
          )}
        </Box>
        <Button 
          fullWidth 
          variant="outlined" 
          size="small" 
          startIcon={<OpenInNew />}
          onClick={() => navigate(`/student/books/${book.key}`)}
          sx={{ mt: 1, fontSize: '0.75rem', py: 0.5 }}
        >
          View Details
        </Button>
      </CardContent>
    </Card>
  );

  return (
    <Box>
      {/* Header section */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Welcome back, {user?.username || 'User'}!
        </Typography>
      </Box>

      {/* Modern Search Section with Live Suggestions */}
      <Box sx={{ position: 'relative', mb: 4 }}>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <TextField
            fullWidth
            placeholder="Search books by title, author, ISBN or subject..."
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
              setShowSuggestions(true);
            }}
            onFocus={() => setShowSuggestions(true)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                triggerSearch(searchQuery);
              }
            }}
            InputProps={{
              startAdornment: <Search sx={{ color: 'text.secondary', mr: 1 }} />
            }}
          />
          {searchQuery && (
            <IconButton onClick={handleClearSearch} sx={{ border: '1px solid rgba(255,255,255,0.1)' }}>
              <Clear />
            </IconButton>
          )}
          <Button 
            variant="contained" 
            onClick={() => triggerSearch(searchQuery)}
            sx={{
              background: 'linear-gradient(90deg, #6366f1 0%, #4f46e5 100%)',
              px: 4
            }}
          >
            Search
          </Button>
        </Box>

        {/* Suggestion Dropdown */}
        {showSuggestions && (suggestions.length > 0 || recentSearches.length > 0) && (
          <Paper 
            sx={{ 
              position: 'absolute', 
              top: '100%', 
              left: 0, 
              right: 0, 
              zIndex: 10, 
              background: '#1e293b', 
              border: '1px solid rgba(255,255,255,0.1)',
              mt: 1, 
              maxHeight: 400, 
              overflowY: 'auto' 
            }}
          >
            {recentSearches.length > 0 && (
              <Box p={1.5}>
                <Typography variant="caption" color="text.secondary" display="flex" alignItems="center" gap={0.5}>
                  <History fontSize="inherit" /> Recent Searches
                </Typography>
                <Box display="flex" gap={1} flexWrap="wrap" mt={1}>
                  {recentSearches.map((term, idx) => (
                    <Chip 
                      key={idx} 
                      label={term} 
                      onClick={() => {
                        setSearchQuery(term);
                        triggerSearch(term);
                      }} 
                      onDelete={() => {
                        const updated = recentSearches.filter(t => t !== term);
                        setRecentSearches(updated);
                        localStorage.setItem('recentSearches', JSON.stringify(updated));
                      }}
                      size="small"
                      sx={{ background: 'rgba(255,255,255,0.08)', cursor: 'pointer' }}
                    />
                  ))}
                </Box>
                <Divider sx={{ my: 1.5, borderColor: 'rgba(255,255,255,0.1)' }} />
              </Box>
            )}

            {suggestions.length > 0 && (
              <List>
                {suggestions.map((item) => (
                  <ListItem key={item.key} disablePadding>
                    <ListItemButton 
                      onClick={() => {
                        setSearchQuery(item.title);
                        navigate(`/student/books/${item.key}`);
                      }}
                    >
                      <Book sx={{ mr: 2, color: 'primary.main', fontSize: 20 }} />
                      <ListItemText 
                        primary={item.title} 
                        secondary={item.authors && item.authors.length > 0 ? `By ${item.authors.join(', ')}` : 'Unknown Author'} 
                      />
                    </ListItemButton>
                  </ListItem>
                ))}
              </List>
            )}
          </Paper>
        )}
      </Box>

      {/* Search Overlay Dismissal */}
      {showSuggestions && (
        <Box 
          sx={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, zIndex: 5 }} 
          onClick={() => setShowSuggestions(false)} 
        />
      )}

      {/* Render Search Results Grid or structured homepage shelves */}
      {searchResults.length > 0 ? (
        <Box mb={4}>
          <Typography variant="h5" fontWeight="bold" mb={2}>
            Search Results ({searchResults.length})
          </Typography>
          {searchLoading ? (
            <Box display="flex" justifyContent="center" py={4}><CircularProgress /></Box>
          ) : (
            <Grid container spacing={3}>
              {searchResults.map((book) => (
                <Grid item key={book.key}>
                  <BookCard book={book} />
                </Grid>
              ))}
            </Grid>
          )}
        </Box>
      ) : (
        /* Modern Structured shelves */
        <Box display="flex" flexDirection="column" gap={4}>
          {SHELVES.map((shelf) => {
            const books = shelfData[shelf.id] || [];
            return (
              <Box key={shelf.id}>
                <Typography variant="h5" fontWeight="bold" mb={2}>
                  {shelf.title}
                </Typography>
                
                {shelvesLoading ? (
                  <Box display="flex" gap={2} sx={{ overflowX: 'auto', pb: 2 }}>
                    {[...Array(6)].map((_, idx) => (
                      <Box key={idx} sx={{ width: 180, flexShrink: 0 }}>
                        <Skeleton variant="rectangular" height={180} sx={{ borderRadius: 1 }} />
                        <Skeleton variant="text" width="80%" sx={{ mt: 1 }} />
                        <Skeleton variant="text" width="50%" />
                      </Box>
                    ))}
                  </Box>
                ) : books.length > 0 ? (
                  <Box 
                    sx={{ 
                      display: 'flex', 
                      overflowX: 'auto', 
                      gap: 2, 
                      pb: 2,
                      scrollBehavior: 'smooth',
                      '&::-webkit-scrollbar': { height: 6 },
                      '&::-webkit-scrollbar-thumb': { background: 'rgba(255,255,255,0.1)', borderRadius: 3 }
                    }}
                  >
                    {books.map((book) => (
                      <BookCard key={book.key} book={book} />
                    ))}
                  </Box>
                ) : (
                  <Typography color="text.secondary" variant="body2">No books found in this shelf.</Typography>
                )}
              </Box>
            );
          })}
        </Box>
      )}
    </Box>
  );
}
