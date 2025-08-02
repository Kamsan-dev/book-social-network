import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ToastService } from './layout/toast.service';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-root',
  standalone: true,
  providers: [MessageService],
  imports: [RouterOutlet, ToastModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  private toastService: ToastService = inject(ToastService);
  private messageService = inject(MessageService);

  public ngOnInit(): void {
    //this.initFontAwesome();
    this.listenToastService();
  }

  private listenToastService(): void {
    this.toastService.sendSub.subscribe({
      next: (newMessage) => {
        if (newMessage && newMessage.summary !== this.toastService.INIT_STATE) {
          this.messageService.add(newMessage);
        }
      },
    });
  }
}
