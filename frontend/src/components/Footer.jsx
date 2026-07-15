import React from 'react';
import { Box, Typography } from '@mui/material';

export default function Footer() {
  return (
    <Box
      component="footer"
      sx={{
        py: 3,
        px: 2,
        mt: 'auto',
        textAlign: 'center',
        background: 'rgba(30, 41, 59, 0.4)',
        backdropFilter: 'blur(8px)',
        borderTop: '1px solid rgba(255, 255, 255, 0.05)',
      }}
    >
      <Typography variant="body2" color="text.secondary">
        © 2026 BookVerse. Developed by Sahasra. All Rights Reserved.
      </Typography>
    </Box>
  );
}
