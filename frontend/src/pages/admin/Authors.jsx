import React, { useEffect, useState } from 'react';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button,
  TextField, Box, Typography, TablePagination, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions
} from '@mui/material';
import { Edit, Delete, Add } from '@mui/icons-material';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function Authors() {
  const [authors, setAuthors] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  // Dialog State
  const [open, setOpen] = useState(false);
  const [editId, setEditId] = useState(null);
  const [name, setName] = useState('');
  const [biography, setBiography] = useState('');

  const fetchAuthors = async () => {
    try {
      const res = await api.get('/authors', {
        params: {
          query: search,
          page: page,
          size: rowsPerPage
        }
      });
      setAuthors(res.data.content);
      setTotalItems(res.data.totalElements);
    } catch (e) {
      toast.error("Failed to load authors.");
    }
  };

  useEffect(() => {
    fetchAuthors();
  }, [page, rowsPerPage, search]);

  const handleOpen = (item = null) => {
    if (item) {
      setEditId(item.id);
      setName(item.name);
      setBiography(item.biography || '');
    } else {
      setEditId(null);
      setName('');
      setBiography('');
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
      const payload = { name, biography };
      if (editId) {
        await api.put(`/authors/${editId}`, payload);
        toast.success("Author updated successfully!");
      } else {
        await api.post('/authors', payload);
        toast.success("Author created successfully!");
      }
      fetchAuthors();
      handleClose();
    } catch (err) {
      toast.error(err.response?.data?.message || "Operation failed.");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this author?")) return;
    try {
      await api.delete(`/authors/${id}`);
      toast.success("Author deleted successfully!");
      fetchAuthors();
    } catch (e) {
      toast.error("Failed to delete author.");
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Author Management
        </Typography>
        <Button variant="contained" startIcon={<Add />} onClick={() => handleOpen()}>
          Add Author
        </Button>
      </Box>

      <Box mb={2}>
        <TextField
          fullWidth
          variant="outlined"
          label="Search authors by name..."
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
              <TableCell>Biography</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {authors.map((a) => (
              <TableRow key={a.id}>
                <TableCell>{a.id}</TableCell>
                <TableCell>{a.name}</TableCell>
                <TableCell>{a.biography || '-'}</TableCell>
                <TableCell>
                  <IconButton color="primary" onClick={() => handleOpen(a)}>
                    <Edit />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(a.id)}>
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
        <DialogTitle>{editId ? 'Edit Author' : 'Add Author'}</DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent sx={{ minWidth: 400 }}>
            <TextField
              fullWidth
              label="Author Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              multiline
              rows={4}
              label="Biography"
              value={biography}
              onChange={(e) => setBiography(e.target.value)}
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
