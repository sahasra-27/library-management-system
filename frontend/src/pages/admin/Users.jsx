import React, { useEffect, useState } from 'react';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button,
  TextField, Box, Typography, TablePagination, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions, MenuItem, Chip
} from '@mui/material';
import { Edit, Delete, ToggleOn, ToggleOff, Add } from '@mui/icons-material';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  // Dialog Add/Edit State
  const [open, setOpen] = useState(false);
  const [editId, setEditId] = useState(null);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('USER');
  const [occupation, setOccupation] = useState('');

  const fetchUsers = async () => {
    try {
      const res = await api.get('/users', {
        params: {
          query: search,
          page: page,
          size: rowsPerPage
        }
      });
      setUsers(res.data.content);
      setTotalItems(res.data.totalElements);
    } catch (e) {
      toast.error("Failed to load users list.");
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [page, rowsPerPage, search]);

  const handleOpen = (item = null) => {
    if (item) {
      setEditId(item.id);
      setUsername(item.username);
      setEmail(item.email);
      setPhone(item.phone || '');
      setPassword(''); // keep blank on edit
      setRole(item.role || 'USER');
      setOccupation(item.occupation || '');
    } else {
      setEditId(null);
      setUsername('');
      setEmail('');
      setPhone('');
      setPassword('');
      setRole('USER');
      setOccupation('');
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username.trim() || !email.trim()) return;

    try {
      const payload = {
        username,
        email,
        phone,
        password: password || undefined,
        role,
        occupation: occupation || 'Other',
      };

      if (editId) {
        await api.put(`/users/${editId}`, payload);
        toast.success("User updated successfully!");
      } else {
        if (!password) {
          toast.warning("Password is required for new users.");
          return;
        }
        await api.post('/users', payload);
        toast.success("User created successfully!");
      }
      fetchUsers();
      handleClose();
    } catch (err) {
      toast.error(err.response?.data?.message || "Operation failed.");
    }
  };

  const toggleStatus = async (user) => {
    const nextStatus = user.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
    try {
      await api.patch(`/users/${user.id}/status`, null, {
        params: { status: nextStatus }
      });
      toast.success(`User status changed to ${nextStatus}!`);
      fetchUsers();
    } catch (e) {
      toast.error("Failed to update user status.");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to permanently delete this user?")) return;
    try {
      await api.delete(`/users/${id}`);
      toast.success("User deleted successfully!");
      fetchUsers();
    } catch (e) {
      toast.error("Failed to delete user.");
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          User Management
        </Typography>
        <Button variant="contained" startIcon={<Add />} onClick={() => handleOpen()}>
          Add User
        </Button>
      </Box>

      <Box mb={2}>
        <TextField
          fullWidth
          variant="outlined"
          label="Search users by name, email or phone..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
        />
      </Box>

      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Username</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Phone</TableCell>
              <TableCell>Occupation</TableCell>
              <TableCell>Roles</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((u) => (
              <TableRow key={u.id}>
                <TableCell>{u.id}</TableCell>
                <TableCell>{u.username}</TableCell>
                <TableCell>{u.email}</TableCell>
                <TableCell>{u.phone || '-'}</TableCell>
                <TableCell>{u.occupation || '-'}</TableCell>
                <TableCell>{u.role === 'STUDENT' || u.role === 'USER' ? 'User' : u.role}</TableCell>
                <TableCell>
                  <Chip
                    label={u.status}
                    color={u.status === 'ACTIVE' ? 'success' : 'error'}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  <IconButton color="primary" onClick={() => handleOpen(u)}>
                    <Edit />
                  </IconButton>
                  <IconButton color="warning" onClick={() => toggleStatus(u)}>
                    {u.status === 'ACTIVE' ? <ToggleOn /> : <ToggleOff />}
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(u.id)}>
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
        <DialogTitle>{editId ? 'Edit User' : 'Add User'}</DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent sx={{ minWidth: 400 }}>
            <TextField
              fullWidth
              label="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              margin="dense"
              required
            />
            <TextField
              fullWidth
              label="Email Address"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              margin="dense"
              required
            />
            <TextField
              fullWidth
              label="Phone Number"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              margin="dense"
            />
            {!editId && (
              <TextField
                fullWidth
                label="Password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                margin="dense"
                required
              />
            )}
            <TextField
              fullWidth
              label="Occupation"
              value={occupation}
              onChange={(e) => setOccupation(e.target.value)}
              margin="dense"
            />
            <TextField
              fullWidth
              select
              label="Role"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              margin="dense"
            >
              <MenuItem value="USER">User</MenuItem>
              <MenuItem value="ADMIN">Admin</MenuItem>
            </TextField>
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
