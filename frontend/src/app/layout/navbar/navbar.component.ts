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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent {
  sidebarService = inject(SidebarService);
  responsiveService = inject(ResponsiveService);
  authService = inject(AuthService);

  connectedUser = computed(() => this.authService.userSig() as UserDTO);
  connectedUserUrlImage = computed(() => {
    const user = this.connectedUser();
    return user.profileImageId
      ? `${environment?.API_URL}/user/${user.publicId}/profile-image?t=${Date.now()}`
      : 'https://www.shutterstock.com/image-vector/user-profile-icon-vector-avatar-600nw-2247726673.jpg';
  });

  onSidebarToggleClick(event: MouseEvent | TouchEvent): void {
    event.stopImmediatePropagation();
    this.sidebarService.toggle();
  }
}
