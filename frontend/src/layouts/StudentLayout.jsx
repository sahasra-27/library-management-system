import React, { useState, useEffect, useContext } from 'react';
import { Box, Toolbar } from '@mui/material';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import Footer from '../components/Footer';
import { AuthContext } from '../context/AuthContext';
import { Navigate, Outlet } from 'react-router-dom';
import api from '../services/api';

const drawerWidth = 240;

export default function StudentLayout() {
  const { user, loading, hasRole } = useContext(AuthContext);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (user) {
      const fetchUnread = async () => {
        try {
          const res = await api.get('/notifications/unread/count', {
            params: { userId: user.id }
          });
          setUnreadCount(res.data);
        } catch (e) {
          console.error("Failed to fetch unread notifications count", e);
        }
      };
      fetchUnread();
      const interval = setInterval(fetchUnread, 30000);
      return () => clearInterval(interval);
    }
  }, [user]);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  if (loading) return null;

  if (!user || !hasRole('USER')) {
    return <Navigate to="/login" replace />;
  }

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', flexDirection: 'column' }}>
      <Navbar onMenuClick={handleDrawerToggle} unreadCount={unreadCount} />
      <Box sx={{ display: 'flex', flex: 1 }}>
        <Sidebar mobileOpen={mobileOpen} handleDrawerToggle={handleDrawerToggle} isAdmin={false} />
        <Box
          component="main"
          sx={{
            flexGrow: 1,
            p: 3,
            width: { sm: `calc(100% - ${drawerWidth}px)` },
            display: 'flex',
            flexDirection: 'column',
          }}
        >
          <Toolbar />
          <Box sx={{ flex: 1 }}>
            <Outlet />
          </Box>
          <Footer />
        </Box>
      </Box>
    </Box>
  );
}
