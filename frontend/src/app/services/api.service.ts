import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApiService {
  /** Base URL for all API calls, taken from environment configuration */
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Registers a new user.
   * @param username user's desired username
   * @param password user's password
   * @returns Observable for HTTP response
   */
  register(username: string, password: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/register`, { username, password }, {
      withCredentials: true,
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  /**
  * Logs in a user by verifying credentials.
  * Sets cookies for session-based authentication.
  * @param username username
  * @param password password
  * @returns Observable emitting the logged-in username
  */
  login(username: string, password: string): Observable<string> {
    // trim whitespace to avoid issues
    const loginData = {
      username: username.trim(),
      password: password.trim()
    };

    console.log('Attempting login with:', { username: loginData.username }); // Don't log password

    return this.http.post<string>(`${this.baseUrl}/auth/login`, loginData, {
      withCredentials: true,
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      responseType: 'text' as 'json' // Since backend returns just a string (username)
    }).pipe(
      tap(username => {
        console.log('Login successful, received username:', username);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Login error:', error.status, error.error);
        return throwError(() => error);
      })
    );
  }

  /**
  * Retrieves the currently logged-in user's username.
  * @returns Observable emitting the username as string
  */
  me(): Observable<string> {
    return this.http.get(`${this.baseUrl}/auth/me`, {
      withCredentials: true,
      responseType: 'text'
    }).pipe(
      tap(user => console.log('Current user:', user)),
      catchError((error: HttpErrorResponse) => {
        console.error('Me endpoint error:', error.status, error.error);
        return throwError(() => error);
      })
    );
  }

  /**
  * Logs out the current user by clearing session on the server.
  * @returns Observable for HTTP response
  */
  logout(): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/logout`, {}, {
      withCredentials: true,
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  /**
   * Creates preferences for the logged-in user.
   * @param preferences list of preferences
   * @returns Observable for HTTP response
   */
  createPreferences(preferences: string[]): Observable<any> {
    return this.http.post(`${this.baseUrl}/preferences`, { preferences }, { withCredentials: true });
  }

  /**
   * Updates existing preferences for the logged-in user.
   * @param preferences list of updated preferences
   * @returns Observable for HTTP response
   */
  updatePreferences(preferences: string[]): Observable<any> {
    return this.http.put(`${this.baseUrl}/preferences`, { preferences }, { withCredentials: true });
  }

  /**
   * Retrieves preferences of the logged-in user.
   * @returns Observable emitting a list of preferences
   */
  getPreferences(): Observable<any> {
    return this.http.get(`${this.baseUrl}/preferences`, { withCredentials: true });
  }

  // /**
  // * Creates preferences for the logged-in user.
  // * @param preferences list of preferences
  // * @returns Observable for HTTP response
  // */
  // createPreferences(preferences: string[]): Observable<any> {
  //   return this.http.post(`${this.baseUrl}/preferences`, { preferences }, { withCredentials: true });
  // }

  // /**
  //    * Updates existing preferences for the logged-in user.
  //    * @param preferences list of updated preferences
  //    * @returns Observable for HTTP response
  //    */
  // updatePreferences(preferences: string[]): Observable<any> {
  //   return this.http.put(`${this.baseUrl}/preferences`, { preferences }, { withCredentials: true });
  // }


  // /**
  //   * Retrieves preferences of the logged-in user.
  //   * @returns Observable emitting a list of preferences
  //   */
  // getPreferences(): Observable<any> {
  //   return this.http.get(`${this.baseUrl}/preferences`, { withCredentials: true });
  // }


}