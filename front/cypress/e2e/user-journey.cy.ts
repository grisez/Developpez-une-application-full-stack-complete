/**
 * Complete journey of a new member, through the real user interface and API:
 * registration, subscription, feed, publication, comment, profile, logout and login.
 */
describe('User journey', () => {
  // A unique account per run, so that the test can be replayed on the same database
  const suffix = Date.now().toString(36);
  const username = `e2e-${suffix}`;
  const email = `${username}@mdd.dev`;
  const password = 'E2e!Password1';
  const articleTitle = `Article e2e ${suffix}`;

  it('lets a visitor register, follow a topic, publish, comment, then log out and back in', () => {
    // Registration from the home page
    cy.visit('/');
    cy.contains('a', "S'inscrire").click();
    cy.get('#username').type(username);
    cy.get('#email').type(email);
    cy.get('#password').type(password);
    cy.contains('button', "S'inscrire").click();

    // A new member has no subscription yet
    cy.location('pathname').should('eq', '/feed');
    cy.contains('Votre fil est vide');

    // Subscription from the topics page
    cy.get('nav').contains('a', 'Thèmes').click();
    cy.get('button[aria-label="S\'abonner au thème Java"]').click();
    cy.get('button[aria-label="S\'abonner au thème Java"]').should('not.exist');
    cy.contains('button', 'Déjà abonné').should('be.disabled');

    // Publication of an article on the followed topic
    cy.get('nav').contains('a', 'Articles').click();
    cy.contains('a', 'Créer un article').click();
    cy.get('#topic').select('Java');
    cy.get('#title').type(articleTitle);
    cy.get('#content').type('Contenu publié par le test de bout en bout.');
    cy.contains('button', 'Créer').click();

    // The article appears first in the feed, with its author
    cy.location('pathname').should('eq', '/feed');
    cy.get('main li').first().should('contain', articleTitle).and('contain', username);

    // Comment on the article page
    cy.contains('a', articleTitle).click();
    cy.get('h1').should('contain', articleTitle);
    cy.get('#comment').type('Premier commentaire du test.');
    cy.get('button[aria-label="Envoyer le commentaire"]').click();
    cy.get('section li').last().should('contain', 'Premier commentaire du test.').and('contain', username);

    // The profile lists the subscription, which can be removed
    cy.get('a[aria-label="Mon profil"]').filter(':visible').click();
    cy.get('#username').should('have.value', username);
    cy.get('button[aria-label="Se désabonner du thème Java"]').click();
    cy.get('button[aria-label="Se désabonner du thème Java"]').should('not.exist');

    // Logout closes the session: protected pages send back to the login page
    cy.contains('button', 'Se déconnecter').filter(':visible').click();
    cy.location('pathname').should('eq', '/');
    cy.visit('/feed');
    cy.location('pathname').should('eq', '/login');

    // Login with the username
    cy.get('#identifier').type(username);
    cy.get('#password').type(password);
    cy.contains('button', 'Se connecter').click();
    cy.location('pathname').should('eq', '/feed');
  });

  it('refuses a wrong password with a generic message', () => {
    cy.visit('/login');
    cy.get('#identifier').type('alice');
    cy.get('#password').type('Wrong!Password1');
    cy.contains('button', 'Se connecter').click();

    cy.get('[role="alert"]').should('contain', 'Identifiant ou mot de passe incorrect');
    cy.location('pathname').should('eq', '/login');
  });

  it('shows the field errors of an invalid registration', () => {
    cy.visit('/register');
    cy.get('#username').type('al');
    cy.get('#email').type('not-an-email');
    cy.get('#password').type('weak');
    cy.contains('button', "S'inscrire").click();

    cy.get('#username-error').should('be.visible');
    cy.get('#email-error').should('be.visible');
    cy.location('pathname').should('eq', '/register');
  });
});
