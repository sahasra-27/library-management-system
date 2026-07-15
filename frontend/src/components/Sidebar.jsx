import React from 'react';
import { Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Toolbar, Box, Divider } from '@mui/material';
import {
  Dashboard, Book, Category, People, Assignment, KeyboardReturn, History,
  Notifications, Settings, Person, LocalAtm, LibraryBooks, BarChart, Gavel
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';

const drawerWidth = 240;

export default function Sidebar({ mobileOpen, handleDrawerToggle, isAdmin }) {
  const navigate = useNavigate();
  const location = useLocation();

  const adminMenu = [
    { text: 'Dashboard', icon: <Dashboard />, path: '/admin/dashboard' },
    { text: 'Books', icon: <Book />, path: '/admin/books' },
    { text: 'Categories', icon: <Category />, path: '/admin/categories' },
    { text: 'Authors', icon: <Person />, path: '/admin/authors' },
    { text: 'Publishers', icon: <LibraryBooks />, path: '/admin/publishers' },
    { text: 'Users', icon: <People />, path: '/admin/users' },
    { text: 'Issue Books', icon: <Assignment />, path: '/admin/issues' },
    { text: 'Returns', icon: <KeyboardReturn />, path: '/admin/returns' },
    { text: 'Reservations', icon: <History />, path: '/admin/reservations' },
    { text: 'Fine Management', icon: <LocalAtm />, path: '/admin/fines' },
    { text: 'Reports', icon: <BarChart />, path: '/admin/reports' },
    { text: 'Notifications', icon: <Notifications />, path: '/admin/notifications' },
    { text: 'Settings', icon: <Settings />, path: '/admin/settings' },
  ];

  const studentMenu = [
    { text: 'Dashboard', icon: <Dashboard />, path: '/student/dashboard' },
    { text: 'Search Books', icon: <Book />, path: '/student/search-books' },
    { text: 'Library', icon: <Assignment />, path: '/student/my-books' },
    { text: 'Return Books', icon: <KeyboardReturn />, path: '/student/return-books' },
    { text: 'Borrow History', icon: <History />, path: '/student/borrow-history' },
    { text: 'Reservations', icon: <History />, path: '/student/reservations' },
    { text: 'My Fines', icon: <LocalAtm />, path: '/student/my-fines' },
    { text: 'Notifications', icon: <Notifications />, path: '/student/notifications' },
    { text: 'Profile', icon: <Person />, path: '/student/profile' },
  ];

  const menu = isAdmin ? adminMenu : studentMenu;

  const drawer = (
    <Box sx={{ background: 'rgba(15, 23, 42, 0.95)', height: '100%', color: 'white' }}>
      <Toolbar />
      <Divider sx={{ borderColor: 'rgba(255, 255, 255, 0.1)' }} />
      <List>
        {menu.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton
              selected={location.pathname === item.path}
              onClick={() => {
                navigate(item.path);
                if (mobileOpen) handleDrawerToggle();
              }}
              sx={{
                '&.Mui-selected': {
                  backgroundColor: 'rgba(99, 102, 241, 0.25)',
                  borderLeft: '4px solid #6366f1',
                  color: '#818cf8',
                },
                '&:hover': {
                  backgroundColor: 'rgba(255, 255, 255, 0.05)',
                },
              }}
            >
              <ListItemIcon sx={{ color: location.pathname === item.path ? '#818cf8' : 'rgba(255, 255, 255, 0.7)' }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Box>
  );

  return (
    <Box
      component="nav"
      sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
      aria-label="mailbox folders"
    >
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={handleDrawerToggle}
        ModalProps={{
          keepMounted: true,
        }}
        sx={{
          display: { xs: 'block', sm: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, borderRight: 'none' },
        }}
      >
        {drawer}
      </Drawer>
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', sm: 'block' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, borderRight: 'none' },
        }}
        open
      >
        {drawer}
      </Drawer>
    </Box>
  );
}
