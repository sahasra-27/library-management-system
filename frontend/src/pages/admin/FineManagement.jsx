import React, { useEffect, useState } from 'react';
import { 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, 
  Button, TextField, Box, Typography, TablePagination, Chip, MenuItem, 
  Dialog, DialogTitle, DialogContent, DialogActions, FormControl, 
  InputLabel, Select, Autocomplete, Grid, Card, CardContent, IconButton 
} from '@mui/material';
import { Delete, Edit, Payment, Add, AccountBalanceWallet, CheckCircle, SettingsBackupRestore } from '@mui/icons-material';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function FineManagement() {
  const [fines, setFines] = useState([]);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  // Stats
  const [stats, setStats] = useState({ totalUnpaid: 0, totalCollected: 0 });

  // Dropdown options
  const [students, setStudents] = useState([]);
  const [studentIssues, setStudentIssues] = useState([]);

  // Modals
  const [openAdd, setOpenAdd] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);
  const [selectedFine, setSelectedFine] = useState(null);

  // Form States
  const [formStudent, setFormStudent] = useState(null);
  const [formIssue, setFormIssue] = useState(null);
  const [formAmount, setFormAmount] = useState('');
  const [formReason, setFormReason] = useState('');
  const [formDueDate, setFormDueDate] = useState('');
  const [formStatus, setFormStatus] = useState('UNPAID');

  const fetchFines = async () => {
    try {
      const res = await api.get('/fines', {
        params: {
          status: statusFilter,
          query: search,
          page: page,
          size: rowsPerPage
        }
      });
      setFines(res.data.content);
      setTotalItems(res.data.totalElements);
      fetchStats();
    } catch (e) {
      toast.error("Failed to load fines data.");
    }
  };

  const fetchStats = async () => {
    try {
      const res = await api.get('/fines/stats');
      setStats(res.data);
    } catch (e) {
      console.error("Failed to fetch fine stats", e);
    }
  };

  const fetchStudents = async () => {
    try {
      const res = await api.get('/users', { params: { page: 0, size: 1000 } });
      const studentUsers = res.data.content.filter(u => u.role === 'STUDENT' || u.role === 'student' || u.role === 'USER' || u.role === 'user');
      setStudents(studentUsers);
    } catch (e) {
      console.error("Failed to load students list", e);
    }
  };

  const fetchStudentIssues = async (studentId) => {
    if (!studentId) {
      setStudentIssues([]);
      return;
    }
    try {
      const res = await api.get('/issues', { params: { userId: studentId, page: 0, size: 100 } });
      setStudentIssues(res.data.content);
    } catch (e) {
      console.error("Failed to load user checkouts", e);
    }
  };

  useEffect(() => {
    fetchFines();
  }, [page, rowsPerPage, search, statusFilter]);

  useEffect(() => {
    fetchStudents();
  }, []);

  useEffect(() => {
    if (formStudent) {
      fetchStudentIssues(formStudent.id);
    } else {
      setFormIssue(null);
      setStudentIssues([]);
    }
  }, [formStudent]);

  const handlePayFine = async (id) => {
    if (!window.confirm("Confirm payment of this fine?")) return;
    try {
      await api.post(`/fines/${id}/pay`);
      toast.success("Fine marked as PAID successfully!");
      fetchFines();
    } catch (e) {
      toast.error("Failed to pay fine.");
    }
  };

  const handleUnpayFine = async (id) => {
    if (!window.confirm("Confirm marking this fine as UNPAID?")) return;
    try {
      await api.post(`/fines/${id}/unpay`);
      toast.success("Fine marked as UNPAID successfully!");
      fetchFines();
    } catch (e) {
      toast.error("Failed to mark fine as unpaid.");
    }
  };

  const handleDeleteFine = async (id) => {
    if (!window.confirm("Are you sure you want to delete this fine record?")) return;
    try {
      await api.delete(`/fines/${id}`);
      toast.success("Fine record deleted successfully.");
      fetchFines();
    } catch (e) {
      toast.error("Failed to delete fine.");
    }
  };

  const handleOpenAdd = () => {
    setFormStudent(null);
    setFormIssue(null);
    setFormAmount('');
    setFormReason('');
    setFormDueDate(new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]); // Default 7 days due date
    setFormStatus('UNPAID');
    setOpenAdd(true);
  };

  const handleCreateFine = async () => {
    if (!formStudent) {
      toast.error("Please select a student.");
      return;
    }
    if (!formAmount || parseFloat(formAmount) < 0) {
      toast.error("Please enter a valid non-negative amount.");
      return;
    }
    try {
      await api.post('/fines', {
        userId: formStudent.id,
        issueId: formIssue ? formIssue.id : null,
        amount: parseFloat(formAmount),
        reason: formReason,
        dueDate: formDueDate ? formDueDate + "T23:59:59" : null,
        status: formStatus
      });
      toast.success("Fine added successfully!");
      setOpenAdd(false);
      fetchFines();
    } catch (e) {
      toast.error(e.response?.data?.message || "Failed to create fine.");
    }
  };

  const handleOpenEdit = (fine) => {
    setSelectedFine(fine);
    setFormAmount(fine.amount);
    setFormReason(fine.reason || '');
    setFormDueDate(fine.dueDate ? fine.dueDate.split('T')[0] : '');
    setFormStatus(fine.status);
    setOpenEdit(true);
  };

  const handleUpdateFine = async () => {
    if (!formAmount || parseFloat(formAmount) < 0) {
      toast.error("Please enter a valid non-negative amount.");
      return;
    }
    try {
      await api.put(`/fines/${selectedFine.id}`, {
        amount: parseFloat(formAmount),
        reason: formReason,
        dueDate: formDueDate ? formDueDate + "T23:59:59" : null,
        status: formStatus
      });
      toast.success("Fine details updated successfully!");
      setOpenEdit(false);
      fetchFines();
    } catch (e) {
      toast.error(e.response?.data?.message || "Failed to update fine details.");
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Fine Management
        </Typography>
        <Button 
          variant="contained" 
          startIcon={<Add />} 
          onClick={handleOpenAdd}
          sx={{
            background: 'linear-gradient(90deg, #10b981 0%, #059669 100%)',
            boxShadow: '0 4px 14px 0 rgba(16, 185, 129, 0.4)',
          }}
        >
          Add Fine
        </Button>
      </Box>

      {/* Stats Cards */}
      <Grid container spacing={3} mb={4}>
        <Grid xs={12} sm={6}>
          <Card sx={{ background: 'linear-gradient(135deg, rgba(239, 68, 68, 0.2) 0%, rgba(220, 38, 38, 0.05) 100%)', backdropFilter: 'blur(8px)', border: '1px solid rgba(239, 68, 68, 0.3)' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <AccountBalanceWallet sx={{ color: '#ef4444', fontSize: 40 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary" fontWeight="bold">
                    Total Outstanding Unpaid Fines
                  </Typography>
                  <Typography variant="h4" color="#ef4444" fontWeight="bold">
                    ₹{stats.totalUnpaid ? parseFloat(stats.totalUnpaid).toFixed(2) : '0.00'}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid xs={12} sm={6}>
          <Card sx={{ background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.2) 0%, rgba(5, 150, 105, 0.05) 100%)', backdropFilter: 'blur(8px)', border: '1px solid rgba(16, 185, 129, 0.3)' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <CheckCircle sx={{ color: '#10b981', fontSize: 40 }} />
                <Box>
                  <Typography variant="body2" color="text.secondary" fontWeight="bold">
                    Total Collected Fines
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

      {/* Filters and Search */}
      <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
        <TextField
          select
          label="Status"
          value={statusFilter}
          onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}
          sx={{ minWidth: 150 }}
        >
          <MenuItem value="ALL">All Fines</MenuItem>
          <MenuItem value="UNPAID">Unpaid</MenuItem>
          <MenuItem value="PAID">Paid</MenuItem>
          <MenuItem value="OVERDUE">Overdue</MenuItem>
        </TextField>

        <TextField
          fullWidth
          variant="outlined"
          label="Search by student username, user ID, book title, or barcode..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
        />
      </Box>

      {/* Fines Table */}
      <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)', backdropFilter: 'blur(8px)' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Fine ID</TableCell>
              <TableCell>Student Name</TableCell>
              <TableCell>Book Title</TableCell>
              <TableCell>Barcode</TableCell>
              <TableCell>Issue Date</TableCell>
              <TableCell>Return Date</TableCell>
              <TableCell>Due Date</TableCell>
              <TableCell>Days Late</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell>Reason</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Payment Date</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {fines.map((f) => (
              <TableRow key={f.id}>
                <TableCell>{f.id}</TableCell>
                <TableCell>{f.username}</TableCell>
                <TableCell>{f.bookTitle}</TableCell>
                <TableCell>{f.barcode}</TableCell>
                <TableCell>{f.issueDate ? new Date(f.issueDate).toLocaleDateString() : 'N/A'}</TableCell>
                <TableCell>{f.returnDate ? new Date(f.returnDate).toLocaleDateString() : '-'}</TableCell>
                <TableCell>{f.dueDate ? new Date(f.dueDate).toLocaleDateString() : 'N/A'}</TableCell>
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
                  <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 0.5 }}>
                    {f.status !== 'PAID' ? (
                      <IconButton size="small" color="success" title="Mark as Paid" onClick={() => handlePayFine(f.id)}>
                        <Payment />
                      </IconButton>
                    ) : (
                      <IconButton size="small" color="warning" title="Mark as Unpaid" onClick={() => handleUnpayFine(f.id)}>
                        <SettingsBackupRestore />
                      </IconButton>
                    )}
                    <IconButton size="small" color="primary" title="Edit Fine" onClick={() => handleOpenEdit(f)}>
                      <Edit />
                    </IconButton>
                    <IconButton size="small" color="error" title="Delete Record" onClick={() => handleDeleteFine(f.id)}>
                      <Delete />
                    </IconButton>
                  </Box>
                </TableCell>
              </TableRow>
            ))}
            {fines.length === 0 && (
              <TableRow>
                <TableCell colSpan={13} align="center">
                  <Typography variant="body1" py={3} color="text.secondary">
                    No fines found matching search criteria.
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

      {/* ADD DIALOG */}
      <Dialog 
        open={openAdd} 
        onClose={() => setOpenAdd(false)}
        slotProps={{
          paper: {
            sx: { bgcolor: '#0f172a', color: 'white', minWidth: 450 }
          }
        }}
      >
        <DialogTitle fontWeight="bold">Issue Manual Fine</DialogTitle>
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
              options={studentIssues}
              getOptionLabel={(option) => `${option.bookTitle} (Barcode: ${option.barcode || 'N/A'})`}
              value={formIssue}
              onChange={(event, newValue) => setFormIssue(newValue)}
              disabled={!formStudent}
              renderInput={(params) => (
                <TextField 
                  {...params} 
                  label={formStudent ? "Link to Checkout (Optional)" : "Select User First"} 
                  variant="outlined"
                  sx={{ input: { color: 'white' } }}
                />
              )}
            />

            <TextField
              label="Fine Amount (₹)"
              type="number"
              value={formAmount}
              onChange={(e) => setFormAmount(e.target.value)}
              fullWidth
              slotProps={{ htmlInput: { min: 0, step: "0.01" } }}
            />

            <TextField
              label="Reason for Fine"
              value={formReason}
              onChange={(e) => setFormReason(e.target.value)}
              fullWidth
              multiline
              rows={2}
            />

            <TextField
              type="date"
              label="Due Date"
              value={formDueDate}
              onChange={(e) => setFormDueDate(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
              fullWidth
            />

            <FormControl fullWidth>
              <InputLabel>Initial Status</InputLabel>
              <Select
                value={formStatus}
                label="Initial Status"
                onChange={(e) => setFormStatus(e.target.value)}
              >
                <MenuItem value="UNPAID">Unpaid</MenuItem>
                <MenuItem value="PAID">Paid</MenuItem>
                <MenuItem value="OVERDUE">Overdue</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={() => setOpenAdd(false)} color="inherit">Cancel</Button>
          <Button onClick={handleCreateFine} variant="contained" color="success">Add Fine</Button>
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
        <DialogTitle fontWeight="bold">Modify Fine Record ID: {selectedFine?.id}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <Typography variant="body2" color="text.secondary">
              Student: {selectedFine?.username} <br />
              Linked Item: {selectedFine?.bookTitle}
            </Typography>

            <TextField
              label="Fine Amount (₹)"
              type="number"
              value={formAmount}
              onChange={(e) => setFormAmount(e.target.value)}
              fullWidth
              slotProps={{ htmlInput: { min: 0, step: "0.01" } }}
            />

            <TextField
              label="Reason for Fine"
              value={formReason}
              onChange={(e) => setFormReason(e.target.value)}
              fullWidth
              multiline
              rows={2}
            />

            <TextField
              type="date"
              label="Due Date"
              value={formDueDate}
              onChange={(e) => setFormDueDate(e.target.value)}
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
                <MenuItem value="UNPAID">Unpaid</MenuItem>
                <MenuItem value="PAID">Paid</MenuItem>
                <MenuItem value="OVERDUE">Overdue</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={() => setOpenEdit(false)} color="inherit">Cancel</Button>
          <Button onClick={handleUpdateFine} variant="contained" color="primary">Save Changes</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
