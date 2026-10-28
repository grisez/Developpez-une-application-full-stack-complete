import { defineConfig } from 'cypress';

/**
 * End-to-end tests run against the whole application: the Angular dev server (npm start) and the back-end
 * started with the dev profile, both running before "npm run e2e".
 */
export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    specPattern: 'cypress/e2e/**/*.cy.ts',
    supportFile: false,
    // The first requests after the back-end starts (Argon2 hashing, JIT warm-up) can exceed the 4 s default
    defaultCommandTimeout: 10000,
    video: false,
    viewportWidth: 1280,
    viewportHeight: 800,
  },
});
