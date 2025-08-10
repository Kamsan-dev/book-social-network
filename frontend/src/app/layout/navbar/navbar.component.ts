import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, EventEmitter, inject, input, Output } from '@angular/core';
import { SidebarService } from '../sidebar/sidebar.service';
import { ResponsiveService } from '../../shared/services/response.service';
import { AuthService } from '../../features/auth/services/auth.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { UserDTO } from '../../features/auth/models/auth.model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
  // changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent {
  sidebarService = inject(SidebarService);
  responsiveService = inject(ResponsiveService);
  authService = inject(AuthService);

  connectedUser = computed(() => this.authService.userSig() as UserDTO);
  connectedUserUrlImage = computed(() => `${environment?.API_URL}/user/${this.connectedUser().publicId}/profile-image`);

  onSidebarToggleClick(event: MouseEvent | TouchEvent): void {
    event.stopImmediatePropagation();
    this.sidebarService.toggle();
  }
}
