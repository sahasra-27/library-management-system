import React, { useEffect, useState } from 'react';
import { Card, CardContent, Typography, TextField, Button, Box, Grid, CircularProgress } from '@mui/material';
import api from '../../services/api';
import { toast } from 'react-toastify';

export default function Settings() {
  const [settings, setSettings] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const fetchSettings = async () => {
    try {
      const res = await api.get('/settings');
      setSettings(res.data);
    } catch (e) {
      toast.error("Failed to load settings.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSettings();
  }, []);

  const handleChange = (key, value) => {
    setSettings(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      // Send individual post updates for each key
      const promises = Object.keys(settings).map(key => 
        api.post('/settings', null, { params: { key, value: settings[key] } })
      );
      await Promise.all(promises);
      toast.success("Settings updated successfully!");
    } catch (e) {
      toast.error("Failed to save settings.");
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
        Library Config Settings
      </Typography>

      <Card sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
        <CardContent>
          <form onSubmit={handleSave}>
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Library Name"
                  value={settings.library_name || ''}
                  onChange={(e) => handleChange('library_name', e.target.value)}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Support Email"
                  value={settings.email || ''}
                  onChange={(e) => handleChange('email', e.target.value)}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Support Phone"
                  value={settings.phone || ''}
                  onChange={(e) => handleChange('phone', e.target.value)}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Fine Amount per Day (INR)"
                  type="number"
                  value={settings.fine_amount || ''}
                  onChange={(e) => handleChange('fine_amount', e.target.value)}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Borrow Duration (Days)"
                  type="number"
                  value={settings.borrow_duration || ''}
                  onChange={(e) => handleChange('borrow_duration', e.target.value)}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Max Borrow Books Limit"
                  type="number"
                  value={settings.max_books || ''}
                  onChange={(e) => handleChange('max_books', e.target.value)}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={2}
                  label="Library Address"
                  value={settings.library_address || ''}
                  onChange={(e) => handleChange('library_address', e.target.value)}
                />
              </Grid>
            </Grid>

            <Box mt={4}>
              <Button type="submit" variant="contained" disabled={submitting}>
                {submitting ? 'Saving...' : 'Save Settings'}
              </Button>
            </Box>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}
