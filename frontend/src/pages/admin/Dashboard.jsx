import React, { useEffect, useState } from 'react';
import { Grid, Card, CardContent, Typography, Box, CircularProgress } from '@mui/material';
import { Book, People, Assignment, KeyboardReturn, History, LocalAtm, Error, Warning } from '@mui/icons-material';
import { Bar, Doughnut } from 'react-chartjs-2';
import api from '../../services/api';
import { motion } from 'framer-motion';
import GlobalBooksWidget from '../../components/GlobalBooksWidget';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const res = await api.get('/dashboard');
        setStats(res.data);
      } catch (e) {
        console.error("Failed to load dashboard analytics", e);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  const statCards = [
    { title: 'Total Books', value: stats?.totalBooks || 0, icon: <Book fontSize="large" color="primary" /> },
    { title: 'Available Books', value: stats?.availableBooks || 0, icon: <Book fontSize="large" color="success" /> },
    { title: 'Borrowed Books', value: stats?.borrowedBooks || 0, icon: <Assignment fontSize="large" color="warning" /> },
    { title: 'Returned Books', value: stats?.returnedBooks || 0, icon: <KeyboardReturn fontSize="large" color="info" /> },
    { title: 'Lost Books', value: stats?.lostBooks || 0, icon: <Error fontSize="large" color="error" /> },
    { title: 'Registered Users', value: stats?.registeredUsers || 0, icon: <People fontSize="large" color="primary" /> },
    { title: 'Pending Requests', value: stats?.pendingRequests || 0, icon: <History fontSize="large" color="warning" /> },
    { title: 'Issued Today', value: stats?.issuedToday || 0, icon: <Assignment fontSize="large" color="secondary" /> },
    { title: 'Returned Today', value: stats?.returnedToday || 0, icon: <KeyboardReturn fontSize="large" color="success" /> },
  ];

  // Chart Data
  const doughnutData = {
    labels: stats?.booksByCategory ? Object.keys(stats.booksByCategory) : [],
    datasets: [
      {
        data: stats?.booksByCategory ? Object.values(stats.booksByCategory) : [],
        backgroundColor: ['#6366f1', '#a855f7', '#10b981', '#f59e0b', '#ef4444', '#3b82f6'],
        borderWidth: 1,
      },
    ],
  };

  const barData = {
    labels: stats?.monthlyIssues ? Object.keys(stats.monthlyIssues) : [],
    datasets: [
      {
        label: 'Issues',
        data: stats?.monthlyIssues ? Object.values(stats.monthlyIssues) : [],
        backgroundColor: '#6366f1',
      },
      {
        label: 'Returns',
        data: stats?.monthlyReturns ? Object.values(stats.monthlyReturns) : [],
        backgroundColor: '#10b981',
      },
    ],
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" mb={3} color="text.primary">
        Library Admin Dashboard
      </Typography>

      <Grid container spacing={3} mb={4}>
        {statCards.map((card, idx) => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={idx}>
            <motion.div
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3, delay: idx * 0.05 }}
            >
              <Card sx={{ background: 'rgba(30, 41, 59, 0.45)' }}>
                <CardContent sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Box>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      {card.title}
                    </Typography>
                    <Typography variant="h4" fontWeight="bold">
                      {card.value}
                    </Typography>
                  </Box>
                  <Box sx={{ p: 1.5, background: 'rgba(255, 255, 255, 0.05)', borderRadius: '50%' }}>
                    {card.icon}
                  </Box>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={4}>
        <Grid item xs={12} md={8}>
          <Card sx={{ p: 2, background: 'rgba(30, 41, 59, 0.45)' }}>
            <Typography variant="h6" fontWeight="bold" mb={2}>
              Monthly Activity (Issues & Returns)
            </Typography>
            <Box height={300}>
              <Bar data={barData} options={{ responsive: true, maintainAspectRatio: false }} />
            </Box>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card sx={{ p: 2, background: 'rgba(30, 41, 59, 0.45)' }}>
            <Typography variant="h6" fontWeight="bold" mb={2}>
              Books Category Split
            </Typography>
            <Box sx={{ height: 300, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
              <Doughnut data={doughnutData} options={{ responsive: true, maintainAspectRatio: false }} />
            </Box>
          </Card>
        </Grid>
      </Grid>

      <GlobalBooksWidget />
    </Box>
  );
}
