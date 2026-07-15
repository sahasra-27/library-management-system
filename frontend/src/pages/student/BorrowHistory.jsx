import React, { useEffect, useState, useContext } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Box, Typography, TablePagination, Chip } from '@mui/material';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

export default function BorrowHistory() {
  const { user } = useContext(AuthContext);
  const [history, setHistory] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [loading, setLoading] = useState(true);

  const fetchHistory = async () => {
    try {
      // 1. Fetch all checkout logs (includes active and returned)
      const issuesRes = await api.get('/issues', {
        params: {
          userId: user.id,
          page: 0,
          size: 1000
        }
      });
      const allIssues = issuesRes.data.content || [];

      // 2. Fetch return logs to match return dates and conditions
      const returnsRes = await api.get('/returns', {
        params: {
          userId: user.id,
          page: 0,
          size: 1000
        }
      });
      const allReturns = returnsRes.data.content || [];

      // Merge: match issue with its return log to populate return details
      const merged = allIssues.map(issue => {
        const matchingReturn = allReturns.find(
          ret => ret.barcode === issue.barcode && 
          new Date(ret.issueDate).getTime() === new Date(issue.issueDate).getTime()
        );
        return {
          ...issue,
          returnDate: matchingReturn ? matchingReturn.returnDate : null,
          bookCondition: matchingReturn ? matchingReturn.bookCondition : '-'
        };
      });

      // Sort by issueDate descending
      merged.sort((a, b) => new Date(b.issueDate) - new Date(a.issueDate));

      setHistory(merged);
      setTotalItems(merged.length);
    } catch (e) {
      toast.error("Failed to load borrowing history.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user) {
      fetchHistory();
    }
  }, [user]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  // Get paginated slice
  const paginatedHistory = history.slice(page * rowsPerPage, (page + 1) * rowsPerPage);

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        My Borrowing History
      </Typography>

      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Title</TableCell>
              <TableCell>Barcode</TableCell>
              <TableCell>Issue Date</TableCell>
              <TableCell>Due Date</TableCell>
              <TableCell>Return Date</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Condition</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {paginatedHistory.map((r) => (
              <TableRow key={r.id}>
                <TableCell>{r.bookTitle}</TableCell>
                <TableCell>{r.barcode}</TableCell>
                <TableCell>{new Date(r.issueDate).toLocaleDateString()}</TableCell>
                <TableCell>{new Date(r.dueDate).toLocaleDateString()}</TableCell>
                <TableCell>
                  {r.returnDate ? new Date(r.returnDate).toLocaleDateString() : '-'}
                </TableCell>
                <TableCell>
                  {r.returnDate ? (
                    <Chip label="RETURNED" size="small" color="success" variant="outlined" />
                  ) : r.status === 'OVERDUE' ? (
                    <Chip label="OVERDUE" size="small" color="error" />
                  ) : (
                    <Chip label="ACTIVE" size="small" color="primary" />
                  )}
                </TableCell>
                <TableCell>{r.bookCondition}</TableCell>
              </TableRow>
            ))}
            {history.length === 0 && !loading && (
              <TableRow>
                <TableCell colSpan={7} align="center">
                  <Typography variant="body1" py={2} color="text.secondary">
                    You have no borrowing history.
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
