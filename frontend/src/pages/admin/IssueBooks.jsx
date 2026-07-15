import React, { useEffect, useState } from 'react';
import { Card, CardContent, Typography, TextField, Button, Box, MenuItem, Grid, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, TablePagination } from '@mui/material';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function IssueBooks() {
  const [users, setUsers] = useState([]);
  const [userId, setUserId] = useState('');
  const [barcode, setBarcode] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Helper selectors
  const [booksList, setBooksList] = useState([]);
  const [selectedBookId, setSelectedBookId] = useState('');
  const [availableBarcodes, setAvailableBarcodes] = useState([]);
  const [allActiveIssues, setAllActiveIssues] = useState([]);

  // Issues list
  const [issues, setIssues] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');

  const fetchUsers = async () => {
    try {
      const res = await api.get('/users');
      setUsers(res.data.content);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchBooks = async () => {
    try {
      const res = await api.get('/books', { params: { size: 100 } });
      setBooksList(res.data.content || []);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchAllActiveIssues = async () => {
    try {
      const res = await api.get('/issues', { params: { size: 1000 } });
      const active = (res.data.content || []).filter(i => i.status === 'ISSUED' || i.status === 'OVERDUE');
      setAllActiveIssues(active);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchIssues = async () => {
    try {
      const res = await api.get('/issues', {
        params: {
          query: searchQuery,
          page: page,
          size: rowsPerPage
        }
      });
      setIssues(res.data.content);
      setTotalItems(res.data.totalElements);
    } catch (e) {
      toast.error("Failed to load issue logs.");
    }
  };

  useEffect(() => {
    fetchUsers();
    fetchBooks();
    fetchAllActiveIssues();
  }, []);

  useEffect(() => {
    const book = booksList.find(b => b.id === selectedBookId);
    if (book) {
      const codes = [];
      for (let i = 1; i <= book.quantity; i++) {
        const numStr = String(i).padStart(3, '0');
        codes.push(`BAR-${book.isbn}-${numStr}`);
      }
      setAvailableBarcodes(codes);
    } else {
      setAvailableBarcodes([]);
    }
  }, [selectedBookId, booksList]);

  useEffect(() => {
    fetchIssues();
  }, [page, rowsPerPage, searchQuery]);

  const handleIssue = async (e) => {
    e.preventDefault();
    if (!userId || !barcode.trim()) {
      toast.warning("Please fill in all checkout parameters.");
      return;
    }
    setSubmitting(true);
    try {
      await api.post('/issues', { userId, barcode: barcode.trim() });
      toast.success("Book issued successfully!");
      setBarcode('');
      setUserId('');
      setSelectedBookId('');
      fetchIssues();
      fetchAllActiveIssues();
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to issue book.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        Issue Books (Check-out)
      </Typography>

      <Grid container spacing={4}>
        <Grid item xs={12} md={4}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
            <CardContent>
              <Typography variant="h6" fontWeight="bold" mb={2}>
                New Checkout Session
              </Typography>
              <form onSubmit={handleIssue}>
                <TextField
                  fullWidth
                  select
                  label="Select User"
                  value={userId}
                  onChange={(e) => setUserId(e.target.value)}
                  margin="normal"
                  required
                >
                  {users.map((u) => (
                    <MenuItem key={u.id} value={u.id}>
                      {u.username} ({u.email})
                    </MenuItem>
                  ))}
                </TextField>

                <TextField
                  fullWidth
                  select
                  label="Select Book (Optional)"
                  value={selectedBookId}
                  onChange={(e) => {
                    setSelectedBookId(e.target.value);
                    setBarcode('');
                  }}
                  margin="normal"
                >
                  <MenuItem value="">-- Type / Scan Barcode Manually --</MenuItem>
                  {booksList.map((b) => (
                    <MenuItem key={b.id} value={b.id}>
                      {b.title} (ISBN: {b.isbn})
                    </MenuItem>
                  ))}
                </TextField>

                {selectedBookId ? (
                  <TextField
                    fullWidth
                    select
                    label="Book Copy Barcode"
                    value={barcode}
                    onChange={(e) => setBarcode(e.target.value)}
                    margin="normal"
                    required
                    helperText="Select one of the copy barcodes for this book"
                  >
                    <MenuItem value="">-- Select Barcode --</MenuItem>
                    {availableBarcodes.map((code) => {
                      const borrowed = allActiveIssues.some(i => i.barcode === code);
                      return (
                        <MenuItem key={code} value={code} disabled={borrowed}>
                          {code} {borrowed ? '(Borrowed)' : '(Available)'}
                        </MenuItem>
                      );
                    })}
                  </TextField>
                ) : (
                  <TextField
                    fullWidth
                    label="Book Copy Barcode"
                    value={barcode}
                    onChange={(e) => setBarcode(e.target.value)}
                    placeholder="e.g. BAR-9781234567890-001"
                    margin="normal"
                    required
                    helperText="Scan or type physical copy barcode"
                  />
                )}

                <Button
                  fullWidth
                  type="submit"
                  variant="contained"
                  disabled={submitting}
                  sx={{ mt: 3 }}
                >
                  {submitting ? 'Processing...' : 'Checkout Book'}
                </Button>
              </form>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={8}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', p: 2 }}>
            <Typography variant="h6" fontWeight="bold" mb={2}>
              Active Checkout Log
            </Typography>

            <Box mb={2}>
              <TextField
                fullWidth
                label="Search checkout logs..."
                value={searchQuery}
                onChange={(e) => { setSearchQuery(e.target.value); setPage(0); }}
              />
            </Box>

            <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.2)' }}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Borrower</TableCell>
                    <TableCell>Book Title</TableCell>
                    <TableCell>Barcode</TableCell>
                    <TableCell>Issue Date</TableCell>
                    <TableCell>Due Date</TableCell>
                    <TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {issues.map((i) => (
                    <TableRow key={i.id}>
                      <TableCell>{i.username}</TableCell>
                      <TableCell>{i.bookTitle}</TableCell>
                      <TableCell>{i.barcode}</TableCell>
                      <TableCell>{new Date(i.issueDate).toLocaleDateString()}</TableCell>
                      <TableCell>{new Date(i.dueDate).toLocaleDateString()}</TableCell>
                      <TableCell>{i.status}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
              <TablePagination
                component="div"
                count={totalItems}
                page={page}
                onPageChange={(e, p) => setPage(p)}
                rowsPerPage={rowsPerPage}
                onRowsPerPageChange={(e) => { setRowsPerPage(parseInt(e.target.value, 10)); setPage(0); }}
              />
            </TableContainer>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
