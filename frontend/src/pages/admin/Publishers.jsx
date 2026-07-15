import React, { useEffect, useState } from 'react';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button,
  TextField, Box, Typography, TablePagination, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions
} from '@mui/material';
import { Edit, Delete, Add } from '@mui/icons-material';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function Publishers() {
  const [publishers, setPublishers] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  // Dialog State
  const [open, setOpen] = useState(false);
  const [editId, setEditId] = useState(null);
  const [name, setName] = useState('');
  const [address, setAddress] = useState('');
  const [phone, setPhone] = useState('');

  const fetchPublishers = async () => {
    try {
      const res = await api.get('/publishers', {
        params: {
          query: search,
          page: page,
          size: rowsPerPage
        }
      });
      setPublishers(res.data.content);
      setTotalItems(res.data.totalElements);
    } catch (e) {
      toast.error("Failed to load publishers.");
    }
  };

  useEffect(() => {
    fetchPublishers();
  }, [page, rowsPerPage, search]);

  const handleOpen = (item = null) => {
    if (item) {
      setEditId(item.id);
      setName(item.name);
      setAddress(item.address || '');
      setPhone(item.phone || '');
    } else {
      setEditId(null);
      setName('');
      setAddress('');
      setPhone('');
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name.trim()) return;

    try {
      const payload = { name, address, phone };
      if (editId) {
        await api.put(`/publishers/${editId}`, payload);
        toast.success("Publisher updated successfully!");
      } else {
        await api.post('/publishers', payload);
        toast.success("Publisher created successfully!");
      }
      fetchPublishers();
      handleClose();
    } catch (err) {
      toast.error(err.response?.data?.message || "Operation failed.");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this publisher?")) return;
    try {
      await api.delete(`/publishers/${id}`);
      toast.success("Publisher deleted successfully!");
      fetchPublishers();
    } catch (e) {
      toast.error("Failed to delete publisher.");
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Publisher Management
        </Typography>
        <Button variant="contained" startIcon={<Add />} onClick={() => handleOpen()}>
          Add Publisher
        </Button>
      </Box>

      <Box mb={2}>
        <TextField
          fullWidth
          variant="outlined"
          label="Search publishers by name..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
        />
      </Box>

      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Address</TableCell>
              <TableCell>Phone</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {publishers.map((p) => (
              <TableRow key={p.id}>
                <TableCell>{p.id}</TableCell>
                <TableCell>{p.name}</TableCell>
                <TableCell>{p.address || '-'}</TableCell>
                <TableCell>{p.phone || '-'}</TableCell>
                <TableCell>
                  <IconButton color="primary" onClick={() => handleOpen(p)}>
                    <Edit />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(p.id)}>
                    <Delete />
                  </IconButton>
                </TableCell>
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

      {/* Save Dialog */}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>{editId ? 'Edit Publisher' : 'Add Publisher'}</DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent sx={{ minWidth: 400 }}>
            <TextField
              fullWidth
              label="Publisher Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Phone Number"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              margin="normal"
            />
            <TextField
              fullWidth
              multiline
              rows={3}
              label="Address"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              margin="normal"
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancel</Button>
            <Button type="submit" variant="contained">Save</Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
}
