/**
 * Screenshots of every screen on desktop and mobile, used by the UI/UX annex (UI-UX.md).
 * The logged-in screens use the demo account "alice" of the dev profile.
 */
const devices = {
  desktop: { width: 1280, height: 800 },
  mobile: { width: 375, height: 812 },
};

describe('Screens of the application', () => {
  for (const [device, { width, height }] of Object.entries(devices)) {
    it(`captures the ${device} screens`, () => {
      const capture = (name: string) => {
        // Wait for the page and its logo: a screenshot taken too early would be blank or without the logo
        cy.get('main').should('be.visible');
        cy.document().should((document) => {
          const visibleImages = [...document.images].filter((image) => image.offsetParent !== null);
          expect(visibleImages.every((image) => image.complete && image.naturalWidth > 0)).to.equal(true);
        });
        cy.screenshot(`${device}/${name}`, { capture: 'fullPage' });
      };
      cy.viewport(width, height);

      cy.visit('/');
      capture('01-home');

      cy.visit('/register');
      capture('02-register');

      cy.visit('/login');
      capture('03-login');
      cy.get('#identifier').type('alice');
      cy.get('#password').type('Demo!Mdd2026');
      cy.contains('button', 'Se connecter').click();

      cy.location('pathname').should('eq', '/feed');
      cy.get('main li').should('have.length.greaterThan', 0);
      capture('04-feed');

      cy.visit('/articles/1001');
      cy.get('section li').should('have.length.greaterThan', 0);
      capture('05-article');

      cy.visit('/articles/new');
      cy.get('#topic option').should('have.length.greaterThan', 1);
      capture('06-create-article');

      cy.visit('/topics');
      cy.get('main li').should('have.length', 8);
      capture('07-topics');

      cy.visit('/profile');
      cy.get('#username').should('have.value', 'alice');
      capture('08-profile');

      if (device === 'mobile') {
        cy.get('button[aria-controls="mobile-menu"]').click();
        cy.get('#mobile-menu').should('be.visible');
        cy.screenshot(`${device}/09-menu`, { capture: 'viewport' });
      }

      cy.visit('/unknown-page');
      capture('10-not-found');
    });
  }
});
