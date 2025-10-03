import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, of } from 'rxjs';
import { ApiService } from './api.service';
import { tap } from 'rxjs/operators';

/**
 * AuthService
 * 
 * Manages authentication state and user session.
 * Provides reactive `user$` and methods to set, fetch, or clear the current user.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  // Holds the current user (username or null)
  // BehaviorSubject lets components subscribe and react immediately
  private userSubject = new BehaviorSubject<string | null>(null);

  // Public observable for components to subscribe to user changes
  user$ = this.userSubject.asObservable();

  /**
 * Initializes the AuthService.
 * Silently fetches the currently logged-in user from the backend
 * to set the initial frontend authentication state (`user$`).
 */
  constructor(private api: ApiService) {
    this.fetchMe().subscribe(() => { }, () => { });
  }

  /**
  * Calls backend /auth/me to fetch the logged-in user.
  * Updates userSubject with username or null on error.
  */
  fetchMe() {
    return this.api.me().pipe(
      tap((username) => this.userSubject.next(username)),
      catchError((err) => {
        this.userSubject.next(null);
        return of(null);
      })
    );
  }

  /**
  * Manually update the user in frontend state (after login)
  */
  setUser(username: string | null) {
    this.userSubject.next(username);
  }

  /**
   * Logs out the user on frontend side:
   * - Clears JSESSIONID cookie (basic session invalidation)
   * - Resets user state to null
   */
  logout() {
    document.cookie = 'JSESSIONID=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    this.userSubject.next(null);
  }
}
