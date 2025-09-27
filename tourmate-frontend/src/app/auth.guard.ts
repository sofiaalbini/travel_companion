import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { ApiService } from './services/api.service';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { AuthService } from './services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private api: ApiService, private router: Router, private auth: AuthService) {}

  canActivate(): Observable<boolean> {
    return this.api.me().pipe(
      map((username) => {
        if (username) {
          this.auth.setUser(username);
          return true;
        }
        this.router.navigate(['/login']);
        return false;
      }),
      catchError(() => {
        this.router.navigate(['/login']);
        this.auth.setUser(null);
        return of(false);
      })
    );
  }
}
