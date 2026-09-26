import React, { useEffect, useState } from 'react';
import { Card, CardContent, Typography, Button, Box, Grid, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, TablePagination, TextField } from '@mui/material';
import { PictureAsPdf } from '@mui/icons-material';
import api, { API_BASE_URL } from '../../services/api';
import { toast } from 'react-toastify';

export default function Reports() {
  const [logs, setLogs] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  const fetchLogs = async () => {
    try {
      const res = await api.get('/reports/audit-logs', {
        params: {
          query: search,
          page: page,
          size: rowsPerPage
        }
      });
      setLogs(res.data.content);
      setTotalItems(res.data.totalElements);
    } catch (e) {
      toast.error("Failed to load audit logs.");
    }
  };

  useEffect(() => {
    fetchLogs();
  }, [page, rowsPerPage, search]);

  const handleDownloadPdf = (reportType) => {
    window.open(`${API_BASE_URL}/reports/pdf/${reportType}`, '_blank');
    toast.success(`Downloading ${reportType} PDF report...`);
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        System Reports & Audit Logs
      </Typography>

      <Typography variant="h6" fontWeight="bold" mb={2}>
        PDF PDF Export Module
      </Typography>
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', textAlign: 'center', p: 2 }}>
            <CardContent>
              <PictureAsPdf fontSize="large" color="error" sx={{ mb: 1 }} />
              <Typography fontWeight="bold" gutterBottom>Books Report</Typography>
              <Button size="small" variant="contained" onClick={() => handleDownloadPdf('books')}>
                Export PDF
              </Button>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', textAlign: 'center', p: 2 }}>
            <CardContent>
              <PictureAsPdf fontSize="large" color="error" sx={{ mb: 1 }} />
              <Typography fontWeight="bold" gutterBottom>Users Report</Typography>
              <Button size="small" variant="contained" onClick={() => handleDownloadPdf('users')}>
                Export PDF
              </Button>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', textAlign: 'center', p: 2 }}>
            <CardContent>
              <PictureAsPdf fontSize="large" color="error" sx={{ mb: 1 }} />
              <Typography fontWeight="bold" gutterBottom>Borrow Issues</Typography>
              <Button size="small" variant="contained" onClick={() => handleDownloadPdf('issues')}>
                Export PDF
              </Button>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', textAlign: 'center', p: 2 }}>
            <CardContent>
              <PictureAsPdf fontSize="large" color="error" sx={{ mb: 1 }} />
              <Typography fontWeight="bold" gutterBottom>Fines Report</Typography>
              <Button size="small" variant="contained" onClick={() => handleDownloadPdf('fines')}>
                Export PDF
              </Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Typography variant="h6" fontWeight="bold" mb={2}>
        System Audit Logs
      </Typography>

      <Box mb={2}>
        <TextField
          fullWidth
          label="Search logs by action or details..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
        />
      </Box>

      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>User</TableCell>
              <TableCell>Action</TableCell>
              <TableCell>Details</TableCell>
              <TableCell>Timestamp</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {logs.map((log) => (
              <TableRow key={log.id}>
                <TableCell>{log.id}</TableCell>
                <TableCell>{log.username}</TableCell>
                <TableCell>{log.action}</TableCell>
                <TableCell>{log.details}</TableCell>
                <TableCell>{new Date(log.timestamp).toLocaleString()}</TableCell>
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
    </Box>
  );
}
