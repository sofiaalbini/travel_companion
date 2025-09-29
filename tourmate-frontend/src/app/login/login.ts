import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { NotificationComponent } from '../shared/system_notification/notification.component';

/**
 * Component for the login page.
 * Handles user authentication and session initialization.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, NotificationComponent],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  // Form fields bound to input values
  username = '';
  password = '';
  
  //Used to disable the submit button and show loading state while waiting for backend response.
  loading = false;

  /** Reference to the notification component for showing messages */
  @ViewChild(NotificationComponent) notification!: NotificationComponent;

  /**
  * Creates the LoginComponent
  * @param api Service for making API calls to the backend
  * @param auth Service for handling authentication state in the frontend
  * @param router Angular Router for navigation after login
  */
  constructor(private api: ApiService, private auth: AuthService, private router: Router) { }

  /** Handles user login */
  login() {
    // check for empty inputs
    if (!this.username || !this.password) {
      this.notification.showMessage('Please enter username and password', 'success');
      return;
    }

    this.loading = true;
    console.log('Starting login process...');

    // call API service to login
    this.api.login(this.username, this.password).subscribe({
      next: (username) => {
        console.log('Login succeeded, received username:', username);

        // small delay to ensure session is properly set
        setTimeout(() => {
          // fetch current user info
          this.api.me().subscribe({
            next: (username) => {
              this.auth.setUser(username);
              this.router.navigate(['/preferences']);
            },
            error: (err: HttpErrorResponse) => {
              console.error('Error fetching user info:', err);
              this.loading = false;
              this.notification.showMessage(`Login succeeded but fetching user info failed: ${err.status} ${err.error}`, 'error');
            }
          });
        }, 100);
      },
      error: (err: HttpErrorResponse) => {
        console.error('Login error:', err);
        this.loading = false;

        if (err.status === 401) {
          this.notification.showMessage('Invalid username or password', 'error');
        } else if (err.status === 0) {
          this.notification.showMessage('Cannot connect to server. Please check if the backend is running.', 'error');
        } else {
          this.notification.showMessage(`Login failed: ${err.status} - ${err.error || err.message}`, 'error');
        }
      }
    });
  }
}