// notification.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

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
  message: string | null = null;
  type: 'success' | 'error' = 'success';

  showMessage(msg: string, type: 'success' | 'error' = 'success', duration = 3000) {
    this.message = msg;
    this.type = type;
    setTimeout(() => this.message = null, duration);
  }
}
