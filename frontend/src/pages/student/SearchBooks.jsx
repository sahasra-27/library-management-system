import React, { useEffect, useState, useContext } from 'react';
import { Card, CardContent, Typography, TextField, Button, Box, Grid, CircularProgress, Chip, MenuItem } from '@mui/material';
import api, { SERVER_BASE_URL } from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

export default function SearchBooks() {
  const { user } = useContext(AuthContext);
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [categories, setCategories] = useState([]);
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [languageFilter, setLanguageFilter] = useState('ALL');

  const fetchDropdowns = async () => {
    try {
      const res = await api.get('/categories/all');
      setCategories(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const res = await api.get('/books', {
        params: {
          query: searchQuery,
          page: 0,
          size: 50
        }
      });
      let fetchedBooks = res.data.content;
      if (categoryFilter !== 'ALL') {
        fetchedBooks = fetchedBooks.filter(b => b.categoryId === categoryFilter);
      }
      if (languageFilter !== 'ALL') {
        fetchedBooks = fetchedBooks.filter(b => {
          if (languageFilter === 'Other') {
            const known = ['English', 'Telugu', 'Hindi', 'Tamil', 'Kannada', 'Malayalam'];
            return !b.language || !known.includes(b.language);
          }
          return b.language === languageFilter;
        });
      }
      setBooks(fetchedBooks);
    } catch (e) {
      toast.error("Failed to load catalog books.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDropdowns();
  }, []);

  useEffect(() => {
    fetchBooks();
  }, [searchQuery, categoryFilter, languageFilter]);

  const handleReserve = async (bookId) => {
    try {
      await api.post(`/issues/reserve`, null, {
        params: { userId: user.id, bookId }
      });
      toast.success("Book reserved successfully!");
      fetchBooks();
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to reserve book.");
    }
  };

  const getCoverImageSrc = (book) => {
    const cover = book.coverImageUrl || book.coverImage;
    if (!cover) return null;
    if (cover.startsWith('http://') || cover.startsWith('https://')) {
      return cover;
    }
    return `${SERVER_BASE_URL}${cover}`;
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        Search Catalog
      </Typography>

      <Box display="flex" gap={2} mb={4}>
        <TextField
          select
          label="Category"
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          sx={{ minWidth: 180 }}
        >
          <MenuItem value="ALL">All Categories</MenuItem>
          {categories.map((c) => (
            <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>
          ))}
        </TextField>

        <TextField
          select
          label="Language"
          value={languageFilter}
          onChange={(e) => setLanguageFilter(e.target.value)}
          sx={{ minWidth: 150 }}
        >
          <MenuItem value="ALL">All Languages</MenuItem>
          <MenuItem value="English">English</MenuItem>
          <MenuItem value="Telugu">Telugu</MenuItem>
          <MenuItem value="Hindi">Hindi</MenuItem>
          <MenuItem value="Tamil">Tamil</MenuItem>
          <MenuItem value="Kannada">Kannada</MenuItem>
          <MenuItem value="Malayalam">Malayalam</MenuItem>
          <MenuItem value="Other">Other</MenuItem>
        </TextField>

        <TextField
          fullWidth
          variant="outlined"
          label="Search by ISBN, title, author or category..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Grid container spacing={3}>
          {books.map((book) => (
            <Grid item xs={12} sm={6} md={4} key={book.id}>
              <Card sx={{ height: '100%', background: 'rgba(30, 41, 59, 0.45)', display: 'flex', flexDirection: 'column' }}>
                <Box height={200} sx={{ background: '#334155', position: 'relative' }}>
                  {getCoverImageSrc(book) ? (
                    <img 
                      src={getCoverImageSrc(book)} 
                      alt={book.title} 
                      style={{ width: '100%', height: '100%', objectFit: 'contain', background: '#0f172a' }} 
                    />
                  ) : (
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', color: 'text.secondary' }}>
                      <Typography variant="body2">No Book Cover</Typography>
                    </Box>
                  )}
                  <Chip
                    label={book.availableQuantity > 0 ? 'AVAILABLE' : 'RESERVE QUEUE'}
                    color={book.availableQuantity > 0 ? 'success' : 'warning'}
                    size="small"
                    sx={{ position: 'absolute', top: 10, right: 10 }}
                  />
                </Box>
                <CardContent sx={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                  <Typography variant="caption" color="primary" fontWeight="bold">
                    {book.categoryName}
                  </Typography>
                  <Typography variant="h6" fontWeight="bold" gutterBottom>
                    {book.title}
                  </Typography>
                  {book.subtitle && (
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      {book.subtitle}
                    </Typography>
                  )}
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    By {book.authorName}
                  </Typography>
                  <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
                    ISBN: {book.isbn} | Publisher: {book.publisherName}
                  </Typography>
                  
                  <Box mt="auto">
                    {book.numberOfPages && (
                      <Typography variant="body2" mb={1} color="text.secondary">
                        Pages: {book.numberOfPages}
                      </Typography>
                    )}
                    <Typography variant="body2" mb={1} color="text.primary">
                      Shelf: {book.shelfNumber || '-'} | Rack: {book.rackNumber || '-'}
                    </Typography>
                    <Typography variant="body2" mb={2} color="text.secondary">
                      Available: {book.availableQuantity} / {book.quantity} copies
                    </Typography>
                    
                    {book.availableQuantity === 0 && (
                      <Button fullWidth variant="contained" color="warning" onClick={() => handleReserve(book.id)}>
                        Reserve Book
                      </Button>
                    )}
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
          {books.length === 0 && (
            <Grid item xs={12}>
              <Typography variant="body1" align="center" py={4} color="text.secondary">
                No matching books found.
              </Typography>
            </Grid>
          )}
        </Grid>
      )}
    </Box>
  );
}
