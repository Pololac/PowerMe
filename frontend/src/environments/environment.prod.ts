export const environment = {
  production: true,

  apiUrl: '/api', // ✅ Fonctionne en dev ET prod !

  // Features de production
  debug: false,
  enableMocks: false,
  logLevel: 'error',

  // Services externes (clés de production)
  mapboxToken: 'pk.live.REAL_TOKEN',
  stripePublicKey: 'pk_live_REAL_KEY',
};
