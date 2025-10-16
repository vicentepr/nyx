import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NgClass } from '@angular/common';

import { TopBarComponent } from '@app/core/components/top-bar/top-bar.component';
import { SideMenuComponent } from '@app/core/components/side-menu/side-menu.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, TopBarComponent, SideMenuComponent, NgClass],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent {
  protected readonly menuOpened = signal(true);

  toggleMenu(): void {
    this.menuOpened.update((value) => !value);
  }

  closeMenu(): void {
    this.menuOpened.set(false);
  }
}
