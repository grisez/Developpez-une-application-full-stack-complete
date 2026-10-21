/** Error body returned by the API, following RFC 9457 (problem details). */
export interface ProblemDetail {
  status: number;
  detail: string;
  /** Message of each invalid or conflicting field, keyed by field name. */
  errors?: Record<string, string>;
}
