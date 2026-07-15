import React, { useEffect, useState } from 'react';
import { Box, Card, CardContent, Typography, Grid, CircularProgress, Tabs, Tab } from '@mui/material';
import axios from 'axios';

const fallbackIndia = [
  {
    id: "fb_ind_1",
    title: "The Discovery of India",
    authors: "Jawaharlal Nehru",
    description: "Written by India's first Prime Minister during his imprisonment, this book provides an analysis of Indian history, philosophy, and culture.",
    image: "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=200&auto=format&fit=crop&q=60"
  },
  {
    id: "fb_ind_2",
    title: "Gitanjali (Song Offerings)",
    authors: "Rabindranath Tagore",
    description: "A collection of beautiful poems by Rabindranath Tagore, for which he won the Nobel Prize in Literature in 1913.",
    image: "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=200&auto=format&fit=crop&q=60"
  },
  {
    id: "fb_ind_3",
    title: "Train to Pakistan",
    authors: "Khushwant Singh",
    description: "A historical novel depicting the human tragedy of the partition of India in 1947, focusing on a border village.",
    image: "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=200&auto=format&fit=crop&q=60"
  }
];

const fallbackWorld = [
  {
    id: "fb_wld_1",
    title: "Pride and Prejudice",
    authors: "Jane Austen",
    description: "A romantic novel of manners written by Jane Austen, charting the emotional development of protagonist Elizabeth Bennet.",
    image: "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=200&auto=format&fit=crop&q=60"
  },
  {
    id: "fb_wld_2",
    title: "Hamlet",
    authors: "William Shakespeare",
    description: "A tragedy written by William Shakespeare, depicting Prince Hamlet and his revenge against his uncle Claudius.",
    image: "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=200&auto=format&fit=crop&q=60"
  },
  {
    id: "fb_wld_3",
    title: "War and Peace",
    authors: "Leo Tolstoy",
    description: "A literary masterpiece by Leo Tolstoy that details the history of the French invasion of Russia and the impact of the Napoleonic era.",
    image: "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=200&auto=format&fit=crop&q=60"
  }
];

export default function GlobalBooksWidget() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [tabValue, setTabValue] = useState(0); // 0 = India, 1 = World

  useEffect(() => {
    const fetchBooks = async () => {
      setLoading(true);
      try {
        const query = tabValue === 0 ? 'india' : 'world classics';
        const res = await axios.get(`https://www.googleapis.com/books/v1/volumes?q=${query}&maxResults=6`);
        const items = res.data.items || [];
        const parsed = items.map(item => {
          const info = item.volumeInfo || {};
          return {
            id: item.id,
            title: info.title || 'Untitled',
            authors: info.authors ? info.authors.join(', ') : 'Unknown Author',
            description: info.description || 'No description available for this book.',
            image: info.imageLinks?.thumbnail || info.imageLinks?.smallThumbnail || ''
          };
        });
        setBooks(parsed);
      } catch (err) {
        console.error("Error fetching books from Google Books API, loading fallbacks", err);
        const offlineData = tabValue === 0 ? fallbackIndia : fallbackWorld;
        setBooks(offlineData);
      } finally {
        setLoading(false);
      }
    };
    fetchBooks();
  }, [tabValue]);

  return (
    <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', p: 3, mt: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3, flexWrap: 'wrap', gap: 2 }}>
        <Typography variant="h5" fontWeight="bold">
          Global Book Discoveries
        </Typography>
        <Tabs 
          value={tabValue} 
          onChange={(e, val) => setTabValue(val)} 
          textColor="primary" 
          indicatorColor="primary"
          sx={{ borderBottom: 1, borderColor: 'rgba(255, 255, 255, 0.1)' }}
        >
          <Tab label="Featured India" sx={{ fontWeight: 'bold', color: 'rgba(255, 255, 255, 0.7)' }} />
          <Tab label="World Classics" sx={{ fontWeight: 'bold', color: 'rgba(255, 255, 255, 0.7)' }} />
        </Tabs>
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 6 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Grid container spacing={3}>
          {books.map((book) => (
            <Grid item xs={12} sm={6} md={4} key={book.id}>
              <Card 
                sx={{ 
                  height: '100%', 
                  background: 'rgba(15, 23, 42, 0.6)', 
                  border: '1px solid rgba(255, 255, 255, 0.08)',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    boxShadow: '0 8px 24px rgba(99, 102, 241, 0.25)',
                    borderColor: 'rgba(99, 102, 241, 0.4)'
                  },
                  display: 'flex',
                  flexDirection: 'column'
                }}
              >
                <Box 
                  sx={{ 
                    height: 180, 
                    display: 'flex', 
                    justifyContent: 'center', 
                    alignItems: 'center', 
                    background: 'rgba(0, 0, 0, 0.3)',
                    p: 2,
                    overflow: 'hidden'
                  }}
                >
                  {book.image ? (
                    <img 
                      src={book.image} 
                      alt={book.title} 
                      style={{ 
                        maxHeight: '100%', 
                        maxWidth: '100%', 
                        objectFit: 'contain',
                        boxShadow: '0 4px 10px rgba(0,0,0,0.3)'
                      }} 
                    />
                  ) : (
                    <Typography color="text.secondary" variant="body2">No Cover Available</Typography>
                  )}
                </Box>
                <CardContent sx={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 1 }}>
                  <Typography 
                    variant="h6" 
                    fontWeight="bold" 
                    sx={{ 
                      lineHeight: 1.2, 
                      overflow: 'hidden', 
                      textOverflow: 'ellipsis', 
                      display: '-webkit-box', 
                      WebkitLineClamp: 2, 
                      WebkitBoxOrient: 'vertical',
                      minHeight: '2.4em'
                    }}
                  >
                    {book.title}
                  </Typography>
                  <Typography variant="body2" color="primary" noWrap>
                    By {book.authors}
                  </Typography>
                  <Typography 
                    variant="caption" 
                    color="text.secondary" 
                    sx={{ 
                      overflow: 'hidden', 
                      textOverflow: 'ellipsis', 
                      display: '-webkit-box', 
                      WebkitLineClamp: 3, 
                      WebkitBoxOrient: 'vertical',
                      lineHeight: 1.4,
                      mt: 1
                    }}
                  >
                    {book.description}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Card>
  );
}
