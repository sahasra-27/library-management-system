import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Card, CardContent, Typography, Button, Grid, CircularProgress, Paper, Divider } from '@mui/material';
import { ArrowBack } from '@mui/icons-material';
import api from '../../services/api';
import { toast } from 'react-toastify';

const placeholderAuthor = 'https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=300&auto=format&fit=crop&q=60';

export default function AuthorDetails() {
  const { authorId } = useParams();
  const navigate = useNavigate();

  const [author, setAuthor] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAuthorDetails = async () => {
      setLoading(true);
      try {
        const res = await api.get(`/openlibrary/authors/${authorId}`);
        setAuthor(res.data);
      } catch (err) {
        toast.error("Failed to load author details from Open Library.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchAuthorDetails();
  }, [authorId]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!author) {
    return (
      <Box p={3} textAlign="center">
        <Typography color="text.secondary">Author details could not be found.</Typography>
        <Button startIcon={<ArrowBack />} onClick={() => navigate(-1)} sx={{ mt: 2 }}>Back</Button>
      </Box>
    );
  }

  return (
    <Box>
      <Button startIcon={<ArrowBack />} onClick={() => navigate(-1)} sx={{ mb: 3 }}>
        Back
      </Button>

      <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', p: 4 }}>
        <Grid container spacing={4} alignItems="flex-start">
          {/* Author Image */}
          <Grid item xs={12} sm={4} md={3}>
            <Paper sx={{ p: 1.5, background: 'rgba(15, 23, 42, 0.5)', display: 'flex', justifyContent: 'center' }}>
              <img 
                src={author.photoUrl || placeholderAuthor} 
                alt={author.name} 
                style={{ width: '100%', maxHeight: 350, objectFit: 'cover', borderRadius: 4 }}
                onError={(e) => { e.target.src = placeholderAuthor; }}
              />
            </Paper>
          </Grid>

          {/* Author Biography Details */}
          <Grid item xs={12} sm={8} md={9}>
            <CardContent sx={{ p: 0 }}>
              <Typography variant="h3" fontWeight="bold" gutterBottom>
                {author.name}
              </Typography>

              {(author.birthDate || author.deathDate) && (
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  {author.birthDate || 'N/A'} {author.deathDate ? ` - ${author.deathDate}` : ''}
                </Typography>
              )}

              <Divider sx={{ my: 2, borderColor: 'rgba(255, 255, 255, 0.1)' }} />

              <Typography variant="h6" fontWeight="bold" mb={1.5}>
                Biography
              </Typography>
              <Typography variant="body1" color="text.secondary" sx={{ whiteSpace: 'pre-line', lineHeight: 1.7 }}>
                {author.biography || 'No biography details are available for this author.'}
              </Typography>
            </CardContent>
          </Grid>
        </Grid>
      </Card>
    </Box>
  );
}
