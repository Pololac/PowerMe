export const environment = {
  production: true,

  apiBaseUrl: '/api', // OK en prod grâce au reverse proxy

  // Features de production
  debug: false,
  enableMocks: false,
  logLevel: 'error',

  // Services externes (clés de production)
  mapboxToken: 'pk.live.REAL_TOKEN',
  stripePublicKey: 'pk_live_REAL_KEY',
};
