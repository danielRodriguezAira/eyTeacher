import {Component, inject, OnInit, signal} from '@angular/core';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {CommonModule} from '@angular/common';
import {trigger, state, style, transition, animate} from '@angular/animations';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {MatDividerModule} from '@angular/material/divider';
import {AuthenticationService} from './infrastructure/services/auth.service';
import {CategoryService} from './infrastructure/services/category.service';
import {Category} from './domain/entities/category';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatDividerModule
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
  animations: [
    trigger('expandCollapse', [
      state('collapsed', style({
        height: '0px',
        minHeight: '0',
        display: 'none'
      })),
      state('expanded', style({
        height: '*'
      })),
      transition('expanded <=> collapsed', animate('300ms cubic-bezier(0.4, 0, 0.2, 1)'))
    ])
  ]
})
export class App implements OnInit {
  protected readonly title = signal('EyTeacher');
  protected categories: Category[] = [];

  private readonly router = inject(Router);
  private readonly auth = inject(AuthenticationService);
  private readonly categoryService = inject(CategoryService);

  ngOnInit(): void {
    this.categories = this.categoryService.getCategories();
  }

  isLoginRoute(): boolean {
    return this.router.url.startsWith('/login');
  }

  onLogout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
