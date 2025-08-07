import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from '../../features/auth/services/auth.service';
import { SidebarService } from '../sidebar/sidebar.service';
import { ResponsiveService } from '../../shared/services/response.service';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [RouterOutlet, CommonModule],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MainComponent {
  authService = inject(AuthService);
  sidebarService = inject(SidebarService);
  responsiveService = inject(ResponsiveService);
}
