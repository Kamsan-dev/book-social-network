import { computed, Injectable, signal } from '@angular/core';
import { ResponsiveService } from '../../shared/services/response.service';

@Injectable({ providedIn: 'root' })
export class SidebarService {
  private collapsed$ = signal<boolean>(false);
  readonly collapsedSig = computed(() => this.collapsed$());

  private open$ = signal<boolean>(false);
  readonly openSig = computed(() => this.open$());

  constructor(private responsiveService: ResponsiveService) {}

  toggle(): void {
    if (this.responsiveService.phone() || this.responsiveService.tablet()) {
      this.open$.update((value) => !value);
      return;
    }
    this.collapsed$.update((value) => !value);
  }
}
