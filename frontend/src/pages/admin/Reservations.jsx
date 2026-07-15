import React, { useEffect, useState } from 'react';
import { 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, 
  TextField, Box, Typography, TablePagination, Chip, Button, Dialog, 
  DialogTitle, DialogContent, DialogActions, FormControl, InputLabel, 
  Select, MenuItem, Autocomplete, IconButton 
} from '@mui/material';
import { Delete, Edit, Cancel, Add, CheckCircle, Bookmark } from '@mui/icons-material';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function Reservations() {
  const [reservations, setReservations] = useState([]);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  // Users and books for dropdowns
  const [students, setStudents] = useState([]);
  const [books, setBooks] = useState([]);

  // Dialog States
  const [openAdd, setOpenAdd] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);
  const [selectedRes, setSelectedRes] = useState(null);

  // Form States
  const [formStudent, setFormStudent] = useState(null);
  const [formBook, setFormBook] = useState(null);
  const [formExpiryDate, setFormExpiryDate] = useState('');
  const [formStatus, setFormStatus] = useState('PENDING');

  const fetchReservations = async () => {
    try {
      const res = await api.get('/reservations', {
        params: {
          status: statusFilter,
          query: search,
          page: page,
          size: rowsPerPage
        }
      });
      setReservations(res.data.content);
      setTotalItems(res.data.totalElements);
    } catch (e) {
      toast.error("Failed to load reservations list.");
    }
  };

  const fetchStudentsAndBooks = async () => {
    try {
      const [usersRes, booksRes] = await Promise.all([
        api.get('/users', { params: { page: 0, size: 1000 } }),
        api.get('/books', { params: { page: 0, size: 1000 } })
      ]);
      // Filter out only students for the dropdown
      const studentUsers = usersRes.data.content.filter(u => u.role === 'STUDENT' || u.role === 'student' || u.role === 'USER' || u.role === 'user');
      setStudents(studentUsers);
      setBooks(booksRes.data.content);
    } catch (e) {
      console.error("Failed to load dropdown sources", e);
    }
  };

  useEffect(() => {
    fetchReservations();
  }, [page, rowsPerPage, search, statusFilter]);

  useEffect(() => {
    fetchStudentsAndBooks();
  }, []);

  const handleOpenAdd = () => {
    setFormStudent(null);
    setFormBook(null);
    setFormExpiryDate(new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]); // Default 3 days expiry
    setFormStatus('PENDING');
    setOpenAdd(true);
  };

  const handleCreateReservation = async () => {
    if (!formStudent || !formBook) {
      toast.error("Please select both a student and a book.");
      return;
    }
    try {
      await api.post('/reservations', {
        userId: formStudent.id,
        bookId: formBook.id,
        status: formStatus,
        expiryDate: formExpiryDate ? formExpiryDate + "T23:59:59" : null
      });
      toast.success("Reservation created successfully!");
      setOpenAdd(false);
      fetchReservations();
    } catch (e) {
      toast.error(e.response?.data?.message || "Failed to create reservation.");
    }
  };

  const handleOpenEdit = (res) => {
    setSelectedRes(res);
    setFormExpiryDate(res.expiryDate ? res.expiryDate.split('T')[0] : '');
    setFormStatus(res.status);
    setOpenEdit(true);
  };

  const handleUpdateReservation = async () => {
    try {
      await api.put(`/reservations/${selectedRes.id}`, {
        status: formStatus,
        expiryDate: formExpiryDate ? formExpiryDate + "T23:59:59" : null
      });
      toast.success("Reservation updated successfully!");
      setOpenEdit(false);
      fetchReservations();
    } catch (e) {
      toast.error(e.response?.data?.message || "Failed to update reservation.");
    }
  };

  const handleQuickUpdateStatus = async (id, status) => {
    try {
      await api.put(`/reservations/${id}`, {
        status: status
      });
      toast.success(`Reservation status updated to ${status}!`);
      fetchReservations();
    } catch (e) {
      toast.error("Failed to update status.");
    }
  };

  const handleQuickReadyForPickup = async (id) => {
    try {
      const expiry = new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString().split('T')[0] + "T23:59:59";
      await api.put(`/reservations/${id}`, {
        status: 'READY_FOR_PICKUP',
        expiryDate: expiry
      });
      toast.success("Reservation is now Ready for Pickup!");
      fetchReservations();
    } catch (e) {
      toast.error("Failed to update status.");
    }
  };

  const handleCancelReservation = async (id) => {
    if (!window.confirm("Are you sure you want to cancel this reservation?")) return;
    try {
      await api.put(`/reservations/${id}`, {
        status: 'CANCELLED'
      });
      toast.success("Reservation cancelled successfully.");
      fetchReservations();
    } catch (e) {
      toast.error("Failed to cancel reservation.");
    }
  };

  const handleDeleteReservation = async (id) => {
    if (!window.confirm("Confirm deletion of this reservation? This cannot be undone.")) return;
    try {
      await api.delete(`/reservations/${id}`);
      toast.success("Reservation deleted successfully.");
      fetchReservations();
    } catch (e) {
      toast.error("Failed to delete reservation.");
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'warning';
      case 'APPROVED': return 'info';
      case 'READY_FOR_PICKUP': return 'success';
      case 'COMPLETED': return 'primary';
      case 'CANCELLED': return 'error';
      default: return 'default';
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Book Reservations Queue
        </Typography>
        <Button 
          variant="contained" 
          startIcon={<Add />} 
          onClick={handleOpenAdd}
          sx={{
            background: 'linear-gradient(90deg, #6366f1 0%, #4f46e5 100%)',
            boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.4)',
          }}
        >
          Add Reservation
        </Button>
      </Box>

      <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
        <TextField
          select
          label="Status Filter"
          value={statusFilter}
          onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}
          sx={{ minWidth: 180 }}
        >
          <MenuItem value="ALL">All Statuses</MenuItem>
          <MenuItem value="PENDING">Pending</MenuItem>
          <MenuItem value="APPROVED">Approved</MenuItem>
          <MenuItem value="READY_FOR_PICKUP">Ready for Pickup</MenuItem>
          <MenuItem value="CANCELLED">Cancelled</MenuItem>
          <MenuItem value="COMPLETED">Completed</MenuItem>
        </TextField>

        <TextField
          fullWidth
          variant="outlined"
          label="Search by student name, user ID, book title, or ISBN..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
        />
      </Box>

      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)', backdropFilter: 'blur(8px)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Res ID</TableCell>
              <TableCell>User Name</TableCell>
              <TableCell>User ID</TableCell>
              <TableCell>Book Title</TableCell>
              <TableCell>ISBN</TableCell>
              <TableCell>Reservation Date</TableCell>
              <TableCell>Expiry Date</TableCell>
              <TableCell>Queue Pos</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {reservations.map((r) => (
              <TableRow key={r.id}>
                <TableCell>{r.id}</TableCell>
                <TableCell>{r.username}</TableCell>
                <TableCell>{r.userId}</TableCell>
                <TableCell>{r.bookTitle}</TableCell>
                <TableCell>{r.isbn}</TableCell>
                <TableCell>{new Date(r.reservationDate).toLocaleDateString()}</TableCell>
                <TableCell>{r.expiryDate ? new Date(r.expiryDate).toLocaleDateString() : 'N/A'}</TableCell>
                <TableCell>{r.queuePosition !== null ? r.queuePosition : '-'}</TableCell>
                <TableCell>
                  <Chip
                    label={r.status.replace(/_/g, ' ')}
                    color={getStatusColor(r.status)}
                    size="small"
                  />
                </TableCell>
                <TableCell align="right">
                  <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 0.5 }}>
                    {r.status === 'PENDING' && (
                      <IconButton size="small" color="success" title="Approve Reservation" onClick={() => handleQuickUpdateStatus(r.id, 'APPROVED')}>
                        <CheckCircle />
                      </IconButton>
                    )}
                    {(r.status === 'PENDING' || r.status === 'APPROVED') && (
                      <IconButton size="small" color="info" title="Mark Ready for Pickup" onClick={() => handleQuickReadyForPickup(r.id)}>
                        <Bookmark />
                      </IconButton>
                    )}
                    <IconButton size="small" color="primary" title="Edit" onClick={() => handleOpenEdit(r)}>
                      <Edit />
                    </IconButton>
                    {(r.status === 'PENDING' || r.status === 'APPROVED' || r.status === 'READY_FOR_PICKUP') && (
                      <IconButton size="small" color="warning" title="Cancel" onClick={() => handleCancelReservation(r.id)}>
                        <Cancel />
                      </IconButton>
                    )}
                    <IconButton size="small" color="error" title="Delete" onClick={() => handleDeleteReservation(r.id)}>
                      <Delete />
                    </IconButton>
                  </Box>
                </TableCell>
              </TableRow>
            ))}
            {reservations.length === 0 && (
              <TableRow>
                <TableCell colSpan={10} align="center">
                  <Typography variant="body1" py={3} color="text.secondary">
                    No reservations found matching the search criteria.
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

      {/* CREATE DIALOG */}
      <Dialog 
        open={openAdd} 
        onClose={() => setOpenAdd(false)}
        slotProps={{
          paper: {
            sx: { bgcolor: '#0f172a', color: 'white', minWidth: 450 }
          }
        }}
      >
        <DialogTitle fontWeight="bold">Create New Book Reservation</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <Autocomplete
              options={students}
              getOptionLabel={(option) => `${option.username} (ID: ${option.id})`}
              value={formStudent}
              onChange={(event, newValue) => setFormStudent(newValue)}
              renderInput={(params) => (
                <TextField 
                  {...params} 
                  label="Select User" 
                  variant="outlined"
                  sx={{ input: { color: 'white' } }}
                />
              )}
            />

            <Autocomplete
              options={books}
              getOptionLabel={(option) => `${option.title} (ISBN: ${option.isbn})`}
              value={formBook}
              onChange={(event, newValue) => setFormBook(newValue)}
              renderInput={(params) => (
                <TextField 
                  {...params} 
                  label="Select Book" 
                  variant="outlined"
                  sx={{ input: { color: 'white' } }}
                />
              )}
            />

            <TextField
              type="date"
              label="Expiry Date"
              value={formExpiryDate}
              onChange={(e) => setFormExpiryDate(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
              fullWidth
            />

            <FormControl fullWidth>
              <InputLabel>Status</InputLabel>
              <Select
                value={formStatus}
                label="Status"
                onChange={(e) => setFormStatus(e.target.value)}
              >
                <MenuItem value="PENDING">Pending</MenuItem>
                <MenuItem value="APPROVED">Approved</MenuItem>
                <MenuItem value="READY_FOR_PICKUP">Ready for Pickup</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={() => setOpenAdd(false)} color="inherit">Cancel</Button>
          <Button onClick={handleCreateReservation} variant="contained" color="primary">Create</Button>
        </DialogActions>
      </Dialog>

      {/* EDIT DIALOG */}
      <Dialog 
        open={openEdit} 
        onClose={() => setOpenEdit(false)}
        slotProps={{
          paper: {
            sx: { bgcolor: '#0f172a', color: 'white', minWidth: 400 }
          }
        }}
      >
        <DialogTitle fontWeight="bold">Edit Reservation ID: {selectedRes?.id}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <Typography variant="body2" color="text.secondary">
              Book: {selectedRes?.bookTitle} <br />
              Student: {selectedRes?.username}
            </Typography>

            <TextField
              type="date"
              label="Expiry Date"
              value={formExpiryDate}
              onChange={(e) => setFormExpiryDate(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
              fullWidth
            />

            <FormControl fullWidth>
              <InputLabel>Status</InputLabel>
              <Select
                value={formStatus}
                label="Status"
                onChange={(e) => setFormStatus(e.target.value)}
              >
                <MenuItem value="PENDING">Pending</MenuItem>
                <MenuItem value="APPROVED">Approved</MenuItem>
                <MenuItem value="READY_FOR_PICKUP">Ready for Pickup</MenuItem>
                <MenuItem value="CANCELLED">Cancelled</MenuItem>
                <MenuItem value="COMPLETED">Completed</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={() => setOpenEdit(false)} color="inherit">Cancel</Button>
          <Button onClick={handleUpdateReservation} variant="contained" color="primary">Save Changes</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
