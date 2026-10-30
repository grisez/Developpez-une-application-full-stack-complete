import { mkdirSync, renameSync } from 'node:fs';
import { dirname, join } from 'node:path';

import { defineConfig } from 'cypress';

import e2eConfig from './cypress.config';

/**
 * Captures the screens of the application for the UI/UX annex ("npm run screenshots"), with the same prerequisites
 * as the end-to-end tests: the front-end and the back-end with the dev profile running.
 */
export default defineConfig({
  e2e: {
    ...e2eConfig.e2e,
    specPattern: 'cypress/docs/**/*.cy.ts',
    setupNodeEvents(on, config) {
      // Cypress stores the screenshots per spec file: they are moved to docs/screenshots/<device>/<page>.png instead
      on('after:screenshot', (details) => {
        // Screenshots taken automatically on a failure have no name: they stay in the default folder
        if (!details.name) {
          return;
        }
        const path = join(config.projectRoot, '..', 'docs', 'screenshots', `${details.name}.png`);
        mkdirSync(dirname(path), { recursive: true });
        renameSync(details.path, path);
        return { path };
      });
    },
  },
});
