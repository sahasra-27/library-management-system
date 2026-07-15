import React, { useContext, useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Card, CardContent, Typography, TextField, Button, Box, Grid, Avatar, CircularProgress, Alert } from '@mui/material';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

export default function Profile() {
  const { user, updateProfile } = useContext(AuthContext);
  const [photoLoading, setPhotoLoading] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    const fetchFreshUser = async () => {
      try {
        const res = await api.get(`/users/${user.id}`);
        updateProfile({ ...user, ...res.data });
      } catch (err) {
        console.error("Failed to fetch fresh user details:", err);
      }
    };
    if (user?.id) {
      fetchFreshUser();
    }
  }, [user?.id]);

  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  const handlePhotoUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setPhotoLoading(true);
    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await api.post(`/users/${user.id}/profile-photo`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      const photoPath = res.data.profilePhoto;
      updateProfile({ ...user, profilePhoto: photoPath });
      toast.success("Profile photo updated successfully!");
    } catch (err) {
      toast.error("Failed to upload profile picture.");
    } finally {
      setPhotoLoading(false);
    }
  };

  const handlePasswordChange = async (data) => {
    setPasswordLoading(true);
    setErrorMsg('');
    try {
      await api.patch(`/users/${user.id}/change-password`, null, {
        params: {
          oldPassword: data.oldPassword,
          newPassword: data.newPassword
        }
      });
      toast.success("Password changed successfully!");
      reset();
    } catch (err) {
      setErrorMsg(err.response?.data?.message || "Failed to update password.");
      toast.error("Password change failed!");
    } finally {
      setPasswordLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3}>
        My Profile
      </Typography>

      <Grid container spacing={4}>
        <Grid item xs={12} md={4}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', textAlign: 'center', p: 3 }}>
            <CardContent sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                <Avatar
                  src={user?.profilePhoto ? `http://localhost:8080${user.profilePhoto}` : undefined}
                  sx={{ width: 120, height: 120 }}
                />
              </Box>
              
              <Typography variant="h5" fontWeight="bold" mb={1}>
                {user?.username}
              </Typography>
              <Typography variant="body2" color="text.secondary" mb={0.5}>
                Email: {user?.email || 'N/A'}
              </Typography>
              <Typography variant="body2" color="text.secondary" mb={0.5}>
                Role: {user?.role === 'STUDENT' || user?.role === 'USER' ? 'User' : user?.role}
              </Typography>
              <Typography variant="body2" color="text.secondary" mb={3}>
                Occupation: {user?.occupation || 'N/A'}
              </Typography>

              <Button
                variant="outlined"
                component="label"
                disabled={photoLoading}
                size="small"
              >
                {photoLoading ? 'Uploading...' : 'Upload Photo'}
                <input
                  type="file"
                  hidden
                  accept="image/*"
                  onChange={handlePhotoUpload}
                />
              </Button>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={8}>
          <Card sx={{ background: 'rgba(30, 41, 59, 0.45)', p: 2 }}>
            <CardContent>
              <Typography variant="h6" fontWeight="bold" mb={3}>
                Security Credentials
              </Typography>

              {errorMsg && <Alert severity="error" sx={{ mb: 2 }}>{errorMsg}</Alert>}

              <form onSubmit={handleSubmit(handlePasswordChange)}>
                <TextField
                  fullWidth
                  label="Current Password"
                  type="password"
                  {...register('oldPassword', { required: 'Old password is required' })}
                  error={!!errors.oldPassword}
                  helperText={errors.oldPassword?.message}
                  margin="normal"
                />

                <TextField
                  fullWidth
                  label="New Password"
                  type="password"
                  {...register('newPassword', { 
                    required: 'New password is required',
                    minLength: { value: 6, message: 'Minimum 6 characters required' }
                  })}
                  error={!!errors.newPassword}
                  helperText={errors.newPassword?.message}
                  margin="normal"
                />

                <Button
                  type="submit"
                  variant="contained"
                  disabled={passwordLoading}
                  sx={{ mt: 3 }}
                >
                  {passwordLoading ? 'Updating...' : 'Change Password'}
                </Button>
              </form>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
