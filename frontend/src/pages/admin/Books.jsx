import React, { useEffect, useState } from 'react';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button,
  TextField, Box, Typography, TablePagination, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions, Chip
} from '@mui/material';
import { Edit, Delete, Restore, FileUpload, FileDownload, Add } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import api, { API_BASE_URL } from '../../services/api';
import { toast } from 'react-toastify';
import { ToggleButtonGroup, ToggleButton, Grid, Card, CardContent, CircularProgress, List, ListItem, ListItemButton, ListItemText, Divider } from '@mui/material';
import { Search, Clear, CloudDownload, History } from '@mui/icons-material';

const placeholderImage = 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=200&auto=format&fit=crop&q=60';

export default function Books() {
  const navigate = useNavigate();
  const [books, setBooks] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalBooks, setTotalBooks] = useState(0);

  // Dialog State
  const [openDelete, setOpenDelete] = useState(false);
  const [selectedBookId, setSelectedBookId] = useState(null);
  const [openUpload, setOpenUpload] = useState(false);
  const [uploadFile, setUploadFile] = useState(null);

  // Open Library States
  const [viewMode, setViewMode] = useState('inventory'); // 'inventory' or 'openlibrary'
  const [olSearchQuery, setOlSearchQuery] = useState('');
  const [olDebouncedQuery, setOlDebouncedQuery] = useState('');
  const [olSuggestions, setOlSuggestions] = useState([]);
  const [olShowSuggestions, setOlShowSuggestions] = useState(false);
  const [olSearchResults, setOlSearchResults] = useState([]);
  const [olSearchLoading, setOlSearchLoading] = useState(false);
  const [olRecentSearches, setOlRecentSearches] = useState([]);
  const [olActionLoading, setOlActionLoading] = useState(false);

  // Debounced query trigger
  useEffect(() => {
    const timer = setTimeout(() => {
      setOlDebouncedQuery(olSearchQuery);
    }, 450);
    return () => clearTimeout(timer);
  }, [olSearchQuery]);

  // Suggestions API fetch
  useEffect(() => {
    const getSuggestions = async () => {
      if (olDebouncedQuery.trim().length < 3) {
        setOlSuggestions([]);
        return;
      }
      try {
        const res = await api.get('/openlibrary/search', {
          params: { q: olDebouncedQuery, type: 'all' }
        });
        setOlSuggestions(res.data.slice(0, 5));
      } catch (err) {
        console.error(err);
      }
    };
    getSuggestions();
  }, [olDebouncedQuery]);

  // Initialize search history
  useEffect(() => {
    const history = localStorage.getItem('olRecentSearches');
    if (history) {
      setOlRecentSearches(JSON.parse(history));
    }
  }, []);

  const triggerOlSearch = async (query) => {
    if (!query.trim()) return;
    setOlSearchLoading(true);
    setOlShowSuggestions(false);

    let updatedHistory = [query, ...olRecentSearches.filter(q => q !== query)].slice(0, 8);
    setOlRecentSearches(updatedHistory);
    localStorage.setItem('olRecentSearches', JSON.stringify(updatedHistory));

    try {
      const res = await api.get('/openlibrary/search', {
        params: { q: query, type: 'all' }
      });
      setOlSearchResults(res.data);
    } catch (err) {
      console.error(err);
      toast.error("Failed to query Open Library API.");
    } finally {
      setOlSearchLoading(false);
    }
  };

  const handleImportAndSave = async (workId) => {
    setOlActionLoading(true);
    try {
      toast.info("Importing book into database...");
      const res = await api.post(`/books/import/${workId}`);
      toast.success(`Successfully imported: "${res.data.title}"!`);
      fetchBooks();
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to import book.");
    } finally {
      setOlActionLoading(false);
    }
  };

  const handleImportAndEdit = async (workId) => {
    setOlActionLoading(true);
    try {
      toast.info("Importing book details...");
      const res = await api.post(`/books/import/${workId}`);
      toast.success(`Imported: "${res.data.title}". Redirecting to details editor...`);
      navigate(`/admin/books/edit/${res.data.id}`);
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to import book for editing.");
    } finally {
      setOlActionLoading(false);
    }
  };

  const fetchBooks = async () => {
    try {
      const res = await api.get('/books', {
        params: {
          query: search,
          page: page,
          size: rowsPerPage,
          sortBy: 'title',
          sortDir: 'asc'
        }
      });
      setBooks(res.data.content);
      setTotalBooks(res.data.totalElements);
    } catch (e) {
      console.error(e);
      toast.error("Failed to load books catalog!");
    }
  };

  useEffect(() => {
    fetchBooks();
  }, [page, rowsPerPage, search]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleDeleteClick = (id) => {
    setSelectedBookId(id);
    setOpenDelete(true);
  };

  const confirmDelete = async () => {
    try {
      await api.delete(`/books/${selectedBookId}`);
      toast.success("Book deleted successfully!");
      fetchBooks();
    } catch (e) {
      toast.error("Failed to delete book.");
    } finally {
      setOpenDelete(false);
    }
  };

  const handleRestore = async (id) => {
    try {
      await api.patch(`/books/${id}/restore`);
      toast.success("Book restored successfully!");
      fetchBooks();
    } catch (e) {
      toast.error("Failed to restore book.");
    }
  };

  const handleUploadSubmit = async () => {
    if (!uploadFile) {
      toast.warning("Please choose a CSV file first.");
      return;
    }
    const formData = new FormData();
    formData.append("file", uploadFile);
    try {
      await api.post('/books/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      toast.success("CSV Books imported successfully!");
      fetchBooks();
      setOpenUpload(false);
    } catch (e) {
      toast.error("Failed to process CSV file.");
    }
  };

  const handleExportExcel = () => {
    window.open(`${API_BASE_URL}/books/export/excel`, '_blank');
  };

  const handleExportCsv = () => {
    window.open(`${API_BASE_URL}/books/export/csv`, '_blank');
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Book Catalog
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button variant="contained" startIcon={<Add />} onClick={() => navigate('/admin/books/new')}>
            Add Book
          </Button>
          <Button variant="outlined" startIcon={<FileUpload />} onClick={() => setOpenUpload(true)}>
            Import CSV
          </Button>
          <Button variant="outlined" startIcon={<FileDownload />} onClick={handleExportExcel}>
            Excel
          </Button>
          <Button variant="outlined" startIcon={<FileDownload />} onClick={handleExportCsv}>
            CSV
          </Button>
        </Box>
      </Box>

      {/* Toggle View Mode */}
      <Box sx={{ mb: 3, display: 'flex', justifyContent: 'center' }}>
        <ToggleButtonGroup
          value={viewMode}
          exclusive
          onChange={(e, val) => val && setViewMode(val)}
          color="primary"
        >
          <ToggleButton value="inventory" sx={{ px: 3 }}>Library Inventory (MySQL)</ToggleButton>
          <ToggleButton value="openlibrary" sx={{ px: 3 }}>Search Open Library API</ToggleButton>
        </ToggleButtonGroup>
      </Box>

      {viewMode === 'inventory' ? (
        <>
          <Box mb={2}>
            <TextField
              fullWidth
              variant="outlined"
              label="Search by ISBN, title, author, category..."
              value={search}
              onChange={(e) => { setSearch(e.target.value); setPage(0); }}
            />
          </Box>

          <TableContainer component={Paper} sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ISBN</TableCell>
                  <TableCell>Title</TableCell>
                  <TableCell>Author</TableCell>
                  <TableCell>Category</TableCell>
                  <TableCell>Qty</TableCell>
                  <TableCell>Avail</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {books.map((book) => (
                  <TableRow key={book.id}>
                    <TableCell>{book.isbn}</TableCell>
                    <TableCell>{book.title}</TableCell>
                    <TableCell>{book.authorName}</TableCell>
                    <TableCell>{book.categoryName}</TableCell>
                    <TableCell>{book.quantity}</TableCell>
                    <TableCell>{book.availableQuantity}</TableCell>
                    <TableCell>
                      <Chip
                        label={book.status}
                        color={book.status === 'ACTIVE' ? 'success' : 'error'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <IconButton color="primary" onClick={() => navigate(`/admin/books/edit/${book.id}`)}>
                        <Edit />
                      </IconButton>
                      {book.status === 'ACTIVE' ? (
                        <IconButton color="error" onClick={() => handleDeleteClick(book.id)}>
                          <Delete />
                        </IconButton>
                      ) : (
                        <IconButton color="success" onClick={() => handleRestore(book.id)}>
                          <Restore />
                        </IconButton>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
            <TablePagination
              component="div"
              count={totalBooks}
              page={page}
              onPageChange={handleChangePage}
              rowsPerPage={rowsPerPage}
              onRowsPerPageChange={handleChangeRowsPerPage}
            />
          </TableContainer>
        </>
      ) : (
        /* Open Library Search View */
        <Box>
          <Box sx={{ position: 'relative', mb: 4 }}>
            <Box sx={{ display: 'flex', gap: 1 }}>
              <TextField
                fullWidth
                placeholder="Search books globally by title, author, ISBN or subject..."
                value={olSearchQuery}
                onChange={(e) => {
                  setOlSearchQuery(e.target.value);
                  setOlShowSuggestions(true);
                }}
                onFocus={() => setOlShowSuggestions(true)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    triggerOlSearch(olSearchQuery);
                  }
                }}
                InputProps={{
                  startAdornment: <Search sx={{ color: 'text.secondary', mr: 1 }} />
                }}
              />
              {olSearchQuery && (
                <IconButton onClick={() => {
                  setOlSearchQuery('');
                  setOlSearchResults([]);
                  setOlSuggestions([]);
                }} sx={{ border: '1px solid rgba(255,255,255,0.1)' }}>
                  <Clear />
                </IconButton>
              )}
              <Button 
                variant="contained" 
                onClick={() => triggerOlSearch(olSearchQuery)}
                sx={{
                  background: 'linear-gradient(90deg, #6366f1 0%, #4f46e5 100%)',
                  px: 4
                }}
              >
                Search
              </Button>
            </Box>

            {/* Suggestion Dropdown */}
            {olShowSuggestions && (olSuggestions.length > 0 || olRecentSearches.length > 0) && (
              <Paper 
                sx={{ 
                  position: 'absolute', 
                  top: '100%', 
                  left: 0, 
                  right: 0, 
                  zIndex: 10, 
                  background: '#1e293b', 
                  border: '1px solid rgba(255,255,255,0.1)',
                  mt: 1, 
                  maxHeight: 300, 
                  overflowY: 'auto' 
                }}
              >
                {olRecentSearches.length > 0 && (
                  <Box p={1.5}>
                    <Typography variant="caption" color="text.secondary" display="flex" alignItems="center" gap={0.5}>
                      <History fontSize="inherit" /> Recent Searches
                    </Typography>
                    <Box display="flex" gap={1} flexWrap="wrap" mt={1}>
                      {olRecentSearches.map((term, idx) => (
                        <Chip 
                          key={idx} 
                          label={term} 
                          onClick={() => {
                            setOlSearchQuery(term);
                            triggerOlSearch(term);
                          }} 
                          onDelete={() => {
                            const updated = olRecentSearches.filter(t => t !== term);
                            setOlRecentSearches(updated);
                            localStorage.setItem('olRecentSearches', JSON.stringify(updated));
                          }}
                          size="small"
                          sx={{ background: 'rgba(255,255,255,0.08)', cursor: 'pointer' }}
                        />
                      ))}
                    </Box>
                    <Divider sx={{ my: 1.5, borderColor: 'rgba(255,255,255,0.1)' }} />
                  </Box>
                )}

                {olSuggestions.length > 0 && (
                  <List>
                    {olSuggestions.map((item) => (
                      <ListItem key={item.key} disablePadding>
                        <ListItemButton 
                          onClick={() => {
                            setOlSearchQuery(item.title);
                            triggerOlSearch(item.title);
                          }}
                        >
                          <Book sx={{ mr: 2, color: 'primary.main', fontSize: 20 }} />
                          <ListItemText 
                            primary={item.title} 
                            secondary={item.authors && item.authors.length > 0 ? `By ${item.authors.join(', ')}` : 'Unknown Author'} 
                          />
                        </ListItemButton>
                      </ListItem>
                    ))}
                  </List>
                )}
              </Paper>
            )}
          </Box>

          {/* Search Overlay Dismissal */}
          {olShowSuggestions && (
            <Box 
              sx={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, zIndex: 5 }} 
              onClick={() => setOlShowSuggestions(false)} 
            />
          )}

          {olSearchLoading ? (
            <Box display="flex" justifyContent="center" py={8}><CircularProgress /></Box>
          ) : olSearchResults.length > 0 ? (
            <Grid container spacing={3}>
              {olSearchResults.map((book) => (
                <Grid item xs={12} sm={6} md={4} lg={3} key={book.key}>
                  <Card 
                    sx={{ 
                      height: '100%', 
                      background: 'rgba(30, 41, 59, 0.45)', 
                      border: '1px solid rgba(255, 255, 255, 0.08)',
                      display: 'flex',
                      flexDirection: 'column'
                    }}
                  >
                    <Box sx={{ height: 180, overflow: 'hidden', background: '#1e293b' }}>
                      <img 
                        src={book.coverUrl || placeholderImage} 
                        alt={book.title} 
                        style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        onError={(e) => { e.target.src = placeholderImage; }}
                      />
                    </Box>
                    <CardContent sx={{ p: 2, flexGrow: 1, display: 'flex', flexDirection: 'column', gap: 1 }}>
                      <Typography variant="body1" fontWeight="bold" sx={{ lineHeight: 1.2, height: '2.4em', overflow: 'hidden', textOverflow: 'ellipsis', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical' }}>
                        {book.title}
                      </Typography>
                      <Typography variant="caption" color="text.secondary" display="block">
                        By {book.authors && book.authors.length > 0 ? book.authors.join(', ') : 'Unknown'}
                      </Typography>
                      <Typography variant="caption" color="text.secondary" display="block">
                        Published: {book.firstPublishYear || 'N/A'}
                      </Typography>
                      <Typography variant="caption" color="primary" display="block" noWrap>
                        ISBN: {book.isbn || 'N/A'}
                      </Typography>

                      <Box display="flex" gap={1} mt="auto" pt={1}>
                        <Button 
                          fullWidth 
                          variant="contained" 
                          size="small" 
                          disabled={olActionLoading}
                          onClick={() => handleImportAndSave(book.key)}
                          sx={{ fontSize: '0.7rem' }}
                        >
                          Import
                        </Button>
                        <Button 
                          fullWidth 
                          variant="outlined" 
                          size="small" 
                          disabled={olActionLoading}
                          onClick={() => handleImportAndEdit(book.key)}
                          sx={{ fontSize: '0.7rem' }}
                        >
                          Edit
                        </Button>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          ) : (
            <Typography variant="body1" align="center" color="text.secondary" py={4}>
              Search to discover and import books from the Open Library catalog.
            </Typography>
          )}
        </Box>
      )}

      {/* Delete Confirmation */}
      <Dialog open={openDelete} onClose={() => setOpenDelete(false)}>
        <DialogTitle>Delete Book</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to soft delete this book from catalog?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDelete(false)}>Cancel</Button>
          <Button color="error" variant="contained" onClick={confirmDelete}>Delete</Button>
        </DialogActions>
      </Dialog>

      {/* CSV Import Modal */}
      <Dialog open={openUpload} onClose={() => setOpenUpload(false)}>
        <DialogTitle>Import Books (CSV)</DialogTitle>
        <DialogContent>
          <Typography mb={2}>Select a valid books csv file.</Typography>
          <input type="file" accept=".csv" onChange={(e) => setUploadFile(e.target.files[0])} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenUpload(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleUploadSubmit}>Upload</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
