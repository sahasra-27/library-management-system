import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Card, CardContent, Typography, TextField, Button, Box, MenuItem, CircularProgress, Grid, ToggleButton, ToggleButtonGroup } from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function BookForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fetchingApi, setFetchingApi] = useState(false);

  // Dropdown lists
  const [categories, setCategories] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [publishers, setPublishers] = useState([]);

  // Entry options state
  const [entryMode, setEntryMode] = useState('isbn'); // 'isbn' or 'manual'

  // Fetch ISBN search state
  const [isbnSearch, setIsbnSearch] = useState('');

  // File Upload State
  const [imageFile, setImageFile] = useState(null);
  const [coverUrl, setCoverUrl] = useState('');

  const { register, handleSubmit, setValue, formState: { errors } } = useForm();

  const fetchDropdowns = async () => {
    try {
      const [catRes, authRes, pubRes] = await Promise.all([
        api.get('/categories/all'),
        api.get('/authors/all'),
        api.get('/publishers/all'),
      ]);
      setCategories(catRes.data);
      setAuthors(authRes.data);
      setPublishers(pubRes.data);
    } catch (e) {
      toast.error("Failed to load selectors metadata.");
    }
  };

  useEffect(() => {
    const fetchBook = async () => {
      if (!isEdit) return;
      setLoading(true);
      try {
        const res = await api.get(`/books/${id}`);
        const book = res.data;
        setValue('isbn', book.isbn);
        setValue('title', book.title);
        setValue('subtitle', book.subtitle || '');
        setValue('authorId', book.authorId);
        setValue('publisherId', book.publisherId);
        setValue('categoryId', book.categoryId);
        setValue('language', book.language);
        setValue('edition', book.edition);
        setValue('publicationYear', book.publicationYear);
        setValue('price', book.price);
        setValue('shelfNumber', book.shelfNumber);
        setValue('rackNumber', book.rackNumber);
        setValue('quantity', book.quantity);
        setValue('numberOfPages', book.numberOfPages || '');
        setValue('pageCount', book.pageCount || '');
        setValue('description', book.description);
        setCoverUrl(book.coverImageUrl || book.coverImage || '');
        setValue('coverImageUrl', book.coverImageUrl || '');
      } catch (e) {
        toast.error("Failed to load book record.");
        navigate('/admin/books');
      } finally {
        setLoading(false);
      }
    };

    fetchDropdowns().then(fetchBook);
  }, [id, isEdit, setValue, navigate]);

  const handleFetchBookByIsbn = async () => {
    if (!isbnSearch.trim()) {
      toast.warning("Please enter an ISBN number first.");
      return;
    }
    setFetchingApi(true);
    try {
      const res = await api.get(`/books/isbn/${isbnSearch.trim()}`);
      const info = res.data;
      
      // Re-fetch dropdowns so that auto-created category/author/publisher exist
      await fetchDropdowns();

      // Populate form
      setValue('isbn', info.isbn || isbnSearch);
      setValue('title', info.title || '');
      setValue('subtitle', info.subtitle || '');
      setValue('authorId', info.authorId || '');
      setValue('publisherId', info.publisherId || '');
      setValue('categoryId', info.categoryId || '');
      setValue('language', info.language || 'English');
      setValue('publicationYear', info.publicationYear || '');
      setValue('numberOfPages', info.numberOfPages || '');
      setValue('pageCount', info.pageCount || '');
      setValue('description', info.description || '');
      
      if (info.coverImageUrl) {
        setCoverUrl(info.coverImageUrl);
        setValue('coverImage', info.coverImageUrl);
        setValue('coverImageUrl', info.coverImageUrl);
      } else {
        setCoverUrl('');
        setValue('coverImage', '');
        setValue('coverImageUrl', '');
      }
      toast.success("Book details fetched from Open Library API successfully!");
    } catch (e) {
      if (e.response?.status === 404) {
        toast.error("Book not found. You can enter details manually below.");
        setEntryMode('manual');
      } else {
        toast.error(e.response?.data?.message || "Error communicating with Open Library API.");
      }
    } finally {
      setFetchingApi(false);
    }
  };

  const onSubmit = async (data) => {
    setSubmitting(true);
    const formData = new FormData();
    // Include cover image URL if no file is uploaded and URL exists
    if (!imageFile && coverUrl) {
      data.coverImage = coverUrl;
      data.coverImageUrl = coverUrl;
    }
    formData.append("book", JSON.stringify(data));
    if (imageFile) {
      formData.append("file", imageFile);
    }

    try {
      if (isEdit) {
        await api.put(`/books/${id}`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        toast.success("Book updated successfully!");
      } else {
        await api.post('/books', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        toast.success("Book added successfully!");
      }
      navigate('/admin/books');
    } catch (e) {
      toast.error(e.response?.data?.message || "Failed to save book.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '50vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        {isEdit ? 'Edit Book' : 'Add New Book'}
      </Typography>

      {!isEdit && (
        <Box sx={{ mb: 3, display: 'flex', justifyContent: 'center' }}>
          <ToggleButtonGroup
            value={entryMode}
            exclusive
            onChange={(e, val) => val && setEntryMode(val)}
            color="primary"
          >
            <ToggleButton value="isbn" sx={{ px: 3 }}>Option 1: Search using ISBN</ToggleButton>
            <ToggleButton value="manual" sx={{ px: 3 }}>Option 2: Manual Entry</ToggleButton>
          </ToggleButtonGroup>
        </Box>
      )}

      {entryMode === 'isbn' && !isEdit && (
        <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', mb: 3 }}>
          <CardContent>
            <Typography variant="h6" fontWeight="bold" mb={2}>
              Fetch Metadata from Open Library
            </Typography>
            <Box display="flex" gap={2} alignItems="center">
              <TextField
                label="Enter ISBN"
                variant="outlined"
                value={isbnSearch}
                onChange={(e) => setIsbnSearch(e.target.value)}
                sx={{ minWidth: 280 }}
                placeholder="e.g. 9780131872486"
              />
              <Button 
                variant="contained" 
                onClick={handleFetchBookByIsbn} 
                disabled={fetchingApi}
                sx={{
                  background: 'linear-gradient(90deg, #6366f1 0%, #4f46e5 100%)',
                  boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.4)',
                  height: 56
                }}
              >
                {fetchingApi ? <CircularProgress size={24} color="inherit" /> : 'Fetch Book'}
              </Button>
            </Box>
          </CardContent>
        </Card>
      )}

      <Card sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="ISBN"
                  {...register('isbn', { required: 'ISBN is required' })}
                  error={!!errors.isbn}
                  helperText={errors.isbn?.message}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Title"
                  {...register('title', { required: 'Title is required' })}
                  error={!!errors.title}
                  helperText={errors.title?.message}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Subtitle"
                  {...register('subtitle')}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  select
                  label="Category"
                  defaultValue=""
                  {...register('categoryId', { required: 'Category is required' })}
                  error={!!errors.categoryId}
                  helperText={errors.categoryId?.message}
                >
                  {categories.map((c) => (
                    <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  select
                  label="Author"
                  defaultValue=""
                  {...register('authorId', { required: 'Author is required' })}
                  error={!!errors.authorId}
                  helperText={errors.authorId?.message}
                >
                  {authors.map((a) => (
                    <MenuItem key={a.id} value={a.id}>{a.name}</MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  select
                  label="Publisher"
                  defaultValue=""
                  {...register('publisherId', { required: 'Publisher is required' })}
                  error={!!errors.publisherId}
                  helperText={errors.publisherId?.message}
                >
                  {publishers.map((p) => (
                    <MenuItem key={p.id} value={p.id}>{p.name}</MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12} sm={3}>
                <TextField
                  fullWidth
                  label="Language"
                  {...register('language')}
                />
              </Grid>

              <Grid item xs={12} sm={3}>
                <TextField
                  fullWidth
                  label="Edition"
                  {...register('edition')}
                />
              </Grid>

              <Grid item xs={12} sm={3}>
                <TextField
                  fullWidth
                  label="Publication Year"
                  type="number"
                  {...register('publicationYear')}
                />
              </Grid>

              <Grid item xs={12} sm={3}>
                <TextField
                  fullWidth
                  label="Number of Pages"
                  type="number"
                  {...register('numberOfPages')}
                />
              </Grid>

              <Grid item xs={12} sm={3}>
                <TextField
                  fullWidth
                  label="Price (INR)"
                  type="number"
                  step="0.01"
                  {...register('price')}
                />
              </Grid>

              <Grid item xs={12} sm={3}>
                <TextField
                  fullWidth
                  label="Shelf Number"
                  {...register('shelfNumber')}
                />
              </Grid>

              <Grid item xs={12} sm={3}>
                <TextField
                  fullWidth
                  label="Rack Number"
                  {...register('rackNumber')}
                />
              </Grid>

              <Grid item xs={12} sm={3}>
                <TextField
                  fullWidth
                  label="Quantity"
                  type="number"
                  {...register('quantity', { 
                    required: 'Quantity is required',
                    min: { value: 0, message: 'Cannot be negative' }
                  })}
                  error={!!errors.quantity}
                  helperText={errors.quantity?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={4}
                  label="Description"
                  {...register('description')}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Cover Image URL"
                  {...register('coverImageUrl')}
                  onChange={(e) => {
                    setCoverUrl(e.target.value);
                  }}
                />
              </Grid>

              <Grid item xs={12}>
                <Box display="flex" gap={4} alignItems="center">
                  <Box>
                    <Typography variant="body2" mb={1} color="text.secondary">
                      Upload Book Cover Image
                    </Typography>
                    <input
                      type="file"
                      accept="image/*"
                      onChange={(e) => {
                        setImageFile(e.target.files[0]);
                        setCoverUrl(''); // Clear fetched URL preview on manual upload
                      }}
                    />
                  </Box>
                  {coverUrl && (
                    <Box display="flex" flexDirection="column" alignItems="center">
                      <Typography variant="caption" color="text.secondary" mb={0.5}>Cover Preview</Typography>
                      <img 
                        src={coverUrl} 
                        alt="Book Cover Preview" 
                        style={{ maxHeight: 120, borderRadius: 4, border: '1px solid rgba(255, 255, 255, 0.2)' }}
                      />
                    </Box>
                  )}
                </Box>
              </Grid>
            </Grid>

            <Box mt={4} display="flex" gap={2}>
              <Button type="submit" variant="contained" disabled={submitting}>
                {submitting ? <CircularProgress size={24} color="inherit" /> : 'Save Book'}
              </Button>
              <Button variant="outlined" onClick={() => navigate('/admin/books')}>
                Cancel
              </Button>
            </Box>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}
