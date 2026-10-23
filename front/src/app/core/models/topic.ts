/** A topic as returned by the API, with the subscription state of the logged-in user. */
export interface Topic {
  id: number;
  name: string;
  description: string;
  subscribed: boolean;
}
