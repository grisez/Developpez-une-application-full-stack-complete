import { Topic } from './topic';

/** Profile of the logged-in user, as returned by the API. */
export interface Profile {
  id: number;
  username: string;
  email: string;
  subscriptions: Topic[];
}

/** Changes sent from the profile page; an empty password keeps the current one. */
export interface UpdateProfileRequest {
  email: string;
  username: string;
  password: string;
}
