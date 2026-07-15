import React, { useContext, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Card, CardContent, Typography, TextField, Button, Box, Link, CircularProgress, Alert, FormControl, InputLabel, Select, MenuItem } from '@mui/material';
import { motion } from 'framer-motion';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

export default function Register() {
  const { register: registerApi } = useContext(AuthContext);
  const navigate = useNavigate();
  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const { register, handleSubmit, watch, formState: { errors } } = useForm({
    defaultValues: {
      role: 'user',
      occupation: '',
      customOccupation: ''
    }
  });
  const password = watch('password');
  const selectedOccupation = watch('occupation');

  const onSubmit = async (data) => {
    setLoading(true);
    setErrorMsg('');
    try {
      const finalOccupation = data.occupation === 'Other' ? data.customOccupation : data.occupation;
      await registerApi(
        data.username, 
        data.email, 
        data.password, 
        data.phone, 
        data.role || 'user', 
        finalOccupation
      );
      toast.success("Registration successful! Please login.");
      navigate('/login');
    } catch (err) {
      console.error(err);
      setErrorMsg(err.response?.data?.message || 'Registration failed. Try again.');
      toast.error("Registration failed!");
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
        <Card sx={{ maxWidth: 450, width: '100%', background: 'rgba(30, 41, 59, 0.75)', backdropFilter: 'blur(16px)', p: 2 }}>
          <CardContent>
            <Box sx={{ textAlign: 'center', mb: 3 }}>
              <Typography variant="h4" fontWeight="bold" color="primary" gutterBottom>
                Create Account
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Join the BookVerse network
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
                margin="dense"
                {...register('username', { 
                  required: 'Username is required',
                  minLength: { value: 3, message: 'Minimum 3 characters required' }
                })}
                error={!!errors.username}
                helperText={errors.username?.message}
              />

              <TextField
                fullWidth
                label="Email Address"
                variant="outlined"
                margin="dense"
                {...register('email', { 
                  required: 'Email is required',
                  pattern: { value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: 'Invalid email address' }
                })}
                error={!!errors.email}
                helperText={errors.email?.message}
              />

              <TextField
                fullWidth
                label="Phone Number"
                variant="outlined"
                margin="dense"
                {...register('phone')}
              />

              <FormControl fullWidth margin="dense" variant="outlined">
                <InputLabel id="role-select-label" sx={{ color: 'rgba(255, 255, 255, 0.7)' }}>Role</InputLabel>
                <Select
                  labelId="role-select-label"
                  id="role-select"
                  label="Role"
                  defaultValue="user"
                  {...register('role')}
                  sx={{
                    color: 'white',
                    '.MuiOutlinedInput-notchedOutline': {
                      borderColor: 'rgba(255, 255, 255, 0.23)',
                    },
                    '&:hover .MuiOutlinedInput-notchedOutline': {
                      borderColor: 'white',
                    },
                    '.MuiSvgIcon-root': {
                      color: 'white',
                    }
                  }}
                  MenuProps={{
                    slotProps: {
                      paper: {
                        sx: {
                          bgcolor: '#1e293b',
                          color: 'white',
                          '& .MuiMenuItem-root': {
                            '&:hover': {
                              bgcolor: 'rgba(255, 255, 255, 0.08)',
                            },
                            '&.Mui-selected': {
                              bgcolor: 'rgba(99, 102, 241, 0.3)',
                              '&:hover': {
                                bgcolor: 'rgba(99, 102, 241, 0.4)',
                              }
                            }
                          }
                        }
                      }
                    }
                  }}
                >
                  <MenuItem value="user">User</MenuItem>
                  <MenuItem value="admin">Admin</MenuItem>
                </Select>
              </FormControl>

              <FormControl fullWidth margin="dense" variant="outlined" error={!!errors.occupation}>
                <InputLabel id="occupation-select-label" sx={{ color: 'rgba(255, 255, 255, 0.7)' }}>Occupation</InputLabel>
                <Select
                  labelId="occupation-select-label"
                  id="occupation-select"
                  label="Occupation"
                  defaultValue=""
                  {...register('occupation', { required: 'Occupation is required' })}
                  sx={{
                    color: 'white',
                    '.MuiOutlinedInput-notchedOutline': {
                      borderColor: 'rgba(255, 255, 255, 0.23)',
                    },
                    '&:hover .MuiOutlinedInput-notchedOutline': {
                      borderColor: 'white',
                    },
                    '.MuiSvgIcon-root': {
                      color: 'white',
                    }
                  }}
                  MenuProps={{
                    slotProps: {
                      paper: {
                        sx: {
                          bgcolor: '#1e293b',
                          color: 'white',
                          '& .MuiMenuItem-root': {
                            '&:hover': {
                              bgcolor: 'rgba(255, 255, 255, 0.08)',
                            },
                            '&.Mui-selected': {
                              bgcolor: 'rgba(99, 102, 241, 0.3)',
                              '&:hover': {
                                bgcolor: 'rgba(99, 102, 241, 0.4)',
                              }
                            }
                          }
                        }
                      }
                    }
                  }}
                >
                  <MenuItem value="Student">Student</MenuItem>
                  <MenuItem value="Teacher">Teacher</MenuItem>
                  <MenuItem value="Professor">Professor</MenuItem>
                  <MenuItem value="Writer">Writer</MenuItem>
                  <MenuItem value="Researcher">Researcher</MenuItem>
                  <MenuItem value="Developer">Developer</MenuItem>
                  <MenuItem value="Engineer">Engineer</MenuItem>
                  <MenuItem value="Librarian">Librarian</MenuItem>
                  <MenuItem value="Journalist">Journalist</MenuItem>
                  <MenuItem value="Doctor">Doctor</MenuItem>
                  <MenuItem value="Business Owner">Business Owner</MenuItem>
                  <MenuItem value="Other">Other</MenuItem>
                </Select>
                {errors.occupation && (
                  <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.5, display: 'block' }}>
                    {errors.occupation.message}
                  </Typography>
                )}
              </FormControl>

              {selectedOccupation === 'Other' && (
                <TextField
                  fullWidth
                  label="Specify Occupation"
                  variant="outlined"
                  margin="dense"
                  {...register('customOccupation', { required: 'Please specify your occupation' })}
                  error={!!errors.customOccupation}
                  helperText={errors.customOccupation?.message}
                />
              )}

              <TextField
                fullWidth
                label="Password"
                type="password"
                variant="outlined"
                margin="dense"
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
                margin="dense"
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
                  mt: 3,
                  background: 'linear-gradient(90deg, #6366f1 0%, #4f46e5 100%)',
                  boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.4)',
                }}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : 'Register'}
              </Button>
            </form>

            <Box sx={{ mt: 3, textAlign: 'center' }}>
              <Typography variant="body2" color="text.secondary">
                Already have an account?{' '}
                <Link href="/login" fontWeight="bold" color="primary">
                  Login Here
                </Link>
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </motion.div>
    </Box>
  );
}
