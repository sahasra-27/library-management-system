import React, { useContext } from 'react';
import { AppBar, Toolbar, Typography, IconButton, Menu, MenuItem, Box, Badge } from '@mui/material';
import { Menu as MenuIcon, Brightness4, Brightness7, AccountCircle, Notifications } from '@mui/icons-material';
import { ThemeModeContext } from '../context/ThemeContext';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Navbar({ onMenuClick, unreadCount = 0 }) {
  const { toggleThemeMode } = useContext(ThemeModeContext);
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = async () => {
    handleClose();
    await logout();
    navigate('/login');
  };

  const handleProfile = () => {
    handleClose();
    if (user.role === 'ADMIN') {
      navigate('/admin/profile');
    } else {
      navigate('/student/profile');
    }
  };

  return (
    <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1, background: 'rgba(30, 41, 59, 0.85)', backdropFilter: 'blur(10px)', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }}>
      <Toolbar>
        <IconButton
          color="inherit"
          aria-label="open drawer"
          edge="start"
          onClick={onMenuClick}
          sx={{ mr: 2, display: { sm: 'none' } }}
        >
          <MenuIcon />
        </IconButton>
        <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1, fontWeight: 'bold', letterSpacing: 0.5, color: '#6366f1' }}>
          BookVerse
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <IconButton sx={{ ml: 1 }} onClick={toggleThemeMode} color="inherit">
            <Brightness7 sx={{ display: { xs: 'none', sm: 'inline-block' } }} />
            <Brightness4 sx={{ display: { xs: 'inline-block', sm: 'none' } }} />
          </IconButton>

          <IconButton color="inherit" onClick={() => navigate(user && user.role === 'ADMIN' ? '/admin/notifications' : '/student/notifications')}>
            <Badge badgeContent={unreadCount} color="error">
              <Notifications />
            </Badge>
          </IconButton>

          {user && (
            <div>
              <IconButton
                size="large"
                aria-label="account of current user"
                aria-controls="menu-appbar"
                aria-haspopup="true"
                onClick={handleMenu}
                color="inherit"
              >
                <AccountCircle />
              </IconButton>
              <Menu
                id="menu-appbar"
                anchorEl={anchorEl}
                anchorOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                keepMounted
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                open={Boolean(anchorEl)}
                onClose={handleClose}
              >
                <MenuItem onClick={handleProfile}>Profile</MenuItem>
                <MenuItem onClick={handleLogout}>Logout</MenuItem>
              </Menu>
            </div>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
}
