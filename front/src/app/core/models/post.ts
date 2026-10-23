/** An article as displayed in the news feed ("post" in the code, following the specifications glossary). */
export interface Post {
  id: number;
  title: string;
  content: string;
  /** ISO 8601 date set by the server. */
  createdAt: string;
  authorUsername: string;
  topicId: number;
  topicName: string;
}

/** Order of the news feed: most recent first (desc) or oldest first (asc). */
export type FeedSort = 'desc' | 'asc';
