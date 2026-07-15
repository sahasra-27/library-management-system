import React, { useEffect, useState, useContext } from 'react';
import { Card, CardContent, Typography, Button, Box, List, ListItem, ListItemText, Divider, Chip } from '@mui/material';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { toast } from 'react-toastify';

export default function Notifications() {
  const { user } = useContext(AuthContext);
  const [notifications, setNotifications] = useState([]);

  const fetchNotifications = async () => {
    try {
      const res = await api.get('/notifications', {
        params: { userId: user.id }
      });
      setNotifications(res.data);
    } catch (e) {
      toast.error("Failed to load notifications.");
    }
  };

  useEffect(() => {
    if (user) fetchNotifications();
  }, [user]);

  const handleMarkAllRead = async () => {
    try {
      await api.post('/notifications/read-all', null, {
        params: { userId: user.id }
      });
      toast.success("All notifications marked as read!");
      fetchNotifications();
    } catch (e) {
      toast.error("Failed to update status.");
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          System Notifications
        </Typography>
        <Button variant="outlined" onClick={handleMarkAllRead}>
          Mark All as Read
        </Button>
      </Box>

      <Card sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
        <CardContent>
          {notifications.length === 0 ? (
            <Typography variant="body1" align="center" py={4} color="text.secondary">
              No notifications found.
            </Typography>
          ) : (
            <List>
              {notifications.map((n, idx) => (
                <React.Fragment key={n.id}>
                  <ListItem sx={{ py: 2 }}>
                    <ListItemText
                      primary={
                        <Box display="flex" alignItems="center" gap={1} mb={0.5}>
                          <Chip
                            label={n.type.replace('_', ' ')}
                            color={
                              n.type.includes('FINE') ? 'error' :
                              n.type.includes('SUCCESS') ? 'success' : 'info'
                            }
                            size="small"
                          />
                          {!n.readStatus && <Chip label="Unread" color="warning" size="small" />}
                          <Typography variant="caption" color="text.secondary" sx={{ ml: 'auto' }}>
                            {new Date(n.sentAt).toLocaleString()}
                          </Typography>
                        </Box>
                      }
                      secondary={
                        <Typography variant="body1" color="text.primary">
                          {n.message}
                        </Typography>
                      }
                    />
                  </ListItem>
                  {idx < notifications.length - 1 && <Divider />}
                </React.Fragment>
              ))}
            </List>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
