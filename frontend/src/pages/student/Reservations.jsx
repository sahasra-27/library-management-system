import React, { useEffect, useState, useContext } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, Box, Typography, Chip } from '@mui/material';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

export default function StudentReservations() {
  const { user } = useContext(AuthContext);
  const [reservations, setReservations] = useState([]);

  const fetchReservations = async () => {
    try {
      const res = await api.get('/reservations', {
        params: {
          userId: user.id,
          page: 0,
          size: 50
        }
      });
      setReservations(res.data.content);
    } catch (e) {
      toast.error("Failed to load reservations queue.");
    }
  };

  useEffect(() => {
    if (user) fetchReservations();
  }, [user]);

  const handleCancel = async (id) => {
    if (!window.confirm("Are you sure you want to cancel this reservation?")) return;
    try {
      await api.delete(`/issues/reserve/${id}`, {
        params: { userId: user.id }
      });
      toast.success("Reservation cancelled successfully.");
      fetchReservations();
    } catch (e) {
      toast.error("Failed to cancel reservation.");
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
      <Typography variant="h4" fontWeight="bold" mb={3}>
        My Book Reservations
      </Typography>

      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Title</TableCell>
              <TableCell>ISBN</TableCell>
              <TableCell>Reservation Date</TableCell>
              <TableCell>Queue Position</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {reservations.map((r) => (
              <TableRow key={r.id}>
                <TableCell>{r.bookTitle}</TableCell>
                <TableCell>{r.isbn}</TableCell>
                <TableCell>{new Date(r.reservationDate).toLocaleDateString()}</TableCell>
                <TableCell>{r.queuePosition !== null ? r.queuePosition : '-'}</TableCell>
                <TableCell>
                  <Chip
                    label={r.status.replace(/_/g, ' ')}
                    color={getStatusColor(r.status)}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  {r.status === 'PENDING' && (
                    <Button variant="outlined" color="error" size="small" onClick={() => handleCancel(r.id)}>
                      Cancel
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
            {reservations.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  <Typography variant="body1" py={2} color="text.secondary">
                    You have no active book reservations.
                  </Typography>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
