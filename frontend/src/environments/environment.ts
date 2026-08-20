export const environment = {
  production: true,
  apiUrl: 'http://localhost:8080',
  oauth: {
    /** Base do Spring Authorization Server (/oauth2/authorize, /oauth2/token, /oauth2/jwks). */
    issuer: 'http://localhost:8080',
    /** Client público registrado em RegisteredClientSeeder.algafoodWeb(). */
    clientId: 'algafood-web',
    redirectUri: 'http://localhost:4200/authorized',
    scope: 'READ WRITE',
  },
};
