import React, { useContext, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Card, CardContent, Typography, TextField, Button, Box, Link, CircularProgress, Alert } from '@mui/material';
import { motion } from 'framer-motion';
import { AuthContext } from '../context/AuthContext';
import { toast } from 'react-toastify';

export default function ForgotPassword() {
  const { forgotPassword } = useContext(AuthContext);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const { register, handleSubmit, formState: { errors } } = useForm();

  const onSubmit = async (data) => {
    setLoading(true);
    setErrorMsg('');
    try {
      await forgotPassword(data.email);
      setSuccess(true);
      toast.success("Reset email sent! Check console logs.");
    } catch (err) {
      console.error(err);
      setErrorMsg(err.response?.data?.message || 'Error occurred. Please try again.');
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
                Reset Password
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Enter email to receive reset credentials
              </Typography>
            </Box>

            {success ? (
              <Box sx={{ textAlign: 'center', py: 2 }}>
                <Alert severity="success" sx={{ mb: 2 }}>
                  We have sent a password reset link to your email.
                </Alert>
                <Link href="/login" fontWeight="bold" color="primary">
                  Back to Login
                </Link>
              </Box>
            ) : (
              <form onSubmit={handleSubmit(onSubmit)}>
                {errorMsg && <Alert severity="error" sx={{ mb: 2 }}>{errorMsg}</Alert>}
                <TextField
                  fullWidth
                  label="Email Address"
                  variant="outlined"
                  margin="normal"
                  {...register('email', { 
                    required: 'Email is required',
                    pattern: { value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: 'Invalid email address' }
                  })}
                  error={!!errors.email}
                  helperText={errors.email?.message}
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
                  {loading ? <CircularProgress size={24} color="inherit" /> : 'Send Reset Link'}
                </Button>

                <Box sx={{ mt: 3, textAlign: 'center' }}>
                  <Typography variant="body2" color="text.secondary">
                    Remember password?{' '}
                    <Link href="/login" fontWeight="bold" color="primary">
                      Login Here
                    </Link>
                  </Typography>
                </Box>
              </form>
            )}
          </CardContent>
        </Card>
      </motion.div>
    </Box>
  );
}
