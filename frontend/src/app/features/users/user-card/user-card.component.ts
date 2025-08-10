import { ChangeDetectionStrategy, Component, computed, EventEmitter, input, Output } from '@angular/core';
import { UserDTO } from '../../auth/models/auth.model';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-user-card',
  standalone: true,
  imports: [],
  templateUrl: './user-card.component.html',
  styleUrl: './user-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserCardComponent {
  user = input.required<UserDTO>();

  @Output()
  updateUserClick = new EventEmitter<UserDTO>();

  imageUrl = computed(() => {
    const u = this.user();
    return u.profileImageId
      ? `${environment?.API_URL}/user/${this.user().publicId}/profile-image?t=${Date.now()}`
      : 'https://www.shutterstock.com/image-vector/user-profile-icon-vector-avatar-600nw-2247726673.jpg';
  });

  onUpdateUserClick(event: MouseEvent | TouchEvent) {
    event.stopImmediatePropagation();
    this.updateUserClick.emit(this.user());
  }
}
