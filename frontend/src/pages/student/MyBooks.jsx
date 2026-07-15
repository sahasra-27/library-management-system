import React, { useEffect, useState, useContext } from 'react';
import {
  Grid, Card, CardContent, Typography, Box, TextField, Button, Chip,
  Divider, Paper, Alert, AlertTitle, CircularProgress
} from '@mui/material';
import {
  Book as BookIcon, Assignment, AccessTime, Autorenew,
  BookmarkBorder, Search, LibraryAddCheck
} from '@mui/icons-material';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

export default function MyBooks() {
  const { user } = useContext(AuthContext);
  const [books, setBooks] = useState([]);
  const [issues, setIssues] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [allIssuesInLibrary, setAllIssuesInLibrary] = useState([]);
  const [selectedBook, setSelectedBook] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  const fetchData = async () => {
    try {
      // 1. Get all active books
      const booksRes = await api.get('/books', { params: { size: 200 } });
      const activeBooks = (booksRes.data.content || []).filter(b => b.status === 'ACTIVE');
      setBooks(activeBooks);

      // 2. Get student's checkout logs
      const issuesRes = await api.get('/issues', {
        params: {
          userId: user.id,
          page: 0,
          size: 100
        }
      });
      setIssues(issuesRes.data.content || []);

      // 3. Get student's reservations
      const reservationsRes = await api.get('/reservations', {
        params: {
          userId: user.id,
          page: 0,
          size: 100
        }
      });
      setReservations(reservationsRes.data.content || []);

      // 4. Fetch library-wide active checkouts to determine barcode availability
      const libraryIssuesRes = await api.get('/issues', { params: { size: 1000 } });
      setAllIssuesInLibrary(libraryIssuesRes.data.content || []);
    } catch (e) {
      toast.error("Failed to load catalog data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user) {
      fetchData();
    }
  }, [user]);

  // Keep selected book reference fresh if data updates
  useEffect(() => {
    if (selectedBook) {
      const fresh = books.find(b => b.id === selectedBook.id);
      setSelectedBook(fresh || null);
    }
  }, [books]);

  const handleIssueBook = async () => {
    if (!selectedBook) return;
    setActionLoading(true);

    // Find the first copy barcode of the selected book that is NOT currently borrowed
    let targetBarcode = '';
    for (let i = 1; i <= selectedBook.quantity; i++) {
      const numStr = String(i).padStart(3, '0');
      const testBarcode = `BAR-${selectedBook.isbn}-${numStr}`;
      const isBorrowed = allIssuesInLibrary.some(
        iss => iss.barcode === testBarcode && (iss.status === 'ISSUED' || iss.status === 'OVERDUE')
      );
      if (!isBorrowed) {
        targetBarcode = testBarcode;
        break;
      }
    }

    // Fallback if none found but available quantity is > 0
    if (!targetBarcode) {
      targetBarcode = `BAR-${selectedBook.isbn}-001`;
    }

    try {
      await api.post('/issues', {
        userId: user.id,
        barcode: targetBarcode
      });
      toast.success(`Successfully checked out: "${selectedBook.title}" (Copy: ${targetBarcode})`);
      await fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || "Self checkout failed. Please contact the librarian.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleRenew = async (issueId) => {
    setActionLoading(true);
    try {
      await api.post(`/issues/${issueId}/renew`);
      toast.success("Book copy renewed successfully!");
      await fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || "Renewal failed. Check reservation queue.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleReserve = async (bookId) => {
    setActionLoading(true);
    try {
      await api.post('/issues/reserve', null, {
        params: { userId: user.id, bookId }
      });
      toast.success("Book reserved successfully!");
      await fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to reserve book.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleCancelReservation = async (reservationId) => {
    if (!window.confirm("Are you sure you want to cancel this reservation?")) return;
    setActionLoading(true);
    try {
      await api.delete(`/issues/reserve/${reservationId}`, {
        params: { userId: user.id }
      });
      toast.success("Reservation cancelled.");
      await fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to cancel reservation.");
    } finally {
      setActionLoading(false);
    }
  };

  // Helper matching states
  const getBookCheckout = (bookId) => {
    return issues.find(i => i.bookId === bookId && (i.status === 'ISSUED' || i.status === 'OVERDUE'));
  };

  const getBookReservation = (bookId) => {
    return reservations.find(r => r.bookId === bookId && r.status === 'PENDING');
  };

  const filteredBooks = books.filter(b =>
    b.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    b.authorName.toLowerCase().includes(searchQuery.toLowerCase()) ||
    b.categoryName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  const activeCheckout = selectedBook ? getBookCheckout(selectedBook.id) : null;
  const activeReservation = selectedBook ? getBookReservation(selectedBook.id) : null;

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        Library Workspace
      </Typography>

      <Grid container spacing={3}>
        {/* Left Side: Books Catalog in Grid Form */}
        <Grid item xs={12} md={7}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', height: '75vh', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ p: 2, pb: 0 }}>
              <TextField
                fullWidth
                size="small"
                placeholder="Search catalog by title, author, or category..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                InputProps={{
                  startAdornment: <Search fontSize="small" sx={{ mr: 1, color: 'text.secondary' }} />
                }}
                sx={{ mb: 2 }}
              />
              <Typography variant="subtitle2" color="text.secondary" mb={1.5}>
                Available Catalog ({filteredBooks.length} books)
              </Typography>
            </CardContent>
            <Divider />
            <Box sx={{ flex: 1, overflowY: 'auto', p: 2 }}>
              <Grid container spacing={2}>
                {filteredBooks.map((book) => {
                  const hasCheckedOut = getBookCheckout(book.id);
                  const hasReserved = getBookReservation(book.id);
                  const isSelected = selectedBook?.id === book.id;

                  return (
                    <Grid item xs={12} sm={6} key={book.id}>
                      <Card
                        onClick={() => setSelectedBook(book)}
                        sx={{
                          cursor: 'pointer',
                          background: isSelected ? 'rgba(99, 102, 241, 0.18)' : 'rgba(30, 41, 59, 0.25)',
                          border: isSelected ? '2px solid #6366f1' : '1px solid rgba(255, 255, 255, 0.08)',
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            transform: 'translateY(-2px)',
                            background: 'rgba(30, 41, 59, 0.45)'
                          },
                          height: '100%',
                          display: 'flex',
                          flexDirection: 'column',
                          justifyContent: 'space-between'
                        }}
                      >
                        <CardContent sx={{ p: 2 }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1, alignItems: 'center' }}>
                            <Chip label={book.categoryName} size="small" color="secondary" variant="outlined" sx={{ height: 18, fontSize: '0.65rem' }} />
                            {hasCheckedOut && (
                              <Chip label="Borrowed" size="small" color="primary" sx={{ height: 18, fontSize: '0.65rem' }} />
                            )}
                            {hasReserved && (
                              <Chip label="Reserved" size="small" color="warning" sx={{ height: 18, fontSize: '0.65rem' }} />
                            )}
                          </Box>
                          <Typography variant="body1" fontWeight="bold" gutterBottom sx={{
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            display: '-webkit-box',
                            WebkitLineClamp: 2,
                            WebkitBoxOrient: 'vertical',
                            lineHeight: 1.2,
                            minHeight: '2.4em'
                          }}>
                            {book.title}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" noWrap>
                            By {book.authorName}
                          </Typography>
                          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>
                            Available: {book.availableQuantity} / {book.quantity} copies
                          </Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                  );
                })}
                {filteredBooks.length === 0 && (
                  <Grid item xs={12}>
                    <Box sx={{ p: 3, textAlign: 'center' }}>
                      <Typography color="text.secondary">No books found.</Typography>
                    </Box>
                  </Grid>
                )}
              </Grid>
            </Box>
          </Card>
        </Grid>

        {/* Right Side: Details and Borrowing status */}
        <Grid item xs={12} md={5}>
          {selectedBook ? (
            <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', minHeight: '75vh', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ p: 4, display: 'flex', flexDirection: 'column', gap: 3, flex: 1 }}>
                
                {/* Book Header Details */}
                <Box>
                  <Chip label={selectedBook.categoryName} color="secondary" size="small" sx={{ mb: 1.5 }} />
                  <Typography variant="h5" fontWeight="bold" gutterBottom>
                    {selectedBook.title}
                  </Typography>
                  <Typography variant="body1" color="text.secondary" gutterBottom>
                    By {selectedBook.authorName}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    ISBN: {selectedBook.isbn} | Publisher: {selectedBook.publisherName} | Edition: {selectedBook.edition || '1st'} ({selectedBook.publicationYear || 'N/A'})
                  </Typography>
                </Box>

                <Divider />

                {/* Library availability status */}
                <Box>
                  <Typography variant="subtitle1" fontWeight="bold" mb={1}>
                    Catalog Copies
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 3, alignItems: 'center' }}>
                    <Box>
                      <Typography variant="body2" color="text.secondary">Available Copies</Typography>
                      <Typography variant="h6" fontWeight="bold" color="success.main">
                        {selectedBook.availableQuantity} / {selectedBook.quantity}
                      </Typography>
                    </Box>
                    <Box>
                      <Typography variant="body2" color="text.secondary">Shelf Location</Typography>
                      <Typography variant="h6" fontWeight="bold">
                        {selectedBook.shelfNumber || '-'} (Rack {selectedBook.rackNumber || '-'})
                      </Typography>
                    </Box>
                  </Box>
                </Box>

                <Divider />

                {/* User Borrow/Reserve Status Section */}
                <Box sx={{ flex: 1 }}>
                  <Typography variant="subtitle1" fontWeight="bold" mb={2}>
                    Your Personal Status
                  </Typography>

                  {activeCheckout ? (
                    <Alert severity="info" icon={<Assignment />} sx={{ background: 'rgba(2, 136, 209, 0.12)', border: '1px solid rgba(2, 136, 209, 0.3)' }}>
                      <AlertTitle sx={{ fontWeight: 'bold' }}>Active Borrowing Session</AlertTitle>
                      <Typography variant="body2" sx={{ mt: 1 }}>
                        Barcode: <strong>{activeCheckout.barcode}</strong>
                      </Typography>
                      <Typography variant="body2">
                        Borrowed on: <strong>{new Date(activeCheckout.issueDate).toLocaleDateString()}</strong>
                      </Typography>
                      <Typography variant="body2" sx={{ color: activeCheckout.status === 'OVERDUE' ? 'error.main' : 'inherit', fontWeight: activeCheckout.status === 'OVERDUE' ? 'bold' : 'normal' }}>
                        Due Date: <strong>{new Date(activeCheckout.dueDate).toLocaleDateString()}</strong>
                      </Typography>
                      {activeCheckout.fineAmount > 0 && (
                        <Typography variant="body2" color="error.main" sx={{ fontWeight: 'bold' }}>
                          Outstanding Fine: ₹{activeCheckout.fineAmount}
                        </Typography>
                      )}
                      
                      <Box sx={{ mt: 2 }}>
                        <Button
                          variant="contained"
                          size="small"
                          startIcon={<Autorenew />}
                          disabled={actionLoading}
                          onClick={() => handleRenew(activeCheckout.id)}
                          sx={{ textTransform: 'none' }}
                        >
                          Renew Checkout
                        </Button>
                      </Box>
                    </Alert>
                  ) : activeReservation ? (
                    <Alert severity="warning" icon={<AccessTime />} sx={{ background: 'rgba(237, 108, 2, 0.12)', border: '1px solid rgba(237, 108, 2, 0.3)' }}>
                      <AlertTitle sx={{ fontWeight: 'bold' }}>Pending Reservation Queue</AlertTitle>
                      <Typography variant="body2" sx={{ mt: 1 }}>
                        Reserved on: <strong>{new Date(activeReservation.reservationDate).toLocaleDateString()}</strong>
                      </Typography>
                      <Typography variant="body2" sx={{ mb: 2 }}>
                        You will be notified as soon as a copy becomes available.
                      </Typography>
                      <Button
                        variant="outlined"
                        color="warning"
                        size="small"
                        disabled={actionLoading}
                        onClick={() => handleCancelReservation(activeReservation.id)}
                        sx={{ textTransform: 'none' }}
                      >
                        Cancel Reservation
                      </Button>
                    </Alert>
                  ) : (
                    <Box>
                      <Typography variant="body2" color="text.secondary" mb={2}>
                        You do not currently have this book checked out or reserved.
                      </Typography>
                      
                      {selectedBook.availableQuantity > 0 ? (
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                          <Alert severity="success">
                            Copies are available! You can self-issue this book now.
                          </Alert>
                          <Button
                            variant="contained"
                            color="success"
                            startIcon={<LibraryAddCheck />}
                            disabled={actionLoading}
                            onClick={handleIssueBook}
                            sx={{ textTransform: 'none', fontWeight: 'bold' }}
                          >
                            Issue Book (Self Checkout)
                          </Button>
                        </Box>
                      ) : (
                        <Box>
                          <Alert severity="error" sx={{ mb: 2 }}>
                            All copies are currently checked out.
                          </Alert>
                          <Button
                            variant="contained"
                            color="warning"
                            startIcon={<BookmarkBorder />}
                            disabled={actionLoading}
                            onClick={() => handleReserve(selectedBook.id)}
                            sx={{ textTransform: 'none' }}
                          >
                            Reserve Book (Join Queue)
                          </Button>
                        </Box>
                      )}
                    </Box>
                  )}
                </Box>
                
              </CardContent>
            </Card>
          ) : (
            <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', height: '75vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <BookIcon sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No Book Selected
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Choose a book card from the grid on the left to view details and check out or reserve.
                </Typography>
              </CardContent>
            </Card>
          )}
        </Grid>
      </Grid>
    </Box>
  );
}
