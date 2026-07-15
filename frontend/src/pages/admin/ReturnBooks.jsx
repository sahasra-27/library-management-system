import React, { useEffect, useState, useContext } from 'react';
import { Card, CardContent, Typography, TextField, Button, Box, MenuItem, Grid, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, TablePagination } from '@mui/material';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

export default function ReturnBooks() {
  const { user } = useContext(AuthContext);
  const [barcode, setBarcode] = useState('');
  const [condition, setCondition] = useState('GOOD');
  const [submitting, setSubmitting] = useState(false);

  // Helper selectors
  const [activeIssues, setActiveIssues] = useState([]);
  const [selectedIssueId, setSelectedIssueId] = useState('');

  // Returns list
  const [returns, setReturns] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');

  const fetchReturns = async () => {
    try {
      const res = await api.get('/returns', {
        params: {
          query: searchQuery,
          page: page,
          size: rowsPerPage
        }
      });
      setReturns(res.data.content);
      setTotalItems(res.data.totalElements);
    } catch (e) {
      toast.error("Failed to load return logs.");
    }
  };

  const fetchActiveIssues = async () => {
    try {
      const res = await api.get('/issues', { params: { size: 100 } });
      const active = (res.data.content || []).filter(i => i.status === 'ISSUED' || i.status === 'OVERDUE');
      setActiveIssues(active);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchReturns();
    fetchActiveIssues();
  }, [page, rowsPerPage, searchQuery]);

  useEffect(() => {
    const issue = activeIssues.find(i => i.id === selectedIssueId);
    if (issue) {
      setBarcode(issue.barcode);
    } else {
      setBarcode('');
    }
  }, [selectedIssueId, activeIssues]);

  const handleReturn = async (e) => {
    e.preventDefault();
    if (!barcode.trim()) {
      toast.warning("Barcode is required.");
      return;
    }
    setSubmitting(true);
    try {
      await api.post('/returns', { barcode: barcode.trim(), bookCondition: condition }, {
        params: { processedBy: user.id }
      });
      toast.success("Book returned successfully!");
      setBarcode('');
      setCondition('GOOD');
      setSelectedIssueId('');
      fetchReturns();
      fetchActiveIssues();
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to return book.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        Return Management
      </Typography>

      <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', mb: 4 }}>
        <CardContent>
          <form onSubmit={handleReturn}>
            <Box mb={2}>
              <TextField
                fullWidth
                select
                label="Select Active Checked Out Copy (Optional)"
                value={selectedIssueId}
                onChange={(e) => setSelectedIssueId(e.target.value)}
                margin="normal"
              >
                <MenuItem value="">-- Type / Scan Barcode Manually --</MenuItem>
                {activeIssues.map((i) => (
                  <MenuItem key={i.id} value={i.id}>
                    {i.bookTitle} ({i.barcode}) - {i.username}
                  </MenuItem>
                ))}
              </TextField>
            </Box>

            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12} sm={5}>
                <TextField
                  fullWidth
                  label="Scan Barcode / Enter Barcode"
                  value={barcode}
                  onChange={(e) => setBarcode(e.target.value)}
                  placeholder="e.g. BAR-9781491903070-001"
                  required
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  select
                  fullWidth
                  label="Book Condition"
                  value={condition}
                  onChange={(e) => setCondition(e.target.value)}
                >
                  <MenuItem value="GOOD">Good</MenuItem>
                  <MenuItem value="DAMAGED">Damaged</MenuItem>
                  <MenuItem value="LOST">Lost</MenuItem>
                </TextField>
              </Grid>
              <Grid item xs={12} sm={3}>
                <Button
                  fullWidth
                  type="submit"
                  variant="contained"
                  color="secondary"
                  disabled={submitting}
                  sx={{ height: '56px' }}
                >
                  {submitting ? 'Processing...' : 'Process Return'}
                </Button>
              </Grid>
            </Grid>
          </form>
        </CardContent>
      </Card>

      <Typography variant="h6" fontWeight="bold" mb={2}>
        Recent Returns History
      </Typography>

      <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
        <TextField
          variant="outlined"
          size="small"
          placeholder="Search by barcode or student..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          sx={{ width: 300 }}
        />
      </Box>

      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Title</TableCell>
              <TableCell>Barcode</TableCell>
              <TableCell>Borrower</TableCell>
              <TableCell>Return Date</TableCell>
              <TableCell>Condition</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {returns.map((r) => (
              <TableRow key={r.id}>
                <TableCell>{r.bookTitle}</TableCell>
                <TableCell>{r.barcode}</TableCell>
                <TableCell>{r.username} ({r.userEmail})</TableCell>
                <TableCell>{new Date(r.returnDate).toLocaleString()}</TableCell>
                <TableCell>{r.bookCondition}</TableCell>
              </TableRow>
            ))}
            {returns.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  <Typography variant="body1" py={2} color="text.secondary">
                    No returns logged.
                  </Typography>
                </TableCell>
              </TableRow>
            )}
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
    </Box>
  );
}
