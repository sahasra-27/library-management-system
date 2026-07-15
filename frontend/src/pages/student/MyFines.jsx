import React, { useEffect, useState, useContext } from 'react';
import { 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, 
  Button, Box, Typography, TablePagination, Chip, Grid, Card, CardContent 
} from '@mui/material';
import { AccountBalanceWallet, CheckCircle, Payment } from '@mui/icons-material';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

export default function MyFines() {
  const { user } = useContext(AuthContext);
  const [fines, setFines] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [loading, setLoading] = useState(true);

  // Stats
  const [stats, setStats] = useState({ totalUnpaid: 0, totalCollected: 0 });

  const fetchFines = async () => {
    try {
      const res = await api.get('/fines', {
        params: {
          userId: user.id,
          page: page,
          size: rowsPerPage
        }
      });
      setFines(res.data.content || []);
      setTotalItems(res.data.totalElements || 0);
      fetchStats();
    } catch (e) {
      toast.error("Failed to load fines data.");
    } finally {
      setLoading(false);
    }
  };

  const fetchStats = async () => {
    try {
      const res = await api.get('/fines/stats', {
        params: {
          userId: user.id
        }
      });
      setStats(res.data);
    } catch (e) {
      console.error("Failed to fetch fine stats", e);
    }
  };

  useEffect(() => {
    if (user) {
      fetchFines();
    }
  }, [user, page, rowsPerPage]);

  const handlePayFine = async (id, amount) => {
    if (!window.confirm(`Confirm payment of ₹${amount} for this fine record?`)) return;
    try {
      await api.post(`/fines/${id}/pay`);
      toast.success("Fine paid successfully!");
      fetchFines();
    } catch (e) {
      toast.error(e.response?.data?.message || "Failed to make payment.");
    }
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        My Fines & Penalties
      </Typography>

      {/* Stats Cards */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6}>
          <Card sx={{ background: 'linear-gradient(135deg, rgba(239, 68, 68, 0.2) 0%, rgba(220, 38, 38, 0.05) 100%)', backdropFilter: 'blur(8px)', border: '1px solid rgba(239, 68, 68, 0.3)' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <AccountBalanceWallet sx={{ color: '#ef4444', fontSize: 40 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary" fontWeight="bold">
                    Total Outstanding Fine
                  </Typography>
                  <Typography variant="h4" color="#ef4444" fontWeight="bold">
                    ₹{stats.totalUnpaid ? parseFloat(stats.totalUnpaid).toFixed(2) : '0.00'}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6}>
          <Card sx={{ background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.2) 0%, rgba(5, 150, 105, 0.05) 100%)', backdropFilter: 'blur(8px)', border: '1px solid rgba(16, 185, 129, 0.3)' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <CheckCircle sx={{ color: '#10b981', fontSize: 40 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary" fontWeight="bold">
                    Total Paid Fine
                  </Typography>
                  <Typography variant="h4" color="#10b981" fontWeight="bold">
                    ₹{stats.totalCollected ? parseFloat(stats.totalCollected).toFixed(2) : '0.00'}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Fines Table */}
      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)', backdropFilter: 'blur(8px)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Fine ID</TableCell>
              <TableCell>Book Title</TableCell>
              <TableCell>Issue Date</TableCell>
              <TableCell>Due Date</TableCell>
              <TableCell>Return Date</TableCell>
              <TableCell>Days Late</TableCell>
              <TableCell>Fine Amount</TableCell>
              <TableCell>Fine Reason</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Payment Date</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {fines.map((f) => (
              <TableRow key={f.id}>
                <TableCell>{f.id}</TableCell>
                <TableCell>{f.bookTitle}</TableCell>
                <TableCell>{f.issueDate ? new Date(f.issueDate).toLocaleDateString() : 'N/A'}</TableCell>
                <TableCell>{f.dueDate ? new Date(f.dueDate).toLocaleDateString() : 'N/A'}</TableCell>
                <TableCell>{f.returnDate ? new Date(f.returnDate).toLocaleDateString() : '-'}</TableCell>
                <TableCell>{f.daysLate !== null ? `${f.daysLate} days` : '-'}</TableCell>
                <TableCell>₹{f.amount}</TableCell>
                <TableCell>{f.reason || 'N/A'}</TableCell>
                <TableCell>
                  <Chip
                    label={f.status}
                    color={f.status === 'PAID' ? 'success' : f.status === 'OVERDUE' ? 'warning' : 'error'}
                    size="small"
                  />
                </TableCell>
                <TableCell>{f.paidDate ? new Date(f.paidDate).toLocaleString() : '-'}</TableCell>
                <TableCell align="right">
                  {f.status !== 'PAID' ? (
                    <Button
                      variant="contained"
                      color="success"
                      size="small"
                      startIcon={<Payment />}
                      onClick={() => handlePayFine(f.id, f.amount)}
                      sx={{
                        background: 'linear-gradient(90deg, #10b981 0%, #059669 100%)',
                        boxShadow: '0 2px 10px 0 rgba(16, 185, 129, 0.3)',
                      }}
                    >
                      Pay Fine
                    </Button>
                  ) : (
                    <Chip label="Paid" color="success" size="small" variant="outlined" />
                  )}
                </TableCell>
              </TableRow>
            ))}
            {fines.length === 0 && !loading && (
              <TableRow>
                <TableCell colSpan={11} align="center">
                  <Typography variant="body1" py={3} color="text.secondary">
                    No fines or penalties recorded.
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
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </TableContainer>
    </Box>
  );
}
