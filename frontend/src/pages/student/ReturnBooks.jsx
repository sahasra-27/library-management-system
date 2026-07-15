import React, { useEffect, useState, useContext } from 'react';
import { 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, 
  Box, Typography, TablePagination, Chip, Button 
} from '@mui/material';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

export default function ReturnBooks() {
  const { user } = useContext(AuthContext);
  const [issuedBooks, setIssuedBooks] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [loading, setLoading] = useState(true);

  const fetchActiveIssues = async () => {
    try {
      const res = await api.get('/issues', {
        params: {
          userId: user.id,
          page: 0,
          size: 1000
        }
      });
      // Filter out only active borrow sessions (ISSUED or OVERDUE)
      const active = (res.data.content || []).filter(
        item => item.status === 'ISSUED' || item.status === 'OVERDUE'
      );
      setIssuedBooks(active);
      setTotalItems(active.length);
    } catch (e) {
      toast.error("Failed to load your issued books.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user) {
      fetchActiveIssues();
    }
  }, [user]);

  const handleReturnBook = async (barcode, title) => {
    if (!window.confirm(`Are you sure you want to return "${title}"?`)) return;
    try {
      await api.post(`/returns?processedBy=${user.id}`, {
        barcode: barcode,
        bookCondition: 'GOOD'
      });
      toast.success(`"${title}" has been successfully returned!`);
      fetchActiveIssues();
    } catch (e) {
      toast.error(e.response?.data?.message || "Failed to return book.");
    }
  };

  const getDaysRemaining = (dueDate) => {
    const due = new Date(dueDate);
    const today = new Date();
    due.setHours(0,0,0,0);
    today.setHours(0,0,0,0);
    const diffTime = due.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const paginatedIssues = issuedBooks.slice(page * rowsPerPage, (page + 1) * rowsPerPage);

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        Return Checked-out Books
      </Typography>

      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)', backdropFilter: 'blur(8px)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Book Title</TableCell>
              <TableCell>Author</TableCell>
              <TableCell>ISBN</TableCell>
              <TableCell>Issue Date</TableCell>
              <TableCell>Due Date</TableCell>
              <TableCell>Days Remaining</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {paginatedIssues.map((item) => {
              const daysRemaining = getDaysRemaining(item.dueDate);
              return (
                <TableRow key={item.id}>
                  <TableCell>{item.bookTitle}</TableCell>
                  <TableCell>{item.authorName || 'N/A'}</TableCell>
                  <TableCell>{item.isbn || 'N/A'}</TableCell>
                  <TableCell>{new Date(item.issueDate).toLocaleDateString()}</TableCell>
                  <TableCell>{new Date(item.dueDate).toLocaleDateString()}</TableCell>
                  <TableCell>
                    {daysRemaining > 0 ? (
                      <Chip label={`${daysRemaining} days left`} size="small" color="success" variant="outlined" />
                    ) : daysRemaining === 0 ? (
                      <Chip label="Due Today" size="small" color="warning" />
                    ) : (
                      <Chip label={`${Math.abs(daysRemaining)} days overdue`} size="small" color="error" />
                    )}
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={item.status} 
                      size="small" 
                      color={item.status === 'OVERDUE' ? 'error' : 'primary'} 
                    />
                  </TableCell>
                  <TableCell align="right">
                    <Button 
                      variant="contained" 
                      color="secondary" 
                      size="small"
                      onClick={() => handleReturnBook(item.barcode, item.bookTitle)}
                      sx={{
                        background: 'linear-gradient(90deg, #ec4899 0%, #db2777 100%)',
                        boxShadow: '0 2px 10px 0 rgba(236, 72, 153, 0.3)',
                      }}
                    >
                      Return Book
                    </Button>
                  </TableCell>
                </TableRow>
              );
            })}
            {issuedBooks.length === 0 && !loading && (
              <TableRow>
                <TableCell colSpan={8} align="center">
                  <Typography variant="body1" py={3} color="text.secondary">
                    You have no active checkouts to return.
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
