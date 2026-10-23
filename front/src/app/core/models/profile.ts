import { Topic } from './topic';

/** Profile of the logged-in user, as returned by the API. */
export interface Profile {
  id: number;
  username: string;
  email: string;
  subscriptions: Topic[];
}
