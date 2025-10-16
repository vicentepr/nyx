import { Component, EventEmitter, Output, inject } from '@angular/core';
import { NgIf } from '@angular/common';

import { AuthService } from '@services/auth/auth.service';

@Component({
  selector: 'app-top-bar',
  standalone: true,
  imports: [NgIf],
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss']
})
export class TopBarComponent {
  private readonly authService = inject(AuthService);

  @Output() toggleMenu = new EventEmitter<void>();

  get username(): string | null {
    return this.authService.getUsername();
  }

  onLogout(): void {
    this.authService.logout();
  }
}
