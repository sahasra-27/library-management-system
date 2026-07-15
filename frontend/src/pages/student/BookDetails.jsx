import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Card, CardContent, Typography, Button, Grid, CircularProgress, Chip, Divider, Paper } from '@mui/material';
import { Book as BookIcon, CalendarToday, LocalAtm, LibraryBooks, Assignment, History, ArrowBack } from '@mui/icons-material';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

const placeholderImage = 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=500&auto=format&fit=crop&q=60';

export default function BookDetails() {
  const { workId } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);

  const [book, setBook] = useState(null);
  const [editions, setEditions] = useState([]);
  const [authorDetail, setAuthorDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    const fetchAllDetails = async () => {
      setLoading(true);
      try {
        // 1. Fetch work details
        const res = await api.get(`/openlibrary/works/${workId}`);
        setBook(res.data);

        // 2. Fetch editions
        try {
          const edRes = await api.get(`/openlibrary/works/${workId}/editions`);
          setEditions(edRes.data || []);
        } catch (e) {
          console.error("Failed to load editions", e);
        }

        // 3. Fetch first author biography details
        if (res.data.authorKeys && res.data.authorKeys.length > 0) {
          try {
            const authRes = await api.get(`/openlibrary/authors/${res.data.authorKeys[0]}`);
            setAuthorDetail(authRes.data);
          } catch (e) {
            console.error("Failed to load author biography", e);
          }
        }
      } catch (err) {
        toast.error("Failed to load book detailed information from Open Library.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchAllDetails();
  }, [workId]);

  const handleBorrow = async () => {
    if (!user) {
      toast.warning("Please log in first to borrow books.");
      return;
    }
    setActionLoading(true);
    try {
      // 1. Import book into MySQL
      toast.info("Importing book into MySQL database...");
      const importRes = await api.post(`/books/import/${workId}`);
      const importedBook = importRes.data;

      // 2. Perform library checkout using standard copy BAR-{isbn}-001
      const barcode = `BAR-${importedBook.isbn}-001`;
      await api.post('/issues', {
        userId: user.id,
        barcode: barcode
      });

      toast.success(`Successfully checked out: "${importedBook.title}" (Copy: ${barcode})!`);
      navigate('/student/my-books');
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to complete borrow transaction.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleReserve = async () => {
    if (!user) {
      toast.warning("Please log in first to reserve books.");
      return;
    }
    setActionLoading(true);
    try {
      // 1. Import book into MySQL
      toast.info("Importing book into MySQL database...");
      const importRes = await api.post(`/books/import/${workId}`);
      const importedBook = importRes.data;

      // 2. Perform library reservation
      await api.post(`/issues/reserve`, null, {
        params: { userId: user.id, bookId: importedBook.id }
      });

      toast.success(`Successfully reserved: "${importedBook.title}"!`);
      navigate('/student/reservations');
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to complete reservation.");
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!book) {
    return (
      <Box p={3} textAlign="center">
        <Typography color="text.secondary">Book details could not be found.</Typography>
        <Button startIcon={<ArrowBack />} onClick={() => navigate(-1)} sx={{ mt: 2 }}>Back</Button>
      </Box>
    );
  }

  return (
    <Box>
      <Button startIcon={<ArrowBack />} onClick={() => navigate(-1)} sx={{ mb: 3 }}>
        Back to Dashboard
      </Button>

      <Grid container spacing={4}>
        {/* Cover Image Column */}
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 2, background: 'rgba(30, 41, 59, 0.45)', display: 'flex', justifyContent: 'center' }}>
            <img 
              src={book.coverUrl || placeholderImage} 
              alt={book.title} 
              style={{ width: '100%', maxHeight: 450, objectFit: 'contain', borderRadius: 4 }}
              onError={(e) => { e.target.src = placeholderImage; }}
            />
          </Paper>
        </Grid>

        {/* Book Details Column */}
        <Grid item xs={12} md={8}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', height: '100%' }}>
            <CardContent sx={{ p: 4, display: 'flex', flexDirection: 'column', gap: 3 }}>
              <Box>
                <Typography variant="h3" fontWeight="bold" gutterBottom>
                  {book.title}
                </Typography>
                <Typography variant="h6" color="primary" gutterBottom>
                  By {book.authors && book.authors.length > 0 ? book.authors.join(', ') : 'Unknown Author'}
                </Typography>
                <Box display="flex" gap={1} flexWrap="wrap" mt={1.5}>
                  {book.subjects && book.subjects.slice(0, 5).map((subject, idx) => (
                    <Chip key={idx} label={subject} size="small" variant="outlined" color="secondary" />
                  ))}
                </Box>
              </Box>

              <Divider />

              <Box>
                <Typography variant="h6" fontWeight="bold" gutterBottom>Description</Typography>
                <Typography variant="body1" color="text.secondary" sx={{ whiteSpace: 'pre-line', lineHeight: 1.6 }}>
                  {book.description || 'No description available for this book.'}
                </Typography>
              </Box>

              <Divider />

              {/* Metadata details */}
              <Grid container spacing={2}>
                <Grid item xs={6} sm={4}>
                  <Typography variant="caption" color="text.secondary">First Publish Date</Typography>
                  <Typography variant="body1" fontWeight="bold">{book.firstPublishDate || 'N/A'}</Typography>
                </Grid>
                <Grid item xs={6} sm={4}>
                  <Typography variant="caption" color="text.secondary">Editions Available</Typography>
                  <Typography variant="body1" fontWeight="bold">{book.editionsCount || 1}</Typography>
                </Grid>
                <Grid item xs={6} sm={4}>
                  <Typography variant="caption" color="text.secondary">ISBN Code</Typography>
                  <Typography variant="body1" fontWeight="bold">{book.isbn || 'N/A'}</Typography>
                </Grid>
              </Grid>

              <Box display="flex" gap={2} mt={2}>
                <Button 
                  variant="contained" 
                  size="large" 
                  onClick={handleBorrow} 
                  disabled={actionLoading}
                  startIcon={<BookIcon />}
                  sx={{
                    background: 'linear-gradient(90deg, #6366f1 0%, #4f46e5 100%)',
                    boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.4)',
                    px: 4
                  }}
                >
                  Borrow Book (MySQL)
                </Button>
                <Button 
                  variant="outlined" 
                  size="large" 
                  onClick={handleReserve} 
                  disabled={actionLoading}
                  startIcon={<History />}
                  sx={{ px: 4 }}
                >
                  Reserve Book
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Author Details section */}
      {authorDetail && (
        <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', mt: 4, p: 3 }}>
          <Typography variant="h5" fontWeight="bold" mb={2}>
            About the Author
          </Typography>
          <Box display="flex" gap={3} flexWrap="wrap" alignItems="flex-start">
            {authorDetail.photoUrl && (
              <img 
                src={authorDetail.photoUrl} 
                alt={authorDetail.name} 
                style={{ width: 100, height: 130, objectFit: 'cover', borderRadius: 4, border: '1px solid rgba(255, 255, 255, 0.1)' }}
              />
            )}
            <Box flex={1}>
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                {authorDetail.name}
              </Typography>
              {(authorDetail.birthDate || authorDetail.deathDate) && (
                <Typography variant="caption" color="text.secondary" display="block" mb={1}>
                  {authorDetail.birthDate || 'N/A'} {authorDetail.deathDate ? ` - ${authorDetail.deathDate}` : ''}
                </Typography>
              )}
              <Typography variant="body2" color="text.secondary" sx={{ whiteSpace: 'pre-line', lineHeight: 1.5 }}>
                {authorDetail.biography || 'No biography text is currently available for this author.'}
              </Typography>
              <Button 
                variant="text" 
                size="small" 
                onClick={() => navigate(`/student/author/${book.authorKeys[0]}`)}
                sx={{ mt: 1, p: 0 }}
              >
                View Full Biography
              </Button>
            </Box>
          </Box>
        </Card>
      )}

      {/* Editions section */}
      {editions.length > 0 && (
        <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', mt: 4, p: 3 }}>
          <Typography variant="h5" fontWeight="bold" mb={2}>
            Available Editions ({editions.length})
          </Typography>
          <Grid container spacing={2}>
            {editions.map((ed, idx) => (
              <Grid item xs={12} sm={6} md={4} key={idx}>
                <Paper sx={{ p: 2, background: 'rgba(15, 23, 42, 0.4)', border: '1px solid rgba(255, 255, 255, 0.05)' }}>
                  <Typography variant="body1" fontWeight="bold" noWrap>{ed.title}</Typography>
                  <Typography variant="caption" color="text.secondary" display="block" mt={0.5}>
                    Publish Date: {ed.publishDate || 'Unknown'}
                  </Typography>
                  <Typography variant="caption" color="text.secondary" display="block" noWrap>
                    Publisher: {ed.publishers && ed.publishers.length > 0 ? ed.publishers.join(', ') : 'N/A'}
                  </Typography>
                  <Typography variant="caption" color="primary" display="block" mt={1}>
                    ISBN: {ed.isbn || 'N/A'}
                  </Typography>
                </Paper>
              </Grid>
            ))}
          </Grid>
        </Card>
      )}
    </Box>
  );
}
