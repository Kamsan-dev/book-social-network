import { ChangeDetectionStrategy, Component, inject, input, OnInit } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { IconProp } from '@fortawesome/fontawesome-svg-core';
import { AuthService } from '../../features/auth/services/auth.service';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SidebarService } from './sidebar.service';
import { ResponsiveService } from '../../shared/services/response.service';

export interface sidebarItem {
  label: string;
  icon: IconProp;
  routeLink?: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [FontAwesomeModule, RouterLinkActive, RouterLink, CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SidebarComponent implements OnInit {
  authService = inject(AuthService);
  router = inject(Router);
  sidebarService = inject(SidebarService);
  responsiveService = inject(ResponsiveService);

  sidebarItems = new Array<sidebarItem>();

  ngOnInit(): void {
    this.loadMenu();
  }

  private loadMenu(): void {
    this.sidebarItems = [
      {
        label: 'Users',
        icon: 'users',
        routeLink: 'dashboard',
      },
      {
        label: 'Settings',
        icon: 'gear',
      },
      {
        label: 'Log out',
        icon: 'power-off',
        routeLink: 'logout',
      },
    ];
  }
}
