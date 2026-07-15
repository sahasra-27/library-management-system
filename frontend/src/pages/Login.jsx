import React, { useContext, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Card, CardContent, Typography, TextField, Button, Box, Link, Checkbox, FormControlLabel, CircularProgress, Alert } from '@mui/material';
import { motion } from 'framer-motion';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

export default function Login() {
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();
  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm();

  const onSubmit = async (data) => {
    setLoading(true);
    setErrorMsg('');
    try {
      const userPayload = await login(data.username, data.password);
      toast.success("Welcome back, " + userPayload.username + "!");
      if (userPayload.role === 'ADMIN') {
        navigate('/admin/dashboard');
      } else {
        navigate('/student/dashboard');
      }
    } catch (err) {
      console.error(err);
      setErrorMsg(err.response?.data?.message || 'Invalid username or password.');
      toast.error("Login failed!");
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
                BookVerse
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Book Management System Portal
              </Typography>
            </Box>

            {errorMsg && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {errorMsg}
              </Alert>
            )}

            <form onSubmit={handleSubmit(onSubmit)}>
              <TextField
                fullWidth
                label="Username"
                variant="outlined"
                margin="normal"
                {...register('username', { required: 'Username is required' })}
                error={!!errors.username}
                helperText={errors.username?.message}
              />

              <TextField
                fullWidth
                label="Password"
                type="password"
                variant="outlined"
                margin="normal"
                {...register('password', { required: 'Password is required' })}
                error={!!errors.password}
                helperText={errors.password?.message}
              />

              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2, mb: 2 }}>
                <FormControlLabel
                  control={<Checkbox color="primary" {...register('rememberMe')} />}
                  label={<Typography variant="body2">Remember me</Typography>}
                />
                <Link href="/forgot-password" variant="body2" color="primary">
                  Forgot Password?
                </Link>
              </Box>

              <Button
                fullWidth
                type="submit"
                variant="contained"
                size="large"
                disabled={loading}
                sx={{
                  background: 'linear-gradient(90deg, #6366f1 0%, #4f46e5 100%)',
                  boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.4)',
                }}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : 'Login'}
              </Button>
            </form>

            <Box sx={{ mt: 3, textAlign: 'center' }}>
              <Typography variant="body2" color="text.secondary">
                Don't have an account?{' '}
                <Link href="/register" fontWeight="bold" color="primary">
                  Register Now
                </Link>
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </motion.div>
    </Box>
  );
}
