import {ApplicationConfig, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {TitleStrategy} from '@angular/router';

import {routes} from './app.routes';
import {authInterceptor} from './infrastructure/web/interceptors/auth.interceptor';

class FixedTitleStrategy extends TitleStrategy {
    override updateTitle(): void {}
}

export const appConfig: ApplicationConfig = {
    providers: [
        {provide: TitleStrategy, useClass: FixedTitleStrategy},
        provideBrowserGlobalErrorListeners(),
        provideRouter(routes),
        provideAnimationsAsync(),
        provideHttpClient(
            withInterceptors([authInterceptor])
        ),
        {
            provide: 'LOCALSTORAGE',
            useFactory: () => (typeof window !== 'undefined' ? window.localStorage : null)
        }
    ]
};
