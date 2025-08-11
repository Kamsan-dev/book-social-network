import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, effect, EventEmitter, inject, input, Output, signal } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FileUploadModule } from 'primeng/fileupload';
import { SidebarModule } from 'primeng/sidebar';
import { environment } from '../../../environments/environment';
import { UserDTO } from '../../features/auth/models/auth.model';
import { UserService } from '../../features/users/services/user.service';

@Component({
  selector: 'app-view-user-details',
  standalone: true,
  imports: [CommonModule, SidebarModule, FileUploadModule, FontAwesomeModule],
  templateUrl: './view-user-details.component.html',
  styleUrl: './view-user-details.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ViewUserDetailsComponent {
  userService = inject(UserService);
  user = input.required<UserDTO | null>();
  loading = signal(false);

  connectedUserUrlImage = computed(() => {
    const u = this.user();
    return u?.profileImageId
      ? `${environment?.API_URL}/user/${u?.publicId}/profile-image?t=${Date.now()}`
      : 'https://www.shutterstock.com/image-vector/user-profile-icon-vector-avatar-600nw-2247726673.jpg';
  });
  sidebarVisible = input.required<boolean>();
  uploadedFiles = signal<Array<any>>([]);

  @Output()
  sidebarVisibleChange = new EventEmitter<boolean>();

  onSidebarVisibleChange(event: boolean): void {
    if (!event) {
      this.sidebarVisibleChange.emit(false);
    }
  }

  onUploadProfileImage(event: any, userPublicId: string) {
    const file = event.files[0];
    const formData = new FormData();
    formData.append('file', file);
    this.loading.set(true);
    this.userService.updateProfileImage(formData, userPublicId!);
  }
}
