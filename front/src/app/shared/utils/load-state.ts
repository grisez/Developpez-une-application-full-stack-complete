import { OperatorFunction, catchError, map, of, startWith } from 'rxjs';

/** What a page displays for data loaded from the API: a loading message, the data, or an error with a retry. */
export type LoadState<T> = { status: 'loading' } | { status: 'loaded'; value: T } | { status: 'error' };

/**
 * Turns a request into the successive states of a page. The error becomes a state instead of being rethrown: an
 * error rethrown into a signal would break the page instead of displaying a message.
 */
export function toLoadState<T>(): OperatorFunction<T, LoadState<T>> {
  return (request) =>
    request.pipe(
      map((value): LoadState<T> => ({ status: 'loaded', value })),
      catchError(() => of<LoadState<T>>({ status: 'error' })),
      startWith<LoadState<T>>({ status: 'loading' }),
    );
}
