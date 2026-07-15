import React, { createContext, useState, useMemo } from 'react';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

export const ThemeModeContext = createContext({ toggleThemeMode: () => {} });

export const CustomThemeProvider = ({ children }) => {
  const [mode, setMode] = useState(localStorage.getItem('themeMode') || 'dark');

  const themeMode = useMemo(
    () => ({
      toggleThemeMode: () => {
        setMode((prevMode) => {
          const nextMode = prevMode === 'light' ? 'dark' : 'light';
          localStorage.setItem('themeMode', nextMode);
          return nextMode;
        });
      },
    }),
    []
  );

  const theme = useMemo(
    () =>
      createTheme({
        palette: {
          mode,
          primary: {
            main: mode === 'dark' ? '#6366f1' : '#4f46e5', // indigo
          },
          secondary: {
            main: mode === 'dark' ? '#a855f7' : '#9333ea', // purple
          },
          background: {
            default: mode === 'dark' ? '#0f172a' : '#f8fafc', // slate-900 or slate-50
            paper: mode === 'dark' ? 'rgba(30, 41, 59, 0.7)' : 'rgba(255, 255, 255, 0.7)',
          },
        },
        typography: {
          fontFamily: "'Outfit', 'Inter', sans-serif",
          button: {
            textTransform: 'none',
            fontWeight: 600,
          },
        },
        components: {
          MuiCard: {
            styleOverrides: {
              root: {
                borderRadius: 16,
                backdropFilter: 'blur(12px)',
                border: mode === 'dark' ? '1px solid rgba(255, 255, 255, 0.08)' : '1px solid rgba(0, 0, 0, 0.08)',
                boxShadow: mode === 'dark' 
                  ? '0 4px 30px rgba(0, 0, 0, 0.4)' 
                  : '0 4px 30px rgba(0, 0, 0, 0.05)',
              },
            },
          },
          MuiPaper: {
            styleOverrides: {
              root: {
                backgroundImage: 'none',
              },
            },
          },
        },
      }),
    [mode]
  );

  return (
    <ThemeModeContext.Provider value={themeMode}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </ThemeModeContext.Provider>
  );
};
