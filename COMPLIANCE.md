# Compliance

How MDD handles personal data, following the GDPR (RGPD) and the recommendations of the CNIL.

## Legal notice

| Item | Value |
| --- | --- |
| Publisher | MDD, project carried out by Cindy Grisez as part of an OpenClassrooms training course |
| Contact | `contact@mdd.example` (fictional address, to be replaced before going live) |
| Hosting | Not deployed: the application runs locally with Docker |

## Privacy policy

### Data collected

| Data | Purpose | Storage |
| --- | --- | --- |
| Username | Identify the member and sign their articles and comments | `users` table |
| Email address | Log in and identify the account | `users` table, stored in lower case |
| Password | Log in | `users` table, only as an Argon2id hash: the password itself is never stored |
| Account creation and update dates | Follow the account changes | `users` table |
| Topic subscriptions | Build the news feed | `subscriptions` table |
| Articles and comments | Content published by the member | `posts` and `comments` tables |

No other data is collected: no IP address, no browsing history, no profiling. The data is only used to run the social network and is never shared with third parties.

### Retention

The data is kept as long as the account exists. A member can ask for their account and data to be deleted by writing to the contact address; the deletion is done within one month, as required by the GDPR.

### Rights of the members

Every member has a right of access, rectification, erasure, restriction, objection and portability. Username, email address and password can be changed directly from the profile page; the other rights are exercised through the contact address. A member can also lodge a complaint with the CNIL.

### Cookies

| Cookie | Purpose | Lifetime |
| --- | --- | --- |
| `mdd_token` | Keeps the member logged in (HttpOnly, `SameSite=Strict`) | 7 days, deleted on logout |
| `XSRF-TOKEN` | Protects the forms against CSRF attacks | Browser session |

Both cookies are strictly necessary for the service. The application uses no tracker: no analytics, no advertising, no third-party script or font. According to the CNIL, no consent banner is needed.

## Security of the logs

- No password, token or personal data is written to the logs.
- The two messages logged by the back-end are generic: a rejected data conflict, logged without the conflicting value, and an unexpected error.
- In development, the SQL queries displayed show `?` placeholders, never the values.
- In production, the libraries only log warnings and errors; the application keeps its own startup messages (`application-prod.properties`).

## Improvement axes

- Display the legal notice and the privacy policy as pages of the application, linked from a footer on every page.
- Let members delete their account themselves from the profile page.

The other improvement axes of the project are listed in [ARCHITECTURE.md](ARCHITECTURE.md#limits-and-improvement-axes).
