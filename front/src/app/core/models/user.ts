/** A user as returned by the API. */
export interface User {
  id: number;
  username: string;
  email: string;
}

/** Data sent to create an account. */
export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
}
