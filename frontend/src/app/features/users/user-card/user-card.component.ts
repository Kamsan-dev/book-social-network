import { ChangeDetectionStrategy, Component, input } from '@angular/core';
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

  getUrlImageUser(user: UserDTO): string {
    if (user.profileImageId) {
      return `${environment?.API_URL}/user/${user.publicId}/profile-image`;
    } else {
      return 'https://www.shutterstock.com/image-vector/default-profile-picture-avatar-photo-600nw-1725917284.jpg';
    }
  }
}
