/** A user as returned by the API. */
export interface User {
  id: number;
  username: string;
  email: string;
}

/** Credentials sent to log in: the identifier is either the email or the username. */
export interface LoginRequest {
  identifier: string;
  password: string;
}

/** Data sent to create an account. */
export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
}
