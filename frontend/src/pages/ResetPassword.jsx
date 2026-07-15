import React, { useContext, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Card, CardContent, Typography, TextField, Button, Box, Link, CircularProgress, Alert } from '@mui/material';
import { motion } from 'framer-motion';
import { AuthContext } from '../context/AuthContext';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { toast } from 'react-toastify';

export default function ResetPassword() {
  const { resetPassword } = useContext(AuthContext);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const { register, handleSubmit, watch, formState: { errors } } = useForm();
  const password = watch('password');

  const onSubmit = async (data) => {
    if (!token) {
      setErrorMsg("Invalid or missing reset token.");
      return;
    }
    setLoading(true);
    setErrorMsg('');
    try {
      await resetPassword(token, data.password);
      toast.success("Password reset successfully! Redirecting...");
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      console.error(err);
      setErrorMsg(err.response?.data?.message || 'Error occurred. Password reset failed.');
      toast.error("Operation failed!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%)',
        p: 2,
      }}
    >
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <Card sx={{ maxWidth: 400, width: '100%', background: 'rgba(30, 41, 59, 0.75)', backdropFilter: 'blur(16px)', p: 2 }}>
          <CardContent>
            <Box sx={{ textAlign: 'center', mb: 3 }}>
              <Typography variant="h4" fontWeight="bold" color="primary" gutterBottom>
                New Password
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Set up a secure login credential
              </Typography>
            </Box>

            {!token ? (
              <Alert severity="error" sx={{ mb: 2 }}>
                Reset token is missing. Please check your email link again.
              </Alert>
            ) : (
              <form onSubmit={handleSubmit(onSubmit)}>
                {errorMsg && <Alert severity="error" sx={{ mb: 2 }}>{errorMsg}</Alert>}
                
                <TextField
                  fullWidth
                  label="New Password"
                  type="password"
                  variant="outlined"
                  margin="normal"
                  {...register('password', { 
                    required: 'Password is required',
                    minLength: { value: 6, message: 'Minimum 6 characters required' }
                  })}
                  error={!!errors.password}
                  helperText={errors.password?.message}
                />

                <TextField
                  fullWidth
                  label="Confirm Password"
                  type="password"
                  variant="outlined"
                  margin="normal"
                  {...register('confirmPassword', { 
                    required: 'Please confirm your password',
                    validate: value => value === password || 'Passwords do not match'
                  })}
                  error={!!errors.confirmPassword}
                  helperText={errors.confirmPassword?.message}
                />

                <Button
                  fullWidth
                  type="submit"
                  variant="contained"
                  size="large"
                  disabled={loading}
                  sx={{
                    mt: 2,
                    background: 'linear-gradient(90deg, #6366f1 0%, #4f46e5 100%)',
                    boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.4)',
                  }}
                >
                  {loading ? <CircularProgress size={24} color="inherit" /> : 'Update Password'}
                </Button>
              </form>
            )}
          </CardContent>
        </Card>
      </motion.div>
    </Box>
  );
}
