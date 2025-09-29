import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';


/**
 * NotificationComponent
 * Displays temporary success or error messages.
 */
@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification" *ngIf="message" [ngClass]="type">
      {{ message }}
    </div>
  `,
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent {
  // Current message to display
  message: string | null = null;
  // Message type affects styling
  type: 'success' | 'error' = 'success';

  /**
   * Shows a notification message for a limited duration.
   * @param msg The message text
   * @param type 'success' or 'error' (default 'success')
   * @param duration Duration in ms to display the message (default 3000)
   */
  showMessage(msg: string, type: 'success' | 'error' = 'success', duration = 3000) {
    this.message = msg;
    this.type = type;
    setTimeout(() => this.message = null, duration);
  }
}
