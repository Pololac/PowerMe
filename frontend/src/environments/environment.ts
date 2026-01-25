export const environment = {
  production: true,
  apiBaseUrl: '/api', // OK en prod grâce au reverse proxy

  // Features de production
  debug: false,
  enableMocks: false,
  logLevel: 'error',
};
