import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../services/api.service';
import { NotificationComponent } from '../shared/system_notification/notification.component';

/**
 * RegisterComponent
 * Provides a registration form for new users.
*/
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, NotificationComponent],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  // Form fields bound to input values
  username = '';
  password = '';
  //Used to disable the submit button and show loading state while waiting for backend response.
  loading = false;

  /** Reference to the notification component for showing messages */
  @ViewChild(NotificationComponent) notification!: NotificationComponent;

  /**
 * Creates the RegisterComponent
 * @param api Service for making API calls to the backend
 * @param router Angular Router for navigation after login
 */
  constructor(private api: ApiService, private router: Router) { }

  /** Handles user registration */
  register() {
    this.loading = true;
    // calls backend API to create a new account
    this.api.register(this.username, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.notification.showMessage('Registration successful! Please log in.', 'success');
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000); // success -> redirects to login
      },
      error: (err) => {
        this.loading = false;
        const msg = err?.error || 'Registration failed (username may already exist)';
        this.notification.showMessage(msg, 'error');
      }
    });
  }
}
