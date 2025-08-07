import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, input, Output } from '@angular/core';
import { SidebarService } from '../sidebar/sidebar.service';
import { ResponsiveService } from '../../shared/services/response.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent {
  sidebarService = inject(SidebarService);
  responsiveService = inject(ResponsiveService);
  @Output()
  sidebarToggleClick = new EventEmitter<boolean>();

  onSidebarToggleClick(event: MouseEvent | TouchEvent): void {
    event.stopImmediatePropagation();
    this.sidebarService.toggle();
  }
}
