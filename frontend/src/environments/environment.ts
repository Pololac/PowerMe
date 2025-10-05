export const environment = {
  production: false,
  apiUrl: '/api', // ✅ Fonctionne en dev ET prod !

  // Features de développement
  debug: true,
  enableMocks: false,
  logLevel: 'debug',

  // Services externes (clés de test)
  mapboxToken: 'pk.test.123456',
  stripePublicKey: 'pk_test_123456',
};
