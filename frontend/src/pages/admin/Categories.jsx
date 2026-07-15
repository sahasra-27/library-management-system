import React, { useEffect, useState } from 'react';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button,
  TextField, Box, Typography, TablePagination, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions
} from '@mui/material';
import { Edit, Delete, Add } from '@mui/icons-material';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function Categories() {
  const [categories, setCategories] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  // Modal State
  const [open, setOpen] = useState(false);
  const [editId, setEditId] = useState(null);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');

  const fetchCategories = async () => {
    try {
      const res = await api.get('/categories', {
        params: {
          query: search,
          page: page,
          size: rowsPerPage
        }
      });
      setCategories(res.data.content);
      setTotalItems(res.data.totalElements);
    } catch (e) {
      toast.error("Failed to load categories.");
    }
  };

  useEffect(() => {
    fetchCategories();
  }, [page, rowsPerPage, search]);

  const handleOpen = (item = null) => {
    if (item) {
      setEditId(item.id);
      setName(item.name);
      setDescription(item.description || '');
    } else {
      setEditId(null);
      setName('');
      setDescription('');
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
      const payload = { name, description };
      if (editId) {
        await api.put(`/categories/${editId}`, payload);
        toast.success("Category updated successfully!");
      } else {
        await api.post('/categories', payload);
        toast.success("Category created successfully!");
      }
      fetchCategories();
      handleClose();
    } catch (err) {
      toast.error(err.response?.data?.message || "Operation failed.");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this category?")) return;
    try {
      await api.delete(`/categories/${id}`);
      toast.success("Category deleted successfully!");
      fetchCategories();
    } catch (e) {
      toast.error("Failed to delete category.");
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Category Management
        </Typography>
        <Button variant="contained" startIcon={<Add />} onClick={() => handleOpen()}>
          Add Category
        </Button>
      </Box>

      <Box mb={2}>
        <TextField
          fullWidth
          variant="outlined"
          label="Search category by name..."
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
              <TableCell>Description</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {categories.map((c) => (
              <TableRow key={c.id}>
                <TableCell>{c.id}</TableCell>
                <TableCell>{c.name}</TableCell>
                <TableCell>{c.description || '-'}</TableCell>
                <TableCell>
                  <IconButton color="primary" onClick={() => handleOpen(c)}>
                    <Edit />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(c.id)}>
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

      {/* Save Modal */}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>{editId ? 'Edit Category' : 'Add Category'}</DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent sx={{ minWidth: 400 }}>
            <TextField
              fullWidth
              label="Category Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              multiline
              rows={3}
              label="Description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
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
